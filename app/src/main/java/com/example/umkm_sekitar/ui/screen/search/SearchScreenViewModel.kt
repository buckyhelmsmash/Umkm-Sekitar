package com.example.umkm_sekitar.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umkm_sekitar.data.model.Store
import com.example.umkm_sekitar.data.repository.StoreRepository
import com.example.umkm_sekitar.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResult = MutableStateFlow<Resource<List<Store>>>(Resource.Success(emptyList()))
    val searchResult: StateFlow<Resource<List<Store>>> = _searchResult

    init {
        observeSearch()
    }

    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isEmpty()) {
                        _searchResult.value = Resource.Success(emptyList())
                    } else {
                        _searchResult.value = Resource.Loading()
                        try {
                            storeRepository.getAllStore().collect { stores ->
                                val filtered = stores.filter {
                                    it.storeName.contains(query, true) ||
                                            it.location.contains(query, true) ||
                                            it.category.any { cat -> cat.contains(query, true) }
                                }
                                _searchResult.value = Resource.Success(filtered)
                            }
                        } catch (e: Exception) {
                            _searchResult.value = Resource.Error(e.message ?: "Unknown error")
                        }
                    }
                }
        }
    }
}

