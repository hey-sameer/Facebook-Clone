package com.example.facebookcomposeui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facebookcomposeui.auth.AuthRepository
import com.example.facebookcomposeui.util.RequestState
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    val oneTapClient: SignInClient,
    val auth: FirebaseAuth,
): ViewModel() {
    var signInResponse = MutableStateFlow<RequestState<Boolean>>(RequestState.Idle)
        private set
    var oneTapSignUpResponse = MutableStateFlow<RequestState<BeginSignInResult>>(RequestState.Idle)
        private set
    fun oneTapSignInInitiate(){
        oneTapSignUpResponse.value = RequestState.Loading
        viewModelScope.launch {
            authRepo.oneTapSignUpWithGoogle().collect {
                oneTapSignUpResponse.value = it
            }
        }
    }

    fun signInWithGoogleCredentials(credential: AuthCredential) = viewModelScope.launch(Dispatchers.IO){
        signInResponse.value = RequestState.Idle
        try {
            println("creds: $credential")
            authRepo.firebaseSignInWithGoogle(credential).collect{
                signInResponse.value = it
            }
        }catch (e: Exception){
            signInResponse.value = RequestState.Error(e)
            println(e)
        }
    }
}