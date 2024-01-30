package com.parsanasekhi.store.ui.features.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.parsanasekhi.store.model.data.Product
import com.parsanasekhi.store.ui.theme.MainAppTheme
import com.parsanasekhi.store.ui.theme.myShapes
import com.parsanasekhi.store.util.MyScreens
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.viewmodel.getViewModel
import org.koin.core.parameter.parametersOf

@Preview(showBackground = true)
@Composable
fun CategoryScreenPreview() {
    MainAppTheme {
        CategoryScreen(categoryName = "Category")
    }
}

@Composable
fun CategoryScreen(categoryName: String) {

    val viewModel = getViewModel<CategoryViewModel>(parameters = { parametersOf(categoryName) })
    viewModel.loadDataFromRepository(categoryName)

    val nav = getNavController()

    Column(modifier = Modifier.fillMaxSize()) {
        CategoryAppBar(categoryName)
        CategoryList(viewModel.productsList.value) {
            nav.navigate(MyScreens.ProductScreen.route + "/$it")
        }
    }

}

@Composable
fun CategoryList(products: List<Product>, onItemClicked: (String) -> Unit) {

    LazyColumn(
        content = {
            items(count = products.size, itemContent = {
                CategoryItem(
                    imageUrl = products[it].imgUrl,
                    title = products[it].name,
                    price = products[it].price,
                    sold = products[it].soldItem,
                    id = products[it].productId,
                    onItemClicked = onItemClicked
                )
            })
        },
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )

}

@Composable
fun CategoryItem(
    imageUrl: String,
    title: String,
    price: String,
    sold: String,
    id: String,
    onItemClicked: (String) -> Unit
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
                        fontWeight = FontWeight.Medium
                    )


                    Text(
                        text = "$price Tomans",
                        color = Color.DarkGray,
                        fontSize = 12.sp,
                    )

                }

                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clip(myShapes.medium)
                ) {

                    Text(
                        text = "$sold sold",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(4.dp)
                    )

                }

            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryAppBar(title: String) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth().shadow(elevation = 4.dp),
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                text = title,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}
