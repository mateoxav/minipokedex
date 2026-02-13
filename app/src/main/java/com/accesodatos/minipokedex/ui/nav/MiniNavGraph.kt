package com.accesodatos.minipokedex.ui.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.accesodatos.minipokedex.MiniPokedexApplication
import com.accesodatos.minipokedex.features.detail.PokemonDetailScreen
import com.accesodatos.minipokedex.features.favorites.FavoritesScreen
import com.accesodatos.minipokedex.features.game.GameScreen
import com.accesodatos.minipokedex.features.pokedex.PokedexScreen
import com.accesodatos.minipokedex.features.search.SearchScreen

@Composable
fun MiniNavGraph(navController: NavHostController, paddingValues: PaddingValues) {
    val app = LocalContext.current.applicationContext as MiniPokedexApplication
    val repo = app.container.pokemonRepository

    NavHost(
        navController = navController,
        startDestination = Routes.POKEDEX,
        modifier = Modifier
    ) {
        composable(Routes.POKEDEX) {
            PokedexScreen(repo = repo, paddingValues = paddingValues, onOpenDetail = { navController.navigate(Routes.detail(it)) })
        }
        composable(Routes.SEARCH) {
            SearchScreen(repo = repo, paddingValues = paddingValues, onOpenDetail = { navController.navigate(Routes.detail(it)) })
        }
        composable(Routes.GAME) {
            GameScreen(repo = repo, paddingValues = paddingValues, onOpenDetail = { navController.navigate(Routes.detail(it)) })
        }
        composable(Routes.FAVORITES) {
            FavoritesScreen(repo = repo, paddingValues = paddingValues, onOpenDetail = { navController.navigate(Routes.detail(it)) })
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 1
            PokemonDetailScreen(repo = repo, paddingValues = paddingValues, id = id, onBack = { navController.popBackStack() })
        }
    }
}
