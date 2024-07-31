package com.prm.footballplayers.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.prm.footballplayers.dao.LeagueDao
import com.prm.footballplayers.entities.League

@Database(entities = [League::class], version = 1, exportSchema = false)
abstract class DatabaseLeague: RoomDatabase() {
    abstract fun leagueDao(): LeagueDao
}
