package com.example.shredshare.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.shredshare.R
import com.example.shredshare.ui.theme.components.AppScreenHeader
import com.example.shredshare.viewmodel.AdminDetailsViewModel

@Composable
fun AdminDetailsRoute(
    wardrobeId: Int,
    viewModel: AdminDetailsViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(wardrobeId) {
        viewModel.loadDetails(wardrobeId)
    }

    AdminDetailsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onLogoutClick = onLogoutClick,
        onProfileClick = onProfileClick
    )
}

@Composable
fun AdminDetailsScreen(
    uiState: AdminDetailsViewModel.UiState,
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            AppScreenHeader(
                title = "Детайли за гардероб",
                onBackClick = onBackClick,
                onProfileClick = onProfileClick,
                onLogoutClick = onLogoutClick,
                showProfile = true,
                showCart = false,
                showBookings = false,
                showLogout = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                uiState.error != null -> {
                    Text(
                        text = uiState.error,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }

                uiState.details != null -> {
                    val d = uiState.details

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xE61E293B)
                                )
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    if (!d.imageUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = "http://10.0.2.2:8080${d.imageUrl}",
                                            contentDescription = d.wardrobeName,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(220.dp),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))
                                    }

                                    Text(
                                        text = d.wardrobeName,
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                                    Spacer(modifier = Modifier.height(12.dp))

                                    AdminDetailRow("Курорт", d.resortName)
                                    AdminDetailRow("Телефон", d.phone ?: "-")
                                    AdminDetailRow("Адрес", d.address ?: "-")
                                    AdminDetailRow("Описание", d.description ?: "-")
                                    AdminDetailRow("Статус", d.status)
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xE61E293B)
                                )
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "Информация за собственика",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                                    Spacer(modifier = Modifier.height(12.dp))

                                    AdminDetailRow("Owner ID", d.ownerId?.toString() ?: "-")
                                    AdminDetailRow(
                                        "Име",
                                        "${d.ownerFirstName ?: ""} ${d.ownerLastName ?: ""}".trim().ifEmpty { "-" }
                                    )
                                    AdminDetailRow("Email", d.ownerEmail ?: "-")
                                    AdminDetailRow("Телефон", d.ownerPhone ?: "-")
                                    AdminDetailRow("Роля", d.ownerRole ?: "-")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminDetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 15.sp
        )
    }
}