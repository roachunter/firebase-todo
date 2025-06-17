package com.example.todo.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.AuthService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import com.example.todo.R
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthState()
    )

    private val _effects = Channel<AuthEffect>()
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.OnEmailChange -> updateEmail(event.value)
            is AuthEvent.OnPasswordChange -> updatePassword(event.value)
            AuthEvent.OnLoginClick -> login()
            AuthEvent.OnRegisterClick -> register()
        }
    }

    private fun updateEmail(email: String) {
        _state.update {
            it.copy(
                email = email,
                emailError = null
            )
        }
    }

    private fun updatePassword(password: String) {
        _state.update {
            it.copy(
                password = password,
                passwordError = null
            )
        }
    }

    private fun validateUser(email: String, password: String): Map<String, Int> {
        val errors = mutableMapOf<String, Int>()

        if (email.isBlank()) {
            errors["email"] = R.string.email_empty_error
        } else if (!email.matches(Regex("""(\w+)(\.\w+)*@(\w+)(\.\w+)*"""))) {
            errors["email"] = R.string.email_error
        }

        if (password.isBlank()) {
            errors["password"] = R.string.password_empty_error
        } else if (password.any { it.isWhitespace() }) {
            errors["password"] = R.string.password_error
        }

        return errors.toMap()
    }

    private fun login() {
        val email = _state.value.email.trim()
        val password = _state.value.password.trim()

        val errors = validateUser(email, password)

        if (errors.isNotEmpty()) {
            _state.update {
                it.copy(
                    emailError = errors["email"],
                    passwordError = errors["password"]
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            try {
                authService.login(email, password)
                _effects.send(AuthEffect.OnLoginSuccess)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = R.string.auth_error
                    )
                }
            } finally {
                _state.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun register() {
        val email = _state.value.email.trim()
        val password = _state.value.password.trim()

        val errors = validateUser(email, password)

        if (errors.isNotEmpty()) {
            _state.update {
                it.copy(
                    emailError = errors["email"],
                    passwordError = errors["password"]
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            try {
                authService.register(email, password)
                _effects.send(AuthEffect.OnRegisterSuccess)
            } catch (_: Exception) {
                _state.update {
                    it.copy(
                        error = R.string.auth_error
                    )
                }
            } finally {
                _state.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
}