package com.example.umkm_sekitar.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Cart : Screen("cart_screen")
    object Orders : Screen("orders_screen")
    object Profile : Screen("profile_screen")
    object Auth : Screen("auth_screen")
    object Search : Screen("search_screen")
    object Detail : Screen("detail_screen/{id}")
    object OnGoing : Screen("ongoing_screen/{storeId}/{orderId}")
    object CheckOut : Screen("checkout_screen")
    object LoadingCo : Screen("loading_screen")
}

