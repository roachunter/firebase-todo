package com.example.todo.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.R
import com.example.todo.data.AuthService
import com.example.todo.data.TaskRepository
import com.example.todo.domain.Task
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskViewModel(
    private val authService: AuthService = AuthService(),
    private val taskRepository: TaskRepository = TaskRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(TaskState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskState()
    )

    init {
        loadTasks()
    }

    private val _effects = Channel<TaskEffect>()
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: TaskEvent) {
        when (event) {
            TaskEvent.OnLoadTasksClick -> loadTasks()

            is TaskEvent.OnTaskTitleChange -> updateTitle(event.value)
            TaskEvent.OnAddTaskClick -> addTask()
            is TaskEvent.OnDeleteTaskClick -> deleteTask(event.task)
            is TaskEvent.OnTaskCompletionToggle -> toggleTaskCompleted(event.task)

            TaskEvent.OnLogoutClick -> logout()
        }
    }

    private fun updateTitle(value: String) {
        _state.update { it.copy(taskTitle = value) }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            taskRepository.getTasks()
                .catch {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = R.string.task_loading_error
                        )
                    }
                }
                .collect { tasks ->
                    _state.update { it.copy(isLoading = false, error = null, tasks = tasks) }
                }
        }
    }

    private fun addTask() {
        val title = _state.value.taskTitle.trim()
        if (title.isBlank()) return

        viewModelScope.launch {
            try {
                taskRepository.addTask(title)
                _state.update { it.copy(taskTitle = "") }
            } catch (_: Exception) {
                _effects.send(TaskEffect.TaskAddingFailed)
            }
        }
    }

    private fun toggleTaskCompleted(task: Task) = viewModelScope.launch {
        try {
            taskRepository.toggleCompleted(task)
        } catch (_: Exception) {
            _effects.send(TaskEffect.TaskTogglingFailed)
        }
    }

    private fun deleteTask(task: Task) = viewModelScope.launch {
        try {
            taskRepository.deleteTask(task)
        } catch (_: Exception) {
            _effects.send(TaskEffect.TaskDeletingFailed)
        }
    }

    private fun logout() = viewModelScope.launch {
        try {
            authService.logout()
            _effects.send(TaskEffect.LogoutSuccess)
        } catch (_: Exception) {
            _effects.send(TaskEffect.LogoutFailed)
        }
    }

}