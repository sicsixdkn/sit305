package com.sicsix.talecraft.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sicsix.talecraft.utility.Utility
import com.sicsix.talecraft.viewmodels.WorldViewModel

// Enum class for the different genres and sub-genres
enum class Genre(val genre: String, val subGenres: List<String>) {
    FANTASY("Fantasy", listOf("High Fantasy", "Urban Fantasy", "Dark Fantasy", "Mythic Fantasy")), SCIENCE_FICTION(
        "Science Fiction",
        listOf("Space Opera", "Cyberpunk", "Time Travel", "Dystopian")
    ),
    MYSTERY("Mystery", listOf("Detective", "Cozy Mystery", "Noir", "Supernatural Mystery")), HORROR(
        "Horror",
        listOf("Gothic Horror", "Psychological Horror", "Supernatural Horror", "Slasher")
    ),
    HISTORICAL("Historical", listOf("Medieval", "Victorian", "Ancient Civilizations", "World War Eras")), ADVENTURE(
        "Adventure",
        listOf("Exploration", "Survival", "Swashbuckling", "Lost Worlds")
    ),
    WESTERN("Western", listOf("Classic Western", "Weird West", "Outlaw Tales", "Frontier Stories")), ROMANCE(
        "Romance",
        listOf("Contemporary Romance", "Historical Romance", "Paranormal Romance", "Romantic Comedy")
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorldScreen(navController: NavController, viewModel: WorldViewModel) {
    // Local state for the title and premise text fields
    var title by rememberSaveable { mutableStateOf("") }
    var premise by rememberSaveable { mutableStateOf("") }
    // Local state for the error messages
    var titleError by rememberSaveable { mutableStateOf<String?>(null) }
    var premiseError by rememberSaveable { mutableStateOf<String?>(null) }
    // Local state for the genre and sub-genre dropdowns
    var genreExpanded by remember { mutableStateOf(false) }
    var subGenreExpanded by remember { mutableStateOf(false) }
    var selectedGenre by remember { mutableStateOf(Genre.FANTASY) }
    var selectedSubGenre by remember { mutableStateOf<String>(Genre.FANTASY.subGenres[0]) }
    val subGenreOptions = selectedGenre.subGenres

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp, 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Filled.Public,
                contentDescription = "World",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(128.dp)
            )

            Column(
                Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Let's create a new world!", style = MaterialTheme.typography.headlineMedium
                )
            }


            Spacer(modifier = Modifier.weight(0.33f))

            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = title,
                    onValueChange = {
                        title = it
                        Utility.validateEntry(it)
                    },
                    label = { Text(text = "Title") },
                    singleLine = true,
                    isError = titleError != null,
                    supportingText = {
                        if (titleError != null) {
                            Text(titleError!!)
                        }
                    },
                )

                ExposedDropdownMenuBox(
                    expanded = genreExpanded, onExpandedChange = { genreExpanded = it }, modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true,
                        value = selectedGenre.genre,
                        onValueChange = {},
                        label = { Text("Genre") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genreExpanded) },
                    )
                    ExposedDropdownMenu(expanded = genreExpanded, onDismissRequest = { genreExpanded = false }) {
                        Genre.entries.forEach { genre ->
                            DropdownMenuItem(text = { Text(text = genre.genre) }, onClick = {
                                selectedGenre = genre
                                selectedSubGenre = genre.subGenres[0]
                                genreExpanded = false
                            })

                        }
                    }
                }

                Spacer(modifier = Modifier.size(16.dp))

                ExposedDropdownMenuBox(
                    expanded = subGenreExpanded, onExpandedChange = { subGenreExpanded = it }, modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true,
                        value = selectedSubGenre,
                        onValueChange = { },
                        label = { Text("Sub-Genre") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subGenreExpanded) },
                    )
                    ExposedDropdownMenu(expanded = subGenreExpanded, onDismissRequest = { subGenreExpanded = false }) {
                        subGenreOptions.forEach { subGenre ->
                            DropdownMenuItem(text = { Text(text = subGenre) }, onClick = {
                                selectedSubGenre = subGenre
                                subGenreExpanded = false
                            })
                        }
                    }
                }

                Spacer(modifier = Modifier.size(16.dp))

                OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = premise, onValueChange = {
                    premise = it
                    Utility.validateEntry(it)
                }, label = { Text(text = "Premise") }, singleLine = false, isError = premiseError != null, supportingText = {
                    if (premiseError != null) {
                        Text(premiseError!!)
                    }
                }, minLines = 6, maxLines = 6
                )

                Spacer(modifier = Modifier.size(24.dp))

                Button(
                    onClick = {
                        titleError = Utility.validateEntry(title)
                        premiseError = Utility.validateEntry(premise)

                        if (titleError != null || premiseError != null) {
                            return@Button
                        }

                        // Insert the world into the database
                        viewModel.insertWorld(
                            title = title, genre = selectedGenre.genre, subGenre = selectedSubGenre, premise = premise
                        )
                        // Navigate back to the worlds screen
                        navController.navigate("worlds")
                    }, modifier = Modifier
                        .align(Alignment.End)
                        .fillMaxWidth()
                ) {
                    Text(text = "Create world")
                }
            }
        }
    }
}