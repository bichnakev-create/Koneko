package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KonachanComment(
    @Json(name = "id") val id: Long,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "post_id") val postId: Long,
    @Json(name = "creator") val creator: String? = null,
    @Json(name = "creator_id") val creatorId: Long? = null,
    @Json(name = "body") val body: String
)
