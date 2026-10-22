package com.example.androidtvmania

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidtvmania.data.Catalog
import com.example.androidtvmania.ui.home.HomeScreen
import com.example.androidtvmania.ui.player.PlayerScreen
import com.example.androidtvmania.ui.theme.TvManiaTheme

@Composable
fun TvManiaApp() {
    TvManiaTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    onStreamSelected = { stream ->
                        navController.navigate("player/${stream.id}")
                    },
                )
            }
            composable(
                route = "player/{streamId}",
                arguments = listOf(navArgument("streamId") { type = NavType.StringType }),
            ) { entry ->
                val streamId = entry.arguments?.getString("streamId").orEmpty()
                val stream = Catalog.find(streamId)
                if (stream == null) {
                    LaunchedEffect(streamId) { navController.popBackStack() }
                } else {
                    PlayerScreen(
                        stream = stream,
                        onBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}
