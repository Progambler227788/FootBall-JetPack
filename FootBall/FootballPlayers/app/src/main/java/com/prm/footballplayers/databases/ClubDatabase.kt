package com.prm.footballplayers.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.prm.footballplayers.dao.ClubDao
import com.prm.footballplayers.entities.Clubs

@Database(entities = [Clubs::class], version = 1, exportSchema = false)
abstract class ClubDatabase : RoomDatabase() {
    abstract fun clubDao(): ClubDao
}
