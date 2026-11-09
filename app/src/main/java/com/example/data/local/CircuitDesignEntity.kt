package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "circuit_designs")
data class CircuitDesignEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val componentsJson: String,
    val wiresJson: String,
    val componentCount: Int = 0
)
