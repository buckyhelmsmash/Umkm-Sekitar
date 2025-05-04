package com.example.umkm_sekitar.ui.screen.checkout

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.umkm_sekitar.data.model.CartItemWithProduct
import com.example.umkm_sekitar.data.model.CheckoutItem
import com.example.umkm_sekitar.data.model.CheckoutOrder
import com.example.umkm_sekitar.data.model.ShippingOption
import com.example.umkm_sekitar.ui.component.PaymentMethodItem
import com.example.umkm_sekitar.ui.component.ShippingOptionItem
import com.example.umkm_sekitar.ui.component.formatCurrency
import com.example.umkm_sekitar.ui.navigation.Screen
import com.example.umkm_sekitar.ui.screen.auth.AuthViewModel
import com.example.umkm_sekitar.ui.screen.cart.CartViewModel
import com.example.umkm_sekitar.ui.theme.RoyalBlue
import com.example.umkm_sekitar.util.getCurrentFormattedDateTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckOutScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: CheckOutViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
) {
    val shippingOptions = listOf(
        ShippingOption(Icons.Default.Refresh, "Antar", 5000.0),
        ShippingOption(Icons.Default.Refresh, "Ambil Sendiri", 0.0)
    )
    var cartWithProducts by remember {
        mutableStateOf<Map<String, List<CartItemWithProduct>>>(emptyMap())
    }
    var selectedShippingIndex by remember { mutableIntStateOf(0) }
    val userId = authViewModel.currentUser.collectAsState().value?.uid.orEmpty()


    val productTotal = cartWithProducts.values
        .flatten()
        .filter { it.cartItem.checked == true && it.product != null }
        .sumOf { it.cartItem.quantity.toDouble() * it.product!!.price.toDouble() }

    val sumCount = productTotal + shippingOptions[selectedShippingIndex].fee

    LaunchedEffect(userId) {
        cartViewModel.getCartItemsWithProductDetails(userId).collectLatest {
            cartWithProducts = it
        }
    }

    val scope = rememberCoroutineScope()
    val selectedIndex by viewModel.selectedIndex.collectAsState()
    val (iconId, label) = viewModel.paymentMethods.getOrNull(selectedIndex)
        ?: Pair(android.R.drawable.ic_menu_help, "Belum Dipilih")

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        scope.launch {
            for (cartItemWithProduct in cartWithProducts.values.flatten()) {
                val productId = cartItemWithProduct.cartItem.productId
                cartViewModel.updateCheckedItem(userId, productId, false)
                delay(100)
            }
            delay(200)
            navController.navigate(route = Screen.Cart.route)
        }
    }



    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Pilih Metode Pembayaran",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                viewModel.paymentMethods.forEachIndexed { index, (icon, name) ->
                    PaymentMethodItem(
                        iconId = icon,
                        text = name,
                        isSelected = selectedIndex == index,
                        onClick = {
                            viewModel.selectPaymentMethod(index)
                            showBottomSheet = false
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }


    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(60.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Harga", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = formatCurrency(sumCount + 2000.0),
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = RoyalBlue
                    )
                }
                Button(
                    onClick = {
                        val checkoutOrder = CheckoutOrder(
                            orderId = UUID.randomUUID().toString(),
                            userId = userId,
                            total = sumCount + 2000.0,
                            shippingMethod = shippingOptions[selectedShippingIndex].label,
                            shippingFee = shippingOptions[selectedShippingIndex].fee,
                            orderFee = 2000.0,
                            storeId = cartWithProducts.values.flatten()
                                .firstOrNull { it.cartItem.checked == true }
                                ?.cartItem?.storeId.orEmpty(),
                            status = "Pending",
                            createdAt = getCurrentFormattedDateTime(),
                            paymentMethod = label,
                            items = cartWithProducts.values.flatten()
                                .filter { it.cartItem.checked == true }
                                .map {
                                    CheckoutItem(
                                        url = it.product?.photo ?: "",
                                        productId = it.product?.id.orEmpty(),
                                        name = it.product?.productName.orEmpty(),
                                        quantity = it.cartItem.quantity,
                                        storeId = it.cartItem.storeId,
                                        price = it.product?.price?.toDouble() ?: 0.0
                                    )
                                }
                        )
                        viewModel.saveCheckoutOrder(checkoutOrder)
                        navController.navigate(route = Screen.LoadingCo.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) {
                    Text(
                        text = "Lakukan Pesanan",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
        ) {

            cartWithProducts.forEach { (_, items) ->
                item {
                    Text(
                        text = "Toko: ${items.firstOrNull()?.storeName ?: "Toko Tidak Diketahui"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 5.dp, bottom = 3.dp)
                    )
                }

                items(items.filter { it.cartItem.checked == true }) { cartItemWithProduct ->
                    val product = cartItemWithProduct.product
                    if (product != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(8.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = product.photo),
                                contentDescription = product.productName,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = product.productName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text("Qty: ${cartItemWithProduct.cartItem.quantity}")
                                Text(
                                    text = formatCurrency(product.price.toDouble()),
                                    color = RoyalBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }


            item {
                Text("Opsi Pengambilan")
                Spacer(Modifier.height(8.dp))

                shippingOptions.forEachIndexed { index, option ->
                    ShippingOptionItem(
                        iconId = option.icon,
                        text = option.label,
                        price = option.fee,
                        isSelected = index == selectedShippingIndex,
                        onClick = { selectedShippingIndex = index }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            item {
                Text(
                    "Rincian",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 5.dp, bottom = 3.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sub Total")
                    Text(formatCurrency(productTotal))
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Biaya Pengantaran")
                    Text(formatCurrency(shippingOptions[selectedShippingIndex].fee))
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Biaya Pesanan")
                    Text(formatCurrency(2000.0))
                }

            }

            item {
                Text(
                    "Metode Pembayaran", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 5.dp, bottom = 3.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = iconId),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = label,
                            fontSize = 16.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(
                                1f,
                                fill = false
                            )
                        )
                    }
                    TextButton(onClick = { showBottomSheet = true }) {
                        Text("Ubah", color = RoyalBlue)
                    }
                }
            }
        }
    }
}
