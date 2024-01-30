package com.parsanasekhi.store.ui.features.main

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.parsanasekhi.store.R
import com.parsanasekhi.store.model.data.Ad
import com.parsanasekhi.store.model.data.Product
import com.parsanasekhi.store.ui.theme.CardViewBackground
import com.parsanasekhi.store.ui.theme.MainAppTheme
import com.parsanasekhi.store.ui.theme.myShapes
import com.parsanasekhi.store.util.CATEGORY
import com.parsanasekhi.store.util.MyScreens
import com.parsanasekhi.store.util.NO_PAYMENT
import com.parsanasekhi.store.util.NetworkChecker
import com.parsanasekhi.store.util.PAYMENT_PENDING
import com.parsanasekhi.store.util.PAYMENT_SUCCESS
import com.parsanasekhi.store.util.TAGS
import com.parsanasekhi.store.util.stylePrice
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.viewmodel.getViewModel
import ir.dunijet.dunibazaar.model.data.CheckOut
import org.koin.core.parameter.parametersOf

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {

    val nav = getNavController()

    val backgroundColor = MaterialTheme.colorScheme.background
    val uiController = rememberSystemUiController()
    SideEffect { uiController.setStatusBarColor(backgroundColor) }

    val context = LocalContext.current
    val mainViewModel =
        getViewModel<MainViewModel>(parameters = {
            parametersOf(NetworkChecker(context).isInternetConnected)
        })
    mainViewModel.getBadgeNumber()

    if (mainViewModel.getPaymentStatus() == PAYMENT_PENDING) {
        if (NetworkChecker(context).isInternetConnected) {
            mainViewModel.getCheckoutData()
        }
    }

    Column(
        modifier = Modifier.verticalScroll(
            state = ScrollState(0)
        )
    ) {

        if (mainViewModel.showProgressBar.value)
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )

        TopToolbar(
            onProfileIconClicked = {
                nav.navigate(MyScreens.ProfileScreen.route)
            },
            badgeNum = mainViewModel.badgeNumber.value.toInt(),
            onShopIconClicked = {
                if (NetworkChecker(context).isInternetConnected)
                    nav.navigate(MyScreens.CartScreen.route)
                else Toast.makeText(
                    context,
                    "Please check your internet connection.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        CategoryBar(CATEGORY) {
            nav.navigate(MyScreens.CategoryScreen.route + "/$it")
        }
        ShowProductSubjectsAndAds(
            tags = TAGS,
            products = mainViewModel.dataProducts.value,
            ads = mainViewModel.dataAds.value,
            onProductItemClicked = {
                nav.navigate(MyScreens.ProductScreen.route + "/$it")
            },
            onAdPictureClicked = {
                nav.navigate(MyScreens.ProductScreen.route + "/$it")
            }
        )

    }

    if (mainViewModel.showPaymentResultDialog.value) {

        PaymentResultDialog(
            checkoutResult = mainViewModel.checkoutData.value,
            onDismiss = {
                mainViewModel.showPaymentResultDialog.value = false
                mainViewModel.setPaymentStatus(NO_PAYMENT)
            }
        )

    }

}

// -----------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopToolbar(onShopIconClicked: () -> Unit, badgeNum: Int, onProfileIconClicked: () -> Unit) {

    TopAppBar(
        title = {
            Text(
                text = "Bag Store", fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        actions = {
            IconButton(
                modifier = if (badgeNum != 0) {
                    Modifier
                        .width(74.dp)
                        .height(64.dp)
                } else {
                    Modifier
                        .wrapContentSize()
                },
                onClick = onShopIconClicked
            ) {
                if (badgeNum == 0) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null
                    )
                } else {
                    BadgedBox(
                        badge = { Badge { Text(text = badgeNum.toString()) } }
                    ) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
                    }
                }
            }
            IconButton(onClick = { onProfileIconClicked() }) {
                Icon(Icons.Default.Person, null)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )

}

// -----------------------------------------------------------------------------------------

@Composable
fun CategoryBar(subjects: List<Pair<String, Int>>, onCategoryItemClicked: (String) -> Unit) {

    LazyRow(
        content = {
            items(subjects.size) {
                CategoryItem(subjects[it], onCategoryItemClicked)
            }
        },
        contentPadding = PaddingValues(8.dp)
    )

}

@Composable
fun CategoryItem(subject: Pair<String, Int>, onCategoryItemClicked: (String) -> Unit) {

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onCategoryItemClicked(subject.first) },
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Surface(
                shape = myShapes.medium,
                color = CardViewBackground
            ) {
                Image(
                    painter = painterResource(id = subject.second),
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Text(text = subject.first, color = Color.Gray, fontSize = 12.sp)

        }

    }

}

// -----------------------------------------------------------------------------------------

@Composable
fun ShowProductSubjectsAndAds(
    tags: List<String>,
    products: List<Product>,
    ads: List<Ad>,
    onProductItemClicked: (String) -> Unit,
    onAdPictureClicked: (String) -> Unit
) {

    val context = LocalContext.current

    if (products.isNotEmpty()) {
        Column {

            tags.forEachIndexed { it, _ ->
                val filteredProducts = products.filter { product ->
                    product.tags == tags[it]
                }
                ProductSubjectBar(tags[it], filteredProducts.shuffled(), onProductItemClicked)
                if ((it == 1 || it == 2) && ads.size > 1) {
                    AdvertisingBigPicture(ads[(ads.indices).random()], onAdPictureClicked)
                }
            }

        }

    } else if (!NetworkChecker(context).isInternetConnected)
        Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_SHORT).show()
}

// -----------------------------------------------------------------------------------------

@Composable
fun ProductSubjectBar(
    tag: String,
    products: List<Product>,
    onProductItemClicked: (String) -> Unit
) {

    Column {

        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            text = tag,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyRow(
            content = {
                items(products.size) {
                    ProductItem(products[it], onProductItemClicked)
                }
            },
            contentPadding = PaddingValues(8.dp)
        )

    }

}

@Composable
fun ProductItem(product: Product, onProductItemClicked: (String) -> Unit) {

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onProductItemClicked(product.productId) },
        shape = myShapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {

        Column {

            AsyncImage(
                model = product.imgUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(200.dp)
            )

            Text(
                text = product.name,
                modifier = Modifier
                    .sizeIn(maxWidth = 192.dp)
                    .padding(8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = stylePrice(product.price),
                modifier = Modifier
                    .sizeIn(maxWidth = 192.dp)
                    .padding(start = 8.dp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${product.soldItem} sold",
                modifier = Modifier
                    .sizeIn(maxWidth = 192.dp)
                    .padding(start = 8.dp, bottom = 8.dp),
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

        }

    }

}

// -----------------------------------------------------------------------------------------

@Composable
fun AdvertisingBigPicture(ad: Ad, onAdPictureClicked: (String) -> Unit) {

    AsyncImage(
        model = ad.imageURL,
        contentDescription = null,
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .clip(myShapes.medium)
            .clickable { onAdPictureClicked(ad.productId) },
        contentScale = ContentScale.Crop
    )

}

// -----------------------------------------------------------------------------------------

@Composable
private fun PaymentResultDialog(checkoutResult: CheckOut, onDismiss: () -> Unit) {

    Dialog(onDismissRequest = onDismiss) {

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = myShapes.medium,
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Payment Result",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (checkoutResult.order?.status?.toInt() == PAYMENT_SUCCESS) {

                    AsyncImage(
                        model = R.drawable.success_anim,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(110.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "Payment was successful!", style = TextStyle(fontSize = 16.sp))

                    Text(
                        text = "Purchase Amount: " + stylePrice(
                            (checkoutResult.order.amount).substring(
                                0,
                                (checkoutResult.order.amount).length - 1
                            )
                        )
                    )

                } else {

                    AsyncImage(
                        model = R.drawable.fail_anim,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(110.dp)
                            .padding(top = 6.dp, bottom = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "Payment was not successful!", style = TextStyle(fontSize = 16.sp))

                    Text(
                        text = "Purchase Amount: " + stylePrice(
                            (checkoutResult.order!!.amount).substring(
                                0,
                                (checkoutResult.order.amount).length - 1
                            )
                        )
                    )

                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TextButton(onClick = onDismiss) {
                        Text(text = "ok")
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                }
            }
        }
    }
}
