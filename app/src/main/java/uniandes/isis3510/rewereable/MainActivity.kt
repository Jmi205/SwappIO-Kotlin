package uniandes.isis3510.rewereable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import uniandes.isis3510.rewereable.ui.components.BottomMenu
import uniandes.isis3510.rewereable.ui.navigation.Screen
import uniandes.isis3510.rewereable.ui.screens.profile.ProfileScreen
import uniandes.isis3510.rewereable.ui.theme.ReWereableTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReWereableTheme() {
                val navController = rememberNavController()
                // Para saber en qué pantalla estamos y marcar el ícono del menú
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Scaffold nos permite colocar el menú fácilmente al fondo
                Scaffold(
                    bottomBar = {
                        BottomMenu(navController = navController, currentRoute = currentRoute)
                    }
                ) { innerPadding ->
                    // El NavHost gestiona qué pantalla mostrar
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Profile.route, // Iniciamos en Profile para probar
                        modifier = Modifier.padding(innerPadding) // Evita que la pantalla se oculte tras el menú
                    ) {
                        composable(Screen.Home.route) { /* Aquí irá tu HomeScreen() */ }
                        composable(Screen.Donate.route) { /* DonateScreen() */ }
                        composable(Screen.Sell.route) { /* SellScreen() */ }
                        composable(Screen.Inbox.route) { /* InboxScreen() */ }
                        composable(Screen.Profile.route) { ProfileScreen() }
                    }
                }
            }
        }
    }
}