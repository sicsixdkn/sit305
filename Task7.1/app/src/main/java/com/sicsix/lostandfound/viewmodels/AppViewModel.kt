package com.sicsix.lostandfound.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.sicsix.lostandfound.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {
    // LiveData to observe the adverts
    val adverts = liveData(Dispatchers.IO) { emit(appRepository.getAdverts()) }

    /**
     * Gets the lost and found advert with the given ID from the database.
     *
     * @param id The ID of the advert to get.
     * @return The lost and found advert with the given ID, or null if no advert was found.
     */
    fun getAdvertById(id: Int) = liveData(Dispatchers.IO) { emit(appRepository.getAdvertById(id)) }

    /**
     * Saves a new lost or found advert with the given details.
     *
     * @param isLostAdvert True if the advert is for a lost item, false if it is for a found item.
     * @param name The name of the person who found or lost the item.
     * @param phone The phone number of the person who found or lost the item.
     * @param description The description of the item.
     * @param location The location where the item was found or lost.
     * @param date The date when the item was found or lost.
     */
    fun saveAdvert(isLostAdvert: Boolean, name: String, phone: String, description: String, location: String, date: Date) {
        viewModelScope.launch {
            appRepository.createAdvert(isLostAdvert, name, phone, description, location, date)
        }
    }
}