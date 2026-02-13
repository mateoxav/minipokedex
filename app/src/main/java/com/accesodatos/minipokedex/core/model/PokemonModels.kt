package com.accesodatos.minipokedex.core.model

data class PokemonStat(
    val name: String,
    val value: Int
)

data class PokemonDetail(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val types: List<String>,
    val heightMeters: Double,
    val weightKg: Double,
    val abilities: List<String>,
    val moves: List<String>,
    val stats: List<PokemonStat>
)
