package com.sicsix.lostandfound.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sicsix.lostandfound.utility.Utility.Companion.convertDateToRelativeTime
import com.sicsix.lostandfound.viewmodels.AppViewModel
import java.util.Date

@Composable
fun AdvertScreen(viewModel: AppViewModel = viewModel(), advertId: String) {
    // Collecting the advert by ID from the ViewModel
    val advert by viewModel.getAdvertById(advertId.toInt()).observeAsState(null)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp, 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (advert == null) {
                return@Box
            }

            Text(
                text = if (advert?.isLostAdvert == true) {
                    "Lost"
                } else {
                    "Found"
                },
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = advert!!.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "When",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = convertDateToRelativeTime(Date(advert!!.date)),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Location",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = advert!!.location,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Contact",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "${advert!!.name} at ${advert!!.phone}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}