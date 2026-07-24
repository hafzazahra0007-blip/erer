package com.example.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.viewmodel.GameViewModel

object NavRoutes {
    const val HOME = "home"
    const val GAME = "game"
    const val DAILY = "daily"
    const val SHOP = "shop"
    const val SOUNDS = "sounds"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val COMING_SOON = "coming_soon"
}

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    gameViewModel: GameViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
        modifier = modifier
    ) {
        composable(
            route = NavRoutes.HOME,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            HomeScreen(
                viewModel = gameViewModel,
                onStartGame = { navController.navigate(NavRoutes.GAME) },
                onNavigateDaily = { navController.navigate(NavRoutes.DAILY) },
                onNavigateShop = { navController.navigate(NavRoutes.SHOP) },
                onNavigateSounds = { navController.navigate(NavRoutes.SOUNDS) },
                onNavigateStats = { navController.navigate(NavRoutes.STATS) },
                onNavigateSettings = { navController.navigate(NavRoutes.SETTINGS) },
                onNavigateComingSoon = { navController.navigate(NavRoutes.COMING_SOON) }
            )
        }

        composable(
            route = NavRoutes.GAME,
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() }
        ) {
            GameScreen(
                viewModel = gameViewModel,
                onNavigateHome = {
                    navController.popBackStack(NavRoutes.HOME, false)
                }
            )
        }

        composable(
            route = NavRoutes.DAILY,
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() }
        ) {
            DailyChallengeScreen(
                viewModel = gameViewModel,
                onStartDailyPuzzle = { navController.navigate(NavRoutes.GAME) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.SHOP,
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() }
        ) {
            ShopScreen(
                viewModel = gameViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.SOUNDS,
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() }
        ) {
            SoundsScreen(
                viewModel = gameViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.STATS,
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() }
        ) {
            StatsScreen(
                viewModel = gameViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.SETTINGS,
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() }
        ) {
            SettingsScreen(
                viewModel = gameViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.COMING_SOON,
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() }
        ) {
            ComingSoonScreen(
                viewModel = gameViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
