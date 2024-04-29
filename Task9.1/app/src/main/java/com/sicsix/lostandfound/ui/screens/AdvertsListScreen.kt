package com.sicsix.lostandfound.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sicsix.lostandfound.utility.Utility.Companion.convertDateToString
import com.sicsix.lostandfound.viewmodels.AppViewModel
import java.util.Date

@Composable
fun AdvertsListScreen(navController: NavController, viewModel: AppViewModel = viewModel()) {
    // Collecting adverts from the ViewModel
    val adverts by viewModel.adverts.observeAsState(emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp, 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Lost & Found Items",
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            LazyColumn {
                items(adverts) { item ->
                    ElevatedCard(
                        Modifier
                            .padding(vertical = 12.dp)
                            .clickable(
                                onClick = { navController.navigate("advert/${item.id}") }
                            )
                    ) {
                        ListItem(
                            modifier = Modifier.fillMaxWidth(),
                            overlineContent = { Text(if (item.isLostAdvert) { "Lost" } else { "Found" })},
                            headlineContent = { Text(item.description) },
                            supportingContent = {
                                Text(
                                    text = "${convertDateToString(Date(item.date))} - ${item.location}",
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}
