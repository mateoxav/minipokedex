package com.accesodatos.minipokedex.core.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val abilities: List<PokemonAbilityDto>,
    val moves: List<PokemonMoveDto>,
    val types: List<PokemonTypeSlotDto>,
    val stats: List<PokemonStatDto>,
    val sprites: SpritesDto
)

@JsonClass(generateAdapter = true)
data class PokemonAbilityDto(
    val ability: NamedResourceDto
)

@JsonClass(generateAdapter = true)
data class PokemonMoveDto(
    val move: NamedResourceDto
)

@JsonClass(generateAdapter = true)
data class PokemonTypeSlotDto(
    val type: NamedResourceDto
)

@JsonClass(generateAdapter = true)
data class PokemonStatDto(
    @Json(name = "base_stat") val baseStat: Int,
    val stat: NamedResourceDto
)

@JsonClass(generateAdapter = true)
data class NamedResourceDto(
    val name: String,
    val url: String
)

@JsonClass(generateAdapter = true)
data class SpritesDto(
    val other: OtherSpritesDto? = null,
    @Json(name = "front_default") val frontDefault: String? = null
)

@JsonClass(generateAdapter = true)
data class OtherSpritesDto(
    @Json(name = "official-artwork") val officialArtwork: OfficialArtworkDto? = null
)

@JsonClass(generateAdapter = true)
data class OfficialArtworkDto(
    @Json(name = "front_default") val frontDefault: String? = null
)
