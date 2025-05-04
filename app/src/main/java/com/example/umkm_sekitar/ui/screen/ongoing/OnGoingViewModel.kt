package com.example.umkm_sekitar.ui.screen.ongoing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umkm_sekitar.data.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnGoingViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _updateStatusResult = MutableLiveData<Boolean>()
    val updateStatusResult: LiveData<Boolean> = _updateStatusResult

    fun updateOrderStatus(userId: String, orderId: String, newStatus: String) {
        viewModelScope.launch {
            storeRepository.updateOrderStatus(userId, orderId, newStatus)
                .collect { success ->
                    _updateStatusResult.value = success
                }
        }
    }
}
