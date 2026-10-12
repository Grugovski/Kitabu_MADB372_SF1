package com.stadio.kitabu.domain

import com.stadio.kitabu.domain.LoanRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for business rules governing library loans and reservations.
 */
class LoanRulesTest {

    @Test
    fun cleanName_removesSurroundingWhitespace() {
        val input = "   Garreth Pietersen   "
        val result = LoanRules.cleanName(input)
        assertEquals("Garreth Pietersen", result)
    }

    @Test
    fun cleanSearchQuery_removesLeadingTrailingWhitespace() {
        val input = "  Android Development  "
        val result = LoanRules.cleanSearchQuery(input)
        assertEquals("Android Development", result)
    }

    @Test
    fun isValidDeadline_acceptsValidRange() {
        val now = System.currentTimeMillis()
        val deadline = now + 7 * LoanRules.DAY_IN_MILLIS
        assertTrue(LoanRules.isValidDeadline(now, deadline))
    }

    @Test
    fun isValidDeadline_rejectsPastDeadline() {
        val now = System.currentTimeMillis()
        val pastDeadline = now - LoanRules.DAY_IN_MILLIS
        assertFalse(LoanRules.isValidDeadline(now, pastDeadline))
    }

    @Test
    fun isValidDeadline_rejectsExcessiveLoanDuration() {
        val now = System.currentTimeMillis()
        val excessiveDeadline = now + (LoanRules.MAX_LOAN_DAYS + 1) * LoanRules.DAY_IN_MILLIS
        assertFalse(LoanRules.isValidDeadline(now, excessiveDeadline))
    }

    @Test
    fun rentalOptions_containsValidDays() {
        assertTrue(LoanRules.RENTAL_OPTIONS.contains(7))
        assertTrue(LoanRules.RENTAL_OPTIONS.contains(14))
        assertTrue(LoanRules.RENTAL_OPTIONS.contains(21))
    }
}
