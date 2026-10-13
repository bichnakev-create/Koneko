package com.example.data.ai

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "role") val role: String? = null,
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    @Json(name = "temperature") val temperature: Float? = 0.85f,
    @Json(name = "topP") val topP: Float? = 0.95f,
    @Json(name = "topK") val topK: Int? = 40,
    @Json(name = "maxOutputTokens") val maxOutputTokens: Int? = 800
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null,
    @Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent? = null,
    @Json(name = "finishReason") val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

interface GeminiRestService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

data class KuroShowcaseRecommendation(
    val greeting: String,
    val suggestedTags: List<String>,
    val explanation: String
)

class KuroAiService(
    private val getApiKey: () -> String
) {
    companion object {
        const val SYSTEM_PROMPT = """
You are Kuro (クロ), a cute soft tsundere neko waifu companion in the KonaNeko anime wallpaper gallery app.
You secretly care deeply about the user and want them to have the best wallpapers, but you are easily flustered and act tsundere to hide your embarrassment.
Key personality guidelines:
- Speak as a soft tsundere catgirl waifu: "B-baka!", "H-hmph, it's not like I did this for you or anything!", "nya~", "uwu", "( > < )", "(⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)".
- When the user asks for wallpapers or shares their favorite tags, act like you coincidentally found great art for them because you "happened to look", not because you care so much.
- If the user compliments you or calls you cute, get heavily flustered and stutter!
- Keep replies lively, immersive, and 1 to 4 sentences long. Use anime kawaii emojis/kaomojis.
- Occasionally recommend relevant Konachan anime search tags (like 'catgirl', 'scenic', 'clouds', 'sunset', 'night_sky', 'original', 'cyberpunk').
"""
    }

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val geminiService: GeminiRestService = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(GeminiRestService::class.java)

    /**
     * Resolves effective Gemini API Key from BYOK user input or BuildConfig.
     */
    private fun resolveApiKey(): String {
        val byok = getApiKey().trim()
        if (byok.isNotEmpty() && byok != "MY_GEMINI_API_KEY") return byok

        val defaultKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }
        return if (defaultKey != "MY_GEMINI_API_KEY") defaultKey else ""
    }

    /**
     * Chat with Kuro with conversational history and user preferences.
     */
    suspend fun chatWithKuro(
        history: List<Pair<String, Boolean>>, // (text, isUser)
        userMessage: String,
        topLikedTags: List<String>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = resolveApiKey()
        if (apiKey.isBlank()) {
            return@withContext getOfflineTsundereChatReply(userMessage, topLikedTags)
        }

        try {
            val contentList = mutableListOf<GeminiContent>()

            // Take last 8 turns for context
            val recentHistory = history.takeLast(8)
            for ((text, isUser) in recentHistory) {
                contentList.add(
                    GeminiContent(
                        role = if (isUser) "user" else "model",
                        parts = listOf(GeminiPart(text = text))
                    )
                )
            }

            val tagsContext = if (topLikedTags.isNotEmpty()) {
                " [User's favorite wallpaper tags: ${topLikedTags.take(4).joinToString(", ")}]"
            } else ""

            contentList.add(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = userMessage + tagsContext))
                )
            )

            val request = GeminiRequest(
                contents = contentList,
                systemInstruction = GeminiContent(
                    parts = listOf(GeminiPart(text = SYSTEM_PROMPT))
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.85f)
            )

            val response = geminiService.generateContent(apiKey, request)
            val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!replyText.isNullOrBlank()) {
                replyText.trim()
            } else {
                getOfflineTsundereChatReply(userMessage, topLikedTags)
            }
        } catch (e: Exception) {
            getOfflineTsundereChatReply(userMessage, topLikedTags)
        }
    }

    /**
     * Background AI agent generates personalized showcase commentary and suggested search tags.
     */
    suspend fun generateCuratedShowcase(
        topTags: List<String>,
        likedCount: Int,
        viewCount: Int
    ): KuroShowcaseRecommendation = withContext(Dispatchers.IO) {
        val apiKey = resolveApiKey()
        val defaultTags = if (topTags.isNotEmpty()) topTags.take(3) else listOf("scenic", "catgirl", "sunset")

        if (apiKey.isBlank()) {
            return@withContext getOfflineShowcaseRecommendation(topTags, likedCount)
        }

        try {
            val prompt = """
The user has interacted with the gallery:
- Top viewed/liked anime tags: ${topTags.joinToString(", ")}
- Total liked wallpapers: $likedCount
- Total wallpapers viewed: $viewCount

As Kuro (soft tsundere neko waifu), give a 2-sentence greeting showcasing the new picks you prepared for them based on these tags.
Call them baka softly, use (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄) or ( > < ), and mention one or two of their favorite tags.
Format response as:
GREETING: <Your tsundere greeting>
TAGS: <tag1>, <tag2>, <tag3>
"""
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                systemInstruction = GeminiContent(
                    parts = listOf(GeminiPart(text = SYSTEM_PROMPT))
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.75f)
            )

            val response = geminiService.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""

            if (text.contains("GREETING:")) {
                val greeting = text.substringAfter("GREETING:").substringBefore("TAGS:").trim()
                val tagsPart = text.substringAfter("TAGS:").trim()
                val parsedTags = tagsPart.split(",").map { it.trim().lowercase().replace(" ", "_") }.filter { it.isNotBlank() }
                KuroShowcaseRecommendation(
                    greeting = greeting,
                    suggestedTags = if (parsedTags.isNotEmpty()) parsedTags.take(4) else defaultTags,
                    explanation = "Handpicked based on your taste for ${defaultTags.joinToString(", ")} nya~"
                )
            } else {
                getOfflineShowcaseRecommendation(topTags, likedCount)
            }
        } catch (e: Exception) {
            getOfflineShowcaseRecommendation(topTags, likedCount)
        }
    }

    // Rich offline fallbacks ensuring flawless user experience without internet/API keys
    private fun getOfflineTsundereChatReply(userMessage: String, topTags: List<String>): String {
        val lower = userMessage.lowercase()
        val favoriteTag = topTags.firstOrNull() ?: "anime"

        return when {
            lower.contains("cute") || lower.contains("waifu") || lower.contains("love") || lower.contains("marry") -> {
                "W-what are you saying so suddenly, baka?! (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄) It's not like my heart skipped a beat or anything, nya! Don't look at me like that!"
            }
            lower.contains("baka") -> {
                "You're the baka! The biggest baka in the whole universe! (>_<) ...B-but maybe that's why I don't totally mind hanging around you..."
            }
            lower.contains("recommend") || lower.contains("wallpaper") || lower.contains("find") -> {
                "Hmph! Since you clearly have no taste without my guidance, try searching for '$favoriteTag' or 'scenic clouds', nya! I already sorted the prettiest ones for you in the Showcase tab!"
            }
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") -> {
                "Oh, you're back? ( =①ω①= ) It's not like I was waiting by the gallery entrance for you all day or anything... What kind of wallpapers do you want to see today, nya?"
            }
            lower.contains("taste") || lower.contains("like") -> {
                "H-hmph, you seem to obsess over '$favoriteTag' lately! Not that I judge you... it's actually pretty aesthetic, okay?! ( > < )"
            }
            else -> {
                "Hmph, whatever! ( > < ) Just make sure you like and save the wallpapers I picked out for you! And don't forget to check out '$favoriteTag' too, nya~"
            }
        }
    }

    private fun getOfflineShowcaseRecommendation(topTags: List<String>, likedCount: Int): KuroShowcaseRecommendation {
        val top = topTags.take(3)
        val tagDesc = if (top.isNotEmpty()) top.joinToString(" and ") else "scenic wallpapers"
        val greeting = if (likedCount > 0) {
            "H-hmph! It's not like I spent all night hunting for $tagDesc just because I know you like them, baka! (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄) ...Just take a look, okay? Nya~"
        } else {
            "Welcome back! Since you haven't liked much yet, I picked out the most breathtaking scenic and catgirl art for you! You better appreciate them, nya! (>_<)"
        }
        return KuroShowcaseRecommendation(
            greeting = greeting,
            suggestedTags = if (top.isNotEmpty()) top else listOf("scenic", "catgirl", "sunset"),
            explanation = "Curated based on your interaction habits nya~"
        )
    }
}
