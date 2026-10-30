package com.primestrider.kotlinstarter.feature.example.todos

import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.primestrider.kotlinstarter.R
import com.primestrider.kotlinstarter.core.designsystem.component.MessageVariant
import com.primestrider.kotlinstarter.core.designsystem.component.Messenger
import com.primestrider.kotlinstarter.core.designsystem.component.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
@OptIn(ExperimentalCoroutinesApi::class)
class TodosViewModelTest {
    private lateinit var database: ExampleDatabase
    private lateinit var repository: TodosRepository
    private val messenger = Messenger()
    private var now = 0L

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        database =
            Room
                .inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), ExampleDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        repository = TodosRepository(database.todoDao()) { ++now }
    }

    @After
    fun tearDown() {
        database.close()
        Dispatchers.resetMain()
    }

    private fun viewModel(handle: SavedStateHandle = SavedStateHandle()) = TodosViewModel(repository, handle, messenger)

    /** The state once the database has delivered [count] todos. */
    private suspend fun TodosViewModel.settled(count: Int) = state.first { it.loaded && it.total == count }

    @Test
    fun too_short_title_is_rejected_and_not_stored() =
        runTest {
            val viewModel = viewModel()
            viewModel.onInputChange("  ab ")
            viewModel.add()
            assertEquals(R.string.todos_validation_min, viewModel.state.value.inputError)
            assertTrue(repository.todos.first().isEmpty())
        }

    @Test
    fun too_long_title_is_rejected() =
        runTest {
            val viewModel = viewModel()
            viewModel.onInputChange("x".repeat(101))
            viewModel.add()
            assertEquals(R.string.todos_validation_max, viewModel.state.value.inputError)
        }

    @Test
    fun error_updates_live_after_a_failed_submit() =
        runTest {
            val viewModel = viewModel()
            viewModel.onInputChange("ab")
            viewModel.add()
            viewModel.onInputChange("abc")
            assertEquals(null, viewModel.state.value.inputError)
        }

    @Test
    fun valid_title_is_stored_trimmed_and_announced() =
        runTest {
            val viewModel = viewModel()
            messenger.messages.test {
                viewModel.onInputChange("  Buy milk ")
                viewModel.add()
                assertEquals(UiText.Resource(R.string.todos_added), awaitItem().text)
                expectNoEvents()
            }
            assertEquals(
                "Buy milk",
                repository.todos
                    .first()
                    .single()
                    .title,
            )
            assertEquals("", viewModel.state.value.input)
        }

    @Test
    fun active_filter_hides_done_and_progress_counts() =
        runTest {
            repository.add("First")
            repository.add("Second")
            repository.toggle(
                repository.todos
                    .first()
                    .first { it.title == "First" }
                    .id,
            )
            val viewModel = viewModel()
            viewModel.setFilter(TodoFilter.ACTIVE)
            val state = viewModel.settled(2)
            assertEquals(listOf("Second"), state.todos.map { it.title })
            assertEquals(1, state.done)
            assertEquals(2, state.total)
        }

    @Test
    fun input_and_filter_survive_recreation() =
        runTest {
            val handle = SavedStateHandle()
            viewModel(handle).apply {
                onInputChange("draft")
                setFilter(TodoFilter.DONE)
            }
            val restored = viewModel(handle).settled(0)
            assertEquals("draft", restored.input)
            assertEquals(TodoFilter.DONE, restored.filter)
        }

    @Test
    fun clear_completed_with_nothing_done_is_silent() =
        runTest {
            repository.add("Active")
            val viewModel = viewModel()
            messenger.messages.test {
                viewModel.clearCompleted()
                expectNoEvents()
            }
        }

    @Test
    fun remove_deletes_and_announces() =
        runTest {
            repository.add("Gone soon")
            val id =
                repository.todos
                    .first()
                    .single()
                    .id
            val viewModel = viewModel()
            messenger.messages.test {
                viewModel.remove(id)
                assertEquals(UiText.Resource(R.string.todos_removed), awaitItem().text)
            }
            assertTrue(repository.todos.first().isEmpty())
        }

    @Test
    fun failed_insert_keeps_the_draft_and_reports_it() =
        runTest {
            val failing =
                object : TodoDao by database.todoDao() {
                    override suspend fun insert(todo: TodoEntity) = throw android.database.sqlite.SQLiteFullException("disk full")
                }
            val viewModel = TodosViewModel(TodosRepository(failing) { ++now }, SavedStateHandle(), messenger)
            messenger.messages.test {
                viewModel.onInputChange("Buy milk")
                viewModel.add()
                val message = awaitItem()
                assertEquals(MessageVariant.ERROR, message.variant)
                assertEquals(UiText.Resource(R.string.todos_save_failed), message.text)
            }
            assertEquals("Buy milk", viewModel.state.value.input)
        }
}
