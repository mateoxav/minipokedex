package com.accesodatos.minipokedex.features.pokedex

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokedexScreen(
    repo: PokemonRepository,
    paddingValues: PaddingValues,
    onOpenDetail: (Int) -> Unit
) {
    val vm: PokedexViewModel = viewModel(factory = SimpleVmFactory { PokedexViewModel(repo) })
    val state by vm.uiState.collectAsState()

    Column(Modifier.padding(paddingValues).fillMaxSize()) {
        TopAppBar(title = { Text("Pokémon (1–20)") })
        FilterRow(
            types = state.availableTypes,
            selected = state.typeFilter,
            onSelected = vm::setTypeFilter
        )

        when {
            state.loading -> LoadingContent(modifier = Modifier.weight(1f))
            state.error != null -> ErrorContent(message = state.error!!, modifier = Modifier.weight(1f), onRetry = vm::refresh)
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.filtered, key = { it.id }) { p ->
                        PokemonCard(
                            pokemon = p,
                            isFavorite = p.id in state.favoriteIds,
                            onClick = { onOpenDetail(p.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterRow(types: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                types.forEach { t ->
                    DropdownMenuItem(
                        text = { Text(t) },
                        onClick = {
                            onSelected(t)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
