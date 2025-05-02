package com.example.umkm_sekitar.ui.screen.auth

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthScreen(
    onSignedIn: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()

    val signInLauncher = rememberLauncherForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        Log.d("AUTH_RESULT", "AUTH_RESULT CODE: $result")
        val user = FirebaseAuth.getInstance().currentUser
        viewModel.onAuthResult(result.resultCode, user)
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.SignedIn) {
            onSignedIn()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (authState) {
            is AuthState.Initial -> {
                CircularProgressIndicator()
            }
            is AuthState.SignedOut, is AuthState.SignInFailed -> {
                SignInContent(
                    onSignInClick = {
                        val providers = listOf(
                            AuthUI.IdpConfig.GoogleBuilder().build(),
                        )

                        val signInIntent = AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .build()

                        signInLauncher.launch(signInIntent)
                    }
                )
            }
            is AuthState.SignedIn -> {
                // This will be handled by the LaunchedEffect
            }
        }
    }
}

@Composable
fun SignInContent(onSignInClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome to UMKM Sekitar")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSignInClick) {
            Text(text = "Sign In")
        }
    }
}


