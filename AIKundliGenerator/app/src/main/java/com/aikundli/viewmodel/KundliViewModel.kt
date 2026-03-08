package com.aikundli.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aikundli.model.*
import com.aikundli.repository.KundliRepository
import com.aikundli.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── UI State ──────────────────────────────────────────────────────────────

data class KundliUiState(
    val isLoading    : Boolean          = false,
    val kundliResult : KundliResponse?  = null,
    val horoscope    : HoroscopeResult? = null,
    val error        : String?          = null,
    val currentName  : String           = ""
)

data class HoroscopeUiState(
    val isLoading  : Boolean               = false,
    val horoscopes : List<ZodiacHoroscope> = emptyList(),
    val error      : String?               = null
)

// ── ViewModel ─────────────────────────────────────────────────────────────

class KundliViewModel : ViewModel() {

    private val repository = KundliRepository()

    private val _kundliState = MutableStateFlow(KundliUiState())
    val kundliState: StateFlow<KundliUiState> = _kundliState.asStateFlow()

    private val _horoscopeState = MutableStateFlow(HoroscopeUiState())
    val horoscopeState: StateFlow<HoroscopeUiState> = _horoscopeState.asStateFlow()

    // ── Generate Kundli ───────────────────────────────────────────────────

    fun generateKundli(request: KundliRequest) {
        viewModelScope.launch {
            _kundliState.update { it.copy(isLoading = true, error = null, currentName = request.name) }

            when (val result = repository.generateKundli(request)) {
                is Result.Success -> {
                    _kundliState.update {
                        it.copy(isLoading = false, kundliResult = result.data, error = null)
                    }
                    // Fetch horoscope as a non-blocking follow-up
                    fetchHoroscope(
                        name      = request.name,
                        planets   = result.data.planets,
                        ascendant = result.data.ascendant,
                        nakshatra = result.data.nakshatra
                    )
                }
                is Result.Error -> {
                    _kundliState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Result.Loading -> { /* handled above */ }
            }
        }
    }

    // ── Fetch AI Horoscope (non-fatal) ────────────────────────────────────

    private fun fetchHoroscope(
        name      : String,
        planets   : List<PlanetPosition>,
        ascendant : String,
        nakshatra : String
    ) {
        viewModelScope.launch {
            when (val result = repository.getAiHoroscope(name, planets, ascendant, nakshatra)) {
                is Result.Success -> {
                    _kundliState.update { it.copy(horoscope = result.data) }
                }
                is Result.Error -> {
                    // Show fallback — kundli data is still valid
                    _kundliState.update {
                        it.copy(
                            horoscope = HoroscopeResult(
                                personality = "Horoscope unavailable. Check your connection and try again."
                            )
                        )
                    }
                }
                is Result.Loading -> { /* no-op */ }
            }
        }
    }

    // ── Reset / Clear ─────────────────────────────────────────────────────

    fun resetKundliState() {
        _kundliState.update { KundliUiState() }
    }

    fun clearError() {
        _kundliState.update { it.copy(error = null) }
    }
}
