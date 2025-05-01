package com.example.umkm_sekitar.ui.screen.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umkm_sekitar.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val currentUser: StateFlow<FirebaseUser?> = authRepository.currentUser
    val authState: StateFlow<AuthState> = authRepository.authState

    fun signOut(context: Context) {
        viewModelScope.launch {
            authRepository.signOut(context)
        }
    }

    fun onAuthResult(resultCode: Int) {
        authRepository.onAuthResult(resultCode)
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object SignedOut : AuthState()
    data class SignedIn(val user: FirebaseUser) : AuthState()
    object SignInFailed : AuthState()
}
