package com.qz.quantumfitzone.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "http://192.168.100.15:8080/"

    fun createAuthApi(sessionManager: SessionManager): AuthApiService =
        createService(sessionManager, AuthApiService::class.java)

    fun createPersonaApi(sessionManager: SessionManager): PersonaApiService =
        createService(sessionManager, PersonaApiService::class.java)

    fun createMaquinaApi(sessionManager: SessionManager): MaquinaApiService =
        createService(sessionManager, MaquinaApiService::class.java)

    fun createExerciseCatalogApi(sessionManager: SessionManager): ExerciseCatalogApiService =
        createService(sessionManager, ExerciseCatalogApiService::class.java)

    fun createEjercicioApi(sessionManager: SessionManager): EjercicioApiService =
        createService(sessionManager, EjercicioApiService::class.java)

    fun createSerieApi(sessionManager: SessionManager): SerieApiService =
        createService(sessionManager, SerieApiService::class.java)

    fun createRutinaApi(sessionManager: SessionManager): RutinaApiService =
        createService(sessionManager, RutinaApiService::class.java)

    fun createRutinaEjercicioApi(sessionManager: SessionManager): RutinaEjercicioApiService =
        createService(sessionManager, RutinaEjercicioApiService::class.java)

    fun createHistorialEntrenamientoApi(sessionManager: SessionManager): HistorialEntrenamientoApiService =
        createService(sessionManager, HistorialEntrenamientoApiService::class.java)

    fun createHistorialEjercicioApi(sessionManager: SessionManager): HistorialEjercicioApiService =
        createService(sessionManager, HistorialEjercicioApiService::class.java)

    private fun <T> createService(
        sessionManager: SessionManager,
        serviceClass: Class<T>
    ): T {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(serviceClass)
    }
}
