package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CircuitDesignDao {

    @Query("SELECT * FROM circuit_designs ORDER BY timestamp DESC")
    fun getAllDesigns(): Flow<List<CircuitDesignEntity>>

    @Query("SELECT * FROM circuit_designs WHERE id = :id LIMIT 1")
    suspend fun getDesignById(id: Long): CircuitDesignEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDesign(design: CircuitDesignEntity): Long

    @Update
    suspend fun updateDesign(design: CircuitDesignEntity)

    @Delete
    suspend fun deleteDesign(design: CircuitDesignEntity)

    @Query("DELETE FROM circuit_designs WHERE id = :id")
    suspend fun deleteDesignById(id: Long)
}
