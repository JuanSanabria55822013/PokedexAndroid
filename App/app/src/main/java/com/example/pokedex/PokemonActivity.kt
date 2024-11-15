package com.example.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pokedex.services.driverAdapters.PokemonDiverAdapter
import com.example.pokedex.services.models.Ability
import com.example.pokedex.services.models.AbilityEntry
import com.example.pokedex.services.models.Pokemon
import com.example.pokedex.services.models.Type
import com.example.pokedex.services.models.TypeEntry
import com.example.pokedex.ui.theme.PokedexTheme

// PokemonActivity.kt
class PokemonActivity : ComponentActivity() {

    val PokemonDiverAdapter by lazy { PokemonDiverAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val pokemonName = intent.getStringExtra("pokemon_name")
            if (pokemonName.isNullOrBlank()) {
                println("Error: No se recibió el nombre del Pokémon.")
                finish() // Cierra la actividad si el dato es inválido
                return@setContent
            }
            var pokemonDetails by remember { mutableStateOf<Pokemon?>(null) }
            var loadDetails by remember { mutableStateOf(false) }

            if (!loadDetails) {
                println("Cargando detalles de $pokemonName")
                this.PokemonDiverAdapter.getPokemonDetails(
                    pokemonName = pokemonName,
                    loadData = {
                        pokemonDetails = it
                        loadDetails = true
                    },
                    errorData = {
                        println("Error al cargar los detalles del Pokémon")
                        loadDetails = true
                    }
                )
            }

            pokemonDetails?.let { pokemonDetails -> PokemonDetailScreen(pokemonDetails = pokemonDetails, pokemonName = pokemonName) }?: run {
                Text("Cargando detalles $pokemonName ...")
            }
        }
    }
}

@Composable
fun PokemonDetailScreen(
    pokemonDetails: Pokemon,
    pokemonName: String
) {
    PokedexTheme {
        Scaffold(
            topBar = {
                Text(text = "Detalles de ${pokemonName }")
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(text = "Nombre: ${pokemonDetails.name}")
                Text(text = "ID: ${pokemonDetails.id}")
                Text(text = "Altura: ${pokemonDetails.height} decímetros")
                Text(text = "Peso: ${pokemonDetails.weight} hectogramos")
                Text(
                    text = "Tipos: ${pokemonDetails.types.joinToString { it.type.name }}",
                )
                Text(
                    text = "Habilidades: ${pokemonDetails.abilities.joinToString { it.ability.name }}"
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun PokemonDetailScreenPreview() {
    val samplePokemon = Pokemon(
        id = 1,
        name = "bulbasaur",
        height = 7,
        weight = 69,
        types = listOf(TypeEntry(Type(name = "grass")), TypeEntry(Type(name = "poison"))),
        abilities = listOf(AbilityEntry(Ability(name = "overgrow")), AbilityEntry(Ability(name = "chlorophyll")))
    )
    PokemonDetailScreen(pokemonDetails = samplePokemon, pokemonName = "")
}
