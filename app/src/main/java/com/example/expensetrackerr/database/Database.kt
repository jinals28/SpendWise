package com.example.expensetrackerr.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlin.concurrent.Volatile

@Database(entities = [Transaction::class], version = 1)
abstract class Database : RoomDatabase() {

    abstract fun getDao() : Dao

    companion object {

        @Volatile
        private var INSTANCE : com.example.expensetrackerr.database.Database? = null

        fun getDatabase(context: Context) : com.example.expensetrackerr.database.Database {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    com.example.expensetrackerr.database.Database::class.java,
                    "transaction_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}