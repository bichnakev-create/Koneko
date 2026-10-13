package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KonachanPost(
    @Json(name = "id") val id: Long,
    @Json(name = "tags") val tags: String = "",
    @Json(name = "created_at") val createdAt: Long? = null,
    @Json(name = "creator_id") val creatorId: Long? = null,
    @Json(name = "author") val author: String? = null,
    @Json(name = "change") val change: Long? = null,
    @Json(name = "source") val source: String? = null,
    @Json(name = "score") val score: Int = 0,
    @Json(name = "md5") val md5: String? = null,
    @Json(name = "file_size") val fileSize: Long = 0,
    @Json(name = "file_url") val fileUrl: String? = null,
    @Json(name = "is_shown_in_index") val isShownInIndex: Boolean? = true,
    @Json(name = "preview_url") val previewUrl: String? = null,
    @Json(name = "preview_width") val previewWidth: Int? = null,
    @Json(name = "preview_height") val previewHeight: Int? = null,
    @Json(name = "actual_preview_width") val actualPreviewWidth: Int? = null,
    @Json(name = "actual_preview_height") val actualPreviewHeight: Int? = null,
    @Json(name = "sample_url") val sampleUrl: String? = null,
    @Json(name = "sample_width") val sampleWidth: Int? = null,
    @Json(name = "sample_height") val sampleHeight: Int? = null,
    @Json(name = "sample_file_size") val sampleFileSize: Long? = null,
    @Json(name = "jpeg_url") val jpegUrl: String? = null,
    @Json(name = "jpeg_width") val jpegWidth: Int? = null,
    @Json(name = "jpeg_height") val jpegHeight: Int? = null,
    @Json(name = "jpeg_file_size") val jpegFileSize: Long? = null,
    @Json(name = "rating") val rating: String? = "s",
    @Json(name = "has_children") val hasChildren: Boolean? = false,
    @Json(name = "parent_id") val parentId: Long? = null,
    @Json(name = "status") val status: String? = "active",
    @Json(name = "width") val width: Int = 1920,
    @Json(name = "height") val height: Int = 1080,
    @Json(name = "is_held") val isHeld: Boolean? = false
) {
    // Normalizes URL (Konachan sometimes uses protocol-relative //konachan.net/...)
    private fun sanitizeUrl(raw: String?): String {
        if (raw.isNullOrBlank()) return ""
        return if (raw.startsWith("//")) "https:$raw" else raw
    }

    val displayPreviewUrl: String
        get() = sanitizeUrl(previewUrl).ifEmpty { displaySampleUrl }

    val displaySampleUrl: String
        get() = sanitizeUrl(sampleUrl).ifEmpty { sanitizeUrl(jpegUrl).ifEmpty { sanitizeUrl(fileUrl) } }

    val displayFullUrl: String
        get() = sanitizeUrl(fileUrl).ifEmpty { sanitizeUrl(jpegUrl).ifEmpty { displaySampleUrl } }

    val tagList: List<String>
        get() = tags.split(" ").filter { it.isNotBlank() }

    val resolutionLabel: String
        get() = "${width}x${height}"

    val isSafe: Boolean
        get() = rating.equals("s", ignoreCase = true)
}
