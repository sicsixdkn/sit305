package com.sicsix.taskmanager.repository

import com.sicsix.taskmanager.dao.TaskDao
import com.sicsix.taskmanager.model.Task
import kotlinx.coroutines.flow.Flow


/**
 * Repository for managing task data operations.
 *
 * This repository class abstracts the access to task data and provides a clean API for the rest of the application
 * to interact with the task data in the database. It uses a DAO to execute database operations.
 *
 * @property taskDao An instance of TaskDao to perform database operations on tasks.
 */
class TaskRepository(private val taskDao: TaskDao) {
    /**
     * A Flow of list of all tasks, observed for any database changes.
     * This Flow continuously emits task data as it becomes available or changes over time.
     */
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    /**
     * Inserts a task into the database.
     *
     * This is a suspend function and must be called from a coroutine or another suspend function.
     *
     * @param task The task object to be inserted into the database.
     */
    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    /**
     * Updates an existing task in the database.
     *
     * This is a suspend function and must be called from a coroutine or another suspend function.
     *
     * @param task The task object to be updated in the database.
     */
    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    /**
     * Deletes a task from the database.
     *
     * This is a suspend function and must be called from a coroutine or another suspend function.
     *
     * @param task The task object to be deleted from the database.
     */
    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }

    /**
     * Retrieves a task by its ID.
     *
     * Returns a Flow that emits the task object when found, or null if no task is found with the provided ID.
     * This Flow can be collected in a coroutine to get the latest value of the task as it is updated.
     *
     * @param id The unique identifier of the task to be retrieved.
     * @return A Flow emitting the task or null.
     */
    fun getTaskById(id: Int): Flow<Task?> = taskDao.getTaskById(id)
}
