package com.famessa.deli_antojito

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.famessa.deli_antojito.repository.ProductoRepository
import com.famessa.deli_antojito.viewModels.HomeViewModel

class HomeViewModelFactory(private val repository: ProductoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}