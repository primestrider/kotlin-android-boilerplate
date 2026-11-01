package com.primestrider.kotlinstarter.feature.example.todos.ui

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.primestrider.kotlinstarter.feature.example.TodosRoute

/** The todos page. */
fun NavGraphBuilder.todosDestinations(navController: NavController) {
    composable<TodosRoute> {
        val viewModel: TodosViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        TodosScreen(
            state = state,
            onInputChange = viewModel::onInputChange,
            onAdd = viewModel::add,
            onToggle = viewModel::toggle,
            onRemove = viewModel::remove,
            onClearCompleted = viewModel::clearCompleted,
            onFilterChange = viewModel::setFilter,
            onBack = { navController.popBackStack() },
        )
    }
}
