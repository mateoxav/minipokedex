package com.accesodatos.minipokedex.core.util

object PokemonLimit {
    const val MIN_ID = 1
    const val MAX_ID = 20
    fun isAllowed(id: Int) = id in MIN_ID..MAX_ID
}
