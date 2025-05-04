package com.example.umkm_sekitar.data.model


data class CartItemWithProduct(
    val cartItem: CartItem,
    val product: Product?,
    val storeName: String
)
