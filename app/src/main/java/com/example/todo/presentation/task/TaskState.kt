package com.example.todo.presentation.task

import androidx.annotation.StringRes
import com.example.todo.domain.Task

data class TaskState(
    val isLoading: Boolean = false,
    @StringRes val error: Int? = null,
    val tasks: List<Task> = emptyList<Task>(),
    val taskTitle: String = ""
)
