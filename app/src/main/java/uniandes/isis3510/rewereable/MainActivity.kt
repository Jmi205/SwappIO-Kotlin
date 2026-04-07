package uniandes.isis3510.rewereable

import android.os.Bundle
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import uniandes.isis3510.rewereable.domain.repository.AuthRepositoryImpl
import uniandes.isis3510.rewereable.domain.repository.CharityRepositoryImpl
import uniandes.isis3510.rewereable.domain.repository.ProductRepositoryImpl
import uniandes.isis3510.rewereable.domain.repository.UserRepositoryImpl
import uniandes.isis3510.rewereable.ui.activity.ActivityType
import uniandes.isis3510.rewereable.ui.activity.UserActivityScreen
import uniandes.isis3510.rewereable.ui.activity.UserActivityViewModel
import uniandes.isis3510.rewereable.ui.components.BottomMenu
import uniandes.isis3510.rewereable.ui.navigation.Screen
import uniandes.isis3510.rewereable.ui.screens.add.AddProductScreen
import uniandes.isis3510.rewereable.ui.screens.add.AddProductViewModel
import uniandes.isis3510.rewereable.ui.screens.auth.AuthViewModel
import uniandes.isis3510.rewereable.ui.screens.auth.LoginScreen
import uniandes.isis3510.rewereable.ui.screens.auth.RegisterScreen
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
import uniandes.isis3510.rewereable.ui.screens.seller.SellerProfileScreen
import uniandes.isis3510.rewereable.ui.screens.seller.SellerProfileViewModel
import uniandes.isis3510.rewereable.ui.theme.SwappIOTheme
import uniandes.isis3510.rewereable.domain.repository.DropOffRepositoryImpl
import uniandes.isis3510.rewereable.ui.screens.map.MapDropOffScreen
import uniandes.isis3510.rewereable.ui.screens.map.MapDropOffViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        val authRepository = AuthRepositoryImpl(auth, FirebaseFirestore.getInstance())
        val authViewModel = AuthViewModel(authRepository)

        val userRepository = UserRepositoryImpl()
        val productRepository = ProductRepositoryImpl()
        val homeViewModel = HomeViewModel(productRepository, userRepository)

        val charityRepository = CharityRepositoryImpl()
        val donateViewModel = DonateViewModel(charityRepository)

        val dropOffRepository = DropOffRepositoryImpl()

        setContent {
            SwappIOTheme {
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val startDestination = if (auth.currentUser != null) {
                    Screen.Home.route
                } else {
                    Screen.Login.route
                }

                val showBottomBar = currentRoute != Screen.Login.route && currentRoute != Screen.Register.route

                Log.d("MainActivity", "Current User: ${auth.currentUser?.uid}")
                Log.d("Main Activity", "Start Destination: $startDestination")

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomMenu(
                                navController = navController,
                                currentRoute = currentRoute
                            )
                        }
                    }
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Login.route) {
                             LoginScreen(
                                 viewModel = authViewModel,
                                 onNavigateToHome = {
                                     // PopUpTo limpia el historial para que no puedan volver al login con el botón 'Atrás'
                                     navController.navigate(Screen.Home.route) {
                                         popUpTo("login") { inclusive = true }
                                     }
                                 },
                                 onNavigateToRegister = { navController.navigate("register") }
                             )
                        }

                        composable(Screen.Register.route) {
                             RegisterScreen(
                                 viewModel = authViewModel,
                                 onNavigateToHome = {
                                     navController.navigate(Screen.Home.route) {
                                         popUpTo("login") { inclusive = true }
                                     }
                                 },
                                 onNavigateToLogin = { navController.popBackStack() }
                             )
                        }
                        composable(Screen.Home.route) {
                            HomeScreen(
                                viewModel = homeViewModel,
                                onNavigateToDetails = { productId ->
                                    val routeConIdReal = Screen.Product.route.replace("{productId}", productId)
                                    navController.navigate(routeConIdReal)
                                }
                            )
                        }

                        composable(Screen.Profile.route) {
                            val profileViewModel: ProfileViewModel = viewModel(
                                factory = ProfileViewModel.provideFactory(userRepository, authRepository)
                            )

                            ProfileScreen(
                                viewModel = profileViewModel,
                                onNavigateToPurchases = { navController.navigate(Screen.Purchases.route) },
                                onNavigateToListings = { navController.navigate(Screen.Listings.route) },
                                onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                                onLogout = {
                                    navController.navigate("login") {
                                        // Borra todo el historial de navegación para que no puedan volver atrás
                                        popUpTo(0) { inclusive = true }
                                    }
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

                        composable("seller/{sellerId}") { backStackEntry ->
                            val sellerId = backStackEntry.arguments?.getString("sellerId") ?: ""

                            val sellerViewModel: SellerProfileViewModel = viewModel(
                                factory = SellerProfileViewModel.provideFactory(userRepository, productRepository, sellerId)
                            )

                            SellerProfileScreen(
                                viewModel = sellerViewModel,
                                onBackClick = { navController.popBackStack() },
                                onProductClick = { productId ->
                                    navController.navigate(Screen.Product.route.replace("{productId}", productId))
                                }
                            )
                        }



                        composable(Screen.Product.route) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId") ?: ""

                            val detailViewModel: ProductDetailViewModel = viewModel(
                                factory = ProductDetailViewModel.provideFactory(
                                    productRepository = productRepository,
                                    userRepository = userRepository,
                                    productId = productId
                                )
                            )

                            ProductDetailScreen(
                                viewModel = detailViewModel,
                                onBackClick = { navController.popBackStack() },
                                onSellerClick = {sellerId ->
                                    navController.navigate(Screen.Seller.route.replace("{sellerId}", sellerId))
                                }
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
                                onBackClick = { navController.popBackStack() },
                                onNavigateToDropOffMap = {
                                    navController.navigate(Screen.DropOffMap.route)
                                }
                            )
                        }

                        composable(Screen.DropOffMap.route) {
                            val mapDropOffViewModel: MapDropOffViewModel = viewModel(
                                factory = MapDropOffViewModel.provideFactory(dropOffRepository)
                            )

                            MapDropOffScreen(
                                viewModel = mapDropOffViewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Favorites.route) {
                            val favoritesViewModel: UserActivityViewModel = viewModel(
                                factory = UserActivityViewModel.provideFactory(userRepository, productRepository, ActivityType.FAVORITES)
                            )
                            UserActivityScreen(
                                title = "Favorites",
                                viewModel = favoritesViewModel,
                                onBackClick = { navController.popBackStack() },
                                onProductClick = { productId ->
                                    navController.navigate(Screen.Product.route.replace("{productId}", productId))
                                }
                            )
                        }

                        composable(Screen.Listings.route) {
                            val listingsViewModel: UserActivityViewModel = viewModel(
                                factory = UserActivityViewModel.provideFactory(userRepository, productRepository, ActivityType.LISTINGS)
                            )
                            UserActivityScreen(
                                title = "My Listings",
                                viewModel = listingsViewModel,
                                onBackClick = { navController.popBackStack() },
                                onProductClick = { productId ->
                                    navController.navigate(Screen.Product.route.replace("{productId}", productId))
                                }
                            )
                        }

                        composable(Screen.Purchases.route) {
                            val purchasesViewModel: UserActivityViewModel = viewModel(
                                factory = UserActivityViewModel.provideFactory(userRepository, productRepository, ActivityType.PURCHASES)
                            )
                            UserActivityScreen(
                                title = "My Purchases",
                                viewModel = purchasesViewModel,
                                onBackClick = { navController.popBackStack() },
                                onProductClick = { productId ->
                                    navController.navigate(Screen.Product.route.replace("{productId}", productId))
                                }
                            )
                        }

                        composable(Screen.Add.route){

                            val addProductViewModel: AddProductViewModel = viewModel(
                                factory = AddProductViewModel.provideFactory(productRepository, userRepository)
                            )

                            AddProductScreen(
                                viewModel = addProductViewModel,
                                onBackClick = {navController.popBackStack()}
                            ) { }
                        }
                    }
                }
            }
        }
    }
}