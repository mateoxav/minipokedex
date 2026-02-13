package com.accesodatos.minipokedex.features.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.accesodatos.minipokedex.core.repo.PokemonRepository
import com.accesodatos.minipokedex.core.model.PokemonDetail
import com.accesodatos.minipokedex.ui.components.EmptyContent
import com.accesodatos.minipokedex.ui.components.PokemonCard
import kotlinx.coroutines.flow.map


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    repo: PokemonRepository,
    paddingValues: PaddingValues,
    onOpenDetail: (Int) -> Unit
) {
    val favorites by repo.observeFavorites().collectAsState(initial = emptyList())
    val favoriteIds by repo.observeFavoriteIds().map { it.toSet() }.collectAsState(initial = emptySet())

    Column(Modifier.padding(paddingValues).fillMaxSize()) {
        TopAppBar(title = { Text("Favoritos") })

        if (favorites.isEmpty()) {
            EmptyContent("Favoritos vacío\nNo has marcado ningún Pokémon.", modifier = Modifier.fillMaxSize())
            return
        }

        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(favorites, key = { it.id }) { f ->
                val p = PokemonDetail(
                    id = f.id,
                    name = f.name,
                    imageUrl = f.imageUrl,
                    types = f.typesPipe.split("|").filter { it.isNotBlank() },
                    heightMeters = 0.0,
                    weightKg = 0.0,
                    abilities = emptyList(),
                    moves = emptyList(),
                    stats = emptyList()
                )
                PokemonCard(
                    pokemon = p,
                    isFavorite = f.id in favoriteIds,
                    onClick = { onOpenDetail(f.id) }
                )
            }
        }
    }
}
