package com.example.pokedex

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplicationwebservice.R
import com.example.pokedex.services.driverAdapters.ListaPokemonDiverAdapter
import com.example.pokedex.services.models.Pokemon
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
            onClickPokemon = { pokemonName -> goToDetallePokemon(pokemonName) },
            volver = {Volver()}
        )
    }
}
    private fun goToDetallePokemon(pokemonName: String) {
        val intent = Intent(this, PokemonActivity::class.java).apply {
            putExtra("pokemon_name", pokemonName)
        }
        startActivity(intent)
    }
    private fun Volver(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    pokemonList: List<PokemonEntry>,
    regionName: String,
    onClickPokemon: (String) -> Unit,
    volver: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val filteredPokemonList = pokemonList.filter {
        it.pokemon_species.name.contains(searchText, ignoreCase = true)
    }

    PokedexTheme {
        Scaffold(
            topBar = {
                Column {
                    SmallTopAppBar(
                        title = { Text(text = "Región: ${regionName.replaceFirstChar { it.uppercase() }}") },
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Buscar Pokémon") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                        }
                    )
                    Button(
                        onClick = {volver()},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Volver a Regiones")
                    }
                }
            }
        ) { innerPadding ->
            if (filteredPokemonList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron Pokémon",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    items(
                        items = filteredPokemonList,
                        key = { it.entry_number }
                    ) { pokemonEntry ->
                        PokemonItem(
                            pokemonEntry = pokemonEntry,
                            onClickPokemon = onClickPokemon,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonItem(
    pokemonEntry: PokemonEntry,
    onClickPokemon: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { onClickPokemon(pokemonEntry.pokemon_species.name) },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = rememberAsyncImagePainter(pokemonEntry.image_url),
                    contentDescription = "${pokemonEntry.pokemon_species.name} sprite",
                    modifier = Modifier.size(64.dp)
                )

                Text(
                    text = pokemonEntry.pokemon_species.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Ir a detalles",
                    tint = MaterialTheme.colorScheme.primary
                )
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
        onClickPokemon = {},
        volver = {}

    )
}
