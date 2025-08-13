package com.example.weather

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weather.presentation.navigation.Destinations
import com.example.weather.presentation.screens.start.StartScreen
import com.example.weather.presentation.screens.weather.WeatherRoute
import com.example.weather.presentation.ui.theme.WeatherTheme

@Composable
fun WeatherApp() {
    WeatherTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = Destinations.START) {
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
