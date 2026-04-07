package uniandes.isis3510.rewereable.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Donate : Screen("donate")
    object Sell : Screen("sell")
    object Inbox : Screen("inbox")
    object Profile : Screen("profile")

    object Product : Screen("details/{productId}")

    object Purchases : Screen("purchases")

    object Listings : Screen("listings")

    object Favorites : Screen("favorites")

    object Login : Screen("login")

    object Register : Screen("register")

    object Seller: Screen("seller/{sellerId}")

    object Add: Screen("add")



    object CharityDetail : Screen("charity_details/{charityId}")

    object DropOffMap : Screen("dropoff_map")
}