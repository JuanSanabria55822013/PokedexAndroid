package com.example.pokedex.services.models

data class Pokemon(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<TypeEntry>,
    val abilities: List<AbilityEntry>
)
