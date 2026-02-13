package com.accesodatos.minipokedex.core.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String?,
    val typesPipe: String, // Comentario: guardamos tipos como "grass|poison".
    val colorArgb: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val historyId: Long = 0,
    val query: String,
    val pokemonId: Int,
    val source: String,
    val timestamp: Long = System.currentTimeMillis()
)
