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
import com.example.umkm_sekitar.data.model.Toko
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
    val myRef = database.getReference("test")

    private val TAG = "FIREBASE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Toast.makeText(this, "Reading Firebase Data", Toast.LENGTH_SHORT).show()
        myRef.addValueEventListener(object : ValueEventListener {
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
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This will convert the data to a list of Toko objects
//                val tokoList = mutableListOf<Toko>()
//
//                for (tokoSnapshot in dataSnapshot.children) {
//                    val toko = tokoSnapshot.getValue(Toko::class.java)
//                    toko?.let {
//                        tokoList.add(it)
//                        // Log each toko
//                        Log.d(TAG, "Toko: ${it.nama_toko}, Location: ${it.lokasi}")
//                        Log.d(TAG, "Categories: ${it.kategori_toko.joinToString()}")
//                        Log.d(TAG, "Number of items: ${it.list_barang.size}")
//
//                        // Log some items from each toko
//                        it.list_barang.take(2).forEach { barang ->
//                            Log.d(TAG, "  Item: ${barang.nama}, Price: ${barang.harga}, Stock: ${barang.stok}")
//                        }
//                        Log.d(TAG, "------------------------")
//                    }
//                }
//
//                Log.d(TAG, "Total number of toko: ${tokoList.size}")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException())
//            }
//        })

        setContent {
            UmkmSekitarTheme {
                val authViewModel: AuthViewModel = viewModel()
                val authState = authViewModel.authState.collectAsState().value

                Scaffold(modifier = Modifier.fillMaxSize()) {
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
    }}
