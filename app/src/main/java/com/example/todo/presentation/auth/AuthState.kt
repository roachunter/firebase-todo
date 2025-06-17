package com.example.todo.presentation.auth

import androidx.annotation.StringRes

data class AuthState(
    val isLoading: Boolean = false,
    @StringRes val error: Int? = null,

    val email: String = "",
    @StringRes val emailError: Int? = null,
    val password: String = "",
    @StringRes val passwordError: Int? = null
)
