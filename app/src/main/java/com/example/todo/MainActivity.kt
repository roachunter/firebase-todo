package com.example.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todo.presentation.Routes
import com.example.todo.presentation.auth.AuthEffect
import com.example.todo.presentation.auth.AuthViewModel
import com.example.todo.presentation.auth.screens.LoginScreen
import com.example.todo.presentation.auth.screens.RegisterScreen
import com.example.todo.presentation.task.TaskEffect
import com.example.todo.presentation.task.TaskViewModel
import com.example.todo.presentation.task.screens.TaskListScreen
import com.example.todo.ui.theme.TodoTheme
import kotlinx.coroutines.flow.Flow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoTheme {
                val snackbar = remember {
                    SnackbarHostState()
                }

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(snackbar)
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = Routes.Login
                        ) {
                            composable<Routes.Login> {
                                val viewModel by viewModels<AuthViewModel>()
                                val state by viewModel.state.collectAsState()

                                LaunchedEffect(Unit) {
                                    viewModel.effects.collect { effect ->
                                        when (effect) {
                                            AuthEffect.OnLoginSuccess -> {
                                                navController.navigate(Routes.TaskList)
                                            }

                                            else -> Unit
                                        }
                                    }
                                }

                                LoginScreen(
                                    state = state,
                                    onEvent = viewModel::onEvent,
                                    onRegisterClick = {
                                        navController.navigate(Routes.Register)
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                )
                            }

                            composable<Routes.Register> {
                                val viewModel by viewModels<AuthViewModel>()
                                val state by viewModel.state.collectAsState()

                                LaunchedEffect(Unit) {
                                    viewModel.effects.collect { effect ->
                                        when (effect) {
                                            AuthEffect.OnRegisterSuccess -> {
                                                navController.navigate(Routes.TaskList)
                                            }

                                            else -> Unit
                                        }
                                    }
                                }

                                RegisterScreen(
                                    state = state,
                                    onEvent = viewModel::onEvent,
                                    onLoginClick = {
                                        navController.navigate(Routes.Login)
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                )
                            }

                            composable<Routes.TaskList> {
                                val viewModel by viewModels<TaskViewModel>()
                                val state by viewModel.state.collectAsState()

                                TaskEffectProcessor(
                                    effects = viewModel.effects,
                                    snackbarHostState = snackbar,
                                    onLogoutSuccess = {
                                        navController.navigate(Routes.Login)
                                    }
                                )

                                TaskListScreen(
                                    state = state,
                                    onEvent = viewModel::onEvent,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskEffectProcessor(
    effects: Flow<TaskEffect>,
    snackbarHostState: SnackbarHostState,
    onLogoutSuccess: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        effects.collect { effect ->
            val message = when (effect) {
                TaskEffect.TaskAddingSuccess -> R.string.task_added
                TaskEffect.TaskAddingFailed -> R.string.task_adding_failed
                TaskEffect.TaskTogglingFailed -> R.string.task_toggling_error
                TaskEffect.TaskDeletingSuccess -> R.string.task_deleted
                TaskEffect.TaskDeletingFailed -> R.string.task_deleting_error
                TaskEffect.LogoutSuccess -> {
                    onLogoutSuccess()
                    return@collect
                }

                TaskEffect.LogoutFailed -> R.string.logout_failed
            }

            snackbarHostState.showSnackbar(context.resources.getString(message))
        }
    }
}