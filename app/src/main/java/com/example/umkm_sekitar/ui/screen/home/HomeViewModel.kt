package com.example.umkm_sekitar.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umkm_sekitar.data.model.Store
import com.example.umkm_sekitar.data.repository.StoreRepository
import com.example.umkm_sekitar.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _storeList = MutableStateFlow<Resource<List<Store>>>(Resource.Loading())
    val storeList: StateFlow<Resource<List<Store>>> = _storeList

    init {
        getAllStore()
    }

    fun getAllStore() {
        viewModelScope.launch {
            _storeList.value = Resource.Loading()
            storeRepository.getAllStore()
                .catch { e ->
                    _storeList.value = Resource.Error(e.message ?: "Unknown error occurred")
                }
                .collect { storeList ->
                    _storeList.value = Resource.Success(storeList)
                }
        }
    }
}
