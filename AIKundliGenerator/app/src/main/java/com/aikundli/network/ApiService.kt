package com.aikundli.network

import com.aikundli.model.CerebrasRequest
import com.aikundli.model.CerebrasResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CerebrasApiService {
    @POST("v1/chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: CerebrasRequest
    ): Response<CerebrasResponse>
}
