package com.example.taskapp

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// DAO = Data Access Object, aquí van todos los métodos para tocar la base de datos
@Dao
interface TaskDao {

    // Trae todas las tareas ordenadas por id. Flow hace que la lista se actualice sola en la UI
    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun getAllTasks(): Flow<List<Task>>

    // Inserta una tarea nueva. Si ya existe una con el mismo id, la reemplaza
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    // Actualiza una tarea existente
    @Update
    suspend fun updateTask(task: Task)

    // Elimina una tarea
    @Delete
    suspend fun deleteTask(task: Task)
}