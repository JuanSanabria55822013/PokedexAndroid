package com.example.pokedex

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.pokedex.services.driverAdapters.PokemonDiverAdapter
import com.example.pokedex.dataBases.Entities.PokemonEntity
import com.example.pokedex.dataBases.viewsModels.PokemonFavoritos
import com.example.pokedex.services.controllers.TypeService
import com.example.pokedex.services.driverAdapters.ListaPokemonDiverAdapter
import com.example.pokedex.services.driverAdapters.TypeAdapter
import com.example.pokedex.services.models.PokemonEntry
import com.example.pokedex.services.models.PokemonSpecies
import com.example.pokedex.services.models.TypeEntry
import com.example.pokedex.services.models.TypeFiltro
import com.example.pokedex.ui.theme.PokedexTheme


class RegionActivity : ComponentActivity() {

    val ListaPokemonDiverAdapter by lazy { ListaPokemonDiverAdapter() }
    val PokemonDiverAdapter by lazy { PokemonDiverAdapter() }
    val pokemonViewModel by lazy { PokemonFavoritos(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
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



            PokemonListScreen(
                pokemonList = pokemonList,
                regionName = regionName,
                onClickPokemon = { pokemonName -> goToDetallePokemon(pokemonName, regionName) },
                volver = { Volver() },
                ClickFavorito = { pokemonName -> GuardarFavorito(pokemonName) },
            )
        }
    }

    private fun goToDetallePokemon(pokemonName: String, regionName: String) {
        val intent = Intent(this, PokemonActivity::class.java).apply {
            putExtra("pokemon_name", pokemonName)
        }.apply { putExtra("region_name", regionName) }
        startActivity(intent)
    }

    private fun Volver() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    private fun GuardarFavorito(pokemonName: String) {
        PokemonDiverAdapter.getPokemonDetails(
            pokemonName = pokemonName,
            loadData = { pokemon ->
                if (pokemon != null) {
                    pokemonViewModel.savePokemonFavorito(
                        PokemonEntity(
                            id = 0, // Cambia esto según corresponda
                            ident = pokemon.id,
                            name = pokemonName,
                            height = pokemon.height,
                            weight = pokemon.weight,
                            imgUrl = pokemon.imgUrl
                        )
                    )
                } else {
                    println("No se encontró el Pokémon")
                }
            },
            errorData = {
                println("Error al cargar los detalles del Pokémon")
            }
        )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    pokemonList: List<PokemonEntry>,
    regionName: String,
    onClickPokemon: (String) -> Unit,
    ClickFavorito: (String) -> Unit,
    volver: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val filteredPokemonList = pokemonList.filter {
        it.pokemon_species.name.contains(searchText, ignoreCase = true)
    }



    var typeList by remember { mutableStateOf<List<TypeFiltro>>(emptyList()) }

    LaunchedEffect(Unit) {
        val typeAdapter = TypeAdapter()
        typeAdapter.getAllTypes(
            loadData = { typeList = it },
            errorData = { println("Error al cargar los tipos de Pokémon") }
        )
    }

    var showModal by remember { mutableStateOf(false) }
    var selectedPokemonName by remember { mutableStateOf("") }

    PokedexTheme {
        Scaffold(
            topBar = {
                Column {
                    SmallTopAppBar(
                        title = { Text(text = "Región: ${regionName.replaceFirstChar { it.uppercase() }}") },
                        navigationIcon = {
                            IconButton(onClick = { volver() }) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        },
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
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )  {
                    PokemonTypeDropdown(
                        typeList = typeList,
                        onTypeSelected = { selectedType ->
                            println("Tipo seleccionado: ${selectedType.name}")
                        }
                    )
                Spacer(modifier = Modifier.height(8.dp)) // Espaciado reducido
                if (filteredPokemonList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
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
                            .padding(horizontal = 16.dp)
                    ) {

                        items(
                            items = filteredPokemonList,
                            key = { it.entry_number }
                        ) { pokemonEntry ->
                            PokemonItem(
                                pokemonEntry = pokemonEntry,
                                onClickPokemon = onClickPokemon,
                                ClickFavorito = {
                                        pokemonName ->
                                    ClickFavorito(pokemonName)
                                    selectedPokemonName = pokemonName // Actualiza el nombre
                                    showModal = true // Muestra el modal
                                },
                            )
                        }
                    }
                }
            }
        }

        if (showModal) {
            AlertDialog(
                onDismissRequest = { showModal = false },
                confirmButton = {
                    Button(onClick = { showModal = false }) {
                        Text("Aceptar")
                    }
                },
                title = { Text("Pokémon agregado") },
                text = { Text("$selectedPokemonName ha sido agregado a favoritos.") }
            )
        }
    }
}




@Composable
fun PokemonItem(
    pokemonEntry: PokemonEntry,
    onClickPokemon: (String) -> Unit,
    ClickFavorito: (String) -> Unit,
) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().clickable(onClick = { onClickPokemon(pokemonEntry.pokemon_species.name) })
            ) {
                Image(
                    painter = rememberAsyncImagePainter(pokemonEntry.image_url),
                    contentDescription = "${pokemonEntry.pokemon_species.name} sprite",
                    modifier = Modifier.size(140.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clickable(onClick = { onClickPokemon(pokemonEntry.pokemon_species.name) }),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = pokemonEntry.pokemon_species.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { onClickPokemon(pokemonEntry.pokemon_species.name) }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Ir a detalles",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Button(
                onClick = { ClickFavorito(pokemonEntry.pokemon_species.name) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Agregar a favoritos")
            }
        }
    }


}
@Composable
fun PokemonTypeDropdown(
    typeList: List<TypeFiltro>,
    onTypeSelected: (TypeFiltro) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf<TypeFiltro?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp) // Ajusta el espaciado
    ) {
        Text(
            text = "Filtrar por Tipo",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp).align(Alignment.CenterHorizontally) // Menor separación
        )

        Button(
            onClick = { isDropdownExpanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp), // Ajusta la altura del botón
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = selectedType?.name ?: "Seleccionar Tipo", // Texto por defecto
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium// Ajusta el estilo
            )
        }

        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            typeList.forEach { type ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = type.name.replaceFirstChar { it.uppercase() }, // Capitaliza el nombre
                            style = MaterialTheme.typography.bodyMedium // Ajusta estilo
                        )
                    },
                    onClick = {
                        selectedType = type
                        isDropdownExpanded = false
                        onTypeSelected(type)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp) // Espaciado interno del ítem
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
        ClickFavorito = {},
        volver = {},
    )
}
