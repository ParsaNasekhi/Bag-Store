package com.parsanasekhi.store.ui.features.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.parsanasekhi.store.R
import com.parsanasekhi.store.ui.theme.MainAppTheme
import com.parsanasekhi.store.util.MyScreens
import dev.burnoo.cokoin.navigation.getNavController

@Composable
fun IntroScreen() {

    val nav = getNavController()

    Image(
        painter = painterResource(id = R.drawable.img_intro),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.74f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {

        Button(
            onClick = { nav.navigate(MyScreens.SignUpScreen.route) },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text(text = "Sign Up", fontSize = 16.sp)
        }

        Button(
            onClick = { nav.navigate(MyScreens.SignInScreen.route) },
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = Color.DarkGray
            )
        ) {
            Text(text = "Sign In", fontSize = 16.sp)
        }

    }

}

@Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    MainAppTheme {

        IntroScreen()

    }
}