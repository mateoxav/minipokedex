package com.accesodatos.minipokedex.core.di

import android.content.Context
import androidx.room.Room
import com.accesodatos.minipokedex.core.db.AppDatabase
import com.accesodatos.minipokedex.core.network.NetworkModule
import com.accesodatos.minipokedex.core.repo.PokemonRepository

class AppContainer(context: Context) {
    // Comentario: DB singleton del módulo app.
    private val db: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "minipokedex.db"
    ).build()

    private val api = NetworkModule.createPokeApiService()

    val pokemonRepository: PokemonRepository = PokemonRepository(
        api = api,
        favoriteDao = db.favoriteDao(),
        historyDao = db.historyDao()
    )
}
