package com.qz.quantumfitzone.data.remote

import com.qz.quantumfitzone.data.model.PersonaEntity
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface PersonaApiService {
    @POST("QFZServer/personas")
    suspend fun registrar(@Body persona: PersonaEntity): PersonaEntity

    @GET("QFZServer/personas")
    suspend fun obtenerPersonas(): List<PersonaEntity>

    @GET("QFZServer/personas/{correo}")
    suspend fun obtenerPersona(@Path("correo") correo: String): PersonaEntity

    @PUT("QFZServer/personas/{correo}")
    suspend fun actualizarPersona(
        @Path("correo") correo: String,
        @Body persona: PersonaEntity
    ): PersonaEntity

    @DELETE("QFZServer/personas/{correo}")
    suspend fun eliminarPersona(@Path("correo") correo: String)
}
