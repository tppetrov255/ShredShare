package com.example.shredshare.ui.theme.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun AppScreenHeader(
    title: String,
    onBackClick: (() -> Unit)? = null,
    onProfileClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onBookingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    showProfile: Boolean = true,
    showCart: Boolean = true,
    showBookings: Boolean = true,
    showLogout: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBackClick != null) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = Color.White
                )
            }
        }

        Text(
            text = title,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        if (showProfile || showCart || showBookings || showLogout) {
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Меню",
                        tint = Color.White
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (showProfile) {
                        DropdownMenuItem(
                            text = { Text("Профил") },
                            onClick = {
                                expanded = false
                                onProfileClick()
                            }
                        )
                    }

                    if (showCart) {
                        DropdownMenuItem(
                            text = { Text("Количка") },
                            onClick = {
                                expanded = false
                                onCartClick()
                            }
                        )
                    }

                    if (showBookings) {
                        DropdownMenuItem(
                            text = { Text("Моите резервации") },
                            onClick = {
                                expanded = false
                                onBookingsClick()
                            }
                        )
                    }

                    if (showLogout) {
                        DropdownMenuItem(
                            text = { Text("Изход") },
                            onClick = {
                                expanded = false
                                onLogoutClick()
                            }
                        )
                    }
                }
            }
        }
    }
}