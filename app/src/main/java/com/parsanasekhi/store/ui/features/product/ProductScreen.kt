package com.parsanasekhi.store.ui.features.product

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.parsanasekhi.store.R
import com.parsanasekhi.store.model.data.Comment
import com.parsanasekhi.store.ui.theme.CardViewBackground
import com.parsanasekhi.store.ui.theme.MainAppTheme
import com.parsanasekhi.store.ui.theme.Transparent
import com.parsanasekhi.store.ui.theme.myShapes
import com.parsanasekhi.store.util.MyScreens
import com.parsanasekhi.store.util.NetworkChecker
import com.parsanasekhi.store.util.stylePrice
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.viewmodel.getViewModel

@Preview(showBackground = true)
@Composable
fun ProductScreenPreview() {
    MainAppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.LightGray) {
//            AddProductToCart("500") {}
        }
    }
}

@Composable
fun ProductScreen(productId: String) {

    val context = LocalContext.current
    val viewModel = getViewModel<ProductViewModel>()
    viewModel.loadData(productId, NetworkChecker(context).isInternetConnected)
    val product = viewModel.product.value
    val comments = viewModel.comment.value
    val nav = getNavController()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ProductToolbar(
            badgeNum = viewModel.badgeNumber.value,
            onBackIconClicked = {
                nav.popBackStack()
            },
            onShopIconClicked = {
                nav.navigate(MyScreens.CartScreen.route)
            }
        )

        ProductDesign(
            imageUrl = product.imgUrl,
            subject = product.name,
            details = product.detailText,
            category = product.category
        ) {
            nav.navigate(MyScreens.CategoryScreen.route + "/" + it)
        }

        Divider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp)
        )

        ProductDetail(
            commentsNum = comments.size,
            material = product.material,
            sold = product.soldItem,
            product.tags
        )

        Divider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 4.dp, start = 16.dp, end = 16.dp)
        )

        val haveToShowDialog = remember { mutableStateOf(false) }

        CommentsHeader(comments.isNotEmpty()) {
            if (NetworkChecker(context).isInternetConnected)
                haveToShowDialog.value = true
            else Toast.makeText(
                context,
                "Please check your internet connection",
                Toast.LENGTH_SHORT
            ).show()
        }

        AddCommentDialog(
            haveToShow = haveToShowDialog.value,
            doneEvent = { commentText ->
                if (NetworkChecker(context).isInternetConnected) {
                    viewModel.addNewComment(productId = productId, text = commentText) {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                    haveToShowDialog.value = false
                } else
                    Toast.makeText(
                        context,
                        "Please check your internet connection",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        ) {
            haveToShowDialog.value = false
        }

        CommentsList(comments = comments)

    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            AddProductToCart(price = product.price, viewModel.isAddingProduct.value) {
                if (NetworkChecker(context).isInternetConnected) {
                    viewModel.addProductToCart(productId) {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
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

// -----------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductToolbar(badgeNum: Int, onBackIconClicked: () -> Unit, onShopIconClicked: () -> Unit) {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp),
        title = {
            Text(
                modifier = Modifier
                    .padding(start = 8.dp),
                text = "Details",
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
        }
    )
}

@Composable
fun ProductDesign(
    imageUrl: String,
    subject: String,
    details: String,
    category: String,
    onCategoryClicked: (String) -> Unit
) {

    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .height(256.dp)
            .fillMaxWidth()
            .padding(16.dp)
            .clip(myShapes.medium)
    )

    Text(
        text = subject,
        modifier = Modifier.padding(start = 16.dp),
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )

    Text(
        textAlign = TextAlign.Justify,
        text = details,
        modifier = Modifier.padding(16.dp),
        fontSize = 16.sp
    )

    TextButton(
        modifier = Modifier.padding(start = 16.dp),
        onClick = { onCategoryClicked(category) }
    ) {
        Text(
            text = "#$category",
            textAlign = TextAlign.Justify,
            fontSize = 16.sp
        )
    }

}

@Composable
fun ProductDetail(commentsNum: Int, material: String, sold: String, tag: String) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Column {

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_details_comment),
                    contentDescription = null
                )
                Text(
                    text = if (commentsNum != 1) "$commentsNum comments"
                    else "$commentsNum comment",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_details_material),
                    contentDescription = null
                )
                Text(
                    text = material,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_details_sold),
                    contentDescription = null
                )
                Text(
                    text = "$sold sold",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

        }

        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = myShapes.medium
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = tag, fontSize = 16.sp
            )
        }

    }

}

// -----------------------------------------------------------------------------------------

@Composable
fun CommentsHeader(isThereAnyComment: Boolean, showDialog: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement =
        if (isThereAnyComment) Arrangement.SpaceBetween
        else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (isThereAnyComment)
            Text(text = "Comments", fontWeight = FontWeight.Bold, fontSize = 24.sp)

        TextButton(onClick = { showDialog() }) {
            Text(text = "Add new comment", fontSize = 16.sp)
        }

    }
}

@Composable
fun CommentsList(comments: List<Comment>) {
    Spacer(modifier = Modifier.height(8.dp))
    comments.forEach {
        CommentItem(email = it.userEmail, text = it.text)
    }
    Spacer(modifier = Modifier.height(8.dp))
    AddProductToCart(price = "????", false) {}
}

@Composable
fun CommentItem(email: String, text: String) {
    Card(
        border = BorderStroke(1.dp, Color.Black),
        shape = myShapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {

        Column(modifier = Modifier.padding(8.dp)) {

            Text(text = email, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.padding(vertical = 2.dp))
            Text(text = text, fontSize = 16.sp)

        }

    }
}

// -----------------------------------------------------------------------------------------

@Composable
fun AddProductToCart(price: String, isAddingProduct: Boolean, addProductToCart: () -> Unit) {

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
            Button(onClick = {
                addProductToCart()
            }, shape = myShapes.medium) {
                Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                    Text(
                        text = "Add product to cart",
                        fontWeight = FontWeight.Bold,
                        color =
                        if (!isAddingProduct) Color.Unspecified
                        else Transparent
                    )
                    if (isAddingProduct) DotsTyping()
                }
            }
            Surface(
                shape = myShapes.medium,
                color = CardViewBackground
            ) {
                Text(
                    text = stylePrice(price),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }

}

@Composable
fun AddCommentDialog(haveToShow: Boolean, doneEvent: (String) -> Unit, cancelEvent: () -> Unit) {

    val edtText = remember { mutableStateOf("") }

    if (haveToShow) {

        Dialog(onDismissRequest = { cancelEvent() }) {

            Card(
                shape = myShapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(start = 8.dp, end = 8.dp, top = 12.dp)
                ) {
                    Text(
                        text = "Write your comment",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    OutlinedTextField(
                        value = edtText.value,
                        onValueChange = { edtText.value = it },
                        label = {
                            Text(text = "Comment text")
                        },
                        placeholder = {
                            Text(text = "Comment text")
                        },
                        maxLines = 2
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        TextButton(onClick = { cancelEvent() }) {
                            Text(text = "Cancel", fontSize = 16.sp)
                        }

                        TextButton(onClick = {
                            if (edtText.value.isNotEmpty() && edtText.value.isNotBlank())
                                doneEvent(edtText.value)
                        }) {
                            Text(text = "Done", fontSize = 16.sp)
                        }

                    }

                }
            }
        }

    }

}

// -----------------------------------------------------------------------------------------

@Composable
fun DotsTyping() {

    val dotSize = 10.dp
    val delayUnit = 350
    val maxOffset = 10f

    @Composable
    fun Dot(
        offset: Float
    ) = Spacer(
        Modifier
            .size(dotSize)
            .offset(y = -offset.dp)
            .background(
                color = Color.White,
                shape = CircleShape
            )
            .padding(start = 8.dp, end = 8.dp)
    )

    val infiniteTransition = rememberInfiniteTransition()

    @Composable
    fun animateOffsetWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 4
                0f at delay with LinearEasing
                maxOffset at delay + delayUnit with LinearEasing
                0f at delay + delayUnit * 2
            }
        )
    )

    val offset1 by animateOffsetWithDelay(0)
    val offset2 by animateOffsetWithDelay(delayUnit)
    val offset3 by animateOffsetWithDelay(delayUnit * 2)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = maxOffset.dp)
    ) {
        val spaceSize = 2.dp

        Dot(offset1)
        Spacer(Modifier.width(spaceSize))
        Dot(offset2)
        Spacer(Modifier.width(spaceSize))
        Dot(offset3)
    }
}
