package com.sicsix.talecraft.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sicsix.talecraft.models.World
import com.sicsix.talecraft.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorldViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {
    // LiveData to observe the worlds
    val worlds: LiveData<List<World>> = appRepository.getWorlds()

    /**
     * Inserts a new world with the given details.
     *
     * @param title The title of the world.
     * @param genre The genre of the world.
     * @param subGenre The sub-genre of the world.
     * @param premise The premise of the world.
     */
    fun insertWorld(title: String, genre: String, subGenre: String, premise: String) {
        viewModelScope.launch {
            appRepository.insertWorld(title, genre, subGenre, premise)
        }
    }

    /**
     * Deletes the given world.
     *
     * @param world The world to delete.
     */
    fun deleteWorld(world: World) {
        viewModelScope.launch {
            appRepository.deleteWorld(world)
        }
    }
}