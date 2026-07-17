package com.example.taskapp

import androidx.room.Entity
import androidx.room.PrimaryKey

// Esta clase representa una fila en la tabla "tasks" de la base de datos
@Entity(tableName = "tasks")
data class Task(

    // ID único generado automáticamente por Room
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // título de la tarea
    val title: String,

    // por defecto la tarea no está completada
    val completed: Boolean = false
)