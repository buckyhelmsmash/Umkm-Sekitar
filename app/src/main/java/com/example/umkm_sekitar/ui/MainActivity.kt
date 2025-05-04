package com.example.umkm_sekitar.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.umkm_sekitar.ui.component.BottomNavigationBar
import com.example.umkm_sekitar.ui.navigation.navigation.NavigationItem
import com.example.umkm_sekitar.ui.navigation.Screen
import com.example.umkm_sekitar.ui.screen.auth.AuthState
import com.example.umkm_sekitar.ui.theme.UmkmSekitarTheme
import com.example.umkm_sekitar.util.getAuthState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        enableEdgeToEdge(

        )

        setContent {
            UmkmSekitarTheme {
                val authState = getAuthState()

                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStackEntry?.destination?.route
                val bottomBarRoutes = listOf(
                    Screen.Home.route,
                    Screen.Cart.route,
                    Screen.Orders.route,
                    Screen.Profile.route
                )


                @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
                Scaffold(
                    bottomBar = {
                        if (authState is AuthState.SignedIn && currentDestination in bottomBarRoutes) {
                            BottomNavigationBar(navController)
                        }
                    }
                ) {  innerPadding ->
                    NavigationItem(
                        navController = navController,
                        authState = authState,
                    )
                }
            }
        }
    }
}
