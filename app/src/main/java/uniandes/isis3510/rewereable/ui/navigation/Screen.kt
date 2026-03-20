package uniandes.isis3510.rewereable.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Donate : Screen("donate")
    object Sell : Screen("sell")
    object Inbox : Screen("inbox")
    object Profile : Screen("profile")

    object Product : Screen("details/{productId}")

    object CharityDetail : Screen("charity_details/{charityId}")
}