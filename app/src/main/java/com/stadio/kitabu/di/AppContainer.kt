package com.stadio.kitabu.di

import android.content.Context
import com.stadio.kitabu.data.AppDatabase
import com.stadio.kitabu.data.LibraryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Simple manual dependency injection: creates the database and repository once
 * for the whole app. The scope lives as long as the app, so seeding can't be cancelled.
 */
class AppContainer(context: Context) {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val database = AppDatabase.getDatabase(context, appScope)

    val repository = LibraryRepository(database.libraryDao())
}
