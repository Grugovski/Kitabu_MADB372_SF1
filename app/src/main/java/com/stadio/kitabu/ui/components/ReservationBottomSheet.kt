package com.stadio.kitabu.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stadio.kitabu.data.BookEntity
import com.stadio.kitabu.domain.LoanRules

/**
 * Bottom sheet shown when a user reserves a book. The user enters a name / student
 * number and picks 7, 14 or 21 days. The name is cleaned and checked before confirming.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationBottomSheet(
    book: BookEntity,
    onDismiss: () -> Unit,
    onConfirm: (bookId: Long, userName: String, rentalDays: Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var studentName by rememberSaveable { mutableStateOf("") }
    var nameError by rememberSaveable { mutableStateOf(false) }
    var selectedDays by rememberSaveable { mutableStateOf(LoanRules.RENTAL_OPTIONS.first()) }

    val returnDate = LoanRules.formatDate(System.currentTimeMillis() + selectedDays * LoanRules.DAY_IN_MILLIS)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Reserve: ${book.title}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = studentName,
                onValueChange = {
                    // stop typing at the maximum length
                    if (it.length <= LoanRules.MAX_NAME_LENGTH) {
                        studentName = it
                        nameError = false
                    }
                },
                label = { Text("Student Name / ID") },
                singleLine = true,
                isError = nameError,
                supportingText = {
                    if (nameError) {
                        Text("Student Name / ID is required")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Rental Duration", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LoanRules.RENTAL_OPTIONS.forEach { days ->
                    FilterChip(
                        selected = selectedDays == days,
                        onClick = { selectedDays = days },
                        label = { Text("$days days") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Return by: $returnDate", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val cleanName = LoanRules.cleanName(studentName)
                    if (cleanName.isEmpty()) {
                        nameError = true
                    } else {
                        onConfirm(book.bookId, cleanName, selectedDays)
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp) // large main action button
            ) {
                Text("Confirm Reservation", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
