package com.qz.quantumfitzone.data.remote

import com.qz.quantumfitzone.data.remote.model.SerieDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SerieApiService {
    @POST("QFZServer/series")
    suspend fun crearSerie(@Body serie: SerieDto): SerieDto

    @GET("QFZServer/series")
    suspend fun obtenerSeries(): List<SerieDto>

    @GET("QFZServer/series/{id}")
    suspend fun obtenerSerie(@Path("id") id: Int): SerieDto

    @GET("QFZServer/series/ejercicio/{idEjercicio}")
    suspend fun obtenerSeriesPorEjercicio(@Path("idEjercicio") idEjercicio: Int): List<SerieDto>

    @PUT("QFZServer/series/{id}")
    suspend fun actualizarSerie(@Path("id") id: Int, @Body serie: SerieDto): SerieDto

    @DELETE("QFZServer/series/{id}")
    suspend fun eliminarSerie(@Path("id") id: Int)
}
