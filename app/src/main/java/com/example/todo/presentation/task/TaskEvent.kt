package com.example.todo.presentation.task

import com.example.todo.domain.Task

sealed interface TaskEvent {
    data object OnLoadTasksClick: TaskEvent
    data object OnLogoutClick: TaskEvent

    data class OnTaskTitleChange(val value: String): TaskEvent
    data object OnAddTaskClick: TaskEvent
    data class OnTaskCompletionToggle(val task: Task): TaskEvent
    data class OnDeleteTaskClick(val task: Task): TaskEvent
}

