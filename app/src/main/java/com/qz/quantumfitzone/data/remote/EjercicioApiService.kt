package com.qz.quantumfitzone.data.remote

import com.qz.quantumfitzone.data.remote.model.EjercicioDto
import com.qz.quantumfitzone.data.remote.model.SerieDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EjercicioApiService {
    @POST("QFZServer/ejercicios")
    suspend fun crearEjercicio(@Body ejercicio: EjercicioDto): EjercicioDto

    @GET("QFZServer/ejercicios")
    suspend fun obtenerEjercicios(): List<EjercicioDto>

    @GET("QFZServer/ejercicios/{id}")
    suspend fun obtenerEjercicio(@Path("id") id: Int): EjercicioDto

    @GET("QFZServer/ejercicios/rutina/{idRutina}")
    suspend fun obtenerEjerciciosPorRutina(@Path("idRutina") idRutina: Int): List<EjercicioDto>

    @GET("QFZServer/ejercicios/{id}/series")
    suspend fun obtenerSeriesDeEjercicio(@Path("id") id: Int): List<SerieDto>

    @PUT("QFZServer/ejercicios/{id}")
    suspend fun actualizarEjercicio(
        @Path("id") id: Int,
        @Body ejercicio: EjercicioDto
    ): EjercicioDto

    @DELETE("QFZServer/ejercicios/{id}")
    suspend fun eliminarEjercicio(@Path("id") id: Int)
}
