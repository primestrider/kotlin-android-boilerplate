package com.primestrider.kotlinstarter.feature.example.todos

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The example feature's own database (`example.db`), so `core` never references example entities and deleting
 * `feature/example` deletes it. Bump [version] together with a `Migration`; schemas are exported to `app/schemas`.
 */
@Database(entities = [TodoEntity::class], version = 1, exportSchema = true)
abstract class ExampleDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}
