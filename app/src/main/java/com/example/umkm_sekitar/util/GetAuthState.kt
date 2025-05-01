package com.example.umkm_sekitar.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.umkm_sekitar.ui.screen.auth.AuthState
import com.example.umkm_sekitar.ui.screen.auth.AuthViewModel

@Composable
fun getAuthState(viewModel: AuthViewModel = hiltViewModel()): AuthState {
    val authState by viewModel.authState.collectAsState()
    return authState
}
