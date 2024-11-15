package com.example.pokedex

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.myapplicationwebservice.R
import com.example.pokedex.services.driverAdapters.ListaPokemonDiverAdapter
import com.example.pokedex.services.driverAdapters.PokemonDiverAdapter
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
        PokemonListScreen(pokemonList = pokemonList, regionName = regionName,   onClickPokemon = { pokemonName -> goToDetallePokemon(pokemonName) }
        )
    }
}
    private fun goToDetallePokemon(pokemonName: String) {
        val intent = Intent(this, PokemonActivity::class.java)
        intent.putExtra("pokemon_name", pokemonName)
        startActivity(intent)
    }
}

@Composable
fun PokemonListScreen(
    pokemonList: List<PokemonEntry>,
    regionName: String,
    onClickPokemon: (String) -> Unit
) {
    PokedexTheme {
        Scaffold(
            topBar = {
                Text(text = "Región: $regionName")
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                LazyColumn {
                    items(
                        items = pokemonList,
                        key = { it.entry_number }
                    ) { pokemonEntry ->
                        Column {
                            Row {
                                Text(text = stringResource(id = R.string.namePokemon))
                                Text(text = pokemonEntry.pokemon_species.name)
                            }
                            Button(onClick = { onClickPokemon(pokemonEntry.pokemon_species.name) }) {
                                Text(text = stringResource(id = R.string.go_to_Pokemon))
                            }
                        }
                    }
                }
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
