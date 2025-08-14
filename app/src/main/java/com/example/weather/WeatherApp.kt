package com.example.weather

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weather.presentation.navigation.Destinations
import com.example.weather.presentation.screens.start.StartScreen
import com.example.weather.presentation.screens.weather.WeatherRoute
import com.example.weather.presentation.ui.theme.BluePrimary
import com.example.weather.presentation.ui.theme.WeatherTheme

@Composable
fun WeatherApp() {
    WeatherTheme {
        val navController = rememberNavController()

        Scaffold(
            containerColor = BluePrimary,
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Destinations.START,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            ) {
                composable(Destinations.START) {
                    StartScreen(onStart = { navController.navigate(Destinations.WEATHER) })
                }
                composable(Destinations.WEATHER) {
                    WeatherRoute()
                }
            }
        }
    }
}
