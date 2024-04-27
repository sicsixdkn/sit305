package com.sicsix.llama2chatbot.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sicsix.llama2chatbot.model.ChatEntry
import com.sicsix.llama2chatbot.model.User
import kotlinx.coroutines.flow.Flow

@Database(entities = [ChatEntry::class, User::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Provides the DAOs that offer methods interact with the database.
     */
    abstract fun appDao(): AppDao

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
                    context.applicationContext, AppDatabase::class.java, "llama2chatbot_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Dao
interface AppDao {
    /**
     * Gets a user by their name.
     *
     * @param name The name of the user to get.
     * @return The user with the given name, or null if no user with that name exists.
     */
    @Query("SELECT * FROM users WHERE name = :name LIMIT 1")
    suspend fun getUserByName(name: String): User?

    /**
     * Gets a user by their ID.
     *
     * @param id The ID of the user to get.
     * @return The user with the given ID, or null if no user with that ID exists.
     */
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): User?

    /**
     * Creates a new user.
     *
     * @param user The user to create.
     * @return The ID of the newly created user.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUser(user: User): Long

    /**
     * Gets all chat entries for a user.
     *
     * @param userId The ID of the user to get chat entries for.
     * @return A list of chat entries for the user with the given ID as a [Flow].
     */
    @Query("SELECT * FROM chatentries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getChatEntries(userId: Int): Flow<List<ChatEntry>>

    /**
     * Inserts a chat entry into the database.
     *
     * @param chatEntry The chat entry to insert.
     * @return The ID of the newly inserted chat entry.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatEntry(chatEntry: ChatEntry): Long

    /**
     * Updates a chat entry in the database.
     *
     * @param id The ID of the chat entry to update.
     * @param botMessage The bot message to update the chat entry with.
     */
    @Query("UPDATE chatentries SET botMessage = :botMessage WHERE id = :id")
    suspend fun updateChatEntry(id: Int, botMessage: String)

    /**
     * Gets a chat entry by its ID.
     *
     * @param id The ID of the chat entry to get.
     * @return The chat entry with the given ID, or null if no chat entry with that ID exists.
     */
    @Query("SELECT * FROM chatentries WHERE id = :id LIMIT 1")
    suspend fun getChatEntryById(id: Int): ChatEntry?
}
