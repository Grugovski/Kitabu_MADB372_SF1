package com.stadio.kitabu.domain

/**
 * Outcome of a library action, so the UI can show a message instead of failing silently.
 */
sealed class ActionResult {
    data object Success : ActionResult()
    data class Error(val message: String) : ActionResult()
}
