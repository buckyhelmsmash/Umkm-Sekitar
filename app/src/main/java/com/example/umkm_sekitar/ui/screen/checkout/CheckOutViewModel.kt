package com.example.umkm_sekitar.ui.screen.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umkm_sekitar.R
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
class CheckOutViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
): ViewModel() {
    private val _selectedIndex = MutableStateFlow(0)
    private val _orderId = MutableStateFlow("")
    val selectedIndex: StateFlow<Int> = _selectedIndex
    val orderId: StateFlow<String> = _orderId

    private val _checkoutOrders = MutableStateFlow<List<CheckoutOrder>>(emptyList())
    val checkoutOrders: StateFlow<List<CheckoutOrder>> = _checkoutOrders.asStateFlow()

    private val _isCheckoutOrderSuccessful = MutableStateFlow(false)
    val isCheckoutOrderSuccessful: StateFlow<Boolean> = _isCheckoutOrderSuccessful


    val paymentMethods = listOf(
        R.drawable.cash to "Cash di Toko",
        R.drawable.ovo to "OVO",
        R.drawable.bca to "BCA",
        R.drawable.bni to "BNI",
        R.drawable.bri to "BRI"
    )

    fun saveCheckoutOrder(checkoutOrder: CheckoutOrder) {
        viewModelScope.launch {
            storeRepository.saveCheckoutOrder(checkoutOrder)
                .catch { e ->
                    _isCheckoutOrderSuccessful.value = false
                }
                .collect { success ->
                    _isCheckoutOrderSuccessful.value = success
                }
        }
    }

    fun selectPaymentMethod(index: Int) {
        _selectedIndex.value = index
    }
}
