package com.sicsix.talecraft.repository

import android.content.Context
import androidx.lifecycle.asLiveData
import com.sicsix.talecraft.database.AppDao
import com.sicsix.talecraft.database.AppDatabase
import com.sicsix.talecraft.models.Story
import com.sicsix.talecraft.models.StoryEntry
import com.sicsix.talecraft.models.World
import com.sicsix.talecraft.utility.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    userPreferences: UserPreferences
) {
    private var appDao: AppDao

    init {
        val userDetails = userPreferences.getUserDetails() ?: throw IllegalArgumentException("User details not found")
        appDao = AppDatabase.getDatabase(context, userDetails.id).appDao()
    }

    /**
     * Gets all the worlds.
     *
     * @return A list of all the worlds.
     */
    fun getWorlds() = appDao.getWorlds().asLiveData()

    /**
     * Saves a new world with the given details.
     *
     * @param title The title of the world.
     * @param genre The genre of the world.
     * @param subGenre The sub-genre of the world.
     * @param premise The premise of the world.
     * @return The ID of the newly inserted world.
     */
    suspend fun insertWorld(title: String, genre: String, subGenre: String, premise: String): Long {
        return appDao.insertWorld(World(title = title, genre = genre, subGenre = subGenre, premise = premise))
    }

    /**
     * Gets the world with the given ID.
     *
     * @param worldId The ID of the world to get.
     * @return The world with the given ID.
     */
    suspend fun getWorldById(worldId: Int): World {
        return appDao.getWorldById(worldId)
    }

    /**
     * Deletes the given world.
     *
     * @param world The world to delete.
     */
    suspend fun deleteWorld(world: World) {
        appDao.deleteWorld(world)
    }

    /**
     * Gets all the stories.
     *
     * @return A list of all the stories.
     */
    fun getStories() = appDao.getStories().asLiveData()


    /**
     * Saves a new story with the given details.
     *
     * @param worldId The ID of the world the story belongs to.
     * @param title The title of the story.
     * @param worldTitle The title of the world the story belongs to.
     * @return The ID of the newly inserted story.
     */
    suspend fun insertStory(worldId: Int, title: String, worldTitle: String): Long {
        return appDao.insertStory(Story(worldId = worldId, title = title, worldTitle = worldTitle, wordCount = 0))
    }

    /**
     * Gets the story with the given ID.
     *
     * @param storyId The ID of the story to get.
     * @return The story with the given ID.
     */
    suspend fun getStoryById(storyId: Int): Story {
        return appDao.getStoryById(storyId)
    }

    /**
     * Deletes the given story.
     *
     * @param story The story to delete.
     */
    suspend fun deleteStory(story: Story) {
        appDao.deleteStory(story)
    }

    /**
     * Saves a new story entry with the given details.
     *
     * @param storyId The ID of the story the entry belongs to.
     *
     */
    suspend fun insertStoryEntry(storyId: Int, content: String, isUserSelection: Boolean, options: List<String> = emptyList()) {
        appDao.insertStoryEntry(StoryEntry(storyId = storyId, content = content, isUserSelection = isUserSelection, options = options))
        if (!isUserSelection) {
            val story = appDao.getStoryById(storyId)
            appDao.updateStoryWordCount(storyId, story.wordCount + content.split(" ").size)
        }
    }

    /**
     * Gets the story entries for the story with the given ID.
     *
     * @param storyId The ID of the story to get the entries for.
     * @return The entries for the story with the given ID.
     */
    fun getStoryEntries(storyId: Int) = appDao.getStoryEntries(storyId).asLiveData()
}

