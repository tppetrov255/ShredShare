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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.shredshare.R
import com.example.shredshare.data.dto.equipment.WardrobeListEquipmentDto
import com.example.shredshare.viewmodel.WardrobeListViewModel
import com.example.shredshare.ui.theme.components.AppScreenHeader

@Composable
fun WardrobeListRoute(
    resortId: Int,
    viewModel: WardrobeListViewModel = viewModel(),
    onViewEquipmentClick: (Int, Double?, Double?, String) -> Unit = { _, _, _, _ -> },
    onBackClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onBookingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(resortId) {
        viewModel.loadWardrobes(resortId)
    }

    WardrobeListScreen(
        resortId = resortId,
        wardrobes = uiState.wardrobes,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onViewEquipmentClick = onViewEquipmentClick,
        onBackClick = onBackClick,
        onProfileClick = onProfileClick,
        onCartClick = onCartClick,
        onBookingsClick = onBookingsClick,
        onLogoutClick = onLogoutClick
    )
}

@Composable
fun WardrobeListScreen(
    resortId: Int,
    wardrobes: List<WardrobeListEquipmentDto>,
    isLoading: Boolean = false,
    error: String? = null,
    onViewEquipmentClick: (Int, Double?, Double?, String) -> Unit = { _, _, _, _ -> },
    onBackClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onBookingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
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
                title = "Гардероби в курорта",
                onBackClick = onBackClick,
                onProfileClick = onProfileClick,
                onCartClick = onCartClick,
                onBookingsClick = onBookingsClick,
                onLogoutClick = onLogoutClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                error != null -> {
                    Text(
                        text = error,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }

                wardrobes.isEmpty() -> {
                    Text(
                        text = "Няма одобрени гардероби за тази локация.",
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 20.dp)
                    ) {
                        items(wardrobes) { wardrobe ->
                            WardrobeCard(
                                wardrobe = wardrobe,
                                onViewEquipmentClick = {
                                    onViewEquipmentClick(
                                        wardrobe.wardrobeId,
                                        wardrobe.latitude,
                                        wardrobe.longitude,
                                        wardrobe.name
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WardrobeCard(
    wardrobe: WardrobeListEquipmentDto,
    onViewEquipmentClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xE61E293B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (!wardrobe.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = "http://10.0.2.2:8080${wardrobe.imageUrl}",
                    contentDescription = wardrobe.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(18.dp))
            }

            Text(
                text = wardrobe.name,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Гардероб под наем",
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WardrobeInfoCard(
                    modifier = Modifier.weight(1f),
                    label = "Курорт",
                    value = wardrobe.resortName.ifBlank { "-" }
                )

                WardrobeInfoCard(
                    modifier = Modifier.weight(1f),
                    label = "Телефон",
                    value = wardrobe.phone.ifBlank { "-" }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            WardrobeWideInfoCard(
                label = "Адрес",
                value = wardrobe.address.ifBlank { "-" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Описание",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.06f)
                )
            ) {
                Text(
                    text = wardrobe.description.ifBlank { "-" },
                    color = Color.White,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onViewEquipmentClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xDD2196F3),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Виж артикулите",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun WardrobeInfoCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.06f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun WardrobeWideInfoCard(
    label: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.06f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}