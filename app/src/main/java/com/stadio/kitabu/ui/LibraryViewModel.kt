package com.stadio.kitabu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stadio.kitabu.KitabuApplication
import com.stadio.kitabu.data.BookEntity
import com.stadio.kitabu.data.BookingEntity
import com.stadio.kitabu.data.BookingWithBook
import com.stadio.kitabu.data.LibraryRepository
import com.stadio.kitabu.domain.ActionResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * One ViewModel shared by both screens, so the catalog badges and the
 * reservations dashboard always show the same data.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModel(private val repository: LibraryRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _availableOnly = MutableStateFlow(false)
    val availableOnly: StateFlow<Boolean> = _availableOnly.asStateFlow()

    // One-off message for the snackbar (success or error)
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Re-queries Room every time the search text or the filter changes
    val books: StateFlow<List<BookEntity>> = combine(_searchQuery, _availableOnly) { query, onlyAvailable ->
        query to onlyAvailable
    }
        .flatMapLatest { (query, onlyAvailable) ->
            repository.getBooks(query, onlyAvailable)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeBookings: StateFlow<List<BookingWithBook>> = repository.getActiveBookings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onAvailableOnlyChange(value: Boolean) {
        _availableOnly.value = value
    }

    fun messageShown() {
        _userMessage.value = null
    }

    fun reserveBook(bookId: Long, userName: String, rentalDays: Int) {
        viewModelScope.launch {
            val result = repository.reserveBook(bookId, userName, rentalDays)
            showResult(result, "Reserved! Collect it at the library desk.")
        }
    }

    fun pickUpBooking(booking: BookingEntity) {
        viewModelScope.launch {
            showResult(repository.pickUpBooking(booking), "Enjoy your book!")
        }
    }

    fun renewBooking(booking: BookingEntity) {
        viewModelScope.launch {
            showResult(repository.renewBooking(booking), "Renewed for 7 more days.")
        }
    }

    fun returnBooking(booking: BookingEntity) {
        viewModelScope.launch {
            showResult(repository.returnBooking(booking), "Book returned. Thank you!")
        }
    }

    fun cancelBooking(booking: BookingEntity) {
        viewModelScope.launch {
            showResult(repository.cancelBooking(booking), "Reservation cancelled.")
        }
    }

    private fun showResult(result: ActionResult, successMessage: String) {
        _userMessage.value = when (result) {
            is ActionResult.Success -> successMessage
            is ActionResult.Error -> result.message
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KitabuApplication
                LibraryViewModel(app.container.repository)
            }
        }
    }
}
