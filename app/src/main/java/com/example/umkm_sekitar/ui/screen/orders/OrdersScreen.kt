package com.example.umkm_sekitar.ui.screen.orders

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.umkm_sekitar.data.model.CheckoutOrder
import com.example.umkm_sekitar.ui.component.formatCurrency
import com.example.umkm_sekitar.ui.navigation.Screen
import com.example.umkm_sekitar.ui.screen.auth.AuthViewModel
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@Composable
fun OrdersScreen(
    viewModel: OrderViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController
) {
    val userId = authViewModel.currentUser.collectAsState().value?.uid.orEmpty()

    LaunchedEffect(userId) {
        viewModel.fetchCheckoutOrders(userId)
    }

    val checkoutOrders by viewModel.checkoutOrders.collectAsState()


    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf("Pesanan", "Riwayat")
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { tabs.size }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> PesananTabContent(checkoutOrders, navController)
                1 -> RiwayatTabContent(checkoutOrders, navController)
            }
        }
    }
}

@Composable
fun PesananTabContent(checkoutOrders: List<CheckoutOrder>, navController: NavController) {
    val filteredOrders = checkoutOrders.filter { it.status.lowercase() != "done" }

    if (filteredOrders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tidak ada pesanan.",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(filteredOrders) { order ->
                OrderItem(order) {
                    navController.navigate(route = "${Screen.OnGoing.route}/${order.storeId}/${order.orderId}") {
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItem(
    order: CheckoutOrder,
    onClick: (CheckoutOrder) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth().padding(top = 10.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = {onClick(order)},
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Pesanan ID: ${order.orderId}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = item.url,
                        contentDescription = "Product Image",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text("Produk: ${item.name}", fontWeight = FontWeight.SemiBold)
                        Text("Qty: ${item.quantity}")
                        Text("Harga: ${formatCurrency(item.price)}")
                    }
                }
            }

            VerticalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Total: ${formatCurrency(order.total)}", fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
fun RiwayatTabContent(checkoutOrders: List<CheckoutOrder>, navController: NavController) {
    val filteredOrders = checkoutOrders.filter { it.status.lowercase() == "done" }

    if (filteredOrders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Belum ada riwayat pesanan.",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(filteredOrders) { order ->
                OrderItem(order) {
                    navController.navigate(route = Screen.Home.route)
                }
            }
        }
    }
}

