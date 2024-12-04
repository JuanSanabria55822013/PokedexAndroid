package com.example.pokedex.dataBases.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "pokemons")
data class PokemonEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "ident") val ident: Int, // ID único del Pokémon
    @ColumnInfo(name = "name") val name: String, // Nombre del Pokémon
    @ColumnInfo(name = "height") val height: Int, // Altura del Pokémon
    @ColumnInfo(name = "weight") val weight: Int, // Peso del Pokémon
    @ColumnInfo(name = "imgUrl") val imgUrl: String, // URL de la imagen del Pokémon
    @ColumnInfo(name = "types") val types: String, // Tipos del Pokémon (almacenados como JSON o concatenados)
    @ColumnInfo(name = "abilities") val abilities: String, // Habilidades (almacenadas como JSON o concatenadas)
    @ColumnInfo(name = "stats") val stats: String
) : Serializable

