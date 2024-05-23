package com.sicsix.talecraft.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sicsix.talecraft.models.Story
import com.sicsix.talecraft.models.World
import com.sicsix.talecraft.repository.AppRepository
import com.sicsix.talecraft.utility.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    // LiveData to observe the stories and worlds
    val stories: LiveData<List<Story>> = appRepository.getStories()
    val worlds: LiveData<List<World>> = appRepository.getWorlds()

    // LiveData to observe the active story
    val activeStory = MutableLiveData<Story?>()

    /**
     * Inserts a new story with the given details.
     *
     * @param worldId The ID of the world the story belongs to.
     * @param title The title of the story.
     * @param worldTitle The title of the world the story belongs to.
     */
    fun insertStory(worldId: Int, title: String, worldTitle: String) {
        viewModelScope.launch {
            appRepository.insertStory(worldId, title, worldTitle)
        }
    }

    /**
     * Deletes the given story.
     *
     * @param story The story to delete.
     */
    fun deleteStory(story: Story) {
        viewModelScope.launch {
            appRepository.deleteStory(story)
        }
    }

    /**
     * Gets the name of the active story.
     *
     * @return The name of the active story.
     */
    fun getActiveStory() {
        viewModelScope.launch {
            val activeStoryId = userPreferences.getActiveStoryId()
            if (activeStoryId != -1) {
                val story = appRepository.getStoryById(activeStoryId)
                activeStory.postValue(story)
            }
        }
    }

    /**
     * Sets the active story.
     *
     * @param story The story to set as active.
     */
    fun setActiveStory(story: Story) {
        viewModelScope.launch {
            userPreferences.setActiveStoryId(story.id)
            activeStory.postValue(story)
        }
    }
}