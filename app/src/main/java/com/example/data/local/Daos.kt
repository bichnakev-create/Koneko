package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LikedPostDao {
    @Query("SELECT * FROM liked_posts ORDER BY likedAt DESC")
    fun getAllLikedPosts(): Flow<List<LikedPostEntity>>

    @Query("SELECT * FROM liked_posts ORDER BY likedAt DESC")
    suspend fun getAllLikedPostsList(): List<LikedPostEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM liked_posts WHERE id = :postId)")
    fun isPostLikedFlow(postId: Long): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM liked_posts WHERE id = :postId)")
    suspend fun isPostLiked(postId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedPost(post: LikedPostEntity)

    @Query("DELETE FROM liked_posts WHERE id = :postId")
    suspend fun deleteLikedPost(postId: Long)

    @Query("SELECT COUNT(*) FROM liked_posts")
    fun getLikedCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM liked_posts")
    suspend fun getLikedCount(): Int
}

@Dao
interface LocalCommentDao {
    @Query("SELECT * FROM local_comments WHERE postId = :postId ORDER BY createdAt DESC")
    fun getCommentsForPost(postId: Long): Flow<List<LocalCommentEntity>>

    @Query("SELECT * FROM local_comments WHERE postId = :postId ORDER BY createdAt DESC")
    suspend fun getCommentsForPostList(postId: Long): List<LocalCommentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: LocalCommentEntity)
}

@Dao
interface TagStatDao {
    @Query("SELECT * FROM tag_stats ORDER BY score DESC, viewsCount DESC LIMIT :limit")
    suspend fun getTopTags(limit: Int = 10): List<TagStatEntity>

    @Query("SELECT * FROM tag_stats ORDER BY score DESC, viewsCount DESC LIMIT :limit")
    fun getTopTagsFlow(limit: Int = 10): Flow<List<TagStatEntity>>

    @Query("SELECT * FROM tag_stats WHERE tag = :tag")
    suspend fun getTag(tag: String): TagStatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTag(tagStat: TagStatEntity)

    @Query("SELECT SUM(viewsCount) FROM tag_stats")
    suspend fun getTotalViews(): Int?
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    suspend fun getAllMessagesList(): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAll()
}
