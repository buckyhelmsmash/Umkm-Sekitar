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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.umkm_sekitar.data.model.Store
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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val database = Firebase.database
    private val firebaseTest = database.getReference("test")
    private val firebaseStore = database.getReference("toko")

    private val TAG = "FIREBASE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Toast.makeText(this, "Reading Firebase Data", Toast.LENGTH_SHORT).show()
        firebaseTest.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.value
                Log.d(TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

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

                Scaffold { innerPadding ->

                    when (authState) {
                        is AuthState.SignedIn -> {
                            UserProfileScreen(
                                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 5.dp),
                                viewModel = authViewModel
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
