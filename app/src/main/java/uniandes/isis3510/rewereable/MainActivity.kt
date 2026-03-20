package uniandes.isis3510.rewereable

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
import uniandes.isis3510.rewereable.domain.repository.CharityRepositoryImpl
import uniandes.isis3510.rewereable.domain.repository.ProductRepositoryImpl
import uniandes.isis3510.rewereable.domain.repository.UserRepositoryImpl
import uniandes.isis3510.rewereable.ui.components.BottomMenu
import uniandes.isis3510.rewereable.ui.navigation.Screen
import uniandes.isis3510.rewereable.ui.screens.charity.CharityDetailScreen
import uniandes.isis3510.rewereable.ui.screens.charity.CharityDetailViewModel
import uniandes.isis3510.rewereable.ui.screens.donate.DonateScreen
import uniandes.isis3510.rewereable.ui.screens.donate.DonateViewModel
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

        val charityRepository = CharityRepositoryImpl()
        val donateViewModel = DonateViewModel(charityRepository)

        setContent {
            ReWereableTheme {
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        BottomMenu(
                            navController = navController,
                            currentRoute = currentRoute
                        )
                    }
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                viewModel = homeViewModel,
                                onNavigateToDetails = { productId ->
                                    navController.navigate(Screen.Product.route)
                                }
                            )
                        }

                        composable(Screen.Donate.route) {
                            DonateScreen(
                                viewModel = donateViewModel,
                                onNavigateToCharityDetails = { charityId ->
                                    navController.navigate("charity_details/$charityId")
                                }
                            )
                        }

                        composable(Screen.Sell.route) {
                            /* SellScreen() */
                        }

                        composable(Screen.Inbox.route) {
                            /* InboxScreen() */
                        }

                        composable(Screen.Profile.route) {
                            ProfileScreen(viewModel = profileViewModel)
                        }

                        composable(Screen.Product.route) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId") ?: ""

                            val detailViewModel: ProductDetailViewModel = viewModel(
                                factory = ProductDetailViewModel.provideFactory(
                                    productRepository,
                                    productId
                                )
                            )

                            ProductDetailScreen(
                                viewModel = detailViewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.CharityDetail.route) { backStackEntry ->
                            val charityId = backStackEntry.arguments?.getString("charityId") ?: ""

                            val charityDetailViewModel: CharityDetailViewModel = viewModel(
                                factory = CharityDetailViewModel.provideFactory(
                                    charityRepository,
                                    charityId
                                )
                            )

                            CharityDetailScreen(
                                viewModel = charityDetailViewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}