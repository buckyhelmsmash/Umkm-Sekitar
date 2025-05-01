package com.example.umkm_sekitar

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.umkm_sekitar.data.model.Store
import com.example.umkm_sekitar.ui.component.BottomNavigationBar
import com.example.umkm_sekitar.ui.screen.auth.AuthScreen
import com.example.umkm_sekitar.ui.screen.auth.AuthState
import com.example.umkm_sekitar.ui.screen.auth.AuthViewModel
import com.example.umkm_sekitar.ui.screen.auth.UserProfileScreen
import com.example.umkm_sekitar.ui.theme.UmkmSekitarTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.composable
import com.example.umkm_sekitar.ui.component.Screen
import com.example.umkm_sekitar.ui.screen.cart.CartScreen
import com.example.umkm_sekitar.ui.screen.home.HomeScreen
import com.example.umkm_sekitar.ui.screen.orders.OrdersScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val database = Firebase.database
    private val firebaseStore = database.getReference("store")

    private val TAG = "FIREBASE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        enableEdgeToEdge()

        Toast.makeText(this, "Reading Firebase Data", Toast.LENGTH_SHORT).show()

        firebaseStore.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This will convert the data to a list of Store objects
                val tokoList = mutableListOf<Store>()

                for (tokoSnapshot in dataSnapshot.children) {
                    val toko = tokoSnapshot.getValue(Store::class.java)
                    toko?.let {
                        tokoList.add(it)
                        // Log each toko
                        Log.d(TAG, "Store: ${it.storeName}, Location: ${it.location}")
                        Log.d(TAG, "Categories: ${it.category.joinToString()}")
                        Log.d(TAG, "Number of items: ${it.productList.size}")

                        // Log some items from each toko
                        it.productList.take(2).forEach { product ->
                            Log.d(TAG, "  Item: ${product.productName}, Price: ${product.price}, Stock: ${product.stock}")
                        }
                        Log.d(TAG, "------------------------")
                    }
                }

                Log.d(TAG, "Total number of toko: ${tokoList.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

        setContent {
            UmkmSekitarTheme {
                val authViewModel: AuthViewModel = viewModel()
                val authState = authViewModel.authState.collectAsState().value

                val navController = rememberNavController()

                Scaffold (
                    bottomBar = {
                        if (authState is AuthState.SignedIn) {
                            BottomNavigationBar(navController)
                        }
                    }
                ){ innerPadding ->

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
                                        UserProfileScreen(
                                            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 5.dp),
                                            viewModel = authViewModel
                                        )
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
                                onSignedIn = { /* Navigate to main content */ },
                                viewModel = authViewModel
                            )
                        }
                    }
                }
            }
        }
    }}
