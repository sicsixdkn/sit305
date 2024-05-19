package com.sicsix.talecraft.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sicsix.talecraft.models.Story
import com.sicsix.talecraft.models.StoryEntry
import com.sicsix.talecraft.models.World
import kotlinx.coroutines.flow.Flow

@Database(entities = [World::class, Story::class, StoryEntry::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
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
        fun getDatabase(context: Context, userId: String): AppDatabase {
            // If the INSTANCE is not null, then return it, if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "talecraft_database_$userId"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Dao
interface AppDao {
    /**
     * Gets all worlds.
     *
     * @return A list of all worlds.
     */
    @Query("SELECT * FROM worlds ORDER BY lastAccessed DESC")
    fun getWorlds(): Flow<List<World>>

    /**
    Inserts a world into the database.
     *
     * @param world The world to insert.
     * @return The ID of the newly inserted world.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorld(world: World): Long

    /**
     * Gets the world with the given ID.
     *
     * @param worldId The ID of the world to get.
     * @return The world with the given ID.
     */
    @Query("SELECT * FROM worlds WHERE id = :worldId")
    suspend fun getWorldById(worldId: Int): World

    /**
     * Deletes the given world from the database.
     *
     * @param world The world to delete.
     */
    @Delete
    suspend fun deleteWorld(world: World)

    /**
     * Gets all stories.
     *
     * @return A list of all stories.
     */
    @Query("SELECT * FROM stories ORDER BY lastAccessed DESC")
    fun getStories(): Flow<List<Story>>

    /**
     * Inserts a story into the database.
     *
     * @param story The story to insert.
     * @return The ID of the newly inserted story.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: Story): Long

    /**
     * Gets the story with the given ID.
     *
     * @param storyId The ID of the story to get.
     * @return The story with the given ID.
     */
    @Query("SELECT * FROM stories WHERE id = :storyId")
    suspend fun getStoryById(storyId: Int): Story

    /**
     * Deletes the given story from the database.
     *
     * @param story The story to delete.
     */
    @Delete
    suspend fun deleteStory(story: Story)

    /**
     * Gets all story entries for a story.
     *
     * @param storyId The ID of the story to get story entries for.
     * @return A list of story entries for the story with the given ID as a [Flow].
     */
    @Query("SELECT * FROM story_entries WHERE storyId = :storyId ORDER BY timestamp ASC")
    fun getStoryEntries(storyId: Int): Flow<List<StoryEntry>>

    /**
     * Inserts a story entry into the database.
     *
     * @param storyEntry The story entry to insert.
     * @return The ID of the newly inserted story entry.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStoryEntry(storyEntry: StoryEntry): Long

    /**
     * Updates the word count of the story with the given ID.
     *
     * @param storyId The ID of the story to update.
     * @param wordCount The new word count of the story.
     */
    @Query("UPDATE stories SET wordCount = :wordCount WHERE id = :storyId")
    suspend fun updateStoryWordCount(storyId: Int, wordCount: Int)
}
