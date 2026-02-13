package com.accesodatos.minipokedex.ui.nav

object Routes {
    const val POKEDEX = "pokedex"
    const val SEARCH = "search"
    const val GAME = "game"
    const val FAVORITES = "favorites"
    const val DETAIL = "detail/{id}"
    fun detail(id: Int) = "detail/$id"
}
