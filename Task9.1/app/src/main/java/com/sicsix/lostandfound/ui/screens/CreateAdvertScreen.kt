package com.sicsix.lostandfound.ui.screens

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sicsix.lostandfound.utility.Utility.Companion.convertDateToString
import com.sicsix.lostandfound.utility.Utility.Companion.validateEntry
import com.sicsix.lostandfound.utility.Utility.Companion.validatePhoneNumber
import com.sicsix.lostandfound.viewmodels.AppViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CreateAdvertScreen(navController: NavController, viewModel: AppViewModel = viewModel()) {
    // Local state for the input fields
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var selectedOption by rememberSaveable { mutableStateOf("Lost") }

    // Local state for the date picker
    var date by rememberSaveable { mutableStateOf(Date()) }
    var dateString by rememberSaveable { mutableStateOf("") }
    var datePickerDialog by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        initialDisplayMode = DisplayMode.Picker
    )

    // Local state for the error messages
    var nameError by rememberSaveable { mutableStateOf<String?>(null) }
    var phoneError by rememberSaveable { mutableStateOf<String?>(null) }
    var descriptionError by rememberSaveable { mutableStateOf<String?>(null) }
    var locationError by rememberSaveable { mutableStateOf<String?>(null) }

    // Observe the location and place ID from the view model
    val foundLocation by viewModel.location.observeAsState()
    val foundLatitude by viewModel.latitude.observeAsState()
    val foundLongitude by viewModel.longitude.observeAsState()

    val locationsPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // Set the location and place ID when the user clicks on the "Get current location" button
    LaunchedEffect(foundLocation) {
        if (foundLocation != null) {
            location = foundLocation!!
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp, 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Post type:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(end = 16.dp)
                )
                RadioButton(
                    selected = selectedOption == "Lost",
                    onClick = { selectedOption = "Lost" }
                )
                Text(
                    text = "Lost",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(end = 16.dp)
                )
                RadioButton(
                    selected = selectedOption == "Found",
                    onClick = { selectedOption = "Found" }
                )
                Text(text = "Found", style = MaterialTheme.typography.bodyLarge)
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                onValueChange = {
                    name = it
                    validateEntry(it)
                },
                label = { Text(text = "Name") },
                singleLine = true,
                isError = nameError != null,
                supportingText = {
                    if (nameError != null) {
                        Text(nameError!!)
                    }
                },
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = phone,
                onValueChange = {
                    phone = it
                    validatePhoneNumber(it)
                },
                label = { Text(text = "Phone number") },
                singleLine = true,
                isError = phoneError != null,
                supportingText = {
                    if (phoneError != null) {
                        Text(phoneError!!)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = description,
                onValueChange = {
                    description = it
                    validateEntry(it)
                },
                label = { Text(text = "Description") },
                minLines = 5,
                singleLine = false,
                isError = descriptionError != null,
                supportingText = {
                    if (descriptionError != null) {
                        Text(descriptionError!!)
                    }
                },
            )

            OutlinedTextField(
                modifier = Modifier
                    .clickable(onClick = { datePickerDialog = true })
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                value = dateString,
                label = { Text(text = "Date") },
                singleLine = true,
                onValueChange = {},
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = OutlinedTextFieldDefaults.colors().unfocusedTextColor,
                    disabledBorderColor = OutlinedTextFieldDefaults.colors().unfocusedIndicatorColor,
                    disabledLabelColor = OutlinedTextFieldDefaults.colors().unfocusedTextColor
                )
            )


            if (datePickerDialog) {
                DatePickerDialog(
                    onDismissRequest = { datePickerDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    date = Date(it)
                                    dateString = convertDateToString(date)
                                }
                                datePickerDialog = false
                            },
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { datePickerDialog = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            ExposedDropdownMenuBox(
                expanded = viewModel.locationAutofill.isNotEmpty(),
                onExpandedChange = {
                }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = location,
                    onValueChange = {
                        location = it
                        viewModel.searchPlaces(it)
                    },
                    label = { Text(text = "Location") },
                    singleLine = true,
                    isError = locationError != null,
                    supportingText = {
                        if (locationError != null) {
                            Text(locationError!!)
                        }
                    },
                )

                DropdownMenu(
                    expanded = viewModel.locationAutofill.isNotEmpty(),
                    onDismissRequest = {
                        viewModel.locationAutofill.clear()
                    },
                    modifier = Modifier.exposedDropdownSize(),
                    properties = PopupProperties(focusable = false)
                ) {
                    viewModel.locationAutofill.forEach {
                        DropdownMenuItem(text = { Text(it.address) }, onClick = {
                            location = it.address
                            viewModel.locationAutofill.clear()
                            viewModel.getLatLongForPlaceId(it.placeId)
                        })
                    }
                }
            }

            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    if (locationsPermissionState.status.isGranted) {
                        viewModel.getCurrentLocation()
                    } else {
                        locationsPermissionState.launchPermissionRequest()
                    }
                }) {
                Text("Get current location")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                onClick = {
                    // Validate the input fields before saving the advert
                    nameError = validateEntry(name)
                    phoneError = validatePhoneNumber(phone)
                    descriptionError = validateEntry(description)
                    locationError = validateEntry(location)

                    // If the location is not selected from the list, show an error message
                    if (foundLatitude == null || foundLongitude == null) {
                        locationError = "Please select a location from the list"
                    }

                    // If any of the fields have errors, do not save the advert
                    if (nameError != null || phoneError != null || descriptionError != null || locationError != null) {
                        return@Button
                    }

                    // Save the advert
                    viewModel.saveAdvert(
                        selectedOption == "Lost",
                        name,
                        phone,
                        description,
                        location,
                        foundLatitude!!,
                        foundLongitude!!,
                        date
                    )

                    // Navigate back to the home screen
                    navController.navigate("home")
                }) {
                Text("Save")
            }
        }
    }
}