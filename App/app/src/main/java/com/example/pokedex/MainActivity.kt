package com.example.pokedex

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.pokedex.services.driverAdapters.RegionesDiverAdapter
import com.example.pokedex.services.models.Region
import com.example.pokedex.ui.theme.PokedexTheme

class MainActivity : ComponentActivity() {

    val regionesDiverAdapter by lazy { RegionesDiverAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var regiones by remember { mutableStateOf<List<Region>>(emptyList()) }
            var LoadRegiones by remember { mutableStateOf<Boolean>(false) }
            if (!LoadRegiones) {
                this.regionesDiverAdapter.allRegiones(
                    loadData = {
                        regiones = it
                        LoadRegiones = true
                    },
                    errorData = {
                        println("Error en el servicio")
                        LoadRegiones = true
                    }
                )
            }
            RegionesScreen(regiones = regiones, onClickRegion = { regionName -> goToPokemons(regionName) })
        }
    }

    fun goToPokemons(regionName: String) {
        val intent = Intent(this, RegionActivity::class.java).apply {
            putExtra("region_name", regionName)
        }
        startActivity(intent)
    }

    @Composable
    fun RegionesScreen(
        regiones: List<Region>,
        onClickRegion: (String) -> Unit
    ) {
        PokedexTheme{
            Scaffold(
                topBar = {
                    Text(text = stringResource(id = R.string.title_regiones))
                }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    LazyColumn {
                        items(
                            items = regiones,
                            key = { it.name }
                        ) {
                            Column {
                                Row {
                                    Text(text = stringResource(id = R.string.nameRegion))
                                    Text(text = "${it.name}")
                                }
                                Button(onClick = { onClickRegion(it.name) }) {
                                    Text(text = stringResource(id = R.string.go_to_Region))
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
    fun RegionesScreenPreview() {
        RegionesScreen(
            regiones = emptyList(),
            onClickRegion = {}
        )
    }
}