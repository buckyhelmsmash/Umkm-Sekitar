package com.example.umkm_sekitar.ui.screen.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umkm_sekitar.data.model.CheckoutOrder
import com.example.umkm_sekitar.data.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val storeRepository: StoreRepository
): ViewModel() {

    private val _checkoutOrders = MutableStateFlow<List<CheckoutOrder>>(emptyList())
    val checkoutOrders: StateFlow<List<CheckoutOrder>> = _checkoutOrders.asStateFlow()


    fun fetchCheckoutOrders(userId: String) {
        viewModelScope.launch {
            storeRepository.getCheckoutOrdersByUserId(userId)
                .catch { e ->
                }
                .collect { checkoutOrderList ->
                    _checkoutOrders.value = checkoutOrderList
                }
        }
    }
}