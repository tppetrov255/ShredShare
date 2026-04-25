package com.example.shredshare.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.shredshare.R
import com.example.shredshare.ui.theme.components.OwnerScreenHeader

@Composable
fun OwnerDashboardScreen(
    wardrobes: List<OwnerWardrobeUi>,
    isLoading: Boolean = false,
    error: String? = null,
    onCreateWardrobeClick: () -> Unit = {},
    onManageWardrobeClick: (Int) -> Unit = {},
    onManageOrdersClick: () -> Unit = {},
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
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            OwnerScreenHeader(
                title = "Табло на собственика",
                onManageOrdersClick = onManageOrdersClick,
                onLogoutClick = onLogoutClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Button(
                            onClick = onCreateWardrobeClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xDD2196F3),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Създай нов гардероб",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Моите гардероби",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (error != null) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xE67F1D1D)
                                )
                            ) {
                                Text(
                                    text = error,
                                    color = Color.White,
                                    modifier = Modifier.padding(18.dp)
                                )
                            }
                        }
                    }

                    if (wardrobes.isEmpty() && error == null) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xE61E293B)
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Все още нямате гардероби.",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(wardrobes) { wardrobe ->
                            OwnerWardrobeCard(
                                wardrobe = wardrobe,
                                onManageWardrobeClick = {
                                    onManageWardrobeClick(wardrobe.wardrobeId)
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun OwnerDashboardHeader(
    onLogoutClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Табло на собственика",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Управлявайте вашите гардероби и добавяйте артикули към одобрените.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }

        Button(
            onClick = onLogoutClick,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF475569),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Изход",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun OwnerWardrobeCard(
    wardrobe: OwnerWardrobeUi,
    onManageWardrobeClick: () -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = wardrobe.name,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Курорт: ${wardrobe.resortName}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp
                    )
                }

                OwnerStatusChip(status = wardrobe.status)
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(14.dp))

            WardrobeImage(imageUrl = wardrobe.imageUrl)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OwnerInfoColumn(
                    label = "Адрес",
                    value = wardrobe.address,
                    modifier = Modifier.weight(1f)
                )

                OwnerInfoColumn(
                    label = "Телефон",
                    value = wardrobe.phone,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OwnerInfoRow("Описание", wardrobe.description)

            Spacer(modifier = Modifier.height(16.dp))

            when (wardrobe.status.uppercase()) {
                "APPROVED" -> {
                    Button(
                        onClick = onManageWardrobeClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF16A34A),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Добави артикули / Управлявай")
                    }
                }

                "PENDING" -> {
                    OutlinedButton(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Очаква одобрение")
                    }
                }

                "REJECTED" -> {
                    OutlinedButton(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Отхвърлен")
                    }
                }

                else -> {
                    OutlinedButton(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Неизвестен статус")
                    }
                }
            }
        }
    }
}

@Composable
private fun WardrobeImage(imageUrl: String?) {
    val fullImageUrl = if (!imageUrl.isNullOrBlank()) {
        "http://10.0.2.2:8080$imageUrl"
    } else {
        null
    }

    if (fullImageUrl != null) {
        AsyncImage(
            model = fullImageUrl,
            contentDescription = "Снимка на гардероба",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Fit
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Няма качена снимка",
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun OwnerStatusChip(status: String) {
    val chipColor = when (status.uppercase()) {
        "APPROVED" -> Color(0xFF16A34A)
        "REJECTED" -> Color(0xFFDC2626)
        else -> Color(0xFFF59E0B)
    }

    val statusText = when (status.uppercase()) {
        "APPROVED" -> "Одобрен"
        "REJECTED" -> "Отхвърлен"
        else -> "Очаква"
    }

    AssistChip(
        onClick = {},
        label = {
            Text(
                text = statusText,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = chipColor.copy(alpha = 0.9f),
            labelColor = Color.White
        )
    )
}

@Composable
private fun OwnerInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value.ifBlank { "-" },
            color = Color.White,
            fontSize = 15.sp
        )
    }
}

@Composable
private fun OwnerInfoColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value.ifBlank { "-" },
            color = Color.White,
            fontSize = 15.sp
        )
    }
}