package com.accesodatos.minipokedex

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.accesodatos.minipokedex.ui.MiniPokedexApp
import com.accesodatos.minipokedex.ui.theme.MiniPokedexTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge() // Comentario: edge-to-edge moderno.
        super.onCreate(savedInstanceState)
        setContent {
            MiniPokedexTheme {
                MiniPokedexApp()
            }
        }
    }
}
