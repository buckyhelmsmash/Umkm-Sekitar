package com.example.umkm_sekitar.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umkm_sekitar.data.model.Toko
import com.example.umkm_sekitar.data.repository.TokoRepository
import com.example.umkm_sekitar.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tokoRepository: TokoRepository
) : ViewModel() {

    private val _tokoList = MutableStateFlow<Resource<List<Toko>>>(Resource.Loading())
    val tokoList: StateFlow<Resource<List<Toko>>> = _tokoList

    private val _filteredTokoList = MutableStateFlow<Resource<List<Toko>>>(Resource.Loading())
    val filteredTokoList: StateFlow<Resource<List<Toko>>> = _filteredTokoList

    init {
        getAllToko()
    }

    fun getAllToko() {
        viewModelScope.launch {
            _tokoList.value = Resource.Loading()
            tokoRepository.getAllToko()
                .catch { e ->
                    _tokoList.value = Resource.Error(e.message ?: "Unknown error occurred")
                }
                .collect { tokoList ->
                    _tokoList.value = Resource.Success(tokoList)
                }
        }
    }

    fun filterTokoByCategory(category: String) {
        viewModelScope.launch {
            _filteredTokoList.value = Resource.Loading()
            tokoRepository.getTokoByCategory(category)
                .catch { e ->
                    _filteredTokoList.value = Resource.Error(e.message ?: "Unknown error occurred")
                }
                .collect { tokoList ->
                    _filteredTokoList.value = Resource.Success(tokoList)
                }
        }
    }

    fun filterTokoByLocation(location: String) {
        viewModelScope.launch {
            _filteredTokoList.value = Resource.Loading()
            tokoRepository.getTokoByLocation(location)
                .catch { e ->
                    _filteredTokoList.value = Resource.Error(e.message ?: "Unknown error occurred")
                }
                .collect { tokoList ->
                    _filteredTokoList.value = Resource.Success(tokoList)
                }
        }
    }
}
