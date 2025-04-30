package com.example.umkm_sekitar.data.model

data class Product(
    val description: String = "",
    val photo: String = "",
    val price: Int = 0,
    val productName: String = "",
    val stock: String = "",
    val tag: List<String> = emptyList<String>()
)
