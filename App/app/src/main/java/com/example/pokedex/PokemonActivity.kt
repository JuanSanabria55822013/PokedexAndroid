package com.example.pokedex

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.pokedex.services.driverAdapters.PokemonDiverAdapter
import com.example.pokedex.services.models.Ability
import com.example.pokedex.services.models.AbilityEntry
import com.example.pokedex.services.models.Pokemon
import com.example.pokedex.services.models.Type
import com.example.pokedex.services.models.TypeEntry
import com.example.pokedex.ui.theme.PokedexTheme

class PokemonActivity : ComponentActivity() {

    val PokemonDiverAdapter by lazy { PokemonDiverAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val pokemonName = intent.getStringExtra("pokemon_name") ?: return@setContent
            val regionName = intent.getStringExtra("region_name") ?: return@setContent
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

            pokemonDetails?.let { pokemonDetails ->
                PokemonDetailScreen(
                    pokemonDetails = pokemonDetails,
                    pokemonName = pokemonName,
                    volver = { Volver(regionName) },
                    regionName = regionName
                )
            } ?: run {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        "Cargando detalles de $pokemonName...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }


    }

    private fun Volver(regionName: String) {
        val intent = Intent(this, RegionActivity::class.java).apply {
            putExtra("region_name", regionName)
        }
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonDetails: Pokemon,
    pokemonName: String,
    volver: () -> Unit,
    regionName: String
) {
    PokedexTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detalles de ${pokemonName.capitalize()}") },
                    navigationIcon = {
                        IconButton(onClick = { volver() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.medium
                            )
                    ) {
                        pokemonDetails.imgUrl.let { imageUrl ->
                            Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = "${pokemonDetails.name} sprite",
                                modifier = Modifier
                                    .size(360.dp)
                            )
                        }
                    }
                }

                // Información general
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Información Básica",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "ID: ${pokemonDetails.id}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Región: ${regionName}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Nombre: ${pokemonDetails.name.capitalize()}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Altura: ${pokemonDetails.height} decímetros",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Peso: ${pokemonDetails.weight} hectogramos",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                // Tipos y habilidades
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Tipos",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = pokemonDetails.types.joinToString { it.type.name.capitalize() },
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Divider(color = MaterialTheme.colorScheme.onTertiaryContainer)
                            Text(
                                text = "Habilidades",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = pokemonDetails.abilities.joinToString { it.ability.name.capitalize() },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
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
        abilities = listOf(
            AbilityEntry(Ability(name = "overgrow")),
            AbilityEntry(Ability(name = "chlorophyll"))
        ),
        imgUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
    )
    PokemonDetailScreen(pokemonDetails = samplePokemon, pokemonName = "bulbasaur", volver = {}, regionName = "Kanto")
}
