package com.accesodatos.minipokedex.core.network

import com.accesodatos.minipokedex.core.model.PokemonDetail
import com.accesodatos.minipokedex.core.model.PokemonStat

fun PokemonDto.toDomain(): PokemonDetail {
    // Comentario: PokeAPI usa height=decímetros y weight=hectogramos => /10 para m y kg.
    val heightMeters = height / 10.0
    val weightKg = weight / 10.0

    val image = sprites.other?.officialArtwork?.frontDefault ?: sprites.frontDefault

    return PokemonDetail(
        id = id,
        name = name,
        imageUrl = image,
        types = types.map { it.type.name },
        heightMeters = heightMeters,
        weightKg = weightKg,
        abilities = abilities.map { it.ability.name },
        moves = moves.map { it.move.name },
        stats = stats.map { PokemonStat(name = it.stat.name, value = it.baseStat) }
    )
}
