package com.example.umkm_sekitar.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.umkm_sekitar.data.model.Store
import com.example.umkm_sekitar.util.Resource
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val storeListState by viewModel.storeList.collectAsState()
    val filteredStoreListState by viewModel.filteredStoreList.collectAsState()
    Column (
        modifier = modifier.padding(vertical = 20.dp),
    ){
        Text("Daftar Semua Toko", style = MaterialTheme.typography.titleMedium)
        when (storeListState) {
            is Resource.Loading -> CircularProgressIndicator()
            is Resource.Error -> Text("Error: ${(storeListState as Resource.Error).message}")
            is Resource.Success -> {
                val list = (storeListState as Resource.Success).data
                if (list.isNullOrEmpty()) {
                    Text("Toko tidak ditemukan.")
                } else {
                    LazyColumn (
                        modifier = modifier
                    ){
                        items(list) { toko ->
                            StoreItem(toko = toko)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hasil Filter
        Text("Hasil Filter", style = MaterialTheme.typography.titleMedium)
        when (filteredStoreListState) {
            is Resource.Loading -> CircularProgressIndicator()
            is Resource.Error -> Text("Error: ${(filteredStoreListState as Resource.Error).message}")
            is Resource.Success -> {
                val list = (filteredStoreListState as Resource.Success).data
                if (list.isNullOrEmpty()) {
                    Text("Tidak ditemukan toko yang sesuai.")
                } else {
                    LazyColumn (
                        modifier = modifier
                    ){
                        items(list) { toko ->
                            StoreItem(toko = toko)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoreItem(toko: Store) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Nama: ${toko.storeName}", style = MaterialTheme.typography.bodyLarge)
            Text("Kategori: ${toko.category.joinToString(", ")}")
            Text("Lokasi: ${toko.location}")
        }
    }
}
