package com.example.todo.domain

data class Task(
    val id: String = "",
    val title: String = "",
    val completed: Boolean = false
)
