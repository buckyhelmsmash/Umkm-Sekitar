package com.example.umkm_sekitar.data.model

data class Store(
    val id: String = "",
    val photo: String = "",
    val category: List<String> = listOf(),
    val products: List<Product> = listOf(),
    val location: String = "",
    val storeName: String = "",
    val distance: String = ""
)
