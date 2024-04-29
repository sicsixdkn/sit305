package com.sicsix.lostandfound.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.sicsix.lostandfound.viewmodels.AppViewModel

@Composable
fun MapScreen(navController: NavController, viewModel: AppViewModel = viewModel()) {
    // Get the devices current location
    viewModel.getCurrentLocation()
    // Observe the latitude and longitude from the ViewModel
    val foundLatitude = viewModel.latitude.observeAsState()
    val foundLongitude = viewModel.longitude.observeAsState()

    // Remember the camera position state
    val cameraPositionState = rememberCameraPositionState()

    // Observe the adverts from the ViewModel
    val adverts = viewModel.adverts.observeAsState(emptyList())

    // If the latitude and longitude are not null, set the camera position
    if (foundLatitude.value != null && foundLongitude.value != null) {
        cameraPositionState.position = CameraPosition(
            LatLng(foundLatitude.value!!, foundLongitude.value!!),
            16.0f,
            0.0f,
            0.0f
        )
    }

    // Display the Google Map
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // Add a marker for each advert
        adverts.value.forEach { advert ->
            Marker(
                title = "${if (advert.isLostAdvert) "Lost" else "Found"} - ${advert.description} \r\n (click)",
                state = MarkerState(LatLng(advert.latitude, advert.longitude)),
                onInfoWindowClick = {
                    // Navigate to the advert screen when the marker info window is clicked
                    navController.navigate("advert/${advert.id}")
                }
            )
        }
    }
}