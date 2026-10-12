package com.stadio.kitabu.domain

/**
 * Outcome of a library action, so the UI can show a message instead of failing silently.
 */
sealed class ActionResult {
    data object Success : ActionResult()
    data class Error(val message: String) : ActionResult()

    companion object {
        const val ERROR_INVALID_NAME = "Please enter your name or student number"
        const val ERROR_NAME_TOO_LONG = "Name is too long"
        const val ERROR_INVALID_RENTAL_DAYS = "Please choose a valid rental period"
        const val ERROR_BOOK_ALREADY_RESERVED = "Sorry, that book has just been reserved"
    }
}
