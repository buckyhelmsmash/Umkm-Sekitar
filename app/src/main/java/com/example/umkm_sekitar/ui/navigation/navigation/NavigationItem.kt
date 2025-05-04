package com.example.umkm_sekitar.ui.navigation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.umkm_sekitar.ui.navigation.Screen
import com.example.umkm_sekitar.ui.screen.auth.AuthScreen
import com.example.umkm_sekitar.ui.screen.auth.AuthState
import com.example.umkm_sekitar.ui.screen.cart.CartScreen
import com.example.umkm_sekitar.ui.screen.home.HomeScreen
import com.example.umkm_sekitar.ui.screen.loading.LoadingScreen
import com.example.umkm_sekitar.ui.screen.orders.OrdersScreen
import com.example.umkm_sekitar.ui.screen.profile.ProfileScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationItem(
    navController: NavHostController,
    authState: AuthState,
) {
    NavHost(
        navController = navController,
        startDestination = if (authState is AuthState.SignedIn) Screen.Home.route else Screen.Auth.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Cart.route) {
            CartScreen(navController = navController)
        }
        composable(Screen.Orders.route) {
            OrdersScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
        composable(Screen.LoadingCo.route) {
            LoadingScreen(navController = navController)
        }
        composable(Screen.Auth.route) {
            AuthScreen(
                onSignedIn = { navController.navigate(Screen.Home.route) }
            )
        }
    }
}
