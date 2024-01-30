package com.parsanasekhi.store.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.parsanasekhi.store.di.myModules
import com.parsanasekhi.store.model.repository.TokenInMemory
import com.parsanasekhi.store.model.repository.user.UserRepository
import com.parsanasekhi.store.ui.features.cart.CartScreen
import com.parsanasekhi.store.ui.features.category.CategoryScreen
import com.parsanasekhi.store.ui.features.intro.IntroScreen
import com.parsanasekhi.store.ui.features.product.ProductScreen
import com.parsanasekhi.store.ui.features.profile.ProfileScreen
import com.parsanasekhi.store.ui.features.main.MainScreen
import com.parsanasekhi.store.ui.features.signIn.SignInScreen
import com.parsanasekhi.store.ui.features.signUp.SignUpScreen
import com.parsanasekhi.store.ui.theme.BackgroundMain
import com.parsanasekhi.store.ui.theme.MainAppTheme
import com.parsanasekhi.store.util.KEY_CATEGORY_ARG
import com.parsanasekhi.store.util.KEY_PRODUCT_ARG
import com.parsanasekhi.store.util.MyScreens
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import dev.burnoo.cokoin.navigation.KoinNavHost
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Koin(appDeclaration = {
                androidContext(this@MainActivity)
                modules(myModules)
            }) {
                MainAppTheme {
                    Surface(
                        color = BackgroundMain,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val userRepository: UserRepository = get()
                        userRepository.loadToken()
                        StoreUi()
                    }
                }
            }
        }

    }
}

@Composable
fun StoreUi() {

    Surface(
        color = BackgroundMain,
        modifier = Modifier.fillMaxSize()
    ) {

    }

    val navController = rememberNavController()

    KoinNavHost(navController = navController, startDestination = MyScreens.IntroScreen.route) {

        composable(MyScreens.MainScreen.route) {
                MainScreen()
        }

        composable(
            route = MyScreens.CategoryScreen.route + "/{$KEY_CATEGORY_ARG}",
            arguments = listOf(navArgument(name = KEY_CATEGORY_ARG) {
                type = NavType.StringType
            })
        ) {
            CategoryScreen(it.arguments!!.getString(KEY_CATEGORY_ARG, null))
        }

        composable(
            route = MyScreens.ProductScreen.route + "/{$KEY_PRODUCT_ARG}",
            arguments = listOf(navArgument(KEY_PRODUCT_ARG) {
                type = NavType.StringType
            })
        ) {
            ProductScreen(it.arguments!!.getString(KEY_PRODUCT_ARG, null))
        }

        composable(route = MyScreens.IntroScreen.route) {
            if (TokenInMemory.token != null && TokenInMemory.token != "") {
                MainScreen()
            } else IntroScreen()
        }

        composable(route = MyScreens.ProfileScreen.route) {
            ProfileScreen()
        }

        composable(route = MyScreens.CartScreen.route) {
            CartScreen()
        }

        composable(route = MyScreens.SignInScreen.route) {
            SignInScreen()
        }

        composable(route = MyScreens.SignUpScreen.route) {
            SignUpScreen()
        }

    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MainAppTheme {

    }
}