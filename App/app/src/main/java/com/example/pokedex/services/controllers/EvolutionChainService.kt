package com.example.pokedex.services.controllers

import androidx.lifecycle.viewModelScope
import com.example.pokedex.services.endpoints.EvolutionChainEndpoint
import com.example.pokedex.services.models.EvolutionChainResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EvolutionChainService : BaseService() {

    fun getEvolutionChainById(
        pokemonID: Int,
        success: (evolutionChain: EvolutionChainResponse) -> Unit,
        error: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Llama al endpoint configurado en EvolutionChainEndpoint
                val response = getRetrofit()
                    .create(EvolutionChainEndpoint::class.java)
                    .getEvolutionChain(pokemonID)

                val data = response.body()

                if (response.isSuccessful && data != null) {
                    success(data)
                } else {
                    println("Error en la respuesta: ${response.code()}")
                    error()
                }
            } catch (e: Exception) {
                println("Error en la conexión: ${e.message}")
                error()
            }
        }
    }
}