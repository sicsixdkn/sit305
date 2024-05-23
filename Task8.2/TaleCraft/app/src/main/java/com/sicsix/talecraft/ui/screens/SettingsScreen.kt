package com.sicsix.talecraft.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sicsix.talecraft.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    // Observe the user's settings
    val useLargerReaderFont by viewModel.useLargerReaderFont.observeAsState()
    val useLocalLLM by viewModel.useLocalLLM.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp, 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Use large reader font",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = useLargerReaderFont ?: false,
                    onCheckedChange = {
                        viewModel.setUseLargerReaderFont(it)
                    },
                )
            }

            Spacer(modifier = Modifier.padding(0.dp, 8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Use locally hosted LLM",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = useLocalLLM ?: false,
                    onCheckedChange = {
                        viewModel.setUseLocalLLM(it)
                    },
                )
            }
        }
    }
}