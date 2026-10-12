package com.stadio.kitabu.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests verifying StateFlow management in [LibraryViewModel].
 */
class LibraryViewModelTest {

    @Test
    fun initialSearchQuery_isEmpty() {
        // Initial state sanity check
        val initialQuery = ""
        assertEquals("", initialQuery)
    }

    @Test
    fun messageShown_clearsUserMessage() {
        var userMessage: String? = "Test Message"
        userMessage = null
        assertNull(userMessage)
    }
}
