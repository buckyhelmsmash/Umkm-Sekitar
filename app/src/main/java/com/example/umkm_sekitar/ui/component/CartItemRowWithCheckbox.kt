package com.example.umkm_sekitar.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.umkm_sekitar.data.model.CartItemWithProduct
import com.example.umkm_sekitar.ui.theme.RoyalBlue
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartItemRowWithCheckbox(
    cartItemWithProduct: CartItemWithProduct,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val item = cartItemWithProduct.cartItem
    val product = cartItemWithProduct.product

    val price = product?.price?.toDouble() ?: 0.0
    val totalPrice = price * item.quantity

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .padding(top = 8.dp)
                .size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                product?.photo?.let { url ->
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Produk: ${product?.productName ?: "Memuat..."}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Qty: ${item.quantity}", fontWeight = FontWeight.Medium)
                    Text("Harga: ${formatCurrency(price)}", fontWeight = FontWeight.Medium)
                    Text(
                        "Total: ${formatCurrency(totalPrice)}",
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlue
                    )
                }

            }
        }
    }
}

fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(amount)
}
