package com.stadio.kitabu.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a single library book.
 * Indexed by category and title for performant database search queries.
 */
@Entity(
    tableName = "books",
    indices = [
        Index("category"),
        Index("title")
    ]
)
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val bookId: Long = 0L,
    val title: String,
    val author: String,
    val category: String,
    val isAvailable: Boolean = true
)
