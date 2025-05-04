package com.example.umkm_sekitar.data.model


data class CartItem(
    val storeId: String = "",
    val productId: String = "",
    val quantity: Int = 0,
    var checked: Boolean = false
)
