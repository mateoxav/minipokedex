package com.accesodatos.minipokedex.features.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.accesodatos.minipokedex.core.repo.PokemonRepository
import com.accesodatos.minipokedex.features.SimpleVmFactory
import com.accesodatos.minipokedex.ui.components.ErrorContent
import com.accesodatos.minipokedex.ui.components.LoadingContent
import com.accesodatos.minipokedex.ui.components.PokemonCard
import com.accesodatos.minipokedex.core.util.capFirst

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    repo: PokemonRepository,
    paddingValues: PaddingValues,
    onOpenDetail: (Int) -> Unit
) {
    val vm: SearchViewModel = viewModel(factory = SimpleVmFactory { SearchViewModel(repo) })
    val state by vm.uiState.collectAsState()

    var query by remember { mutableStateOf("") }

    Column(Modifier.padding(paddingValues).fillMaxSize()) {
        TopAppBar(title = { Text("Buscar") })

        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("ID (1–20) o nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { vm.search(query) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("BUSCAR") }
        }

        when {
            state.loading -> LoadingContent(modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
            state.error != null -> ErrorContent(message = state.error!!, modifier = Modifier.fillMaxWidth())
            state.result != null -> {
                Column(Modifier.padding(horizontal = 12.dp)) {
                    PokemonCard(
                        pokemon = state.result!!,
                        isFavorite = state.result!!.id in state.favoriteIds,
                        onClick = { onOpenDetail(state.result!!.id) }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }

        Text("Historial", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 12.dp))
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.history, key = { it.historyId }) { h ->
                Card(onClick = { onOpenDetail(h.pokemonId) }) {
                    Column(Modifier.padding(12.dp)) {
                        Text("#${h.pokemonId}  ${h.query.capFirst()}")
                        Text("Fuente: ${h.source}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
