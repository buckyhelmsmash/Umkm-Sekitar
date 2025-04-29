package com.example.umkm_sekitar.data.repository

import com.example.umkm_sekitar.data.model.Toko
import com.example.umkm_sekitar.data.source.FirebaseDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TokoRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    fun getAllToko(): Flow<List<Toko>> = firebaseDataSource.getAllToko()

    fun getTokoByCategory(category: String): Flow<List<Toko>> =
        firebaseDataSource.getTokoByCategory(category)

    fun getTokoByLocation(location: String): Flow<List<Toko>> =
        firebaseDataSource.getTokoByLocation(location)
}
