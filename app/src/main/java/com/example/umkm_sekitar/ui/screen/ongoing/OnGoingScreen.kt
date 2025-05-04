package com.example.umkm_sekitar.ui.screen.ongoing

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.example.umkm_sekitar.data.model.Store
import com.example.umkm_sekitar.ui.component.CategoryTag
import com.example.umkm_sekitar.ui.component.OrderProgress
import com.example.umkm_sekitar.ui.navigation.Screen
import com.example.umkm_sekitar.ui.screen.auth.AuthViewModel
import com.example.umkm_sekitar.ui.screen.detail.DetailViewModel
import com.example.umkm_sekitar.ui.theme.RoyalBlue
import com.example.umkm_sekitar.util.getCurrentFormattedDateTime
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OnGoingScreen(
    storeId: String = "",
    orderId: String = "",
    viewModel: DetailViewModel = hiltViewModel(),
    auth: AuthViewModel = hiltViewModel(),
    navController: NavController,
) {
    val store by viewModel.store.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(storeId) {
        viewModel.loadStoreById(storeId)
        Log.d("DetailScreen", "Requested storeId = $storeId")
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
                BodyContent(
                    store = store!!,
                    navController = navController,
                    orderId = orderId
                )
            }

            else -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Data toko $storeId tidak ditemukan.", textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BodyContent(store: Store, navController: NavController, orderId: String) {

    val viewModel: OnGoingViewModel = hiltViewModel()
    val auth: AuthViewModel = hiltViewModel()
    val updateStatusResult by viewModel.updateStatusResult.observeAsState()
    val userId = auth.currentUser.collectAsState().value?.uid.orEmpty()

    var currentStep by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (currentStep < 3) {
            delay(5_000L)
            currentStep++
            if (currentStep == 3) {
                viewModel.updateOrderStatus(userId, orderId, "Done")
            }
        }
    }

    updateStatusResult?.let { success ->
        if (success) {
            navController.navigate(route = Screen.Orders.route)
        }
    }

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
                .background(
                    Color.White,
                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                )
        ) {
            Card(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomEnd = 16.dp,
                    bottomStart = 16.dp
                ),
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
                                text = "720 m",
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
                        Text(
                            store.location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            store.category.forEach { cat ->
                                CategoryTag(category = cat)
                            }
                        }
                    }
                }

            }

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(IntrinsicSize.Min)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Pesananmu Sedang Berjalan!",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Mohon ditunggu ya! Belanjaanmu sedang di antar",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentTime = getCurrentFormattedDateTime()

                Text(
                    text = "Pesanan",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = currentTime,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            val icons = listOf(
                Icons.Default.Create,
                Icons.Default.ShoppingCart,
                Icons.Default.Send,
                Icons.Default.Star
            )

            Column(modifier = Modifier.fillMaxSize()) {
                OrderProgress(
                    currentStep = currentStep,
                    icons = icons,
                    activeColor = RoyalBlue
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Selesaikan pesananmu terlebih dahulu agar dapat melakukan pesanan lainnya pada toko ini",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                Button(
                    onClick = {
                        navController.navigate(route = Screen.Orders.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Lihat Pesanan", style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
                }
            }
        }

    }
}
