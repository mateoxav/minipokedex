package com.accesodatos.minipokedex.features.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accesodatos.minipokedex.core.model.HistorySource
import com.accesodatos.minipokedex.core.model.PokemonDetail
import com.accesodatos.minipokedex.core.repo.PokemonRepository
import com.accesodatos.minipokedex.core.util.DataResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DetailUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val pokemon: PokemonDetail? = null,
    val isFavorite: Boolean = false
)

class PokemonDetailViewModel(
    private val repo: PokemonRepository,
    private val id: Int
) : ViewModel() {

    private val base = MutableStateFlow(DetailUiState())

    val uiState: StateFlow<DetailUiState> = combine(
        base,
        repo.observeIsFavorite(id)
    ) { state, isFav ->
        state.copy(isFavorite = isFav)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailUiState())

    init {
        load()
    }

    fun load() = viewModelScope.launch {
        base.update { it.copy(loading = true, error = null) }
        when (val res = repo.getPokemonById(id, HistorySource.LIST_CLICK)) {
            is DataResult.Success -> base.update { it.copy(loading = false, pokemon = res.data) }
            is DataResult.Error -> base.update { it.copy(loading = false, error = res.message) }
        }
    }

    fun toggleFavorite(colorArgb: Int?) = viewModelScope.launch {
        val p = uiState.value.pokemon ?: return@launch
        repo.toggleFavorite(pokemon = p, colorArgb = colorArgb)
    }
}
