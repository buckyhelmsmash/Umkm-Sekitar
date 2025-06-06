package com.example.umkm_sekitar.ui.navigation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.umkm_sekitar.ui.navigation.Screen
import com.example.umkm_sekitar.ui.screen.auth.AuthScreen
import com.example.umkm_sekitar.ui.screen.auth.AuthState
import com.example.umkm_sekitar.ui.screen.cart.CartScreen
import com.example.umkm_sekitar.ui.screen.checkout.CheckOutScreen
import com.example.umkm_sekitar.ui.screen.detail.DetailScreen
import com.example.umkm_sekitar.ui.screen.home.HomeScreen
import com.example.umkm_sekitar.ui.screen.loading.LoadingScreen
import com.example.umkm_sekitar.ui.screen.ongoing.OnGoingScreen
import com.example.umkm_sekitar.ui.screen.orders.OrdersScreen
import com.example.umkm_sekitar.ui.screen.profile.ProfileScreen
import com.example.umkm_sekitar.ui.screen.search.SearchScreen

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
        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }
        composable(Screen.CheckOut.route) {
            CheckOutScreen(navController = navController)
        }
        composable(Screen.LoadingCo.route) {
            LoadingScreen(navController = navController)
        }
        composable(Screen.Auth.route) {
            AuthScreen(
                onSignedIn = { navController.navigate(Screen.Home.route) }
            )
        }

        composable(
            "${Screen.Detail.route}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            DetailScreen(storeId = id ?: "", navController = navController)
        }

        composable(
            "${Screen.OnGoing.route}/{storeId}/{orderId}",
            arguments = listOf(
                navArgument("storeId") { type = NavType.StringType },
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId")
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OnGoingScreen(storeId = storeId ?: "", orderId = orderId, navController = navController)
        }
    }
}
