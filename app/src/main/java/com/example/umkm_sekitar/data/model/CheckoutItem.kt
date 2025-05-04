package com.example.umkm_sekitar.data.model

data class CheckoutItem(
    val productId: String = "",
    val storeId: String = "",
    val name: String = "",
    val url: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
)