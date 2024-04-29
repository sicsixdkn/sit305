package com.sicsix.lostandfound.repository

import android.content.Context
import com.sicsix.lostandfound.database.AppDatabase
import com.sicsix.lostandfound.model.LostAndFound
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Date
import javax.inject.Inject

class AppRepository @Inject constructor(@ApplicationContext context: Context) {
    private val lostAndFoundDao = AppDatabase.getDatabase(context).lostAndFoundDao()

    /**
     * Creates a new lost or found advert with the given details.
     *
     * @param isLostAdvert True if the advert is for a lost item, false if it is for a found item.
     * @param name The name of the person who found or lost the item.
     * @param phone The phone number of the person who found or lost the item.
     * @param description The description of the item.
     * @param location The location where the item was found or lost.
     * @return The newly created advert, or null if the advert could not be created.
     */
    suspend fun createAdvert(
        isLostAdvert: Boolean,
        name: String,
        phone: String,
        description: String,
        location: String,
        latitude: Double,
        longitude: Double,
        date: Date
    ): LostAndFound? {
        val lostAndFound = LostAndFound(
            isLostAdvert = isLostAdvert,
            name = name,
            phone = phone,
            description = description,
            date = date.time,
            location = location,
            latitude = latitude,
            longitude = longitude
        )
        val id = lostAndFoundDao.insert(lostAndFound)
        return lostAndFoundDao.getById(id.toInt())
    }

    /**
     * Deletes the given lost and found advert from the database.
     *
     * @param advert The advert to delete.
     */
    suspend fun deleteAdvert(advert: LostAndFound) {
        lostAndFoundDao.delete(advert)
    }

    /**
     * Gets the lost and found advert with the given ID from the database.
     *
     * @param id The ID of the advert to get.
     * @return The lost and found advert with the given ID, or null if no advert was found.
     */
    suspend fun getAdvertById(id: Int): LostAndFound? {
        return lostAndFoundDao.getById(id)
    }

    /**
     * Gets all the lost and found adverts from the database.
     *
     * @return A list of all the lost and found adverts.
     */
    suspend fun getAdverts(): List<LostAndFound> {
        return lostAndFoundDao.getAll()
    }
}

