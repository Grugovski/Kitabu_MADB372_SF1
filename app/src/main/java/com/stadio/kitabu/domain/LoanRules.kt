package com.stadio.kitabu.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * Loan rules, input cleaning and date helpers in one place.
 */
object LoanRules {
    const val DAY_IN_MILLIS = 24L * 60L * 60L * 1000L
    const val RENEWAL_DAYS = 7
    const val MAX_LOAN_DAYS = 42
    const val MAX_NAME_LENGTH = 40

    val RENTAL_OPTIONS = listOf(7, 14, 21)

    // Trim and collapse repeated spaces: "  Thabo   M " -> "Thabo M"
    fun cleanName(raw: String): String = raw.trim().replace(Regex("\\s+"), " ")

    // % and _ are LIKE wildcards, so they are removed from the search text
    fun cleanSearchQuery(raw: String): String =
        raw.trim().replace("%", "").replace("_", "")

    // Deadline must be after the booking date and not longer than the maximum loan period
    fun isValidDeadline(bookingDate: Long, deadline: Long): Boolean {
        return deadline > bookingDate &&
            deadline - bookingDate <= MAX_LOAN_DAYS * DAY_IN_MILLIS
    }

    // Whole calendar days from today until the deadline (negative = overdue)
    fun daysRemaining(deadline: Long, today: LocalDate = LocalDate.now()): Long {
        val dueDate = Instant.ofEpochMilli(deadline)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        return ChronoUnit.DAYS.between(today, dueDate)
    }

    fun formatDate(millis: Long): String {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(formatter)
    }
}
