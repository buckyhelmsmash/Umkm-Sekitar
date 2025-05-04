package com.example.umkm_sekitar.ui.screen.cart

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.umkm_sekitar.R

@Composable
fun CartScreen() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text(
                text = "Keranjangmu",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E40AF) // warna biru tua seperti pada gambar
            )
        }

        Text(
            text = "Atur Keranjangmu disini",
            fontSize = 17.sp,
            color = Color(0xFF1E40AF),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(top = 40.dp)
        )

        Icon(
            painter = painterResource(id = R.drawable.baseline_shopping_cart_24), // Ganti dengan icon keranjang Anda
            contentDescription = "Cart Icon",
            tint = Color(0xFF3B82F6), // warna biru cart
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.TopEnd)
        )
    }
}

@Preview
@Composable
fun CartScreenPreview() {
    CartScreen()
}

