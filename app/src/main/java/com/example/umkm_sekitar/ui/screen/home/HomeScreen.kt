package com.example.umkm_sekitar.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.umkm_sekitar.ui.component.StoreItemsList
import com.example.umkm_sekitar.util.Resource

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val storeListState by viewModel.storeList.collectAsState()

    Column(
        modifier = modifier.padding(vertical = 20.dp),
    ) {
        Text(
            text = "Tokodekat",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp)
        )
        Text(
            text = "Terasa Erat",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 8.dp)
        )
        var searchText by remember { mutableStateOf("") }

        when (storeListState) {
            is Resource.Loading -> CircularProgressIndicator()
            is Resource.Error -> Text("Error: ${(storeListState as Resource.Error).message}")
            is Resource.Success -> {
                val list = (storeListState as Resource.Success).data
                if (list.isNullOrEmpty()) {
                    Text("Toko tidak ditemukan.")
                } else {
                    LazyColumn(
                        modifier = modifier
                    ) {
                        items(list) { toko ->
                            StoreItemsList(toko = toko)
                        }
                    }
                }
            }
        }
    }
}
