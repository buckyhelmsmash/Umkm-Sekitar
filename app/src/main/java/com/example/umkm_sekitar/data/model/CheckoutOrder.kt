package com.example.umkm_sekitar.data.model

data class CheckoutOrder(
    val orderId: String = "",
    val storeId: String = "",
    val userId: String = "",
    val total: Double = 0.0,
    val shippingMethod: String = "",
    val shippingFee: Double = 0.0,
    val orderFee: Double = 0.0,
    val status: String = "Pending",
    val createdAt: String = "",
    val paymentMethod: String = "",
    val items: List<CheckoutItem> = emptyList()
)