package com.example.todo.presentation.auth.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.todo.R
import com.example.todo.presentation.auth.AuthEvent
import com.example.todo.presentation.auth.AuthState

@Composable
fun RegisterScreen(
    state: AuthState,
    onEvent: (AuthEvent) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = stringResource(R.string.sign_up),
            style = MaterialTheme.typography.displayMedium
        )

        OutlinedTextField(
            value = state.email,
            onValueChange = {
                onEvent(AuthEvent.OnEmailChange(it))
            },
            label = {
                Text(text = stringResource(R.string.email))
            },
            isError = state.emailError != null,
            supportingText = {
                state.emailError?.let {
                    Text(text = stringResource(it))
                }
            }
        )

        OutlinedTextField(
            value = state.password,
            onValueChange = {
                onEvent(AuthEvent.OnPasswordChange(it))
            },
            label = {
                Text(text = stringResource(R.string.password))
            },
            isError = state.passwordError != null,
            supportingText = {
                state.passwordError?.let {
                    Text(text = stringResource(it))
                }
            }
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onLoginClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.sign_in))
            }

            Button(
                onClick = {
                    onEvent(AuthEvent.OnRegisterClick)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.sign_up))
            }
        }
    }
}