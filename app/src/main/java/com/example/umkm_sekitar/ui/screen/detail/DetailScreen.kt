package com.example.umkm_sekitar.ui.screen.detail

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.example.umkm_sekitar.data.model.Product
import com.example.umkm_sekitar.data.model.Store
import com.example.umkm_sekitar.ui.navigation.Screen
import com.example.umkm_sekitar.ui.screen.auth.AuthViewModel
import com.example.umkm_sekitar.ui.theme.RoyalBlue
import kotlinx.coroutines.launch


@Composable
fun DetailScreen(
    storeId: String,
    viewModel: DetailViewModel = hiltViewModel(),
    auth: AuthViewModel = hiltViewModel(),
    navController: NavController
) {
    val store by viewModel.store.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val cartAddedFlow = viewModel.cartAdded
    val isAdding by viewModel.isAddingToCart.collectAsState()
    val addError by viewModel.addToCartError.collectAsState()
    val currentUser by auth.currentUser.collectAsState()

    LaunchedEffect(storeId) {
        viewModel.loadStoreById(storeId)
        Log.d("DetailScreen", "Requested storeId = $storeId")
    }

    LaunchedEffect(cartAddedFlow) {
        cartAddedFlow.collect {
            navController.navigate(Screen.Cart.route) {
                popUpTo(Screen.Home.route) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $error", color = Color.Red, textAlign = TextAlign.Center)
                }
            }
            store != null -> {
                StoreDetailContent(
                    store = store!!,
                    onClick = { product, quantity ->
                        val userId = currentUser?.uid ?: return@StoreDetailContent
                        viewModel.addToCart(userId, product.id, storeId ,quantity)
                    }
                )
                if (isAdding) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                addError?.let { err ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                            .background(Color.Red, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(err, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            else -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Data toko $storeId tidak ditemukan.", textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoreDetailContent(store: Store, onClick: (Product, Int) -> Unit) {
    val allTags = listOf("Semua") + store.products.flatMap { it.tag }.distinct()
    var selectedTag by remember { mutableStateOf("Semua") }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var quantity by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = rememberAsyncImagePainter(store.photo),
            contentDescription = store.storeName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 200.dp)
                .background(Color.White, shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
        ) {
            Card(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-10).dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = store.photo,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        placeholder = painterResource(com.example.umkm_sekitar.R.drawable.no_image),
                        error = painterResource(com.example.umkm_sekitar.R.drawable.no_image)
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(store.storeName, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = store.distance+" m",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Distance",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(store.location, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            store.category.forEach { cat ->
                                Chip(text = cat)
                            }
                        }
                    }
                }

            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(allTags) { tag ->
                    FilterChip(
                        text = tag,
                        selected = tag == selectedTag,
                        onClick = { selectedTag = tag }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            val displayTags = if (selectedTag == "Semua") allTags.drop(1) else listOf(selectedTag)
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                displayTags.forEach { tag ->
                    val products = store.products.filter { it.tag.contains(tag) }
                    if (products.isNotEmpty()) {
                        item {
                            Text(
                                tag,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(products) { product ->
                                    ProductCard(product){
                                        selectedProduct = product
                                        coroutineScope.launch {
                                            bottomSheetState.show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedProduct?.let { product ->
        ModalBottomSheet(
            onDismissRequest = {
                selectedProduct = null
            },
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = product.photo,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(400.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    placeholder = painterResource(com.example.umkm_sekitar.R.drawable.no_image),
                    error = painterResource(com.example.umkm_sekitar.R.drawable.no_image)
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        product.productName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Rp ${product.price}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    product.productDescription,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 0) quantity-- },
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Kurangi")
                        }
                    }

                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Card(
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        IconButton(
                            onClick = { quantity++ },
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Tambah")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                val buttonText by remember {
                    derivedStateOf {
                        if (quantity == 0) {
                            "Kembali ke Menu"
                        } else {
                            val totalPrice = quantity * (product.price.toIntOrNull() ?: 0)
                            "Tambah ke Keranjang - Rp $totalPrice"
                        }
                    }
                }

                val buttonBackgroundColor = if (quantity > 0) RoyalBlue else Color.Transparent
                val buttonTextColor = if (quantity > 0) Color.White else RoyalBlue
                val borderColor = if (quantity > 0) RoyalBlue else RoyalBlue.copy(alpha = 0.5f)

                OutlinedButton(
                    onClick = {
                        if (quantity == 0) {
                            coroutineScope.launch {
                                bottomSheetState.hide()
                                selectedProduct = null
                            }
                        } else {
                            onClick(product, quantity)
                            coroutineScope.launch {
                                bottomSheetState.hide()
                                selectedProduct = null
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(all = 16.dp),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(width = 1.dp, color = borderColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = buttonBackgroundColor
                    )
                ) {
                    Text(buttonText, color = buttonTextColor)
                }

            }
        }
    }


}


@Composable
private fun Chip(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, style = MaterialTheme.typography.bodySmall, color = Color(0xFF3366FF))
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Color(0xFF3366FF) else Color.White,
        border = BorderStroke(1.dp, Color(0xFF3366FF)),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text,
            color = if (selected) Color.White else Color(0xFF3366FF),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(product.photo),
                contentDescription = product.productName,
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
            )
            Spacer(Modifier.height(8.dp))
            Text(
                product.productName,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "Rp ${product.price}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
