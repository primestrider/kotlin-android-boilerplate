package com.primestrider.kotlinstarter.feature.example.todos

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.primestrider.kotlinstarter.feature.example.sdk.sqlite.TransactionDao
import com.primestrider.kotlinstarter.feature.example.sdk.sqlite.TransactionEntity

/**
 * The example feature's own database (`example.db`), so `core` never references example entities and deleting
 * `feature/example` deletes it. Bump [version] together with a `Migration`; schemas are exported to `app/schemas`.
 */
@Database(entities = [TodoEntity::class, TransactionEntity::class], version = 2, exportSchema = true)
abstract class ExampleDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    abstract fun transactionDao(): TransactionDao
}

/** v1 to v2: adds the `transactions` table; `todos` is untouched. The SQL must match [TransactionEntity] exactly. */
val MIGRATION_1_2: Migration =
    object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `transactions` " +
                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `kind` TEXT NOT NULL, " +
                    "`amount` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)",
            )
        }
    }
