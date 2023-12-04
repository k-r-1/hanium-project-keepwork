package com.example.a23_hf069

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded

interface ApiService {
    @FormUrlEncoded
    @POST("android_email_certify_send.php") // 실제 서버 엔드포인트로 변경
    fun requestEmailVerification(
        @Field("email") email: String
    ): Call<ApiResponse>
}

data class ApiResponse(
    val success: Boolean,
    val message: String
)

