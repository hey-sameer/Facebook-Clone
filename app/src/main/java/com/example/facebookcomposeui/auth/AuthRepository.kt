package com.example.facebookcomposeui.auth

import com.example.facebookcomposeui.util.RequestState
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
suspend fun oneTapSignUpWithGoogle(): Flow<RequestState<BeginSignInResult>>
suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): Flow<RequestState<Boolean>>
suspend fun signOut(): Flow<RequestState<Boolean>>
suspend fun revokeAccess(): Flow<RequestState<Boolean>>
}