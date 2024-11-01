package com.example.modul2.data.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Tugas::class], version = 2)
abstract class TugasDB : RoomDatabase() {
    abstract fun tugasDAO(): TugasDAO

    companion object{
        @Volatile
        private var NAME : TugasDB? = null
        @JvmStatic
        fun getDatabase(context: Context): TugasDB {
            if(NAME == null) {
                synchronized(TugasDB::class.java){
                    NAME = Room.databaseBuilder(context.applicationContext,
                        TugasDB::class.java, "tugas_db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return NAME as TugasDB
        }
    }
}


