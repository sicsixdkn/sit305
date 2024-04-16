package com.sicsix.taskmanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a Task entity in the database.
 *
 * @property id The unique identifier for a task. It is auto-generated by the database.
 * @property title The title of the task. This field cannot be null and is used to display the task title in the UI.
 * @property description The description of the task. Provides more detailed information about the task.
 * @property dueDate The due date for the task represented as a timestamp. This is the deadline by which the task should be completed.
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: Long
)