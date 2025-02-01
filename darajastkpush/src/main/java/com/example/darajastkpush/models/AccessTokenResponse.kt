package com.example.darajastkpush.models

import com.google.gson.annotations.SerializedName

data class AccessTokenResponse(
    val access_token: String,
    val expires_in: String
)