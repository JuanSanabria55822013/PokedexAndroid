package com.example.pokedex.services.controllers

import androidx.lifecycle.viewModelScope
import com.example.pokedex.services.endpoints.TypeEndpoint
import com.example.pokedex.services.models.TypeFiltro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TypeService : BaseService() {

    fun getPokemonTypes(
        success: (tipos: List<TypeFiltro>) -> Unit,
        error: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = getRetrofit()
                    .create(TypeEndpoint::class.java)
                    .getPokemonTypes()

                val data = response.body()
                if (response.isSuccessful && data != null) {
                    success(data.results)
                    println(data.count)
                    println(data.results)
                } else {
                    println("Error en la respuesta: ${response.errorBody()?.string()}")
                    error()
                }
            } catch (e: Exception) {
                println("Excepción: ${e.message}")
                error()
            }
        }
    }
}
