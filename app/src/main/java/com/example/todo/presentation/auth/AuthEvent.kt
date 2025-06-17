package com.example.todo.presentation.auth

sealed interface AuthEvent {
    data class OnEmailChange(val value: String): AuthEvent
    data class OnPasswordChange(val value: String): AuthEvent

    data object OnLoginClick: AuthEvent
    data object OnRegisterClick: AuthEvent
}

