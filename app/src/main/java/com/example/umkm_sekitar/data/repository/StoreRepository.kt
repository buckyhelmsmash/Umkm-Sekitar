package com.example.umkm_sekitar.data.repository

import com.example.umkm_sekitar.data.model.Store
import com.example.umkm_sekitar.data.source.FirebaseDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    fun getAllStore(): Flow<List<Store>> = firebaseDataSource.getAllStore()

    fun getStoreByCategory(category: String): Flow<List<Store>> =
        firebaseDataSource.getStoreByCategory(category)

    fun getStoreByLocation(location: String): Flow<List<Store>> =
        firebaseDataSource.getStoreByLocation(location)
}
