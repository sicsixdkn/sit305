package com.sicsix.taskmanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sicsix.taskmanager.dao.TaskDao
import com.sicsix.taskmanager.model.Task
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SampleDataInjector(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Executors.newSingleThreadExecutor().execute {
            context.deleteDatabase("task_database")
            AppDatabase.getDatabase(context).taskDao().insertAll(sampleData)
        }
    }

    private val sampleData = listOf(
        Task(
            title = "Team Meeting",
            description = "Discuss project updates",
            dueDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)
        ),
        Task(
            title = "Client Feedback",
            description = "Compile feedback for review",
            dueDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3)
        ),
        Task(
            title = "Documentation Update",
            description = "Update the project documentation based on recent changes",
            dueDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)
        ),
        Task(
            title = "Budget Review",
            description = "Review project budget for Q2",
            dueDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(10)
        ),
        Task(
            title = "Software Upgrade",
            description = "Plan and schedule software upgrade process",
            dueDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(14)
        )
    )
}

/**
 * Abstract database class for the application.
 * Defines the database configuration and serves as the app's main access point to the persisted data.
 *
 * @property entities List of all entities involved in the database.
 * @property version Database version number.
 * @property exportSchema A boolean that indicates whether to export the database schema into a folder.
 */
@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Provides the DAOs that offer methods to query, update, insert, and delete data in the database.
     */
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Gets the singleton instance of the database.
         *
         * @param context The context used to get the application context to create or retrieve the database.
         * @return The singleton instance of [AppDatabase].
         */
        fun getDatabase(context: Context): AppDatabase {
            // If the INSTANCE is not null, then return it, if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_database"
                )
//                    .addCallback(SampleDataInjector(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
