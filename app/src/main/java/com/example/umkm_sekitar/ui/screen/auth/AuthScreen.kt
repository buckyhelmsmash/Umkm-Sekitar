package com.example.umkm_sekitar.ui.screen.auth

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.umkm_sekitar.R
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
        Log.d("AUTH_RESULT", "AUTH_RESULT CODE: $result.")
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
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.group_7),
            contentDescription = "Logo"
        )
        Text(
            text = "Yuk, Gabung Bersama di Tokodekat!",
            fontSize = 30.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = onSignInClick,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .border(1.dp, Color.Black, RoundedCornerShape(10.dp))
                .fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painterResource(id = R.drawable.google_icons),
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Masuk menggunakan Google")
            }
        }
    }
}

@Preview
@Composable
fun AuthScreenPreview() {
    AuthScreen(onSignedIn = {})
}

@Preview
@Composable
fun SignInContentPreview() {
    SignInContent(onSignInClick = {})
}


