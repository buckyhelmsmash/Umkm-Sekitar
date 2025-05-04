package com.example.umkm_sekitar.ui.screen.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.umkm_sekitar.data.model.CartItemWithProduct
import com.example.umkm_sekitar.ui.component.CartItemRowWithCheckbox
import com.example.umkm_sekitar.ui.component.formatCurrency
import com.example.umkm_sekitar.ui.navigation.Screen
import com.example.umkm_sekitar.ui.screen.auth.AuthViewModel
import com.example.umkm_sekitar.ui.theme.RoyalBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController
) {
    val userId = authViewModel.currentUser.collectAsState().value?.uid.orEmpty()
    var cartWithProducts by remember {
        mutableStateOf<Map<String, List<CartItemWithProduct>>>(emptyMap())
    }
    val selectedItems = remember { mutableStateListOf<CartItemWithProduct>() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.getCartItemsWithProductDetails(userId).collectLatest {
                cartWithProducts = it
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(top = 24.dp, bottom = 24.dp)) {
        Text(
            text = "Keranjangmu",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        if (cartWithProducts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Keranjang kosong")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                cartWithProducts.forEach { (storeId, items) ->
                    val storeName = items.firstOrNull()?.storeName ?: "Toko $storeId"
                    val allChecked = items.all { selectedItems.contains(it) }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                modifier = Modifier
                                    .size(24.dp),
                                checked = allChecked,
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        items.forEach { if (!selectedItems.contains(it)) selectedItems.add(it) }
                                    } else {
                                        selectedItems.removeAll(items)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = storeName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }

                    items(items) { cartItem ->
                        val isChecked = selectedItems.contains(cartItem)
                        CartItemRowWithCheckbox(
                            cartItemWithProduct = cartItem,
                            isChecked = isChecked,
                            onCheckedChange = { checked ->
                                if (checked) selectedItems.add(cartItem)
                                else selectedItems.remove(cartItem)
                            }
                        )
                    }
                }
            }

            val buttonBackgroundColor = if (selectedItems.isNotEmpty()) RoyalBlue else Color.Transparent
            val buttonTextColor = if (selectedItems.isNotEmpty()) Color.White else RoyalBlue
            val borderColor = if (selectedItems.isNotEmpty()) RoyalBlue else RoyalBlue.copy(alpha = 0.5f)
            val count = selectedItems.sumOf {
                val price = it.product?.price?.toDoubleOrNull() ?: 0.0
                price * it.cartItem.quantity
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Harga", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = formatCurrency(count),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = RoyalBlue
                )
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        if (selectedItems.isNotEmpty()) {
                            for (cartItem in selectedItems) {
                                viewModel.updateCheckedItem(
                                    userId,
                                    cartItem.cartItem.productId,
                                    true
                                )
                                delay(100)
                            }
                            delay(200)
                            navController.navigate(route = Screen.CheckOut.route)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 100.dp),
                contentPadding = PaddingValues(all = 16.dp),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(width = 1.dp, color = borderColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = buttonBackgroundColor
                ),
                enabled = selectedItems.isNotEmpty()
            ) {
                Text(
                    text = "Checkout (${selectedItems.size})",
                    color = buttonTextColor
                )
            }

        }
    }
}
