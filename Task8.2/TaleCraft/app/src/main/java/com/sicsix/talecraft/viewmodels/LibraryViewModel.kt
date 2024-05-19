package com.sicsix.talecraft.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sicsix.talecraft.models.Story
import com.sicsix.talecraft.models.World
import com.sicsix.talecraft.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {
    // LiveData to observe the stories and worlds
    val stories: LiveData<List<Story>> = appRepository.getStories()
    val worlds: LiveData<List<World>> = appRepository.getWorlds()

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
}