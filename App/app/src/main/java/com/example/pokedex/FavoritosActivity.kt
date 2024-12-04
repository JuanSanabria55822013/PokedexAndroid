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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplicationwebservice.R
import com.example.pokedex.dataBases.Entities.PokemonEntity
import com.example.pokedex.dataBases.viewsModels.PokemonFavoritos


class FavoritosActivity : ComponentActivity() {
    private val favoritosViewModel: PokemonFavoritos by lazy {
        PokemonFavoritos(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FavoritosScreen(favoritosViewModel)
        }
    }

    private fun Volver() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FavoritosScreen(viewModel: PokemonFavoritos) {
        val favoritos = remember { mutableStateOf<List<PokemonEntity>>(emptyList()) }
        var showDeleteModal by remember { mutableStateOf(false) }
        var selectedPokemon by remember { mutableStateOf<PokemonEntity?>(null) }

        LaunchedEffect(Unit) {
            viewModel.loadPokemons { pokemons ->
                favoritos.value = pokemons
            }
        }

        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = {
                        Text(
                            "Pokémons Favoritos",
                            style = MaterialTheme.typography.headlineLarge,
                            color = colorResource(R.color.white)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { Volver() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = colorResource(R.color.white)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = colorResource(R.color.RojoPokemon))
                )
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                items(favoritos.value) { pokemon ->
                    FavoritoItem(
                        pokemon = pokemon,
                        onDelete = {
                            selectedPokemon = pokemon
                            showDeleteModal = true
                        }
                    )
                }
            }

            if (showDeleteModal && selectedPokemon != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteModal = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deletePokemonFavorito(selectedPokemon!!.id)
                                favoritos.value =
                                    favoritos.value.filter { it.id != selectedPokemon!!.id }
                                showDeleteModal = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.RojoPokemon),
                                contentColor = colorResource(R.color.white)
                            )
                        ) {
                            Text("Eliminar")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDeleteModal = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.RojoPokemon),
                                contentColor = colorResource(R.color.white)
                            )
                        ) {
                            Text("Cancelar")
                        }
                    },
                    title = { Text("Confirmar eliminación") },
                    text = { Text("¿Estás seguro de que quieres eliminar a ${selectedPokemon!!.name} de la lista de favoritos?") }
                )
            }
        }
    }

    @Composable
    fun FavoritoItem(pokemon: PokemonEntity, onDelete: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.RojoSegundario))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Image(
                        painter = rememberAsyncImagePainter(pokemon.imgUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = pokemon.name.capitalize(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = colorResource(R.color.black),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pokemon.types.split(",").forEach { type ->
                        Box(
                            modifier = Modifier
                                .background(colorResource(R.color.RojoPokemon))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = type.capitalize(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorResource(R.color.white)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Altura: ${pokemon.height / 10.0} m | Peso: ${pokemon.weight / 10.0} kg",
                    style = MaterialTheme.typography.titleLarge,
                    color = colorResource(R.color.black)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Base Stats",
                    style = MaterialTheme.typography.titleLarge,
                    color = colorResource(R.color.black)
                )
                Spacer(modifier = Modifier.height(8.dp))
                pokemon.stats.split(",").forEach { stat ->
                    val (name, value) = stat.split(":").map { it.trim() }
                    Column {
                        Text(
                            text = "${name}: ${value}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Habilidades: ${pokemon.abilities.replace(",", ", ")}",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorResource(R.color.black)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onDelete() },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.RojoPokemon),
                        contentColor = colorResource(R.color.white)
                    )
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}