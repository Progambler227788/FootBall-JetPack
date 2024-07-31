package com.prm.footballplayers.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prm.footballplayers.entities.Clubs

@Dao
interface ClubDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClub(club: Clubs)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllClubs(clubs: List<Clubs>)

    @Query("SELECT * FROM clubs")
    suspend fun getAllClubs(): List<Clubs>

    @Query("SELECT * FROM clubs WHERE name LIKE :query OR league LIKE :query")
    fun searchClubs(query: String): List<Clubs>
}
