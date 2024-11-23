package com.example.pokedex.services.endpoints

import com.example.pokedex.services.models.TypeResponse
import retrofit2.Response
import retrofit2.http.GET

interface TypeEndpoint {
    @GET("type/")
    suspend fun getPokemonTypes(): Response<TypeResponse>
}