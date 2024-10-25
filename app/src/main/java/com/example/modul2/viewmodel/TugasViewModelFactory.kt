package com.example.modul2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.modul2.data.model.local.TugasRepository
import com.example.modul2.viewmodel.TugasViewModel

class TugasViewModelFactory(
    private val tugasRepository: TugasRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TugasViewModel::class.java)) {
            return TugasViewModel(tugasRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}