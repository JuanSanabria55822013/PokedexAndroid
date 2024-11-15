package com.example.pokedex

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplicationwebservice.R
import com.example.pokedex.services.driverAdapters.ListaPokemonDiverAdapter
import com.example.pokedex.services.models.PokemonEntry
import com.example.pokedex.ui.theme.PokedexTheme


class RegionActivity : ComponentActivity() {

    val ListaPokemonDiverAdapter by lazy { ListaPokemonDiverAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            val regionName = intent.getStringExtra("region_name") ?: return@setContent
            var pokemonList by remember { mutableStateOf<List<PokemonEntry>>(emptyList()) }
            var LoadListaPokemons by remember { mutableStateOf<Boolean>(false) }
            if (!LoadListaPokemons) {
                this.ListaPokemonDiverAdapter.allPokemonsByRegion(
                    regionName = regionName,
                    loadData = {
                       pokemonList = it
                       LoadListaPokemons = true
                    },
                    errorData = {
                        println("Error en el servicio")
                        LoadListaPokemons = true
                    }
                )
            }
        PokemonListScreen(pokemonList = pokemonList,
            regionName = regionName,
            onClickPokemon = { pokemonName -> goToDetallePokemon(pokemonName) }
        )
    }
}
    private fun goToDetallePokemon(pokemonName: String) {
        val intent = Intent(this, PokemonActivity::class.java).apply {
            putExtra("pokemon_name", pokemonName)
        }
        startActivity(intent)
    }
}

@Composable
fun PokemonListScreen(
    pokemonList: List<PokemonEntry>,
    regionName: String,
    onClickPokemon: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val filteredPokemonList = pokemonList.filter {
        it.pokemon_species.name.contains(searchText, ignoreCase = true)
    }

    PokedexTheme {
        Scaffold(
            topBar = {
                Column {
                    Text(text = "Región: $regionName", modifier = Modifier.padding(8.dp))
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Buscar Pokémon") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                if (filteredPokemonList.isEmpty()) {
                    Text(
                        text = "No se encontraron Pokémon",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn {
                        items(
                            items = filteredPokemonList,
                            key = { it.entry_number }
                        ) { pokemonEntry ->
                            PokemonItem(
                                pokemonEntry = pokemonEntry,
                                onClickPokemon = onClickPokemon
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonItem(
    pokemonEntry: PokemonEntry,
    onClickPokemon: (String) -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = pokemonEntry.pokemon_species.name)
            Button(onClick = { onClickPokemon(pokemonEntry.pokemon_species.name) }) {
                Text(text = stringResource(id = R.string.go_to_Pokemon))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun PokemonListScreenPreview() {
    PokemonListScreen(
        pokemonList = emptyList(),
        regionName = "kanto",
        onClickPokemon = {}
    )
}
