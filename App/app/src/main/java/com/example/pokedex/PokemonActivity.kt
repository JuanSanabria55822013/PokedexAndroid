package com.example.pokedex

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplicationwebservice.R
import com.example.pokedex.services.driverAdapters.EvolutionChainDiverAdapter
import com.example.pokedex.services.driverAdapters.PokemonDiverAdapter
import com.example.pokedex.services.models.Ability
import com.example.pokedex.services.models.AbilityEntry
import com.example.pokedex.services.models.EvolutionChain
import com.example.pokedex.services.models.EvolutionSpecies
import com.example.pokedex.services.models.Pokemon
import com.example.pokedex.services.models.Type
import com.example.pokedex.services.models.TypeEntry
import com.example.pokedex.ui.theme.PokedexTheme

class PokemonActivity : ComponentActivity() {

    val PokemonDiverAdapter by lazy { PokemonDiverAdapter() }
    private val evolutionChainDiverAdapter by lazy { EvolutionChainDiverAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val pokemonName = intent.getStringExtra("pokemon_name") ?: return@setContent
            val regionName = intent.getStringExtra("region_name") ?: return@setContent
            var pokemonDetails by remember { mutableStateOf<Pokemon?>(null) }
            var evolutionChainID by remember {mutableStateOf(0)}
            var evolutionChain by remember { mutableStateOf<EvolutionChain?>(null) }
            var loadDetails by remember { mutableStateOf(false) }
            var loadChain by remember { mutableStateOf(false) }


            if (!loadDetails) {
                println("Cargando detalles de $pokemonName")
                this.PokemonDiverAdapter.getPokemonDetails(
                    pokemonName = pokemonName,
                    loadData = {
                        pokemonDetails = it
                        loadDetails = true
                        if (!loadChain){
                            this.evolutionChainDiverAdapter.getPokemonEvolution(
                                Pokemon_id = pokemonDetails!!.id,
                                loadData = {
                                    evolutionChainID = it.evolution_chain.url.split("/").dropLast(1).last().toInt()
                                    println("Evolution Chain ID ${evolutionChainID}")
                                    loadChain = true
                                    if (evolutionChainID!= null){
                                        this.evolutionChainDiverAdapter.getEvolutionChain(
                                            evolutionChainID = evolutionChainID,
                                            loadData = {
                                                evolutionChain = it
                                            },
                                            errorData = {
                                                println("No se pudo Cargar la evolution Chain")
                                            }
                                        )
                                    }
                                },
                                errorData = {
                                    println("No se pudo Cargar el ID para la Evolution Chain")
                                    loadChain = true
                                }
                            )
                        }
                    },
                    errorData = {
                        println("Error al cargar los detalles del Pokémon")
                        loadDetails = true
                    }
                )
            }

            pokemonDetails?.let { pokemonDetails ->
                evolutionChain?.let {
                    PokemonDetailScreen(
                        pokemonDetails = pokemonDetails,
                        pokemonName = pokemonName,
                        evolutionChain = it,
                        volver = { Volver(regionName) },
                        regionName = regionName,
                        onPokemonClick = { clickedPokemonName ->
                            navigateToPokemonDetails(clickedPokemonName, regionName)
                        }
                    )
                }
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

    private fun navigateToPokemonDetails(pokemonName: String, regionName: String) {
        val intent = Intent(this, PokemonActivity::class.java).apply {
            putExtra("pokemon_name", pokemonName)
            putExtra("region_name", regionName)
        }
        startActivity(intent)
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonDetails: Pokemon,
    evolutionChain: EvolutionChain,
    pokemonName: String,
    volver: () -> Unit,
    regionName: String,
    onPokemonClick: (String) -> Unit // Callback para manejar clics en Pokémon
) {
    PokedexTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detalles de ${pokemonName.capitalize()}",
                        style = MaterialTheme.typography.headlineMedium )
                         },
                    navigationIcon = {
                        IconButton(onClick = { volver() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorResource(R.color.VerdeOscuro),
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
                                colorResource(R.color.VerderSuave),
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

                // Tipos y habilidades
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.VerdeClaro)
                        )
                    ) {
                        Column (
                            modifier = Modifier.padding(16.dp).align(alignment = Alignment.CenterHorizontally),

                        ){
                            Box(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ){
                                Text(
                                    text = "Tipos",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = colorResource(R.color.black)
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) { pokemonDetails.types.forEach{ type ->
                                Box(
                                    modifier = Modifier
                                        .background(colorResource(R.color.VerdeOscuro))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .align(Alignment.CenterVertically)
                                ){
                                    Text(
                                        text = type.type.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = colorResource(R.color.white)
                                    )
                                }
                            }
                            }
                            Box(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ){
                                Text(
                                    text = "Habilidades",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = colorResource(R.color.black)
                                )
                            }
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ){ pokemonDetails.abilities.forEach { Ability ->
                                Box(
                                    modifier = Modifier
                                        .background(colorResource(R.color.VerdeOscuro))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .align(Alignment.CenterVertically)
                                ){
                                    Text(
                                        text = Ability.ability.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = colorResource(R.color.white)
                                    )
                                }
                            }
                            }
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.VerdeClaro)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Información Básica",
                                style = MaterialTheme.typography.headlineSmall,
                                color = colorResource(R.color.black)
                            )
                            Text(
                                text = "ID: ${pokemonDetails.id}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Región: ${regionName}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Nombre: ${pokemonDetails.name.capitalize()}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Altura: ${pokemonDetails.height} decímetros",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Peso: ${pokemonDetails.weight} hectogramos",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }


                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.VerdeClaro)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Estadísticas Base",
                                style = MaterialTheme.typography.headlineSmall,
                                color = colorResource(R.color.black)
                            )
                            pokemonDetails.stats.forEach { stat ->
                                Column {
                                    Text(
                                        text = "${stat.stat.name.capitalize()}: ${stat.base_stat}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    LinearProgressIndicator(
                                        progress = stat.base_stat / 100f,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp),
                                        color = colorResource(R.color.VerdePokemon),
                                        trackColor = colorResource(R.color.VerdeOscuro)
                                    )
                                }
                            }
                        }
                    }
                }
                //Evolution chain
                if (evolutionChain!= null){
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colorResource(R.color.VerderSuave)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Cadena de Evolución",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = colorResource(R.color.black)
                                )
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val speciesList = flattenEvolutionChain(evolutionChain)
                                    items(speciesList) { species ->
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .clickable { onPokemonClick(species.name) }

                                        ) {
                                            val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${species.url.split("/").dropLast(1).last()}.png"
                                            Image(
                                                painter = rememberAsyncImagePainter(imageUrl),
                                                contentDescription = "${species.name} sprite",
                                                modifier = Modifier.size(140.dp)
                                            )
                                            Text(
                                                text = species.name.capitalize(),
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else{
                   item {
                       Text("No hay cadena de pokemones")
                   }
                }
            }
        }
    }
}

fun flattenEvolutionChain(chain: EvolutionChain): List<EvolutionSpecies> {
    val speciesList = mutableListOf(chain.species)
    for (evolution in chain.evolves_to) {
        speciesList.addAll(flattenEvolutionChain(evolution))
    }
    return speciesList
}
@Preview(showBackground = true, widthDp = 360)
@Composable
fun PokemonDetailScreenPreview() {
    val samplePokemon = Pokemon(
        id = 1,
        name = "bulbasaur",
        height = 7,
        weight = 69,
        types = listOf(TypeEntry(Type(name = "grass" )), TypeEntry(Type(name = "poison"))),
        abilities = listOf(
            AbilityEntry(Ability(name = "overgrow")),
            AbilityEntry(Ability(name = "chlorophyll"))
        ),
        stats = listOf(),
        imgUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
    )
    val sampleEvotionChain = EvolutionChain(
        species = (EvolutionSpecies(name = "", url = "")),
        evolves_to = emptyList()
    )
    PokemonDetailScreen(
        pokemonDetails = samplePokemon,
        pokemonName = "bulbasaur",
        volver = {},
        regionName = "Kanto",
        evolutionChain = sampleEvotionChain,
        onPokemonClick = {}
    )
}

