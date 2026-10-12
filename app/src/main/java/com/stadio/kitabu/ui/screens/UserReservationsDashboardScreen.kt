package com.stadio.kitabu.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stadio.kitabu.data.BookingEntity
import com.stadio.kitabu.data.BookingWithBook
import com.stadio.kitabu.ui.LibraryViewModel
import com.stadio.kitabu.ui.components.ReservationCard

/**
 * Screen 2 (stateful): collects the active bookings from the shared ViewModel.
 */
@Composable
fun UserReservationsDashboardScreen(viewModel: LibraryViewModel) {
    val bookings by viewModel.activeBookings.collectAsStateWithLifecycle()

    ReservationsContent(
        bookings = bookings,
        onPickUp = viewModel::pickUpBooking,
        onRenew = viewModel::renewBooking,
        onReturn = viewModel::returnBooking,
        onCancel = viewModel::cancelBooking
    )
}

/**
 * Screen 2 (stateless): list of pending/active bookings with their action buttons.
 */
@Composable
fun ReservationsContent(
    bookings: List<BookingWithBook>,
    onPickUp: (BookingEntity) -> Unit,
    onRenew: (BookingEntity) -> Unit,
    onReturn: (BookingEntity) -> Unit,
    onCancel: (BookingEntity) -> Unit
) {
    if (bookings.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active reservations")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
        ) {
            items(items = bookings, key = { it.booking.bookingId }) { item ->
                ReservationCard(
                    bookingWithBook = item,
                    onPickUp = { onPickUp(item.booking) },
                    onRenew = { onRenew(item.booking) },
                    onReturn = { onReturn(item.booking) },
                    onCancel = { onCancel(item.booking) }
                )
            }
        }
    }
}
