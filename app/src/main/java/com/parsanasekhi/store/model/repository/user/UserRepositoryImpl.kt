package com.parsanasekhi.store.model.repository.user

import android.content.SharedPreferences
import com.google.gson.JsonObject
import com.parsanasekhi.store.model.net.ApiService
import com.parsanasekhi.store.model.repository.TokenInMemory
import com.parsanasekhi.store.util.SUCCESS_VALUE

class UserRepositoryImpl(
    private val apiService: ApiService,
    private val sharedPref: SharedPreferences
) : UserRepository {

    override suspend fun signUp(name: String, userName: String, password: String): String {

        val jsonObject = JsonObject().apply {
            addProperty("name", name)
            addProperty("email", userName)
            addProperty("password", password)
        }

        val response = apiService.signUp(jsonObject)
        return if (response.success) {
            saveToken(response.token)
            saveUserName(userName)
            saveLoginTime()
            TokenInMemory.refreshToken(userName, response.token)
            SUCCESS_VALUE
        } else {
            response.message
        }

    }

    override suspend fun signIn(userName: String, password: String): String {

        val jsonObject = JsonObject().apply {
            addProperty("email", userName)
            addProperty("password", password)
        }

        val response = apiService.signIn(jsonObject)
        return if (response.success) {
            saveToken(response.token)
            saveUserName(userName)
            saveLoginTime()
            TokenInMemory.refreshToken(userName, response.token)
            SUCCESS_VALUE
        } else {
            response.message
        }

    }

    override fun signOut() {
        sharedPref.edit().clear().apply()
        TokenInMemory.refreshToken()
    }

    override fun loadToken() {
        TokenInMemory.refreshToken(getUserName(), getToken())
    }

    override fun saveToken(newToken: String) {
        sharedPref.edit().putString("token", newToken).apply()
    }

    override fun getToken(): String {
        return sharedPref.getString("token", "")!!
    }

    override fun saveUserName(userName: String) {
        sharedPref.edit().putString("username", userName).apply()
    }

    override fun getUserName(): String {
        return sharedPref.getString("username", "")!!
    }

    override fun saveUserLocation(address: String, postalCode: String) {
        sharedPref.edit().apply {
            putString("address", address)
            putString("postalCode", postalCode)
        }.apply()
    }

    override fun getUserLocation(): Pair<String, String> {
        return Pair(
            sharedPref.getString("address", "Click to add")!!,
            sharedPref.getString("postalCode", "Click to add")!!
        )
    }

    override fun saveLoginTime() {
        sharedPref.edit().putString("loginTime", System.currentTimeMillis().toString()).apply()
    }

    override fun getLoginTime(): String {
        return sharedPref.getString("loginTime", "0")!!
    }

}