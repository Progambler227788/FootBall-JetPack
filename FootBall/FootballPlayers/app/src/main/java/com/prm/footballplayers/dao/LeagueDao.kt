package com.prm.footballplayers.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prm.footballplayers.entities.League

@Dao
interface LeagueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeagues(leagues: List<League>)

    @Query("SELECT * FROM leagues")
    suspend fun getAllLeagues(): List<League>
}
