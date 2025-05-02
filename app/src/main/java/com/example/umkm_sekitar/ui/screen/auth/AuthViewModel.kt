package com.example.umkm_sekitar.ui.screen.auth

import android.app.Activity
import android.content.Context
import android.util.Log
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
        logUserData(user)
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
        logUserData(user)
        if (resultCode == Activity.RESULT_OK && user != null) {
            _authState.value = AuthState.SignedIn(user)
            _currentUser.value = user
        } else {
            _authState.value = AuthState.SignInFailed
        }
    }

    private fun logUserData (user: FirebaseUser?) {
        if(user != null){
            Log.d("UserData", "User ID: ${user.uid}")
            Log.d("UserData", "Display Name: ${user.displayName}")
            Log.d("UserData", "Email: ${user.email}")
            Log.d("UserData", "Phone Number: ${user.phoneNumber}")
            Log.d("UserData", "Photo URL: ${user.photoUrl}")
            Log.d("UserData", "Is Email Verified: ${user.isEmailVerified}")
            Log.d("UserData", "Provider ID: ${user.providerId}")

            val providers = user.providerData.joinToString(", ") {
                "${it.providerId} (${it.uid})"
            }
            Log.d("UserData", "Providers: $providers")
        } else {
            Log.e("UserData", "UserData not found")
        }
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object SignedOut : AuthState()
    data class SignedIn(val user: FirebaseUser) : AuthState()
    object SignInFailed : AuthState()
}
