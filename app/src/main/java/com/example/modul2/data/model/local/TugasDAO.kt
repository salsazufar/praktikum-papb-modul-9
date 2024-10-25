package com.example.modul2.data.model.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TugasDAO {
    @Query("SELECT * FROM tugas ORDER BY id DESC")
    fun getAllTugas(): LiveData<List<Tugas>>
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTugas(tugas: Tugas)

    @Query("UPDATE tugas SET selesai = :isCompleted WHERE id = :tugasId")
    fun updateTugasCompletion(tugasId: Int, isCompleted: Boolean)

    @Query("DELETE FROM tugas WHERE id = :tugasId")
    fun deleteTugas(tugasId: Int)
}