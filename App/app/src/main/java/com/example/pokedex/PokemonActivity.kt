package com.example.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplicationwebservice.R
import com.example.pokedex.services.driverAdapters.ListaPokemonDiverAdapter
import com.example.pokedex.services.driverAdapters.PokemonDiverAdapter
import com.example.pokedex.services.models.Ability
import com.example.pokedex.services.models.Pokemon
import com.example.pokedex.services.models.Type
import com.example.pokedex.ui.theme.PokedexTheme

// PokemonActivity.kt
class PokemonActivity : ComponentActivity() {

    private val PokemonDiverAdapter by lazy { PokemonDiverAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val pokemonName = intent.getStringExtra("pokemon_name") ?: return@setContent
            var pokemonDetails by remember { mutableStateOf<Pokemon?>(null) }
            var loadDetails by remember { mutableStateOf(false) }

            if (!loadDetails) {
                PokemonDiverAdapter.getPokemonDetails(
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

            pokemonDetails?.let { details ->
                PokemonDetailScreen(details)
            } ?: run {
                Text("Cargando detalles de $pokemonName...")
            }
        }
    }
}

@Composable
fun PokemonDetailScreen(details: Pokemon) {
    PokedexTheme {
        Scaffold(
            topBar = {
                Text(text = "Detalles de ${details.name}")
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(text = "Nombre: ${details.name}")
                Text(text = "ID: ${details.id}")
                Text(text = "Altura: ${details.height} decímetros")
                Text(text = "Peso: ${details.weight} hectogramos")
                Text(
                    text = "Tipos: ${details.types.joinToString { it.type.name }}",
                )
                Text(
                    text = "Habilidades: ${details.abilities.joinToString { it.ability.name }}"
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
        name = "",
        height = 0,
        weight = 0,
        types = emptyList(),
        abilities = emptyList()
    )
    PokemonDetailScreen(details = samplePokemon)
}
