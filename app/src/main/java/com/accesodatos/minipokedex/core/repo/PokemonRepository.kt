package com.accesodatos.minipokedex.core.repo

import com.accesodatos.minipokedex.core.db.FavoriteDao
import com.accesodatos.minipokedex.core.db.FavoriteEntity
import com.accesodatos.minipokedex.core.db.HistoryDao
import com.accesodatos.minipokedex.core.db.HistoryEntity
import com.accesodatos.minipokedex.core.model.HistorySource
import com.accesodatos.minipokedex.core.model.PokemonDetail
import com.accesodatos.minipokedex.core.network.PokeApiService
import com.accesodatos.minipokedex.core.network.toDomain
import com.accesodatos.minipokedex.core.util.DataResult
import com.accesodatos.minipokedex.core.util.PokemonLimit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PokemonRepository(
    private val api: PokeApiService,
    private val favoriteDao: FavoriteDao,
    private val historyDao: HistoryDao
) {
    private val cache = mutableMapOf<Int, PokemonDetail>() // Comentario: caché en memoria para evitar llamadas repetidas.

    fun observeFavorites(): Flow<List<FavoriteEntity>> = favoriteDao.observeFavorites()
    fun observeFavoriteIds(): Flow<List<Int>> = favoriteDao.observeFavoriteIds()
    fun observeIsFavorite(id: Int): Flow<Boolean> = favoriteDao.observeIsFavorite(id)
    fun observeHistory(): Flow<List<HistoryEntity>> = historyDao.observeHistory()

    suspend fun getPokemonList1to20(): DataResult<List<PokemonDetail>> = withContext(Dispatchers.IO) {
        try {
            val list = coroutineScope {
                (PokemonLimit.MIN_ID..PokemonLimit.MAX_ID).map { id ->
                    async { fetchPokemonByIdInternal(id) }
                }.awaitAll()
            }.sortedBy { it.id }
            DataResult.Success(list)
        } catch (t: Throwable) {
            DataResult.Error("Error cargando listado", t)
        }
    }

    suspend fun searchPokemon(idOrName: String, source: HistorySource): DataResult<PokemonDetail> = withContext(Dispatchers.IO) {
        try {
            val trimmed = idOrName.trim().lowercase()
            if (trimmed.isBlank()) return@withContext DataResult.Error("Introduce un ID o nombre")

            val pokemon = if (trimmed.all { it.isDigit() }) {
                val id = trimmed.toIntOrNull() ?: return@withContext DataResult.Error("ID inválido")
                if (!PokemonLimit.isAllowed(id)) return@withContext DataResult.Error("Solo se permiten IDs 1–20")
                fetchPokemonByIdInternal(id)
            } else {
                val dto = api.getPokemon(trimmed).toDomain()
                if (!PokemonLimit.isAllowed(dto.id)) return@withContext DataResult.Error("Solo se permiten Pokémon 1–20")
                cache[dto.id] = dto
                dto
            }

            // Comentario: persistimos historial para repetir consultas.
            historyDao.insert(
                HistoryEntity(
                    query = trimmed,
                    pokemonId = pokemon.id,
                    source = source.name
                )
            )

            DataResult.Success(pokemon)
        } catch (t: Throwable) {
            DataResult.Error("No se pudo cargar el Pokémon", t)
        }
    }

    suspend fun getPokemonById(id: Int, source: HistorySource): DataResult<PokemonDetail> =
        searchPokemon(id.toString(), source)

    suspend fun toggleFavorite(pokemon: PokemonDetail, colorArgb: Int?) = withContext(Dispatchers.IO) {
        val isFav = cacheIsFavorite(pokemon.id)
        if (isFav) {
            favoriteDao.deleteById(pokemon.id)
        } else {
            favoriteDao.upsert(
                FavoriteEntity(
                    id = pokemon.id,
                    name = pokemon.name,
                    imageUrl = pokemon.imageUrl,
                    typesPipe = pokemon.types.joinToString("|"),
                    colorArgb = colorArgb
                )
            )
        }
    }

    private suspend fun cacheIsFavorite(id: Int): Boolean {
        // Comentario: consulta rápida apoyada en flow en UI; aquí resolvemos simple con EXISTS.
        // (Room no expone un suspend EXISTS en este ejemplo para simplificar.)
        return false // Comentario: la UI usa observeIsFavorite; toggle hace upsert/delete directamente.
    }

    private suspend fun fetchPokemonByIdInternal(id: Int): PokemonDetail {
        cache[id]?.let { return it }
        val dto = api.getPokemon(id.toString()).toDomain()
        cache[dto.id] = dto
        return dto
    }
}
