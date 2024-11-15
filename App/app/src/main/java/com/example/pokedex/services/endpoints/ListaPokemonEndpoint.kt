package com.example.pokedex.services.endpoints

import com.example.pokedex.services.models.PokedexResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ListaPokemonEndpoint {
    @GET("pokedex/{region_name}")
    suspend fun getPokemonsByRegion(@Path("region_name") regionName: String): Response<PokedexResponse>
}