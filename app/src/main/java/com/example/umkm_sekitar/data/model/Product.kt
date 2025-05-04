package com.example.umkm_sekitar.data.model


data class Product(
    val id: String = "",
    val productDescription: String = "",
    val photo: String = "",
    val price: String = "",
    val productName: String = "",
    val stock: String = "",
    val tag: List<String> = emptyList<String>()
)
