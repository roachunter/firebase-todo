package com.example.todo.presentation.task.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.todo.R
import com.example.todo.presentation.task.TaskEvent
import com.example.todo.presentation.task.TaskState
import com.example.todo.presentation.task.components.TaskListItem

@Composable
fun TaskListScreen(
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.tasks),
                style = MaterialTheme.typography.displayMedium
            )

            IconButton(
                onClick = {
                    onEvent(TaskEvent.OnLogoutClick)
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ExitToApp,
                    contentDescription = null
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = state.tasks,
                key = { item -> item.id }
            ) { task ->
                TaskListItem(
                    task = task,
                    onCheck = { onEvent(TaskEvent.OnTaskCompletionToggle(task)) },
                    onDeleteClick = { onEvent(TaskEvent.OnDeleteTaskClick(task)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.taskTitle,
                onValueChange = {
                    onEvent(TaskEvent.OnTaskTitleChange(it))
                },
                label = {
                    Text(text = stringResource(R.string.new_task))
                },
                modifier = Modifier.weight(1f)
            )

            FilledIconButton(
                onClick = {
                    onEvent(TaskEvent.OnAddTaskClick)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null
                )
            }
        }
    }
}