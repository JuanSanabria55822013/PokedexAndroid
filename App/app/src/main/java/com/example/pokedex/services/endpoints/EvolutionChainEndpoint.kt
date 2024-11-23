package com.example.pokedex.services.endpoints

import com.example.pokedex.services.models.EvolutionChainResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EvolutionChainEndpoint {
    @GET("evolution-chain/{Pokemon_id}/")
    suspend fun getEvolutionChain(@Path("Pokemon_id") pokemonID: Int): Response<EvolutionChainResponse>
}