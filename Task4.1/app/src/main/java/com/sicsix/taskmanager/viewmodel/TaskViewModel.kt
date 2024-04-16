package com.sicsix.taskmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sicsix.taskmanager.database.AppDatabase
import com.sicsix.taskmanager.model.Task
import com.sicsix.taskmanager.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    // LiveData holding the list of all tasks fetched from the database.
    val allTasks: LiveData<List<Task>>

    // Private MutableLiveData to handle updates for a single task.
    private val _task = MutableLiveData<Task?>()

    // Public LiveData to expose the currently loaded task.
    val task: LiveData<Task?> get() = _task

    init {
        // Initialization block to get the TaskDao and create the repository.
        val tasksDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(tasksDao)
        // Convert the Flow returned by repository to LiveData for use in the UI.
        allTasks = repository.allTasks.asLiveData()
    }

    /**
     * Inserts a task into the database asynchronously.
     *
     * @param task The task to insert.
     */
    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    /**
     * Updates an existing task in the database asynchronously.
     *
     * @param task The task to update.
     */
    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    /**
     * Deletes a task from the database asynchronously.
     *
     * @param task The task to delete.
     */
    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }

    /**
     * Loads a task by its ID into _task LiveData to be observed by the UI.
     *
     * This method fetches the task asynchronously and updates the LiveData. It is designed to be used
     * when a specific task needs to be retrieved and observed for changes.
     *
     * @param id The ID of the task to load.
     */
    fun loadTaskById(id: Int) = viewModelScope.launch {
        repository.getTaskById(id).collect {
            _task.postValue(it)
        }
    }
}
