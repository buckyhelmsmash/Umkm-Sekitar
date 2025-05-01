package com.example.umkm_sekitar.ui.screen.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.umkm_sekitar.util.getAuthState
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthScreen(
    onSignedIn: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState = getAuthState()

    val signInLauncher = rememberLauncherForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        viewModel.onAuthResult(result.resultCode)
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


