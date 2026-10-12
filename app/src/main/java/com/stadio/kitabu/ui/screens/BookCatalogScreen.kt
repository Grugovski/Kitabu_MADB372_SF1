package com.stadio.kitabu.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stadio.kitabu.data.BookEntity
import com.stadio.kitabu.ui.LibraryViewModel
import com.stadio.kitabu.ui.components.BookCard
import com.stadio.kitabu.ui.components.ReservationBottomSheet

/**
 * Screen 1 (stateful): collects state from the ViewModel and hands it to
 * [BookCatalogContent], which only displays what it is given.
 */
@Composable
fun BookCatalogScreen(viewModel: LibraryViewModel) {
    val books by viewModel.books.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val availableOnly by viewModel.availableOnly.collectAsStateWithLifecycle()

    BookCatalogContent(
        books = books,
        searchQuery = searchQuery,
        availableOnly = availableOnly,
        onSearchChange = viewModel::onSearchQueryChange,
        onAvailableOnlyChange = viewModel::onAvailableOnlyChange,
        onReserve = viewModel::reserveBook
    )
}

/**
 * Screen 1 (stateless): search bar, filter chip and the list of books.
 * Tapping an available book opens the reservation bottom sheet.
 */
@Composable
fun BookCatalogContent(
    books: List<BookEntity>,
    searchQuery: String,
    availableOnly: Boolean,
    onSearchChange: (String) -> Unit,
    onAvailableOnlyChange: (Boolean) -> Unit,
    onReserve: (bookId: Long, userName: String, rentalDays: Int) -> Unit
) {
    // only the id is stored so it survives rotation; the book is looked up from the list
    var selectedBookId by rememberSaveable { mutableStateOf<Long?>(null) }
    val selectedBook = books.firstOrNull { it.bookId == selectedBookId && it.isAvailable }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            label = { Text("Search by title or author") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        FilterChip(
            selected = availableOnly,
            onClick = { onAvailableOnlyChange(!availableOnly) },
            label = { Text("Available only") },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (books.isEmpty() && (searchQuery.isNotBlank() || availableOnly)) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No books found")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items = books, key = { it.bookId }) { book ->
                    BookCard(
                        book = book,
                        onReserveClick = { selectedBookId = it.bookId }
                    )
                }
            }
        }
    }

    selectedBook?.let { book ->
        ReservationBottomSheet(
            book = book,
            onDismiss = { selectedBookId = null },
            onConfirm = { bookId, userName, days ->
                onReserve(bookId, userName, days)
                selectedBookId = null
            }
        )
    }
}
