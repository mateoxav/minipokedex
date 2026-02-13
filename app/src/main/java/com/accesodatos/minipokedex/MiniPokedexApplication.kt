package com.accesodatos.minipokedex

import android.app.Application
import com.accesodatos.minipokedex.core.di.AppContainer

class MiniPokedexApplication : Application() {
    // Comentario: Service locator simple para mantener el proyecto en 1 módulo.
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
