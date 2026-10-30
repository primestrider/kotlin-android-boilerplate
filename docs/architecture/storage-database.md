# Storage: DataStore and Room

Two local stores, for two kinds of data.

| Data | Store | Where in this app |
| --- | --- | --- |
| A few keyed values: theme, flags | DataStore Preferences | `core/storage/` (module and keys), `core/designsystem/theme/ThemePreferenceRepository`, `feature/example/auth/DemoTokenSettings` |
| Secrets | DataStore + Keystore AES/GCM | `core/auth/` (`SessionRepository`, see `auth-session.md`) |
| Structured records you list, filter, or query | Room (SQLite) | `feature/example/todos/` (`ExampleDatabase`) |

Rule of thumb: if you would ever write `WHERE` or `ORDER BY`, it belongs in Room.

## Where a database lives

A `@Database` class lists its entities, so it belongs next to them. The Todos example keeps `ExampleDatabase` (`example.db`) in `feature/example/todos/`: `core` never references example entities, and deleting `feature/example` deletes the database with it.

For your own app, either:

- one database per feature (`feature/orders/OrdersDatabase.kt`), when features do not share tables; or
- a shared `core/database/AppDatabase.kt` listing entities from several features, when they join across tables.

Provide the database as a `@Singleton` from a Hilt module and the DAOs from it (`TodosModule`).

## Pieces

```
TodoEntity (@Entity)  →  TodoDao (@Dao)  →  ExampleDatabase (@Database)  →  TodosRepository  →  TodosViewModel
```

- DAO queries that observe return `Flow<List<…>>`; Room re-emits after every write to the table, so the UI never reloads by hand.
- Writes are `suspend` functions; Room runs them off the main thread.
- The repository adds app rules (trimming, ids, timestamps) and hides the DAO from ViewModels.
- The ViewModel combines the DAO flow with UI state. Start the DAO flow with `onStart { emit(null) }` so input stays responsive before the first query answers, and use `null` to tell "loading" from "empty" (no empty-state flash on open).

## Migrations

- `exportSchema = true` and the KSP argument `room.schemaLocation` (in `app/build.gradle.kts`) write each version's schema to `app/schemas/<database class>/<version>.json`. Commit these files.
- Changing an entity means bumping `version` and adding a `Migration(from, to)` to the builder (`.addMigrations(...)`), or an `@AutoMigration` when Room can infer it from the two exported schemas.
- Never ship `fallbackToDestructiveMigration()` for user data: it silently deletes everything on upgrade.

## Testing

Use an in-memory database under Robolectric:

```kotlin
val database = Room.inMemoryDatabaseBuilder(context, ExampleDatabase::class.java)
    .allowMainThreadQueries()
    .build()
```

- DAO tests assert the SQL (ordering, counts) against it (`TodoDaoTest`).
- ViewModel tests use the real repository on the same in-memory database and a fake clock (`TodosViewModelTest`); wait for the state with `state.first { it.loaded && … }`, because Room answers on its own executor.
- DataStore tests use `testing/InMemoryPreferencesDataStore` (file-backed DataStore is unreliable in JVM tests on Windows).
