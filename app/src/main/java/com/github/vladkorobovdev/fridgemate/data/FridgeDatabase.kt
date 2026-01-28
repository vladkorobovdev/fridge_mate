package com.github.vladkorobovdev.fridgemate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class FridgeDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: FridgeDatabase? = null

        fun getDatabase(context: Context): FridgeDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    FridgeDatabase::class.java,
                    "fridge_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}