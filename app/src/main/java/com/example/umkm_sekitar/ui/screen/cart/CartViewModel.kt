package com.example.umkm_sekitar.ui.screen.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umkm_sekitar.data.model.CartItemWithProduct
import com.example.umkm_sekitar.data.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCartItemsWithProductDetails(userId: String): StateFlow<Map<String, List<CartItemWithProduct>>> {
        return storeRepository.getCartItems(userId)
            .flatMapLatest { cartItems ->
                if (cartItems.isEmpty()) {
                    flowOf(emptyMap())
                } else {
                    val flows = cartItems.map { item ->
                        combine(
                            storeRepository.getProductById(item.storeId, item.productId),
                            storeRepository.getStoreById(item.storeId)
                        ) { product, store ->
                            CartItemWithProduct(
                                cartItem = item,
                                product = product,
                                storeName = store?.storeName ?: "Toko Tidak Diketahui"
                            )
                        }
                    }

                    combine(flows) { array: Array<CartItemWithProduct> ->
                        array.groupBy { it.cartItem.storeId }
                    }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())
    }

    fun updateCheckedItem(userId: String, productId: String, isChecked: Boolean) {
        viewModelScope.launch {
            try {
                storeRepository.updateCartItemChecked(userId, productId, isChecked)
                    .collect { isSuccess ->
                        if (isSuccess) {
                            Log.d("CartViewModel", "Item updated successfully")
                        } else {
                            Log.e("CartViewModel", "Item update failed")
                        }
                    }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error updating checked status: ${e.message}")
            }
        }
    }

}





