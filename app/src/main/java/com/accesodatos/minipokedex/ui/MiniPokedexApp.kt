package com.accesodatos.minipokedex.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.accesodatos.minipokedex.ui.nav.MiniBottomBar
import com.accesodatos.minipokedex.ui.nav.MiniNavGraph

@Composable
fun MiniPokedexApp() {
    val navController = rememberNavController()
    val bottomRoutes = remember { setOf("pokedex", "search", "game", "favorites") }

    Scaffold(
        bottomBar = { MiniBottomBar(navController = navController, visibleRoutes = bottomRoutes) }
    ) { padding ->
        MiniNavGraph(navController = navController, paddingValues = padding)
    }
}
