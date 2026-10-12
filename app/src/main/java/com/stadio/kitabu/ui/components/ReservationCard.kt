package com.stadio.kitabu.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stadio.kitabu.data.BookingStatus
import com.stadio.kitabu.data.BookingWithBook
import com.stadio.kitabu.domain.LoanRules

/**
 * Card for one pending/active booking.
 * PENDING: can be collected or cancelled. ACTIVE: can be renewed or returned.
 */
@Composable
fun ReservationCard(
    bookingWithBook: BookingWithBook,
    onPickUp: () -> Unit,
    onRenew: () -> Unit,
    onReturn: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val booking = bookingWithBook.booking
    val book = bookingWithBook.book
    val isPending = booking.status == BookingStatus.PENDING
    val isActive = booking.status == BookingStatus.ACTIVE

    val daysLeft = LoanRules.daysRemaining(booking.returnDeadline)

    val remainingLabel = when {
        daysLeft < 0 -> "Overdue by ${-daysLeft} day(s)"
        daysLeft == 0L -> "Due today"
        daysLeft == 1L -> "1 day remaining"
        else -> "$daysLeft days remaining"
    }
    val remainingColor = when {
        daysLeft < 0 -> BadgeRed
        daysLeft <= 2 -> BadgeOrange
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(vertical = 6.dp, horizontal = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (isPending) {
                    StatusBadge(label = "Pending pickup", color = BadgeOrange)
                } else {
                    StatusBadge(label = "Active", color = BadgeGreen)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "by ${book.author} • ${book.category}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Reserved by: ${booking.userName}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Booked: ${LoanRules.formatDate(booking.bookingDate)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Due: ${LoanRules.formatDate(booking.returnDeadline)}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = remainingLabel,
                color = remainingColor,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (isPending) {
                // medium full-width button (48dp)
                Button(
                    onClick = onPickUp,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Confirm pickup")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // all three lifecycle buttons stay visible; only the ones that apply are enabled
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onRenew,
                    enabled = isActive,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) { Text("Renew") }
                OutlinedButton(
                    onClick = onReturn,
                    enabled = isActive,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) { Text("Return") }
                TextButton(
                    onClick = onCancel,
                    enabled = isPending,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .weight(0.8f)
                        .height(40.dp)
                ) { Text("Cancel") }
            }
        }
    }
}
