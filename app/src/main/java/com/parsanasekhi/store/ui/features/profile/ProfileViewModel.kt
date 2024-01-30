package com.parsanasekhi.store.ui.features.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.parsanasekhi.store.model.repository.user.UserRepository

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val email = mutableStateOf("")
    val address = mutableStateOf("")
    val postalCode = mutableStateOf("")
    val loginTime = mutableStateOf("")
    val hasToShowLocationDialog = mutableStateOf(false)

    fun loadUserData() {
        email.value = userRepository.getUserName()
        address.value = userRepository.getUserLocation().first
        postalCode.value = userRepository.getUserLocation().second
        loginTime.value = userRepository.getLoginTime()
    }

    fun signOut() {
        userRepository.signOut()
    }

    fun setUserLocation(address: String, postalCode: String) {
        userRepository.saveUserLocation(address, postalCode)
    }

}