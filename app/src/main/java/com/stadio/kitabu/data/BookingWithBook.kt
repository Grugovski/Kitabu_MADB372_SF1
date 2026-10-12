package com.stadio.kitabu.data

import androidx.room.Embedded

/**
 * Result row of the bookings JOIN books query.
 * The two tables have no column names in common, so no prefix is needed.
 */
data class BookingWithBook(
    @Embedded val booking: BookingEntity,
    @Embedded val book: BookEntity
)
