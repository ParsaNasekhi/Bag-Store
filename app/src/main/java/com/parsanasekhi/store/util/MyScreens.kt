package com.parsanasekhi.store.util

sealed class MyScreens(val route: String) {

    object MainScreen : MyScreens("mainScreen")
    object IntroScreen : MyScreens("introScreen")
    object ProductScreen : MyScreens("productScreen")
    object CategoryScreen : MyScreens("categoryScreen")
    object CartScreen : MyScreens("cartScreen")
    object ProfileScreen : MyScreens("profileScreen")
    object SignUpScreen : MyScreens("signUpScreen")
    object SignInScreen : MyScreens("signInScreen")
    object NoInternetScreen : MyScreens("noInternetScreen")

}
