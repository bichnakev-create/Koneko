package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liked_posts")
data class LikedPostEntity(
    @PrimaryKey val id: Long,
    val tags: String,
    val author: String,
    val previewUrl: String,
    val sampleUrl: String,
    val fileUrl: String,
    val width: Int,
    val height: Int,
    val score: Int,
    val likedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "local_comments")
data class LocalCommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val author: String,
    val body: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tag_stats")
data class TagStatEntity(
    @PrimaryKey val tag: String,
    val viewsCount: Int = 0,
    val likesCount: Int = 0,
    val score: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
