package com.parsanasekhi.store.ui.features.cart

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.parsanasekhi.store.R
import com.parsanasekhi.store.model.data.ProductCart
import com.parsanasekhi.store.ui.features.profile.AddUserLocationDataDialog
import com.parsanasekhi.store.ui.theme.CardViewBackground
import com.parsanasekhi.store.ui.theme.MainAppTheme
import com.parsanasekhi.store.ui.theme.Transparent
import com.parsanasekhi.store.ui.theme.myShapes
import com.parsanasekhi.store.util.MyScreens
import com.parsanasekhi.store.util.NetworkChecker
import com.parsanasekhi.store.util.PAYMENT_PENDING
import com.parsanasekhi.store.util.stylePrice
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.viewmodel.getViewModel

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    MainAppTheme {
        CartScreen()
    }
}

@Composable
fun CartScreen() {

    val context = LocalContext.current
    val viewModel = getViewModel<CartViewModel>()
    viewModel.loadCartData()
    val nav = getNavController()
    val dialogState = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        CartToolBar(
            onBackIconClicked = {
                nav.popBackStack()
            },
            onPersonIconClicked = {
                nav.navigate(MyScreens.ProfileScreen.route)
            }
        )

        if (viewModel.products.value.isNotEmpty()) {

            CartItemsList(
                products = viewModel.products.value,
                onItemClicked = {
                    nav.navigate("${MyScreens.ProductScreen.route}/$it")
                },
                addToCartEvent = {
                    if (NetworkChecker(context).isInternetConnected) {
                        viewModel.addToCart(it)
                    } else {
                        Toast.makeText(
                            context,
                            "Please check your internet connection.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                removeFromCartEvent = {
                    if (NetworkChecker(context).isInternetConnected) {
                        viewModel.removeFromCart(it)
                    } else {
                        Toast.makeText(
                            context,
                            "Please check your internet connection.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )

        } else {

            ShowEmptyCartAnimation()

        }

    }

    if (viewModel.products.value.isNotEmpty()) {

        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                Purchase(totalPrice = viewModel.totalPrice.value, isTransparent = false) {
                    if (NetworkChecker(context).isInternetConnected) {

                        val locationData = viewModel.getUserLocation()
                        if (locationData.first == "Click to add" || locationData.second == "Click to add") {
                            dialogState.value = true
                        } else {

                            viewModel.purchaseAll(
                                locationData.first,
                                locationData.second
                            ) { success, link ->

                                if (success) {

                                    Toast.makeText(
                                        context,
                                        "Pay using ZarinPal.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    viewModel.setPaymentStatus(PAYMENT_PENDING)

                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                    context.startActivity(intent)

                                } else {
                                    Toast.makeText(
                                        context,
                                        "Problem in payment",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }

                        }

                    } else {
                        Toast.makeText(
                            context,
                            "Please check your internet connection.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }

    if (dialogState.value) {

        AddUserLocationDataDialog(
            showSaveLocation = true,
            onDismiss = { dialogState.value = false },
            onSubmitClicked = { address, postalCode, isChecked ->
                if (NetworkChecker(context).isInternetConnected) {
                    if (isChecked) {
                        viewModel.setUserLocation(address, postalCode)
                    }
                    viewModel.purchaseAll(address, postalCode) { success, link ->
                        if (success) {
                            Toast.makeText(context, "Pay using ZarinPal", Toast.LENGTH_SHORT).show()
                            viewModel.setPaymentStatus(PAYMENT_PENDING)
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "Problem in payment", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Please check your internet connection.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartToolBar(onBackIconClicked: () -> Unit, onPersonIconClicked: () -> Unit) {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp),
        title = {
            Text(
                modifier = Modifier
                    .padding(start = 8.dp),
                text = "My Cart",
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackIconClicked) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            IconButton(
                modifier = Modifier.wrapContentSize(),
                onClick = onPersonIconClicked
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            }
        }
    )

}


@Composable
fun CartItemsList(
    products: List<ProductCart>,
    onItemClicked: (String) -> Unit,
    addToCartEvent: (String) -> Unit,
    removeFromCartEvent: (String) -> Unit
) {

    LazyColumn(contentPadding = PaddingValues(top = 16.dp)) {

        items(count = products.size) {
            CartItem(
                imageUrl = products[it].imgUrl,
                title = products[it].name,
                category = products[it].category,
                price = products[it].price,
                id = products[it].productId,
                quantity = products[it].quantity ?: "0",
                onItemClicked = onItemClicked,
                addToCartEvent = addToCartEvent,
                removeFromCartEvent = removeFromCartEvent
            )
            if (products.size - 1 == it)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Transparent,
                        contentColor = Transparent
                    )
                ) {
                    Purchase(totalPrice = "000000", isTransparent = true) {}
                }
        }

    }

}

@Composable
fun CartItem(
    imageUrl: String,
    title: String,
    category: String,
    price: String,
    id: String,
    quantity: String,
    onItemClicked: (String) -> Unit,
    addToCartEvent: (String) -> Unit,
    removeFromCartEvent: (String) -> Unit
) {

    Card(
        modifier = Modifier
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            .clickable { onItemClicked(id) },
        shape = myShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {

                Column {

                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "From $category group",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp
                    )

                    Surface(
                        color = CardViewBackground, shape = myShapes.medium,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(12.dp),
                            text = stylePrice((price.toInt() * quantity.toInt()).toString()),
                            color = Color.DarkGray,
                            fontSize = 12.sp,
                        )
                    }

                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        if (quantity.toInt() > 1) {
                            IconButton(onClick = { removeFromCartEvent(id) }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_minus),
                                    contentDescription = null
                                )
                            }
                        } else {
                            IconButton(onClick = { removeFromCartEvent(id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null
                                )
                            }
                        }

                        Text(text = quantity, fontSize = 32.sp)

                        IconButton(onClick = { addToCartEvent(id) }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        }

                    }

                }

            }

        }

    }

}


@Composable
fun ShowEmptyCartAnimation() {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.no_data))
    LottieAnimation(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 32.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}

@Composable
fun Purchase(totalPrice: String, isTransparent: Boolean, purchaseEvent: () -> Unit) {

    Surface(
        modifier = Modifier
            .wrapContentSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    purchaseEvent()
                }, shape = myShapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTransparent) Transparent else MaterialTheme.colorScheme.primary
                )
            ) {
                Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                    Text(
                        text = "Let's purchase",
                        fontWeight = FontWeight.Bold,
                        color = if (isTransparent) Transparent else MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Surface(
                shape = myShapes.medium,
                color = if (isTransparent) Transparent else CardViewBackground
            ) {
                Text(
                    text = "Total: ${stylePrice(totalPrice)}",
                    modifier = Modifier.padding(8.dp),
                    color = if (isTransparent) Transparent else Color.Unspecified
                )
            }
        }
    }

}