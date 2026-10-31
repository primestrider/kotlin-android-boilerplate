package com.primestrider.kotlinstarter.feature.example.sdk.sqlite

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.primestrider.kotlinstarter.feature.example.todos.ExampleDatabase
import com.primestrider.kotlinstarter.feature.example.todos.ExampleDatabase_Impl
import com.primestrider.kotlinstarter.feature.example.todos.MIGRATION_1_2
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.File

/** Review focus 3: a v1 database keeps its todos when upgraded to v2. Reads the exported schemas in `app/schemas`. */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class ExampleDatabaseMigrationTest {
    @get:Rule
    val helper =
        MigrationTestHelper(
            instrumentation = InstrumentationRegistry.getInstrumentation(),
            file = File.createTempFile("migration-test", ".db"),
            driver = AndroidSQLiteDriver(),
            databaseClass = ExampleDatabase::class,
            databaseFactory = { ExampleDatabase_Impl() },
        )

    @Test
    fun todos_survive_the_upgrade_from_v1_to_v2() {
        helper.createDatabase(1).apply {
            execSQL("INSERT INTO todos (id, title, done, createdAt) VALUES ('a', 'Keep me', 1, 42)")
            close()
        }

        val upgraded = helper.runMigrationsAndValidate(2, listOf(MIGRATION_1_2))

        assertEquals("a|Keep me|1|42", upgraded.single("SELECT id || '|' || title || '|' || done || '|' || createdAt FROM todos"))
        assertEquals("0", upgraded.single("SELECT COUNT(*) FROM transactions"))
    }

    private fun SQLiteConnection.single(sql: String): String =
        prepare(sql).use {
            check(it.step()) { "No row for $sql" }
            it.getText(0)
        }
}
