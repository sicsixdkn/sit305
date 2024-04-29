package com.sicsix.lostandfound.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.sicsix.lostandfound.model.AutocompleteResult
import com.sicsix.lostandfound.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val appRepository: AppRepository,
) : ViewModel() {
    // LiveData to observe the adverts
    val adverts = liveData(Dispatchers.IO) { emit(appRepository.getAdverts()) }

    // Fused location client to get the user's location and geocoder to get the address
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val geocoder = Geocoder(context)

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
    fun saveAdvert(
        isLostAdvert: Boolean,
        name: String,
        phone: String,
        description: String,
        location: String,
        latitude: Double,
        longitude: Double,
        date: Date
    ) {
        viewModelScope.launch {
            appRepository.createAdvert(isLostAdvert, name, phone, description, location, latitude, longitude, date)
        }
    }

    // Places API client
    private val placesClient = Places.createClient(context)

    // Job for the search places coroutine
    private var searchJob: Job? = null

    // List of autocomplete results for the location search
    val locationAutofill = mutableStateListOf<AutocompleteResult>()

    /**
     * Searches for places matching the given query.
     *
     * @param query The query to search for.
     */
    fun searchPlaces(query: String) {
        // Cancel the previous search job so that only the latest search is executed
        searchJob?.cancel()
        // Clear the previous search results
        locationAutofill.clear()
        // Start a new search job
        searchJob = viewModelScope.launch {
            // Delay the search to avoid making too many requests
            delay(500)
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setCountries("AU")
                .build()
            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    // Add the search results to the list of autocomplete results
                    locationAutofill += response.autocompletePredictions.map {
                        AutocompleteResult(it.getFullText(null).toString(), it.placeId)
                    }
                }
                .addOnFailureListener {
                    println("Predictions failure: $it")
                }
        }
    }

    // LiveData for the location, latitude, and longitude
    val location = MutableLiveData<String>()
    val latitude = MutableLiveData<Double?>(null)
    val longitude = MutableLiveData<Double?>(null)


    /**
     * Gets the current location of the user.
     */
    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        viewModelScope.launch {
            val currentLocation = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).await()
            val address = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
            if (!address.isNullOrEmpty()) {
                location.postValue(address[0].getAddressLine(0))
                latitude.postValue(currentLocation.latitude)
                longitude.postValue(currentLocation.longitude)
            } else {
                location.postValue("")
                latitude.postValue(null)
                longitude.postValue(null)
            }
        }
    }

    /**
     * Gets the latitude and longitude of the place with the given ID.
     *
     * @param placeId The ID of the place to get the latitude and longitude for.
     */
    fun getLatLongForPlaceId(placeId: String) {
        viewModelScope.launch {
            val request = FetchPlaceRequest.newInstance(placeId, listOf(Place.Field.LAT_LNG))
            placesClient.fetchPlace(request)
                .addOnSuccessListener { response ->
                    val latLng = response.place.latLng
                    if (latLng != null) {
                        latitude.postValue(latLng.latitude)
                        longitude.postValue(latLng.longitude)
                    }
                }
                .addOnFailureListener {
                    println("Fetch place failure: $it")
                }
        }
    }
}