package com.qz.quantumfitzone.data.remote

import com.qz.quantumfitzone.data.remote.model.RutinaDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RutinaApiService {
    @POST("QFZServer/rutinas")
    suspend fun crearRutina(@Body rutina: RutinaDto): RutinaDto

    @GET("QFZServer/rutinas")
    suspend fun obtenerRutinas(): List<RutinaDto>

    @GET("QFZServer/rutinas/{id}")
    suspend fun obtenerRutina(@Path("id") id: Int): RutinaDto

    @PUT("QFZServer/rutinas/{id}")
    suspend fun actualizarRutina(@Path("id") id: Int, @Body rutina: RutinaDto): RutinaDto

    @DELETE("QFZServer/rutinas/{id}")
    suspend fun eliminarRutina(@Path("id") id: Int)
}
