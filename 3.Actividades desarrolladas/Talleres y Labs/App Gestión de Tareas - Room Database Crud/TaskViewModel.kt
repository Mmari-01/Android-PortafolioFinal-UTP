package com.example.taskapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository = TaskRepository(
        TaskDatabase.getDatabase(application).taskDao()
    )

    // La lista de tareas que la UI va a observar
    val allTasks = repository.allTasks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insert(Task(title = title))
        }
    }

    fun updateTask(task: Task, newTitle: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            repository.update(task.copy(title = newTitle))
        }
    }

    fun toggleCompleted(task: Task) {
        viewModelScope.launch {
            repository.update(task.copy(completed = !task.completed))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }
}