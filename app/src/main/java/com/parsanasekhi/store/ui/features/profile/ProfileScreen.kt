package com.parsanasekhi.store.ui.features.profile

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.parsanasekhi.store.R
import com.parsanasekhi.store.ui.theme.MainAppTheme
import com.parsanasekhi.store.ui.theme.myShapes
import com.parsanasekhi.store.util.MyScreens
import com.parsanasekhi.store.util.styleTime
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.viewmodel.getViewModel

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MainAppTheme {
        ProfileScreen()
    }
}

@Composable
fun ProfileScreen() {

    val context = LocalContext.current
    val viewModel = getViewModel<ProfileViewModel>()
    viewModel.loadUserData()
    val nav = getNavController()

    Box {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ProfileToolbar {
                nav.popBackStack()
            }

            ShowAnimation()

            DataSection("Email Address", viewModel.email.value, null)
            DataSection("Address", viewModel.address.value) {
                viewModel.hasToShowLocationDialog.value = true
            }
            DataSection(
                "Postal Code",
                viewModel.postalCode.value
            ) { viewModel.hasToShowLocationDialog.value = true }
            DataSection("Login Time", styleTime(viewModel.loginTime.value.toLong()), null)

            SignOutButton {
                Toast.makeText(context, "Hope to see you again :)", Toast.LENGTH_SHORT).show()
                viewModel.signOut()
                nav.navigate(MyScreens.IntroScreen.route) {
                    popUpTo(MyScreens.IntroScreen.route) {
                        inclusive = true
                    }
                    nav.popBackStack()
                    nav.popBackStack()
                }
            }

        }

        if (viewModel.hasToShowLocationDialog.value) {
            AddUserLocationDataDialog(
                showSaveLocation = false,
                onDismiss = { viewModel.hasToShowLocationDialog.value = false },
                onSubmitClicked = { address, postalCode, _ ->
                    viewModel.setUserLocation(address, postalCode)
                }
            )
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileToolbar(onBackIconClicked: () -> Unit) {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp),
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 42.dp),
                text = "My Profile",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackIconClicked) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
fun ShowAnimation() {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.profile_anim))
    LottieAnimation(
        modifier = Modifier
            .size(256.dp)
            .padding(vertical = 16.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}


@Composable
fun DataSection(subject: String, textToShow: String, OnLocationClicked: (() -> Unit)?) {

    Column(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .clickable { OnLocationClicked?.invoke() },
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = subject,
            style = TextStyle(
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        )

        Text(
            text = textToShow,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(top = 2.dp)
        )

        Divider(
            color = MaterialTheme.colorScheme.primary,
            thickness = 0.5.dp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }

}

@Composable
fun SignOutButton(signOutEvent: () -> Unit) {
    Button(
        onClick = {
            signOutEvent()
        },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(top = 36.dp, bottom = 16.dp)
    ) {
        Text(text = "Sign Out")
    }
}


@Composable
fun AddUserLocationDataDialog(
    showSaveLocation: Boolean,
    onDismiss: () -> Unit,
    onSubmitClicked: (String, String, Boolean) -> Unit
) {

    val context = LocalContext.current
    val checkedState = remember { mutableStateOf(true) }
    val userAddress = remember { mutableStateOf("") }
    val userPostalCode = remember { mutableStateOf("") }
    val fraction = if (showSaveLocation) 0.625f else 0.550f

    Dialog(onDismissRequest = onDismiss) {

        Card(
            modifier = Modifier.fillMaxHeight(fraction),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = myShapes.medium,
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {

                Text(
                    text = "Add location data",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = userAddress.value,
                    onValueChange = { userAddress.value = it },
                    label = {
                        Text(text = "Address")
                    },
                    placeholder = {
                        Text(text = "Address")
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = userPostalCode.value,
                    onValueChange = { userPostalCode.value = it },
                    label = {
                        Text(text = "Postal code")
                    },
                    placeholder = {
                        Text(text = "Postal code")
                    },
                    singleLine = true
                )

                if (showSaveLocation) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, start = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checkedState.value,
                            onCheckedChange = { checkedState.value = it },
                        )
                        Text(text = "Save to profile")
                    }

                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TextButton(onClick = onDismiss) {
                        Text(text = "Cancel")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(onClick = {

                        if (
                            (userAddress.value.isNotEmpty() || userAddress.value.isNotBlank()) &&
                            (userPostalCode.value.isNotEmpty() || userPostalCode.value.isNotBlank())
                        ) {
                            onSubmitClicked(
                                userAddress.value,
                                userPostalCode.value,
                                checkedState.value
                            )
                            onDismiss.invoke()
                        } else {
                            Toast.makeText(
                                context,
                                "Please fill in all fields.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }) {
                        Text(text = "Ok")
                    }
                }
            }
        }
    }
}
