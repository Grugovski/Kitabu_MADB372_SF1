package com.stadio.kitabu.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Room database (singleton). Sample books are inserted the first time it is created.
 */
@Database(
    entities = [BookEntity::class, BookingEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun libraryDao(): LibraryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kitabu_database"
                )
                    .addCallback(KitabuDatabaseCallback(scope))
                    .fallbackToDestructiveMigration() // fine while developing
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }

    private class KitabuDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateSampleBooks(database.libraryDao())
                }
            }
        }

        private suspend fun populateSampleBooks(dao: LibraryDao) {
            // BITWEB 3rd year style textbooks (mobile, web, database and software engineering)
            val sampleBooks = listOf(
                BookEntity(title = "Head First Android Development (3rd Edition)", author = "Dawn Griffiths & David Griffiths", category = "Mobile Development"),
                BookEntity(title = "Head First Kotlin", author = "Dawn Griffiths & David Griffiths", category = "Mobile Development"),
                BookEntity(title = "Kotlin in Action", author = "Dmitry Jemerov & Svetlana Isakova", category = "Programming"),
                BookEntity(title = "Effective Kotlin", author = "Marcin Moskala", category = "Programming"),
                BookEntity(title = "Learning Web Design (5th Edition)", author = "Jennifer Niederst Robbins", category = "Web Development"),
                BookEntity(title = "JavaScript: The Definitive Guide (7th Edition)", author = "David Flanagan", category = "Web Development"),
                BookEntity(title = "Learning PHP, MySQL & JavaScript (5th Edition)", author = "Robin Nixon", category = "Web Development"),
                BookEntity(title = "Database Systems: Design, Implementation & Management", author = "Carlos Coronel & Steven Morris", category = "Databases"),
                BookEntity(title = "Software Engineering (10th Edition)", author = "Ian Sommerville", category = "Software Engineering"),
                BookEntity(title = "Clean Code", author = "Robert C. Martin", category = "Software Engineering")
            )
            dao.insertBooks(sampleBooks)
        }
    }
}
