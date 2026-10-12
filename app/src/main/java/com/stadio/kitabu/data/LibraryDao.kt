package com.stadio.kitabu.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * All book and booking queries. Suspend functions and Flow queries are run
 * by Room on its own background threads, so nothing here blocks the main thread.
 */
@Dao
interface LibraryDao {

    // ---------- Create ----------

    @Insert
    suspend fun insertBook(book: BookEntity): Long

    @Insert
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert
    suspend fun insertBooking(booking: BookingEntity): Long

    // ---------- Read ----------

    @Query("SELECT * FROM books ORDER BY bookId ASC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isAvailable = 1 ORDER BY bookId ASC")
    fun getAvailableBooks(): Flow<List<BookEntity>>

    @Query("SELECT DISTINCT category FROM books ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    // LIKE search on title or author, optionally filtered by category and availability
    @Query(
        "SELECT * FROM books " +
            "WHERE (title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%') " +
            "AND (:category IS NULL OR category = :category) " +
            "AND (:availableOnly = 0 OR isAvailable = 1) " +
            "ORDER BY bookId ASC"
    )
    fun searchAndFilterBooks(query: String, category: String?, availableOnly: Boolean): Flow<List<BookEntity>>

    // LIKE search on title or author, optionally only the available books
    @Query(
        "SELECT * FROM books " +
            "WHERE (title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%') " +
            "AND (:availableOnly = 0 OR isAvailable = 1) " +
            "ORDER BY bookId ASC"
    )
    fun searchBooks(query: String, availableOnly: Boolean): Flow<List<BookEntity>>

    // Pending + active bookings joined with their book (returned ones are hidden)
    @Query(
        "SELECT * FROM bookings " +
            "INNER JOIN books ON bookings.bookOwnerId = books.bookId " +
            "WHERE bookings.status != 'RETURNED' " +
            "ORDER BY bookings.returnDeadline ASC"
    )
    fun getActiveBookingsWithBook(): Flow<List<BookingWithBook>>

    // ---------- Update ----------

    // Only flips the book if it is still available. Returns the rows changed (0 = already taken).
    @Query("UPDATE books SET isAvailable = 0 WHERE bookId = :bookId AND isAvailable = 1")
    suspend fun markBookUnavailable(bookId: Long): Int

    @Query("UPDATE books SET isAvailable = 1 WHERE bookId = :bookId")
    suspend fun markBookAvailable(bookId: Long)

    // Only moves a booking from oldStatus to newStatus (PENDING -> ACTIVE, ACTIVE -> RETURNED)
    @Query("UPDATE bookings SET status = :newStatus WHERE bookingId = :bookingId AND status = :oldStatus")
    suspend fun changeStatus(bookingId: Long, oldStatus: BookingStatus, newStatus: BookingStatus): Int

    // Renew: only active bookings can have their deadline extended
    @Query("UPDATE bookings SET returnDeadline = :newDeadline WHERE bookingId = :bookingId AND status = 'ACTIVE'")
    suspend fun updateReturnDeadline(bookingId: Long, newDeadline: Long): Int

    // ---------- Delete ----------

    // Cancel: only bookings that have not been picked up yet
    @Query("DELETE FROM bookings WHERE bookingId = :bookingId AND status = 'PENDING'")
    suspend fun deletePendingBooking(bookingId: Long): Int

    // ---------- Transactions (all steps succeed or none do) ----------

    @Transaction
    suspend fun reserveBook(booking: BookingEntity): Boolean {
        if (markBookUnavailable(booking.bookOwnerId) == 0) return false
        insertBooking(booking)
        return true
    }

    @Transaction
    suspend fun returnBook(bookingId: Long, bookId: Long): Boolean {
        val changed = changeStatus(bookingId, BookingStatus.ACTIVE, BookingStatus.RETURNED)
        if (changed == 0) return false
        markBookAvailable(bookId)
        return true
    }

    @Transaction
    suspend fun cancelPendingBooking(bookingId: Long, bookId: Long): Boolean {
        if (deletePendingBooking(bookingId) == 0) return false
        markBookAvailable(bookId)
        return true
    }
}
