package com.accesodatos.minipokedex.features.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.accesodatos.minipokedex.core.palette.rememberDominantColor
import com.accesodatos.minipokedex.core.repo.PokemonRepository
import com.accesodatos.minipokedex.core.util.capFirst
import com.accesodatos.minipokedex.features.SimpleVmFactory
import com.accesodatos.minipokedex.ui.components.ErrorContent
import com.accesodatos.minipokedex.ui.components.LoadingContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    repo: PokemonRepository,
    paddingValues: PaddingValues,
    id: Int,
    onBack: () -> Unit
) {
    val vm: PokemonDetailViewModel = viewModel(factory = SimpleVmFactory { PokemonDetailViewModel(repo, id) })
    val state by vm.uiState.collectAsState()

    val dominant = rememberDominantColor(state.pokemon?.imageUrl)

    Column(Modifier.padding(paddingValues).fillMaxSize()) {
        TopAppBar(
            title = { Text("Detalle") },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) }
            },
            actions = {
                IconButton(onClick = { vm.toggleFavorite(dominant.toArgb()) }) {
                    Icon(
                        imageVector = if (state.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = null
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = if (dominant != androidx.compose.ui.graphics.Color.Unspecified) dominant else MaterialTheme.colorScheme.surface
            )
        )

        when {
            state.loading -> LoadingContent(modifier = Modifier.fillMaxSize())
            state.error != null -> ErrorContent(message = state.error!!, modifier = Modifier.fillMaxSize(), onRetry = vm::load)
            state.pokemon != null -> DetailBody(state.pokemon!!)
        }
    }
}

@Composable
private fun DetailBody(p: com.accesodatos.minipokedex.core.model.PokemonDetail) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AsyncImage(model = p.imageUrl, contentDescription = p.name, modifier = Modifier.size(120.dp))
                Column {
                    Text("#${p.id} ${p.name.capFirst()}", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        p.types.forEach { AssistChip(onClick = {}, label = { Text(it.capFirst()) }) }
                    }
                }
            }
        }

        item {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Datos", style = MaterialTheme.typography.titleMedium)
                    Text("Altura: ${"%.1f".format(p.heightMeters)} m")
                    Text("Peso: ${"%.1f".format(p.weightKg)} kg")
                    Text("Habilidades: ${p.abilities.take(3).joinToString(", ")}")
                }
            }
        }

        item {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Stats", style = MaterialTheme.typography.titleMedium)
                    p.stats.take(5).forEach { s ->
                        Text("${s.name.capFirst()}: ${s.value}")
                    }
                }
            }
        }
    }
}
