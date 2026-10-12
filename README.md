Kitabu - MADB372 SF1
Student: 23106807 (Garreth Pietersen)

Library book rental and reservation app built with Kotlin, Jetpack Compose (Material 3),
Room (KSP), Coroutines/Flow and Navigation Compose.

How to run
1. Unzip and open the `Kitabu_MADB372_SF1` folder in Android Studio (Ladybug or newer). Set Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK to JDK 17.
2. Let Gradle sync (Room, Navigation Compose, Lifecycle and the Compose BOM are in `app/build.gradle.kts`).
3. Run on an emulator or device with API 26+.

Books are (Head First Android Development 3rd ed., Kotlin, web, database and software engineering textbooks)

How the app works
1. Catalog – search by title/author, tick *Available only*, tap an available book (or *Reserve*).
2. Reserve – bottom sheet asks for a name/student number and 7, 14 or 21 days. The booking is saved as `PENDING` and the book becomes *Borrowed*.
3. Reservations tab – a pending booking can be collected (`ACTIVE`) or cancelled (row deleted, book available again).
4. An active booking can be renewed (+7 days, max 42 days in total) or returned (`RETURNED`, book available again).

Project structure
com.stadio.kitabu
├── KitabuApplication.kt      creates the AppContainer
├── MainActivity.kt           single activity
├── data/                     BookEntity, BookingEntity, BookingStatus, BookingWithBook,
│                             Converters, LibraryDao, AppDatabase, LibraryRepository
├── di/                       AppContainer (database + repository created once)
├── domain/                   LoanRules (rules, validation, dates), ActionResult
└── ui/                       KitabuApp (NavHost), LibraryViewModel
    ├── theme/                Color, Type, Theme (green KitabuTheme)
    ├── screens/              BookCatalogScreen, UserReservationsDashboardScreen
    └── components/           BookCard, ReservationCard, ReservationBottomSheet, StatusBadge


Theme and design
- Green Material 3 colour scheme in `ui/theme` (Color.kt, Type.kt, Theme.kt) instead of the default purple.
- App bar: logo + Kitabu title with a lighter *Library System* sub-heading; same logo is the launcher icon (vector drawables `kitabu_logo`, `kitabu_launcher_foreground`, prefixed to avoid resource name clashes).
- Bottom navigation bar (Catalog / Reservations) with a green selected indicator.
- Serif font for titles/headings, sans-serif for body text (built-in font families, no extra font files).
- Button sizes: small Reserve, medium Confirm pickup, lifecycle row and a large Confirm Reservation.

Architecture (MVVM)
UI - composables only show state and send events up. Each screen collects state with
`collectAsStateWithLifecycle()` and passes it to a stateless content composable (state hoisting).
ViewModel – `LibraryViewModel` exposes `StateFlow`s. `books` uses `combine` + `flatMapLatest`
so the Room query re-runs when the search text or the filter changes.
Repository – cleans/validates input, applies the loan rules and returns an `ActionResult`.
Data Room entities, DAO and database. Foreign key `bookOwnerId -> bookId` with `CASCADE`.
Threading - every write is a `suspend` DAO call started in `viewModelScope.launch`;
reads are `Flow`s from Room. Nothing runs on the main thread.
Transactions – reserve, return and cancel each update two tables, so they are `@Transaction`
functions. They check the current status first, so double taps or stale screens can't
double-book or wrongly free a book.

Validation
Names are trimmed, repeated spaces removed, max 40 characters, cannot be empty.
Search text is trimmed and `%` / `_` removed before the `LIKE` query.
Return deadline must be after the booking date and at most 42 days after it.
Overdue books are renewed from today, not from the old deadline.

Screenshots

- Reservation
active reservation
book returned
reserved for 7 more days
not activated reservation
renew, return and cancel results


- Catalog
unselected
avalible only filter
reserved
