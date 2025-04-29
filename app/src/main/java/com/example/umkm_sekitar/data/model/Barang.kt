package com.example.umkm_sekitar.data.model

data class Barang(
    val deskripsi: String = "",
    val foto: String = "",
    val harga: String = "",
    val nama: String = "",
    val stok: String = "",
    val tag: List<String> = emptyList<String>()
)
