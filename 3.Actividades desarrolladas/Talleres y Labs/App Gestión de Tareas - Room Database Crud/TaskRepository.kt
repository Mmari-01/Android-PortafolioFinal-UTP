package com.example.taskapp

import kotlinx.coroutines.flow.Flow

// El Repository es el intermediario entre el ViewModel y la base de datos
class TaskRepository(private val taskDao: TaskDao) {

    // Expone la lista de tareas directamente desde el DAO
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    // Llama al DAO para insertar
    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    // Llama al DAO para actualizar
    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    // Llama al DAO para eliminar
    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }
}
