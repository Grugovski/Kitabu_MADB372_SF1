package com.stadio.kitabu.data

import com.stadio.kitabu.domain.ActionResult
import com.stadio.kitabu.domain.LoanRules
import kotlinx.coroutines.flow.Flow

/**
 * Sits between the ViewModel and the DAO. Validates input and applies the loan rules,
 * so the UI never talks to the DAO directly.
 */
class LibraryRepository(private val dao: LibraryDao) {

    fun getBooks(query: String, availableOnly: Boolean): Flow<List<BookEntity>> {
        val cleaned = LoanRules.cleanSearchQuery(query)
        return when {
            cleaned.isEmpty() && availableOnly -> dao.getAvailableBooks()
            cleaned.isEmpty() -> dao.getAllBooks()
            else -> dao.searchBooks(cleaned, availableOnly)
        }
    }

    fun getActiveBookings(): Flow<List<BookingWithBook>> = dao.getActiveBookingsWithBook()

    suspend fun reserveBook(bookId: Long, userName: String, rentalDays: Int): ActionResult {
        val name = LoanRules.cleanName(userName)
        if (name.isEmpty()) {
            return ActionResult.Error("Please enter your name or student number")
        }
        if (name.length > LoanRules.MAX_NAME_LENGTH) {
            return ActionResult.Error("Name is too long")
        }
        if (rentalDays !in LoanRules.RENTAL_OPTIONS) {
            return ActionResult.Error("Please choose a valid rental period")
        }

        val now = System.currentTimeMillis()
        val deadline = now + rentalDays * LoanRules.DAY_IN_MILLIS
        if (!LoanRules.isValidDeadline(now, deadline)) {
            return ActionResult.Error("Invalid return date")
        }

        val booking = BookingEntity(
            bookOwnerId = bookId,
            userName = name,
            bookingDate = now,
            returnDeadline = deadline,
            status = BookingStatus.PENDING
        )
        return if (dao.reserveBook(booking)) {
            ActionResult.Success
        } else {
            ActionResult.Error("Sorry, that book has just been reserved")
        }
    }

    // PENDING -> ACTIVE (student collects the book)
    suspend fun pickUpBooking(booking: BookingEntity): ActionResult {
        val changed = dao.changeStatus(booking.bookingId, BookingStatus.PENDING, BookingStatus.ACTIVE)
        return if (changed > 0) ActionResult.Success else ActionResult.Error("This booking was already collected")
    }

    suspend fun renewBooking(booking: BookingEntity): ActionResult {
        if (booking.status != BookingStatus.ACTIVE) {
            return ActionResult.Error("Collect the book before renewing it")
        }
        // an overdue book is renewed from today, not from its old deadline
        val startFrom = maxOf(booking.returnDeadline, System.currentTimeMillis())
        val newDeadline = startFrom + LoanRules.RENEWAL_DAYS * LoanRules.DAY_IN_MILLIS

        if (!LoanRules.isValidDeadline(booking.bookingDate, newDeadline)) {
            return ActionResult.Error("Maximum loan period of ${LoanRules.MAX_LOAN_DAYS} days reached")
        }
        val changed = dao.updateReturnDeadline(booking.bookingId, newDeadline)
        return if (changed > 0) ActionResult.Success else ActionResult.Error("Could not renew this booking")
    }

    suspend fun returnBooking(booking: BookingEntity): ActionResult {
        return if (dao.returnBook(booking.bookingId, booking.bookOwnerId)) {
            ActionResult.Success
        } else {
            ActionResult.Error("Only collected books can be returned")
        }
    }

    suspend fun cancelBooking(booking: BookingEntity): ActionResult {
        return if (dao.cancelPendingBooking(booking.bookingId, booking.bookOwnerId)) {
            ActionResult.Success
        } else {
            ActionResult.Error("Only reservations that are not collected yet can be cancelled")
        }
    }
}
