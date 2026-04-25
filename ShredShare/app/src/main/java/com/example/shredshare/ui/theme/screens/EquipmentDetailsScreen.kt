package com.example.shredshare.ui.theme.screens

import com.example.shredshare.ui.theme.components.AppScreenHeader
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.shredshare.data.dto.equipment.EquipmentResponseDto
import com.example.shredshare.viewmodel.EquipmentDetailsViewModel

@Composable
fun EquipmentDetailsRoute(
    wardrobeId: Int,
    equipmentId: Int,
    wardrobeName: String,
    viewModel: EquipmentDetailsViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onBookClick: (EquipmentResponseDto) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onBookingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(equipmentId) {
        viewModel.loadEquipment(equipmentId)
    }

    EquipmentDetailsScreen(
        wardrobeId = wardrobeId,
        wardrobeName = wardrobeName,
        item = uiState.item,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onBackClick = onBackClick,
        onBookClick = {
            uiState.item?.let { onBookClick(it) }
        },
        onProfileClick = onProfileClick,
        onCartClick = onCartClick,
        onBookingsClick = onBookingsClick,
        onLogoutClick = onLogoutClick
    )
}
@Composable
fun EquipmentDetailsScreen(
    wardrobeId: Int,
    wardrobeName: String,
    item: EquipmentResponseDto?,
    isLoading: Boolean = false,
    error: String? = null,
    onBackClick: () -> Unit = {},
    onBookClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onBookingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

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
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            AppScreenHeader(
                title = "Детайли за артикул",
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                error != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xE61E293B)
                        )
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }

                item == null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xE61E293B)
                        )
                    ) {
                        Text(
                            text = "Артикулът не е намерен.",
                            color = Color.White,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }

                else -> {
                    EquipmentDetailsContent(
                        wardrobeName = wardrobeName,
                        item = item,
                        onBookClick = onBookClick
                    )
                }
            }
        }
    }
}

@Composable
private fun EquipmentDetailsContent(
    wardrobeName: String,
    item: EquipmentResponseDto,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xE61E293B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            if (!item.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = "http://10.0.2.2:8080${item.imageUrl}",
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(18.dp))
            }

            Text(
                text = item.name,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.type ?: "-",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(16.dp))

            DetailsInfoRow(
                label = "Бранд",
                value = item.brand ?: "-",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.85f)
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailsInfoCard(
                    modifier = Modifier.weight(1f),
                    label = "Размер",
                    value = item.size ?: "-",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Straighten,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f)
                        )
                    }
                )

                DetailsInfoCard(
                    modifier = Modifier.weight(1f),
                    label = "Количество",
                    value = item.quantity?.toString() ?: "-",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Numbers,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailsInfoCard(
                    modifier = Modifier.weight(1f),
                    label = "Цена на ден (€)",
                    value = item.pricePerDay?.toString() ?: "-",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Sell,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f)
                        )
                    }
                )

                DetailsInfoCard(
                    modifier = Modifier.weight(1f),
                    label = "Гардероб",
                    value = wardrobeName,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f)
                        )
                    }
                )
            }

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
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.85f)
                    )

                    Text(
                        text = item.description?.ifBlank { "-" } ?: "-",
                        color = Color.White,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBookClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xDD2196F3),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Добави в количка",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun DetailsInfoRow(
    label: String,
    value: String,
    icon: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(24.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Column {
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
}

@Composable
private fun DetailsInfoCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: @Composable () -> Unit
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
            Box(
                modifier = Modifier.width(24.dp),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}