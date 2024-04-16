package com.sicsix.taskmanager.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sicsix.taskmanager.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the Task entity.
 * This interface provides methods for performing database operations on the tasks table
 */
@Dao
interface TaskDao {
    /**
     * Retrieves all tasks from the database and sorts them by due date in ascending order.
     * @return A Flow that emits a list of tasks whenever the tasks table is updated.
     */
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<Task>>

    /**
     * Inserts multiple tasks into the database.
     * If a task already exists (conflict based on primary key), it replaces the existing task.
     * @param tasks List of tasks to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tasks: List<Task>)

    /**
     * Inserts a single task into the database.
     * @param task The task to be inserted.
     */
    @Insert
    suspend fun insertTask(task: Task)

    /**
     * Updates an existing task in the database.
     * @param task The task to be updated.
     */
    @Update
    suspend fun updateTask(task: Task)

    /**
     * Deletes a task from the database.
     * @param task The task to be deleted.
     */
    @Delete
    suspend fun deleteTask(task: Task)

    /**
     * Retrieves a task by its ID.
     * @param taskId The unique ID of the task.
     * @return A Flow that emits the task with the specified ID, or null if no task with such an ID exists.
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): Flow<Task?>
}
