package com.example.data.repository

import com.example.data.api.KonachanNetworkClient
import com.example.data.local.AppDatabase
import com.example.data.local.LikedPostEntity
import com.example.data.local.LocalCommentEntity
import com.example.data.local.TagStatEntity
import com.example.data.model.KonachanComment
import com.example.data.model.KonachanPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class DisplayComment(
    val id: Long,
    val author: String,
    val text: String,
    val isLocal: Boolean,
    val timestampFormatted: String
)

class WallpaperRepository(
    private val database: AppDatabase
) {
    private val api = KonachanNetworkClient.apiService
    private val likedDao = database.likedPostDao()
    private val commentDao = database.localCommentDao()
    private val tagDao = database.tagStatDao()

    /**
     * Fetch wallpapers from Konachan API.
     */
    suspend fun getWallpapers(
        page: Int = 1,
        limit: Int = 24,
        tags: String? = null,
        safeOnly: Boolean = true
    ): Result<List<KonachanPost>> = withContext(Dispatchers.IO) {
        try {
            val queryTags = buildString {
                if (safeOnly) append("rating:s ")
                if (!tags.isNullOrBlank()) append(tags.trim())
            }.trim().ifEmpty { null }

            val posts = api.getPosts(limit = limit, page = page, tags = queryTags)
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Background AI agent interaction: record user viewing wallpaper and increment tag weights.
     */
    suspend fun recordPostView(post: KonachanPost) = withContext(Dispatchers.IO) {
        val tagList = post.tagList
        for (tag in tagList.take(6)) {
            val existing = tagDao.getTag(tag)
            val updated = if (existing != null) {
                existing.copy(
                    viewsCount = existing.viewsCount + 1,
                    score = existing.score + 1,
                    lastUpdated = System.currentTimeMillis()
                )
            } else {
                TagStatEntity(
                    tag = tag,
                    viewsCount = 1,
                    likesCount = 0,
                    score = 1,
                    lastUpdated = System.currentTimeMillis()
                )
            }
            tagDao.insertOrUpdateTag(updated)
        }
    }

    /**
     * Toggle like for a wallpaper.
     */
    suspend fun toggleLike(post: KonachanPost): Boolean = withContext(Dispatchers.IO) {
        val isLiked = likedDao.isPostLiked(post.id)
        if (isLiked) {
            likedDao.deleteLikedPost(post.id)
            // Decrease weight slightly
            for (tag in post.tagList.take(6)) {
                val existing = tagDao.getTag(tag)
                if (existing != null) {
                    val newLikes = (existing.likesCount - 1).coerceAtLeast(0)
                    val newScore = (existing.score - 4).coerceAtLeast(0)
                    tagDao.insertOrUpdateTag(existing.copy(likesCount = newLikes, score = newScore))
                }
            }
            false
        } else {
            val entity = LikedPostEntity(
                id = post.id,
                tags = post.tags,
                author = post.author ?: "Unknown Artist",
                previewUrl = post.displayPreviewUrl,
                sampleUrl = post.displaySampleUrl,
                fileUrl = post.displayFullUrl,
                width = post.width,
                height = post.height,
                score = post.score
            )
            likedDao.insertLikedPost(entity)
            // Substantially increase weights for user likes!
            for (tag in post.tagList.take(6)) {
                val existing = tagDao.getTag(tag)
                val updated = if (existing != null) {
                    existing.copy(
                        likesCount = existing.likesCount + 1,
                        score = existing.score + 5,
                        lastUpdated = System.currentTimeMillis()
                    )
                } else {
                    TagStatEntity(
                        tag = tag,
                        viewsCount = 1,
                        likesCount = 1,
                        score = 5,
                        lastUpdated = System.currentTimeMillis()
                    )
                }
                tagDao.insertOrUpdateTag(updated)
            }
            true
        }
    }

    suspend fun isPostLiked(postId: Long): Boolean = withContext(Dispatchers.IO) {
        likedDao.isPostLiked(postId)
    }

    fun isPostLikedFlow(postId: Long): Flow<Boolean> = likedDao.isPostLikedFlow(postId)

    fun getAllLikedPosts(): Flow<List<LikedPostEntity>> = likedDao.getAllLikedPosts()

    fun getLikedCountFlow(): Flow<Int> = likedDao.getLikedCountFlow()

    suspend fun getTopUserTags(limit: Int = 5): List<String> = withContext(Dispatchers.IO) {
        tagDao.getTopTags(limit).map { it.tag }
    }

    suspend fun getTotalViews(): Int = withContext(Dispatchers.IO) {
        tagDao.getTotalViews() ?: 0
    }

    /**
     * Combined comments: remote Konachan comments + local user comments
     */
    suspend fun getCommentsForPost(postId: Long): List<DisplayComment> = withContext(Dispatchers.IO) {
        val resultList = mutableListOf<DisplayComment>()

        // 1. Fetch remote comments
        try {
            val remoteComments: List<KonachanComment> = api.getComments(postId)
            for (rc in remoteComments) {
                resultList.add(
                    DisplayComment(
                        id = rc.id,
                        author = rc.creator ?: "Konachan User",
                        text = rc.body,
                        isLocal = false,
                        timestampFormatted = rc.createdAt?.take(10) ?: "Recent"
                    )
                )
            }
        } catch (_: Exception) {
            // Ignore if remote comment fetch fails
        }

        // 2. Fetch local user comments
        val localComments = commentDao.getCommentsForPostList(postId)
        for (lc in localComments) {
            resultList.add(
                DisplayComment(
                    id = lc.id + 1000000L,
                    author = lc.author,
                    text = lc.body,
                    isLocal = true,
                    timestampFormatted = "Just now"
                )
            )
        }

        resultList
    }

    suspend fun addComment(postId: Long, author: String, text: String): DisplayComment = withContext(Dispatchers.IO) {
        val entity = LocalCommentEntity(
            postId = postId,
            author = author,
            body = text
        )
        commentDao.insertComment(entity)
        DisplayComment(
            id = System.currentTimeMillis(),
            author = author,
            text = text,
            isLocal = true,
            timestampFormatted = "Just now"
        )
    }
}
