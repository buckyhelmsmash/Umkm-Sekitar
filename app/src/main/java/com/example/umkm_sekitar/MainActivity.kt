package com.example.umkm_sekitar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.umkm_sekitar.ui.component.BottomNavigationBar
import com.example.umkm_sekitar.ui.component.Screen
import com.example.umkm_sekitar.ui.screen.auth.AuthScreen
import com.example.umkm_sekitar.ui.screen.auth.AuthState
import com.example.umkm_sekitar.ui.screen.cart.CartScreen
import com.example.umkm_sekitar.ui.screen.home.HomeScreen
import com.example.umkm_sekitar.ui.screen.orders.OrdersScreen
import com.example.umkm_sekitar.ui.screen.profile.ProfileScreen
import com.example.umkm_sekitar.ui.theme.UmkmSekitarTheme
import com.example.umkm_sekitar.util.getAuthState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        enableEdgeToEdge()

        setContent {
            UmkmSekitarTheme {
                val authState = getAuthState()

                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        if (authState is AuthState.SignedIn) {
                            BottomNavigationBar(navController)
                        }
                    }
                ) { innerPadding ->

                    when (authState) {
                        is AuthState.SignedIn -> {

                            val graph =
                                navController.createGraph(startDestination = Screen.Home.route) {
                                    composable(route = Screen.Home.route) {
                                        HomeScreen()
                                    }
                                    composable(route = Screen.Cart.route) {
                                        CartScreen()
                                    }
                                    composable(route = Screen.Orders.route) {
                                        OrdersScreen()
                                    }
                                    composable(route = Screen.Profile.route) {
                                        ProfileScreen()
                                    }
                                }
                            NavHost(
                                navController = navController,
                                graph = graph,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        else -> {
                            AuthScreen(
                                onSignedIn = { /* Navigate to main content */ }
                            )
                        }
                    }
                }
            }
        }
    }
}
