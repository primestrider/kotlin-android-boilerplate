package com.primestrider.kotlinstarter.feature.example.todos

import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

/** Todo storage; [clock] stamps `createdAt` (newest first) and is replaceable in tests. */
class TodosRepository(
    private val dao: TodoDao,
    private val clock: () -> Long,
) {
    @Inject
    constructor(dao: TodoDao) : this(dao, System::currentTimeMillis)

    val todos: Flow<List<TodoEntity>> = dao.observeAll()

    suspend fun add(title: String) = dao.insert(TodoEntity(UUID.randomUUID().toString(), title.trim(), done = false, createdAt = clock()))

    suspend fun toggle(id: String) = dao.toggle(id)

    suspend fun remove(id: String) = dao.delete(id)

    suspend fun clearCompleted(): Int = dao.deleteCompleted()
}
