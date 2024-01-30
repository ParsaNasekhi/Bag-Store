package com.parsanasekhi.store.model.repository

object TokenInMemory {

    var token: String? = null
        private set

    var userName: String? = null
        private set

    fun refreshToken(userName: String? = null, token: String? = null) {
        this.token = token
        this.userName = userName
    }

}