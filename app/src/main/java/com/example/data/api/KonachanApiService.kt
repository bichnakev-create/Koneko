package com.example.data.api

import com.example.data.model.KonachanComment
import com.example.data.model.KonachanPost
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface KonachanApiService {
    @GET("post.json")
    suspend fun getPosts(
        @Query("limit") limit: Int = 30,
        @Query("page") page: Int = 1,
        @Query("tags") tags: String? = null
    ): List<KonachanPost>

    @GET("comment.json")
    suspend fun getComments(
        @Query("post_id") postId: Long
    ): List<KonachanComment>
}

object KonachanNetworkClient {
    private const val BASE_URL = "https://konachan.net/"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "KonaNeko/1.0 (Android; Mobile Gallery)")
                .header("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    val apiService: KonachanApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(KonachanApiService::class.java)
    }
}
