package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CivicIssueEntity::class], version = 2, exportSchema = false)
abstract class CivicDatabase : RoomDatabase() {
  abstract fun civicIssueDao(): CivicIssueDao

  companion object {
    @Volatile
    private var INSTANCE: CivicDatabase? = null

    fun getDatabase(context: Context): CivicDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          CivicDatabase::class.java,
          "civicfix_database"
        ).fallbackToDestructiveMigration(true).build()
        INSTANCE = instance
        instance
      }
    }
  }
}
