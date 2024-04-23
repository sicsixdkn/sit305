package com.sicsix.itube.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sicsix.itube.model.PlaylistEntry
import com.sicsix.itube.model.User

@Database(entities = [User::class, PlaylistEntry::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Provides the DAOs that offer methods interact with the database.
     */
    abstract fun userDao(): UserDao
    abstract fun playlistEntryDao(): PlaylistEntryDao

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
                    "itube_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?
}

@Dao
interface PlaylistEntryDao {
    @Insert
    suspend fun insertEntry(entry: PlaylistEntry)

    @Query("SELECT * FROM playlistEntries WHERE userId = :userId")
    suspend fun getEntriesForUser(userId: Int): List<PlaylistEntry>
}
