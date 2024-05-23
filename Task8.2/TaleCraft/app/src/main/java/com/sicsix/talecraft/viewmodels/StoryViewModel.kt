package com.sicsix.talecraft.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sicsix.talecraft.api.RetrofitInstance
import com.sicsix.talecraft.models.Story
import com.sicsix.talecraft.models.StoryEntry
import com.sicsix.talecraft.models.World
import com.sicsix.talecraft.models.dtos.StoryRequest
import com.sicsix.talecraft.models.dtos.WorldInfo
import com.sicsix.talecraft.repository.AppRepository
import com.sicsix.talecraft.utility.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val userPreferences: UserPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // API instance to make network requests
    private val api = RetrofitInstance.api

    // Get the story ID from the saved state handle
    private val storyId: Int = savedStateHandle.get<String>("storyId")?.toInt() ?: throw IllegalArgumentException("Missing storyId")

    // LiveData to observe the story and world
    val story = MutableLiveData<Story>()
    val world = MutableLiveData<World>()

    // LiveData to observe the story entries
    var storyEntries = MutableLiveData<List<StoryEntry>?>()

    // LiveData to observe the loading state of the story entries
    val isLoading = MutableLiveData(true)

    // LiveData to observe the state of querying the LLM
    val isQuerying = MutableLiveData(false)

    init {
        // On initialization, get the story, world, and story entries
        viewModelScope.launch {
            // Set isLoading to true while the data is being loaded
            isLoading.postValue(true)
            // Get the story and world from the repository
            val retrievedStory = appRepository.getStoryById(storyId) ?: throw IllegalArgumentException("Story not found")
            story.postValue(retrievedStory)
            val retrievedWorld = appRepository.getWorldById(retrievedStory.worldId)
            world.postValue(retrievedWorld)
            // Set isLoading to false once the data is loaded
            isLoading.postValue(false)
            // Get the story entries from the repository
            appRepository.getStoryEntries(storyId).collect {
                storyEntries.postValue(it)
            }
        }
    }

    /**
     * Generates a new story entry based on the user selection.
     *
     * @param userSelection The user selection to generate the story entry.
     */
    fun generateStoryEntry(useLocalLLM: Boolean, userSelection: String = "") {
        viewModelScope.launch {
            // Check if the world and story are not null
            if (world.value == null || story.value == null) {
                throw IllegalArgumentException("World or story not found")
            }

            // If the user has made a selection, insert the selection as a story entry so it can be displayed
            if (userSelection.isNotEmpty()) {
                appRepository.insertStoryEntry(storyId, userSelection, true)
            }

            // Set isQuerying to true while the story entry is being generated
            isQuerying.postValue(true)

            // Iterate over all story entries and combine them into a string, excluding the user selections
            val story = storyEntries.value?.joinToString(separator = "\n") { entry ->
                if (entry.isUserSelection) {
                    ""
                } else {
                    entry.content
                }
            } ?: ""

            // Create the world object with the genre, subgenre, and premise
            val world = WorldInfo(world.value!!.genre, world.value!!.subGenre, world.value!!.premise)

            // Create the story request object with the story, user selection, and world
            val storyRequest = StoryRequest(useLocalLLM, story, userSelection, world)

            // Make a network request to generate the next story entry, including the JWT token
            val response = api.generateStoryEntry("Bearer ${userPreferences.getJWTToken()}", storyRequest)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // Insert the generated story entry into the database
                    appRepository.insertStoryEntry(storyId, body.story, false, body.options)
                }
            }

            // Set isQuerying to false once the story entry is generated
            isQuerying.postValue(false)
        }
    }

    /**
     * Reverts the story to the selected entry.
     *
     * @param entry The entry to revert the story to.
     */
    fun revertStory(entry: StoryEntry) {
        viewModelScope.launch {
            appRepository.revertStory(entry)
        }
    }
}