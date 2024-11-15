package com.example.pokedex.services.controllers

import androidx.lifecycle.viewModelScope
import com.example.pokedex.services.endpoints.ListaPokemonEndpoint
import com.example.pokedex.services.models.PokemonEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListaPokemonServices : BaseService() {

    fun getPokemonsByRegion(
        regionName: String,
        success: (pokemonList: List<PokemonEntry>) -> Unit,
        error: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = getRetrofit()
                    .create(ListaPokemonEndpoint::class.java)
                    .getPokemonsByRegion(regionName)

                val data = response.body()
                if (response.isSuccessful && data != null) {
                    success(data.pokemon_entries)
                } else {
                    success(emptyList())
                }
            } catch (e: Exception) {
                println(e)
                error()
            }
        }
    }

}