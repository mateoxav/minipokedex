package com.accesodatos.minipokedex.features.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.accesodatos.minipokedex.core.repo.PokemonRepository
import com.accesodatos.minipokedex.core.util.capFirst
import com.accesodatos.minipokedex.features.SimpleVmFactory
import com.accesodatos.minipokedex.ui.components.ErrorContent
import com.accesodatos.minipokedex.ui.components.LoadingContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    repo: PokemonRepository,
    paddingValues: PaddingValues,
    onOpenDetail: (Int) -> Unit
) {
    val vm: GameViewModel = viewModel(factory = SimpleVmFactory { GameViewModel(repo) })
    val state by vm.uiState.collectAsState()

    Column(Modifier.padding(paddingValues).fillMaxSize()) {
        TopAppBar(title = { Text("Jugar") })

        when {
            state.loading -> LoadingContent(modifier = Modifier.fillMaxSize())
            state.error != null -> ErrorContent(message = state.error!!, modifier = Modifier.fillMaxSize(), onRetry = vm::invokeRandomPokemon)
            state.pokemon == null -> GameStart(onInvoke = vm::invokeRandomPokemon)
            else -> GamePlay(state = state, onCheck = vm::checkAnswers, onInvoke = vm::invokeRandomPokemon, onOpenDetail = onOpenDetail, onResetResult = vm::resetResult)
        }
    }
}

@Composable
private fun GameStart(onInvoke: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("Invoca un Pokémon (1–20)", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text("Se mostrará su nombre/imagen y tendrás que acertar datos.")
            }
        }
        Button(onClick = onInvoke, modifier = Modifier.fillMaxWidth()) { Text("INVOCAR POKÉMON") }
    }
}

@Composable
private fun GamePlay(
    state: GameUiState,
    onCheck: (String, String, String, Set<String>, Set<String>) -> Unit,
    onInvoke: () -> Unit,
    onOpenDetail: (Int) -> Unit,
    onResetResult: () -> Unit
) {
    val p = state.pokemon!!

    var selectedType by remember { mutableStateOf(state.options.typeOptions.firstOrNull().orEmpty()) }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var abilities by remember { mutableStateOf(setOf<String>()) }
    var moves by remember { mutableStateOf(setOf<String>()) }

    // Comentario: scroll para no cortar en pantallas pequeñas.
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AsyncImage(model = p.imageUrl, contentDescription = p.name, modifier = Modifier.size(140.dp))
                Text(p.name.capFirst(), style = MaterialTheme.typography.headlineSmall)
                Text("ID: #${p.id}")
            }
        }

        TypePicker(options = state.options.typeOptions, value = selectedType, onChange = { selectedType = it })

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Altura (m)") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Peso (kg)") },
                modifier = Modifier.weight(1f)
            )
        }

        MultiSelectCard(
            title = "Habilidades (elige 1–2)",
            options = state.options.abilityOptions,
            selected = abilities,
            max = 2,
            onSelectedChange = { abilities = it }
        )

        MultiSelectCard(
            title = "Movimientos (elige 2)",
            options = state.options.moveOptions,
            selected = moves,
            max = 2,
            onSelectedChange = { moves = it }
        )

        Button(
            onClick = { onCheck(selectedType, height, weight, abilities, moves) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("COMPROBAR RESPUESTAS") }

        state.result?.let { r ->
            ResultCard(
                result = r,
                onPlayAgain = {
                    onResetResult()
                    onInvoke()
                },
                onDetail = { onOpenDetail(p.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypePicker(options: List<String>, value: String, onChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo") },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt.capFirst()) },
                    onClick = {
                        onChange(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun MultiSelectCard(
    title: String,
    options: List<String>,
    selected: Set<String>,
    max: Int,
    onSelectedChange: (Set<String>) -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            options.forEach { opt ->
                val checked = opt in selected
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { now ->
                            val new = selected.toMutableSet()
                            if (now) {
                                if (new.size < max) new.add(opt)
                            } else {
                                new.remove(opt)
                            }
                            onSelectedChange(new)
                        }
                    )
                    Text(opt.capFirst())
                }
            }
        }
    }
}

@Composable
private fun ResultCard(result: GameResult, onPlayAgain: () -> Unit, onDetail: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("RESULTADO", style = MaterialTheme.typography.titleLarge)

            Text("Tipo: ${if (result.typeCorrect) "✅ Correcto" else "❌ Incorrecto"}")
            Text("Altura: ${if (result.heightCorrect) "✅ Correcto" else "❌ Incorrecto"}")
            Text("Peso: ${if (result.weightCorrect) "✅ Correcto" else "❌ Incorrecto"}")
            Text("Habilidades: ${result.abilitiesCorrectCount}/2")
            Text("Movimientos: ${result.movesCorrectCount}/2")

            Text("PUNTUACIÓN: ${result.scoreOutOf10}/10", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onPlayAgain, modifier = Modifier.weight(1f)) { Text("JUGAR OTRA VEZ") }
                OutlinedButton(onClick = onDetail, modifier = Modifier.weight(1f)) { Text("VER DETALLE") }
            }
        }
    }
}
