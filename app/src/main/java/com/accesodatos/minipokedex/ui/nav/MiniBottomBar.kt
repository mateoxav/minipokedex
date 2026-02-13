package com.accesodatos.minipokedex.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun MiniBottomBar(navController: NavController, visibleRoutes: Set<String>) {
    val current = navController.currentBackStackEntryAsState().value?.destination?.route
    val shouldShow = current in visibleRoutes

    if (!shouldShow) return

    NavigationBar {
        NavigationBarItem(
            selected = current == Routes.POKEDEX,
            onClick = { navController.navigate(Routes.POKEDEX) { launchSingleTop = true } },
            icon = { androidx.compose.material3.Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text("Pokémon") }
        )
        NavigationBarItem(
            selected = current == Routes.SEARCH,
            onClick = { navController.navigate(Routes.SEARCH) { launchSingleTop = true } },
            icon = { androidx.compose.material3.Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Buscar") }
        )
        NavigationBarItem(
            selected = current == Routes.GAME,
            onClick = { navController.navigate(Routes.GAME) { launchSingleTop = true } },
            icon = { androidx.compose.material3.Icon(Icons.Default.SportsEsports, contentDescription = null) },
            label = { Text("Jugar") }
        )
        NavigationBarItem(
            selected = current == Routes.FAVORITES,
            onClick = { navController.navigate(Routes.FAVORITES) { launchSingleTop = true } },
            icon = { androidx.compose.material3.Icon(Icons.Default.Favorite, contentDescription = null) },
            label = { Text("Favoritos") }
        )
    }
}
