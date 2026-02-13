package com.accesodatos.minipokedex.features.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accesodatos.minipokedex.core.model.HistorySource
import com.accesodatos.minipokedex.core.model.PokemonDetail
import com.accesodatos.minipokedex.core.repo.PokemonRepository
import com.accesodatos.minipokedex.core.util.DataResult
import com.accesodatos.minipokedex.core.util.PokemonLimit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GameOptions(
    val typeOptions: List<String> = emptyList(),
    val abilityOptions: List<String> = emptyList(),
    val moveOptions: List<String> = emptyList(),
    val correctAbilities: Set<String> = emptySet(),
    val correctMoves: Set<String> = emptySet()
)

data class GameResult(
    val typeCorrect: Boolean,
    val heightCorrect: Boolean,
    val weightCorrect: Boolean,
    val abilitiesCorrectCount: Int,
    val movesCorrectCount: Int,
    val scoreOutOf10: Int
)

data class GameUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val pokemon: PokemonDetail? = null,
    val options: GameOptions = GameOptions(),
    val result: GameResult? = null
)

class GameViewModel(private val repo: PokemonRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState

    private var pool: List<PokemonDetail> = emptyList() // Comentario: pool 1–20 para distractores.

    init {
        preloadPool()
    }

    private fun preloadPool() = viewModelScope.launch {
        when (val res = repo.getPokemonList1to20()) {
            is DataResult.Success -> pool = res.data
            else -> Unit
        }
    }

    fun invokeRandomPokemon() = viewModelScope.launch {
        _uiState.update { it.copy(loading = true, error = null, result = null) }
        val id = Random.nextInt(PokemonLimit.MIN_ID, PokemonLimit.MAX_ID + 1)

        when (val res = repo.getPokemonById(id, HistorySource.GAME)) {
            is DataResult.Success -> {
                val p = res.data
                val opts = buildOptions(p)
                _uiState.update { it.copy(loading = false, pokemon = p, options = opts, error = null) }
            }
            is DataResult.Error -> _uiState.update { it.copy(loading = false, error = res.message) }
        }
    }

    fun checkAnswers(
        selectedType: String,
        heightText: String,
        weightText: String,
        selectedAbilities: Set<String>,
        selectedMoves: Set<String>
    ) {
        val p = _uiState.value.pokemon ?: return
        val opts = _uiState.value.options

        val typeCorrect = p.types.contains(selectedType)

        val heightOk = heightText.toDoubleOrNull()?.let { input ->
            kotlin.math.abs(input - p.heightMeters) <= 0.05 // Comentario: tolerancia pequeña.
        } ?: false

        val weightOk = weightText.toDoubleOrNull()?.let { input ->
            kotlin.math.abs(input - p.weightKg) <= 0.2
        } ?: false

        val abilitiesCorrectCount = selectedAbilities.intersect(opts.correctAbilities).size
        val movesCorrectCount = selectedMoves.intersect(opts.correctMoves).size

        // Score: tipo 2, altura 2, peso 2, habilidades 2 (1+1), movimientos 2 (1+1)
        var score = 0
        if (typeCorrect) score += 2
        if (heightOk) score += 2
        if (weightOk) score += 2
        score += (abilitiesCorrectCount.coerceAtMost(2))
        score += (movesCorrectCount.coerceAtMost(2))

        _uiState.update {
            it.copy(
                result = GameResult(
                    typeCorrect = typeCorrect,
                    heightCorrect = heightOk,
                    weightCorrect = weightOk,
                    abilitiesCorrectCount = abilitiesCorrectCount,
                    movesCorrectCount = movesCorrectCount,
                    scoreOutOf10 = score
                )
            )
        }
    }

    fun resetResult() {
        _uiState.update { it.copy(result = null, error = null) }
    }

    private fun buildOptions(p: PokemonDetail): GameOptions {
        val allTypes = pool.flatMap { it.types }.distinct()
        val allAbilities = pool.flatMap { it.abilities }.distinct()
        val allMoves = pool.flatMap { it.moves }.distinct()

        val typeOptions = buildOptionList(correct = p.types, pool = allTypes, size = 6)

        val correctAbilities = p.abilities.shuffled().take(2).toSet()
        val abilityOptions = buildOptionList(correct = correctAbilities.toList(), pool = allAbilities, size = 8)

        val correctMoves = p.moves.shuffled().take(2).toSet()
        val moveOptions = buildOptionList(correct = correctMoves.toList(), pool = allMoves, size = 8)

        return GameOptions(
            typeOptions = typeOptions,
            abilityOptions = abilityOptions,
            moveOptions = moveOptions,
            correctAbilities = correctAbilities,
            correctMoves = correctMoves
        )
    }

    private fun buildOptionList(correct: List<String>, pool: List<String>, size: Int): List<String> {
        val set = linkedSetOf<String>()
        correct.forEach { set.add(it) }
        pool.shuffled().forEach {
            if (set.size < size) set.add(it)
        }
        return set.toList().shuffled()
    }
}
