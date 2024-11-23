package com.example.pokedex.services.driverAdapters

import com.example.pokedex.services.controllers.EvolutionChainService
import com.example.pokedex.services.models.EvolutionChainResponse

class EvolutionChainDiverAdapter {
    private val service: EvolutionChainService = EvolutionChainService()

    fun getEvolutionChain(
        pokemonID: Int,
        loadData: (evolutionChain: EvolutionChainResponse) -> Unit,
        errorData: () -> Unit
    ) {
        this.service.getEvolutionChainById(
            pokemonID = pokemonID,
            success = { loadData(it) },
            error = { errorData() }
        )
    }
}