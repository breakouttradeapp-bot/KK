package com.aikundli.repository

import com.aikundli.model.*
import com.aikundli.network.RetrofitClient
import com.aikundli.util.Constants
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class KundliRepository {

    private val api  = RetrofitClient.cerebrasApi
    private val gson = Gson()

    // ── Generate Kundli ───────────────────────────────────────────────────

    suspend fun generateKundli(request: KundliRequest): Result<KundliResponse> =
        withContext(Dispatchers.IO) {
            try {
                val systemPrompt = """
                    You are an expert Vedic astrology calculator. Given birth details, calculate
                    accurate planetary positions and return ONLY a valid JSON object with NO markdown,
                    NO explanation, NO code fences. Pure JSON only.

                    Return exactly this structure:
                    {
                      "ascendant": "Scorpio",
                      "sunSign": "Leo",
                      "moonSign": "Gemini",
                      "nakshatra": "Rohini",
                      "yoga": "Gaja Kesari Yoga",
                      "planets": [
                        {"planet":"Sun","sign":"Leo","degree":23.5,"house":10,"isRetro":false},
                        {"planet":"Moon","sign":"Gemini","degree":12.3,"house":8,"isRetro":false},
                        {"planet":"Mars","sign":"Aries","degree":5.1,"house":6,"isRetro":false},
                        {"planet":"Mercury","sign":"Virgo","degree":18.9,"house":11,"isRetro":true},
                        {"planet":"Jupiter","sign":"Sagittarius","degree":7.4,"house":2,"isRetro":false},
                        {"planet":"Venus","sign":"Cancer","degree":29.0,"house":9,"isRetro":false},
                        {"planet":"Saturn","sign":"Capricorn","degree":11.6,"house":3,"isRetro":true},
                        {"planet":"Rahu","sign":"Taurus","degree":14.2,"house":7,"isRetro":true},
                        {"planet":"Ketu","sign":"Scorpio","degree":14.2,"house":1,"isRetro":true}
                      ],
                      "houses": [
                        {"house":1,"sign":"Scorpio","degree":15.2},
                        {"house":2,"sign":"Sagittarius","degree":15.2},
                        {"house":3,"sign":"Capricorn","degree":15.2},
                        {"house":4,"sign":"Aquarius","degree":15.2},
                        {"house":5,"sign":"Pisces","degree":15.2},
                        {"house":6,"sign":"Aries","degree":15.2},
                        {"house":7,"sign":"Taurus","degree":15.2},
                        {"house":8,"sign":"Gemini","degree":15.2},
                        {"house":9,"sign":"Cancer","degree":15.2},
                        {"house":10,"sign":"Leo","degree":15.2},
                        {"house":11,"sign":"Virgo","degree":15.2},
                        {"house":12,"sign":"Libra","degree":15.2}
                      ]
                    }
                """.trimIndent()

                val userPrompt = """
                    Calculate the Vedic birth chart for:
                    Name: ${request.name}
                    Gender: ${request.gender}
                    Date of Birth: ${request.dateOfBirth}
                    Time of Birth: ${request.timeOfBirth}
                    Place of Birth: ${request.placeOfBirth}

                    Return ONLY the JSON object. No other text.
                """.trimIndent()

                val response = api.chatCompletion(
                    authorization = "Bearer ${Constants.CEREBRAS_API_KEY}",
                    request       = CerebrasRequest(
                        model       = Constants.CEREBRAS_MODEL,
                        messages    = listOf(
                            ChatMessage("system", systemPrompt),
                            ChatMessage("user", userPrompt)
                        ),
                        maxTokens   = 2048,
                        temperature = 0.2
                    )
                )

                if (!response.isSuccessful) {
                    return@withContext Result.Error(apiErrorMessage(response.code()))
                }

                val content = response.body()
                    ?.choices?.firstOrNull()?.message?.content
                    ?: return@withContext Result.Error("Empty response from AI. Please try again.")

                val json = extractJson(content)
                    ?: return@withContext Result.Error("AI returned unreadable data. Please try again.")

                val result = try {
                    gson.fromJson(json, KundliResponse::class.java)
                } catch (e: JsonSyntaxException) {
                    return@withContext Result.Error("Failed to parse Kundli data. Please try again.")
                } ?: return@withContext Result.Error("Null response from parser.")

                if (result.ascendant.isBlank() && result.planets.isEmpty()) {
                    return@withContext Result.Error("Incomplete Kundli data. Please try again.")
                }

                Result.Success(result.copy(success = true))

            } catch (e: UnknownHostException) {
                Result.Error("No internet connection. Please check your network.")
            } catch (e: SocketTimeoutException) {
                Result.Error("Request timed out. The AI is busy — please try again.")
            } catch (e: SSLException) {
                Result.Error("Secure connection failed. Check your network settings.")
            } catch (e: JsonSyntaxException) {
                Result.Error("Failed to parse AI response. Please try again.")
            } catch (e: Exception) {
                Result.Error(e.localizedMessage?.take(120) ?: "Unexpected error. Please try again.")
            }
        }

    // ── AI Horoscope ──────────────────────────────────────────────────────

    suspend fun getAiHoroscope(
        name      : String,
        planets   : List<PlanetPosition>,
        ascendant : String,
        nakshatra : String = ""
    ): Result<HoroscopeResult> = withContext(Dispatchers.IO) {
        try {
            val planetSummary = planets.joinToString(", ") {
                "${it.planet} in ${it.sign} House ${it.house}${if (it.isRetro) " (Retrograde)" else ""}"
            }.ifEmpty { "Planetary positions not available" }

            val systemPrompt = """
                You are a compassionate Vedic astrologer with 30 years of experience.
                Analyze the birth chart and provide a detailed, personalized, POSITIVE horoscope reading.
                Return ONLY a valid JSON object with NO markdown or code fences:
                {
                  "personality": "2-3 sentences about personality traits",
                  "career": "2-3 sentences about career and ambitions",
                  "marriage": "2-3 sentences about relationships and love life",
                  "finance": "2-3 sentences about wealth and finances",
                  "health": "2-3 sentences about health and vitality",
                  "luckyNumbers": "3 lucky numbers e.g. 3, 7, 21",
                  "luckyColors": "3 lucky colors e.g. Gold, Purple, White"
                }
            """.trimIndent()

            val userPrompt = """
                Horoscope reading for:
                Name: $name
                Ascendant: $ascendant
                ${if (nakshatra.isNotBlank()) "Nakshatra: $nakshatra" else ""}
                Planets: $planetSummary

                Return ONLY the JSON object.
            """.trimIndent()

            val response = api.chatCompletion(
                authorization = "Bearer ${Constants.CEREBRAS_API_KEY}",
                request       = CerebrasRequest(
                    model       = Constants.CEREBRAS_MODEL,
                    messages    = listOf(
                        ChatMessage("system", systemPrompt),
                        ChatMessage("user",   userPrompt)
                    ),
                    maxTokens   = 1500,
                    temperature = 0.4
                )
            )

            if (!response.isSuccessful) {
                return@withContext Result.Error("Horoscope API error: ${response.code()}")
            }

            val content = response.body()
                ?.choices?.firstOrNull()?.message?.content
                ?: return@withContext Result.Error("Empty horoscope response.")

            val json   = extractJson(content) ?: return@withContext fallbackHoroscope()
            val result = try {
                gson.fromJson(json, HoroscopeResult::class.java) ?: fallbackHoroscope().let {
                    return@withContext it
                }
            } catch (e: JsonSyntaxException) {
                return@withContext fallbackHoroscope()
            }

            Result.Success(result)

        } catch (e: UnknownHostException) {
            fallbackHoroscope()
        } catch (e: SocketTimeoutException) {
            fallbackHoroscope()
        } catch (e: Exception) {
            fallbackHoroscope()
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /** Strips markdown fences and extracts the first valid JSON object */
    private fun extractJson(raw: String): String? {
        if (raw.isBlank()) return null
        var text  = raw.trim()
        text = text.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        val start = text.indexOf('{')
        val end   = text.lastIndexOf('}')
        return if (start >= 0 && end > start) text.substring(start, end + 1) else null
    }

    private fun apiErrorMessage(code: Int) = when (code) {
        401  -> "Invalid API key. Add your Cerebras key in Constants.kt."
        429  -> "Rate limit reached. Please wait and try again."
        503  -> "AI service temporarily unavailable. Try again shortly."
        else -> "API error $code. Please try again."
    }

    private fun fallbackHoroscope(): Result<HoroscopeResult> =
        Result.Success(
            HoroscopeResult(
                personality  = "Unable to load horoscope reading. Please check your connection and try again.",
                career       = "",
                marriage     = "",
                finance      = "",
                health       = "",
                luckyNumbers = "",
                luckyColors  = ""
            )
        )
}
