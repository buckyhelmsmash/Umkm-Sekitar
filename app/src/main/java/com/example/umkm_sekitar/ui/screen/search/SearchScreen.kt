package com.example.umkm_sekitar.ui.screen.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.umkm_sekitar.R
import com.example.umkm_sekitar.ui.component.CustomSearchBar
import com.example.umkm_sekitar.ui.component.StoreItemsList
import com.example.umkm_sekitar.util.Resource


@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchScreenViewModel = hiltViewModel(),
    navController: NavController
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResult by viewModel.searchResult.collectAsState()

    Column(modifier = modifier.padding(16.dp). padding(top = 24.dp)) {
        CustomSearchBar(
            value = searchQuery,
            onValueChange = viewModel::updateQuery
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (searchQuery.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Toko yang kamu cari akan muncul dibawah, ketik toko yang ingin kamu cari!",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.search_illustration),
                        contentDescription = "Ilustrasi pencarian toko",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        } else {
            when (searchResult) {
                is Resource.Loading -> CircularProgressIndicator()
                is Resource.Error -> Text("Terjadi kesalahan: ${(searchResult as Resource.Error).message}")
                is Resource.Success -> {
                    val list = (searchResult as Resource.Success).data.orEmpty()

                    when {
                        list.isEmpty() -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Toko yang dicari tidak ada.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        else -> {
                            Text(
                                text = "Hasil dari pencarian: \"$searchQuery\"",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            LazyColumn {
                                items(list) {
                                    StoreItemsList(toko = it, navController = navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
