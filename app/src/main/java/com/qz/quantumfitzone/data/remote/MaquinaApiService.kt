package com.qz.quantumfitzone.data.remote

import com.qz.quantumfitzone.data.remote.model.MaquinaDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MaquinaApiService {
    @POST("QFZServer/maquinas")
    suspend fun crearMaquina(@Body maquina: MaquinaDto): MaquinaDto

    @GET("QFZServer/maquinas")
    suspend fun obtenerMaquinas(): List<MaquinaDto>

    @GET("QFZServer/maquinas/{id}")
    suspend fun obtenerMaquina(@Path("id") id: Int): MaquinaDto

    @PUT("QFZServer/maquinas/{id}")
    suspend fun actualizarMaquina(@Path("id") id: Int, @Body maquina: MaquinaDto): MaquinaDto

    @DELETE("QFZServer/maquinas/{id}")
    suspend fun eliminarMaquina(@Path("id") id: Int)
}
