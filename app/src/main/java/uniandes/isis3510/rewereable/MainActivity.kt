package uniandes.isis3510.rewereable

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import uniandes.isis3510.rewereable.domain.repository.ProductRepositoryImpl
import uniandes.isis3510.rewereable.domain.repository.UserRepositoryImpl
import uniandes.isis3510.rewereable.ui.components.BottomMenu
import uniandes.isis3510.rewereable.ui.navigation.Screen
import uniandes.isis3510.rewereable.ui.screens.home.HomeScreen
import uniandes.isis3510.rewereable.ui.screens.home.HomeViewModel
import uniandes.isis3510.rewereable.ui.screens.product.ProductDetailScreen
import uniandes.isis3510.rewereable.ui.screens.product.ProductDetailViewModel
import uniandes.isis3510.rewereable.ui.screens.profile.ProfileScreen
import uniandes.isis3510.rewereable.ui.screens.profile.ProfileViewModel
import uniandes.isis3510.rewereable.ui.theme.ReWereableTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userRepository = UserRepositoryImpl()
        val profileViewModel = ProfileViewModel(userRepository)

        val productRepository = ProductRepositoryImpl()
        val homeViewModel = HomeViewModel(productRepository, userRepository)

        setContent {
            ReWereableTheme() {
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route



                Scaffold(
                    bottomBar = {
                        BottomMenu(navController = navController, currentRoute = currentRoute)
                    }
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding) // Evita que la pantalla se oculte tras el menú
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                viewModel =  homeViewModel,
                                onNavigateToDetails = { productId -> navController.navigate("details/{productId}")}) }
                        composable(Screen.Donate.route) { /* DonateScreen() */ }
                        composable(Screen.Sell.route) { /* SellScreen() */ }
                        composable(Screen.Inbox.route) { /* InboxScreen() */ }
                        composable(Screen.Profile.route) { ProfileScreen(viewModel = profileViewModel) }
                        composable("details/{productId}") { backStackEntry ->

                                val productId = backStackEntry.arguments?.getString("productId") ?: ""


                                val detailViewModel: ProductDetailViewModel = viewModel(
                                    factory = ProductDetailViewModel.provideFactory(productRepository, productId)
                                )

                                ProductDetailScreen(
                                    viewModel = detailViewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                    }
                }
            }
        }
    }
}