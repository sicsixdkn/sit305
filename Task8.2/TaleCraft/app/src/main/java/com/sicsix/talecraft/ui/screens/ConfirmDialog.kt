package com.sicsix.talecraft.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDialog(
    showDialog: Boolean?,
    title: String,
    content: String?,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    if (showDialog == true) {
        AlertDialog(
            onDismissRequest = {
                onConfirm()
            },
            title = {
                Text(text = title)
            },
            text = {
                if (content != null) {
                    Text(text = content)
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        onConfirm()
                    },
                ) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onCancel()
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}