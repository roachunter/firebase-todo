package com.example.todo.data

import com.example.todo.domain.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TaskRepository {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private fun getUserId() = auth.currentUser?.uid

    fun getTasks(): Flow<List<Task>> = callbackFlow {
        val userId = getUserId()
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db
            .collection("users")
            .document(userId)
            .collection("tasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val tasks = snapshot?.documents?.mapNotNull {
                    it.toObject(Task::class.java)?.copy(id = it.id)
                } ?: emptyList()

                trySend(tasks)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addTask(title: String) {
        getUserId()?.let { userId ->
            val task = Task(title = title, isCompleted = false)
            db.collection("users").document(userId).collection("tasks").add(task).await()
        }
    }

    suspend fun toggleCompleted(task: Task) {
        getUserId()?.let { userId ->
            db.collection("users").document(userId).collection("tasks").document(task.id)
                .update("completed", !task.isCompleted).await()
        }
    }

    suspend fun deleteTask(task: Task) {
        getUserId()?.let { userId ->
            db.collection("users").document(userId).collection("tasks").document(task.id)
                .delete().await()
        }
    }
}