package com.example.todo.presentation.task

sealed interface TaskEffect {
    data object TaskAddingFailed: TaskEffect
    data object TaskTogglingFailed : TaskEffect
    data object TaskDeletingFailed: TaskEffect
    data object LogoutSuccess: TaskEffect
    data object LogoutFailed : TaskEffect
}