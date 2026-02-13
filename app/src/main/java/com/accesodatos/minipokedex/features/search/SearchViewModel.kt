package com.accesodatos.minipokedex.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accesodatos.minipokedex.core.db.HistoryEntity
import com.accesodatos.minipokedex.core.model.HistorySource
import com.accesodatos.minipokedex.core.model.PokemonDetail
import com.accesodatos.minipokedex.core.repo.PokemonRepository
import com.accesodatos.minipokedex.core.util.DataResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val result: PokemonDetail? = null,
    val favoriteIds: Set<Int> = emptySet(),
    val history: List<HistoryEntity> = emptyList()
)

class SearchViewModel(private val repo: PokemonRepository) : ViewModel() {

    private val base = MutableStateFlow(SearchUiState())

    val uiState: StateFlow<SearchUiState> = combine(
        base,
        repo.observeFavoriteIds().map { it.toSet() },
        repo.observeHistory()
    ) { state, favIds, history ->
        state.copy(favoriteIds = favIds, history = history)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState())

    fun search(query: String) = viewModelScope.launch {
        base.update { it.copy(loading = true, error = null, result = null) }
        when (val res = repo.searchPokemon(query, HistorySource.SEARCH)) {
            is DataResult.Success -> base.update { it.copy(loading = false, result = res.data) }
            is DataResult.Error -> base.update { it.copy(loading = false, error = res.message) }
        }
    }
}
