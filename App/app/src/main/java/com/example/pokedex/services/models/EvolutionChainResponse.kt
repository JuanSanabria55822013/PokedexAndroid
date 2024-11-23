package com.example.pokedex.services.models

data class EvolutionChainResponse(
    val id: Int,
    val baby_trigger_item: Any?, // Puede ser un objeto o nulo
    val chain: EvolutionChain
)
