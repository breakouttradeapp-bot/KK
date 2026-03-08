package com.aikundli.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// ── Kundli Request ────────────────────────────────────────────────────────

data class KundliRequest(
    val name         : String,
    val gender       : String,
    @SerializedName("date_of_birth") val dateOfBirth  : String,
    @SerializedName("time_of_birth") val timeOfBirth  : String,
    val placeOfBirth : String,
    val timezone     : String = "Asia/Kolkata"
)

// ── Kundli Response ───────────────────────────────────────────────────────

data class KundliResponse(
    val success   : Boolean              = true,
    val planets   : List<PlanetPosition> = emptyList(),
    val houses    : List<HouseInfo>      = emptyList(),
    val ascendant : String               = "",
    val moonSign  : String               = "",
    val sunSign   : String               = "",
    val nakshatra : String               = "",
    val yoga      : String               = ""
)

data class PlanetPosition(
    val planet  : String  = "",
    val sign    : String  = "",
    val degree  : Double  = 0.0,
    val house   : Int     = 0,
    val isRetro : Boolean = false
)

data class HouseInfo(
    val house  : Int    = 0,
    val sign   : String = "",
    val degree : Double = 0.0
)

// ── Cerebras API models ───────────────────────────────────────────────────

data class CerebrasRequest(
    val model       : String            = "llama3.1-8b",
    @SerializedName("max_tokens") val maxTokens : Int = 2048,
    val messages    : List<ChatMessage>,
    val temperature : Double            = 0.3,
    val stream      : Boolean           = false
)

data class ChatMessage(
    val role    : String,
    val content : String
)

data class CerebrasResponse(
    val id      : String       = "",
    val choices : List<Choice> = emptyList()
)

data class Choice(
    val message: ChatMessage = ChatMessage("assistant", "")
)

// ── Horoscope interpretation ──────────────────────────────────────────────

data class HoroscopeResult(
    val personality  : String = "",
    val career       : String = "",
    val marriage     : String = "",
    val finance      : String = "",
    val health       : String = "",
    val luckyNumbers : String = "",
    val luckyColors  : String = ""
)

// ── Saved Report (Room entity) ─────────────────────────────────────────────

@Entity(tableName = "saved_reports")
data class SavedReport(
    @PrimaryKey(autoGenerate = true) val id : Int    = 0,
    val name          : String  = "",
    val dateOfBirth   : String  = "",
    val ascendant     : String  = "",
    val moonSign      : String  = "",
    val planetsJson   : String  = "",
    val horoscopeJson : String  = "",
    val pdfPath       : String? = null,
    val createdAt     : Long    = System.currentTimeMillis()
)

// ── Daily Horoscope ───────────────────────────────────────────────────────

data class ZodiacHoroscope(
    val sign        : String = "",
    val symbol      : String = "",
    val text        : String = "",
    val luckyNumber : Int    = 0,
    val luckyColor  : String = ""
)
