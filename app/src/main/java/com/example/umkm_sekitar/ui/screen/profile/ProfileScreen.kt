package com.example.umkm_sekitar.ui.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.umkm_sekitar.ui.screen.auth.AuthViewModel

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        currentUser?.let { user ->
            Text(text = "Welcome, ${user.displayName ?: "User"}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Email: ${user.email ?: "Not available"}")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.signOut(context) }) {
                Text(text = "Sign Out")
            }
        }
    }
}
