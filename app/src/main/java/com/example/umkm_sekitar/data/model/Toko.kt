package com.example.umkm_sekitar.data.model

data class Toko(
    val foto: String = "",
    val kategori_toko: List<String> = listOf(),
    val list_barang: List<Barang> = listOf(),
    val lokasi: String = "",
    val nama_toko: String = ""
)
