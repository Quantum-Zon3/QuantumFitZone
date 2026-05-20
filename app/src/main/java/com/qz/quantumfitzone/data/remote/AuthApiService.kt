package com.qz.quantumfitzone.data.remote

import com.qz.quantumfitzone.data.remote.model.AuthResponse
import com.qz.quantumfitzone.data.remote.model.LoginBody
import com.qz.quantumfitzone.data.remote.model.RefreshTokenRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("QFZServer/login")
    suspend fun login(@Body body: LoginBody): AuthResponse

    @POST("QFZServer/refresh-token")
    suspend fun refreshToken(@Body body: RefreshTokenRequest): AuthResponse

    @POST("QFZServer/logout")
    suspend fun logout(): ResponseBody
}
