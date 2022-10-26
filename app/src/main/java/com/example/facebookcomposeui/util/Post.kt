package com.example.facebookcomposeui.util

import android.net.Uri
import java.util.*

data class Post(
    val author: String,
    val authorAvatarUrl: String?,
    val timeStamp: Date,
    val text: String
    )