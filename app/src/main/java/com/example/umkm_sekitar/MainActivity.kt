package com.example.umkm_sekitar

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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

class MainActivity : ComponentActivity() {

    val database = Firebase.database
    val myRef = database.getReference("toko") // Replace "your_data_node" with where you want to store data

    private val TAG = "FIREBASE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Toast.makeText(this, "AAAAAAAAAAAa", Toast.LENGTH_SHORT).show()
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<String>()
                Log.d(TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
        setContent {
            UmkmSekitarTheme {
                val authViewModel: AuthViewModel = viewModel()
                val authState by authViewModel.authState.collectAsState()

                Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
                    when (authState) {
                        is AuthState.SignedIn -> {
                            UserProfileScreen(
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
    }
}
