package com.example.umkm_sekitar.ui.screen.auth

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        val user = auth.currentUser
        _currentUser.value = user
        _authState.value = if (user != null) {
            AuthState.SignedIn(user)
        } else {
            AuthState.SignedOut
        }
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener {
                    _authState.value = AuthState.SignedOut
                    _currentUser.value = null
                }
        }
    }

    fun onAuthResult(resultCode: Int, user: FirebaseUser?) {
        if (resultCode == Activity.RESULT_OK && user != null) {
            _authState.value = AuthState.SignedIn(user)
            _currentUser.value = user
        } else {
            _authState.value = AuthState.SignInFailed
        }
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object SignedOut : AuthState()
    data class SignedIn(val user: FirebaseUser) : AuthState()
    object SignInFailed : AuthState()
}
