package com.example.facebookcomposeui

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facebookcomposeui.util.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

sealed class HomeScreenState {
    object Loading : HomeScreenState()
    data class Loaded(
        val avatarUrl: String,
        val posts: List<Post>
    ) : HomeScreenState()

    object SignInRequired : HomeScreenState()
}

class HomeScreenViewModel : ViewModel() {
    companion object {
        const val AVATAR_TAG = "photourl"
    }
    private val firebaseAuth: FirebaseAuth = Firebase.auth

    var hsState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
        private set

    init {


        if (firebaseAuth.currentUser == null) {
            viewModelScope.launch {
                hsState.emit(HomeScreenState.SignInRequired)
            }
        } else {
            viewModelScope.launch {
//                updateProfile()
                observePost()

            }

        }
    }

    private fun updateProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val request = UserProfileChangeRequest.Builder()
                .setPhotoUri("https://pbs.twimg.com/media/EWI5rXdWkAARQOX?format=jpg&name=4096x4096".toUri())
                .build()
            println("update sending")
            val updateRequest = firebaseAuth.currentUser!!.updateProfile(request)

            updateRequest.await()
            if(updateRequest.isSuccessful)
                println("updateProfile: Successful ${updateRequest.result}")
            else println("updateProfile: Failed ${updateRequest.exception?.localizedMessage}")

        }

    }

    private suspend fun observePost() {
        observePostFlow()
            .map {
                HomeScreenState.Loaded(avatarUrl = getAvatar().toString(), posts = it)
            }
            .collect { state ->
                hsState.emit(state)
            }
    }

    private suspend fun observePostFlow(): Flow<List<Post>> {
        return callbackFlow<List<Post>> {
            val listener =
                Firebase.firestore.collection("post").addSnapshotListener { value, error ->
                    if (error != null) {
                        close(error)
                    } else if (value != null) {
                        val posts = value.map { snapshot ->
                            Post(
                                author = snapshot.getString("author") ?: "My User",
                                authorAvatarUrl = snapshot.getString("avatarUrl"),
                                timeStamp = snapshot.getDate("timestamp") ?: Date(),
                                text = snapshot.getString("text").orEmpty()
                            )
                        }.sortedByDescending { it.timeStamp }
                        trySend(posts)
                    }
                }
            awaitClose {
                listener.remove()
            }
        }
    }


    private fun getAvatar(): Uri {

        val url = firebaseAuth.currentUser!!.photoUrl!!
        println(url)
        return url
    }

    fun statusUpdate(status: String?) {
        if (status.isNullOrEmpty())
            return
        viewModelScope.launch(Dispatchers.IO) {
            val result = Firebase.firestore.collection("post").add(
                hashMapOf(
                    "author" to firebaseAuth.currentUser!!.displayName,
                    "text" to status,
                    "timestamp" to Date(),
                    "avatarUrl" to getAvatar().toString()
                )
            )
            result.await()
            if (result.isSuccessful) {
                println("vohoo status update successful")
            }
            if(result.exception != null){
                println("some exception ${result.exception}")
            }
        }
    }

}
