package com.example.modul2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modul2.data.model.local.Tugas
import com.example.modul2.data.model.local.TugasRepository
import kotlinx.coroutines.launch

class TugasViewModel(private val tugasRepository: TugasRepository) : ViewModel() {

    val listTugas: LiveData<List<Tugas>> = tugasRepository.getAllTugas()

    fun addTugas(matkul: String, detail_tugas: String) {
        val newTugas = Tugas(matkul = matkul, detail_tugas = detail_tugas, selesai = false)
        viewModelScope.launch {
            tugasRepository.insert(newTugas)
        }
    }

    fun updateTugasCompletion(tugasId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            tugasRepository.updateTugasCompletion(tugasId, isCompleted)
        }
    }

    fun deleteTugas(id: Int) {
        viewModelScope.launch {
            tugasRepository.deleteTugas(id)
        }
    }
}