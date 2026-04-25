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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shredshare.ui.theme.components.AppScreenHeader


data class AdminWardrobeRequestUi(
    val id: Int,
    val wardrobeName: String,
    val ownerName: String,
    val resortName: String,
    val phone: String,
    val address: String,
    val description: String,
    val status: String = "PENDING"
)

@Composable
fun AdminDashboardScreen(
    pendingRequests: List<AdminWardrobeRequestUi>,
    totalPending: Int,
    totalApproved: Int,
    totalOwners: Int,
    totalUsers: Int,
    isLoading: Boolean = false,
    onApproveClick: (Int) -> Unit = {},
    onRejectClick: (Int) -> Unit = {},
    onViewDetailsClick: (Int) -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}

) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = com.example.shredshare.R.drawable.login_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    AppScreenHeader(
                        title = "Админ панел",
                        onProfileClick = onProfileClick,
                        onLogoutClick = onLogoutClick ,
                        showProfile = true,
                        showCart = false,
                        showBookings = false,
                        showLogout = true
                    )
                }
            }
        ) { innerPadding ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Управление на заявки, собственици и гардероби",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 15.sp
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SummaryCard(
                                title = "Чакащи",
                                value = totalPending.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            SummaryCard(
                                title = "Одобрени",
                                value = totalApproved.toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SummaryCard(
                                title = "Собственици",
                                value = totalOwners.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            SummaryCard(
                                title = "Потребители",
                                value = totalUsers.toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Заявки за нови гардероби",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (pendingRequests.isEmpty()) {
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
                                        text = "Няма чакащи заявки.",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(pendingRequests) { request ->
                            AdminRequestCard(
                                request = request,
                                onApproveClick = { onApproveClick(request.id) },
                                onRejectClick = { onRejectClick(request.id) },
                                onViewDetailsClick = { onViewDetailsClick(request.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xE61E293B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AdminRequestCard(
    request: AdminWardrobeRequestUi,
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit,
    onViewDetailsClick: () -> Unit
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
                        text = request.wardrobeName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Собственик: ${request.ownerName}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp
                    )
                }

                StatusChip(status = request.status)
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(14.dp))

            AdminInfoRow("Курорт", request.resortName)
            AdminInfoRow("Телефон", request.phone)
            AdminInfoRow("Адрес", request.address)
            AdminInfoRow("Описание", request.description)

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onApproveClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF16A34A),
                        contentColor = Color.White
                    )
                ) {
                    Text("Одобри")
                }

                Button(
                    onClick = onRejectClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC2626),
                        contentColor = Color.White
                    )
                ) {
                    Text("Откажи")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onViewDetailsClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("Преглед на детайли")
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val chipColor = when (status.uppercase()) {
        "APPROVED" -> Color(0xFF16A34A)
        "REJECTED" -> Color(0xFFDC2626)
        else -> Color(0xFFF59E0B)
    }

    val statusText = when (status.uppercase()) {
        "APPROVED" -> "Одобрен"
        "REJECTED" -> "Отхвърлен"
        else -> "Очаква отговор"
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
private fun AdminInfoRow(label: String, value: String) {
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
