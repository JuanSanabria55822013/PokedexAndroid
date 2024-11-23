package com.example.pokedex.services.driverAdapters

import com.example.pokedex.services.controllers.TypeService
import com.example.pokedex.services.models.TypeFiltro

class TypeAdapter {
    private val service = TypeService()

    fun getAllTypes(
        loadData: (list: List<TypeFiltro>) -> Unit,
        errorData: () -> Unit
    ) {
        service.getPokemonTypes(
            success = { loadData(it) },
            error = { errorData() }
        )
        println("que es lo que manada joder ${loadData}")
    }
}
