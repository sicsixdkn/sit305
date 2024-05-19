package com.sicsix.talecraft.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PopupDialog(
    showDialog: Boolean?,
    title: String,
    content: String?,
    onConfirm: () -> Unit
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
                Button(
                    onClick = {
                        onConfirm()
                    }
                ) {
                    Text(text = "Ok")
                }
            }
        )
    }
}