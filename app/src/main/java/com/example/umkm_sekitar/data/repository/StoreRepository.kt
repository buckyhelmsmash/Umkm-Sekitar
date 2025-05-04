package com.example.umkm_sekitar.data.repository

import com.example.umkm_sekitar.data.model.CartItem
import com.example.umkm_sekitar.data.model.CheckoutOrder
import com.example.umkm_sekitar.data.model.Product
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

    fun getStoreById(storeId: String): Flow<Store?> =
        firebaseDataSource.getStoreById(storeId)

    fun addToCart(
        userId: String,
        storeId: String,
        productId: String,
        quantity: Int
    ): Flow<Boolean> =
        firebaseDataSource.addToCart(userId, storeId, productId, quantity)

    fun getCartItems(userId: String): Flow<List<CartItem>> =
        firebaseDataSource.getCartItems(userId)

    fun getProductById(storeId: String, productId: String): Flow<Product?> =
        firebaseDataSource.getProductById(storeId, productId)

    fun saveCheckoutOrder(checkoutOrder: CheckoutOrder): Flow<Boolean> {
        return firebaseDataSource.saveCheckoutOrder(checkoutOrder)
    }

    fun getCheckoutOrdersByUserId(userId: String): Flow<List<CheckoutOrder>> {
        return firebaseDataSource.getCheckoutOrdersByUserId(userId)
    }

    fun updateCartItemChecked(userId: String, productId: String, isChecked: Boolean): Flow<Boolean> {
        return firebaseDataSource.updateCartItemChecked(userId, productId, isChecked)
    }

    fun updateOrderStatus(userId: String, orderId: String, newStatus: String): Flow<Boolean> {
        return firebaseDataSource.updateCheckoutOrderStatus(userId, orderId, newStatus)
    }


}
