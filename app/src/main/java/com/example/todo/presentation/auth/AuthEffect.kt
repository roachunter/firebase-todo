package com.example.todo.presentation.auth

sealed interface AuthEffect {
    data object OnLoginSuccess: AuthEffect
    data object OnRegisterSuccess: AuthEffect
}