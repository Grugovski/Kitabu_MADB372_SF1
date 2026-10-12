package com.stadio.kitabu.data

import androidx.room.TypeConverter

/**
 * Room type converters for enum <-> String mapping.
 */
class Converters {
    @TypeConverter
    fun fromBookingStatus(status: BookingStatus): String = status.name

    @TypeConverter
    fun toBookingStatus(value: String): BookingStatus = BookingStatus.valueOf(value)
}
