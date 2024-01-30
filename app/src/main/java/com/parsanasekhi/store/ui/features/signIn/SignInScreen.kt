package com.parsanasekhi.store.ui.features.signIn

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.parsanasekhi.store.R
import com.parsanasekhi.store.ui.theme.BackgroundMain
import com.parsanasekhi.store.ui.theme.MainAppTheme
import com.parsanasekhi.store.ui.theme.myShapes
import com.parsanasekhi.store.util.MyScreens
import com.parsanasekhi.store.util.NetworkChecker
import com.parsanasekhi.store.util.SUCCESS_VALUE
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.viewmodel.getViewModel

@Composable
fun SignInScreen() {

    val viewModel = getViewModel<SignInViewModel>()
    val nav = getNavController()
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundMain) {

        Box {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f),
                color = MaterialTheme.colorScheme.primary
            ) {}
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppIcon()
            Spacer(modifier = Modifier.padding(16.dp))
            MainCard(viewModel, nav) {
                viewModel.signInUser {
                    if (it == SUCCESS_VALUE) {
                        nav.navigate(MyScreens.MainScreen.route) {
                            popUpTo(MyScreens.IntroScreen.route) {
                                inclusive = true
                            }
                        }
                    } else {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            Spacer(modifier = Modifier.padding(52.dp))
        }

    }

}

@Composable
fun MainCard(viewModel: SignInViewModel, nav: NavController, signInEvent: () -> Unit) {

    val email = viewModel.email.observeAsState("")
    val password = viewModel.password.observeAsState("")
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = myShapes.medium,
        colors = CardDefaults.cardColors(Color.White)
    ) {

        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Sign In",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 18.dp)
            )

            MainTextField(
                hint = "Email",
                edtValue = email.value,
                icon = R.drawable.ic_email,
                valueChanged = { viewModel.email.value = it }
            )

            PasswordTextField(
                hint = "Password",
                edtValue = password.value,
                icon = R.drawable.ic_password,
                valueChanged = { viewModel.password.value = it }
            )

            Button(
                onClick = {

                    if (email.value.isNotEmpty() && password.value.isNotEmpty()
                    ) {
                        if (Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                            if (NetworkChecker(context).isInternetConnected) {

                                signInEvent()

                            } else {
                                Toast.makeText(
                                    context,
                                    "Please check your internet connection.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            Toast.makeText(
                                context,
                                "Email format is not correct.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(context, "Please fill in all the boxes.", Toast.LENGTH_SHORT)
                            .show()
                    }

                }, shape = myShapes.medium,
                modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
            ) {
                Text(text = "Log In", modifier = Modifier.padding(6.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 18.dp)
            ) {

                Text(text = "Don't have an account?")

                TextButton(onClick = {
                    nav.navigate(route = MyScreens.SignUpScreen.route) {
                        popUpTo(route = MyScreens.SignInScreen.route) {
                            inclusive = true
                        }
                    }
                }) {
                    Text(text = "Register Here")
                }

            }


        }

    }


}

@Composable
fun AppIcon() {

    Surface(
        modifier = Modifier
            .clip(CircleShape)
            .size(64.dp)
    ) {

        Image(
            modifier = Modifier.padding(14.dp),
            painter = painterResource(id = R.drawable.ic_icon_app),
            contentDescription = null
        )

    }

}

@Composable
fun MainTextField(hint: String, edtValue: String, icon: Int, valueChanged: (String) -> Unit) {

    OutlinedTextField(
        value = edtValue,
        onValueChange = valueChanged,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 12.dp),
        leadingIcon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null
            )
        },
        placeholder = {
            Text(text = hint)
        },
        label = { Text(text = hint) },
        singleLine = true,
        shape = myShapes.medium,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )

}

@Composable
fun PasswordTextField(hint: String, edtValue: String, icon: Int, valueChanged: (String) -> Unit) {

    val passwordVisibility = remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        value = edtValue,
        onValueChange = valueChanged,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 12.dp),
        leadingIcon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null
            )
        },
        placeholder = {
            Text(text = hint)
        },
        label = { Text(text = hint) },
        singleLine = true,
        shape = myShapes.medium,
        trailingIcon = {
            val image = if (passwordVisibility.value) painterResource(id = R.drawable.ic_visible)
            else painterResource(id = R.drawable.ic_invisible)
            Icon(
                painter = image,
                contentDescription = null,
                modifier = Modifier.clickable {
                    passwordVisibility.value = !passwordVisibility.value
                }
            )
        },
        visualTransformation =
        if (passwordVisibility.value) VisualTransformation.None
        else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )

}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    MainAppTheme {

        SignInScreen()

    }
}