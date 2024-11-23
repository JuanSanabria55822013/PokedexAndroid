package com.example.pokedex.services.models

data class EvolutionChain(
    val species: EvolutionSpecies,
    val evolves_to: List<EvolutionChain>
)
