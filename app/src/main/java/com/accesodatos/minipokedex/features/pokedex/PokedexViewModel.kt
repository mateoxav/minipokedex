package com.accesodatos.minipokedex.features.pokedex

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accesodatos.minipokedex.core.model.PokemonDetail
import com.accesodatos.minipokedex.core.repo.PokemonRepository
import com.accesodatos.minipokedex.core.util.DataResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PokedexUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val all: List<PokemonDetail> = emptyList(),
    val typeFilter: String = "Todos",
    val favoriteIds: Set<Int> = emptySet()
) {
    val filtered: List<PokemonDetail> =
        if (typeFilter == "Todos") all else all.filter { it.types.contains(typeFilter) }

    val availableTypes: List<String> =
        listOf("Todos") + all.flatMap { it.types }.distinct().sorted()
}

class PokedexViewModel(
    private val repo: PokemonRepository
) : ViewModel() {

    private val typeFilter = MutableStateFlow("Todos")

    private val base = MutableStateFlow(PokedexUiState())

    val uiState: StateFlow<PokedexUiState> = combine(
        base,
        typeFilter,
        repo.observeFavoriteIds().map { it.toSet() }
    ) { state, filter, favIds ->
        state.copy(typeFilter = filter, favoriteIds = favIds)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PokedexUiState())

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        base.update { it.copy(loading = true, error = null) }
        when (val res = repo.getPokemonList1to20()) {
            is DataResult.Success -> base.update { it.copy(loading = false, all = res.data) }
            is DataResult.Error -> base.update { it.copy(loading = false, error = res.message) }
        }
    }

    fun setTypeFilter(type: String) {
        typeFilter.value = type
    }
}
