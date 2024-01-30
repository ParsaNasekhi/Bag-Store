package com.parsanasekhi.store.model.data

data class SignInResponse(

    val success: Boolean,
    val message: String,
    val token: String,
    val expiresAt: Int

)
