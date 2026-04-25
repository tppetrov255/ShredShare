package com.example.shredshare.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shredshare.R
import com.example.shredshare.data.dto.OwnerBookingResponse
import com.example.shredshare.ui.theme.components.OwnerScreenHeader
import com.example.shredshare.viewmodel.OwnerBookingsViewModel

@Composable
fun OwnerBookingsRoute(
    ownerId: Int,
    viewModel: OwnerBookingsViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(ownerId) {
        viewModel.loadBookings(ownerId)
    }

    OwnerBookingsScreen(
        ownerId = ownerId,
        uiState = uiState,
        onBackClick = onBackClick,
        onLogoutClick = onLogoutClick,
        onUpdateStatus = { bookingId, newStatus ->
            viewModel.updateBookingStatus(ownerId, bookingId, newStatus)
        },
    )
}

@Composable
fun OwnerBookingsScreen(
    ownerId: Int,
    uiState: OwnerBookingsViewModel.OwnerBookingsUiState,
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onUpdateStatus: (Int, String) -> Unit = { _, _ -> },
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
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp)
        ) {
            OwnerScreenHeader(
                title = "Управление на поръчките",
                onBackClick = onBackClick,
                onLogoutClick = onLogoutClick
            )

            uiState.message?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x3322C55E))
                ) {
                    Text(
                        text = it,
                        color = Color(0xFF4ADE80),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                uiState.error != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xE61E293B))
                    ) {
                        Text(
                            text = uiState.error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }

                uiState.bookings.isEmpty() -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xE61E293B))
                    ) {
                        Text(
                            text = "Все още няма поръчки към вашите артикули.",
                            color = Color.White,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.bookings) { booking ->
                            OwnerBookingCard(
                                booking = booking,
                                onUpdateStatus = onUpdateStatus,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OwnerBookingCard(
    booking: OwnerBookingResponse,
    onUpdateStatus: (Int, String) -> Unit,
) {
    val actions = nextActionsForStatus(booking.status)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xE61E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Поръчка #${booking.bookingId}",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("Клиент: ${booking.customerName}", color = Color.White)
            Text("Имейл: ${booking.customerEmail}", color = Color.White)
            Text("Телефон: ${booking.customerPhone ?: "-"}", color = Color.White)
            Text("Адрес: ${booking.customerAddress ?: "-"}", color = Color.White)

            Spacer(modifier = Modifier.height(10.dp))

            Text("Период: ${booking.startDate} → ${booking.endDate}", color = Color.White)
            Text("Обща сума: €${"%.2f".format(booking.totalPrice)}", color = Color.White)
            Text("Статус: ${booking.status}", color = Color(0xFFFACC15))

            if (booking.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text("Артикули:", color = Color.White, fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(6.dp))

                booking.items.forEach { item ->
                    Text(
                        text = "• ${item.equipmentName} | Гардероб: ${item.wardrobeName} | x${item.quantity} | €${"%.2f".format(item.unitPrice)}/ден",
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            if (actions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    actions.forEach { action ->
                        Button(
                            onClick = { onUpdateStatus(booking.bookingId, action.second) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(action.first)
                        }
                    }
                }
            }

        }
    }
}

private fun nextActionsForStatus(status: String): List<Pair<String, String>> {
    return when (status.uppercase()) {
        "PENDING" -> listOf(
            "Одобри резервацията" to "APPROVED",
            "Откажи резервацията" to "REJECTED"
        )
        "APPROVED" -> listOf(
            "Маркирай като взета" to "PICKED_UP"
        )
        "PICKED_UP" -> listOf(
            "Маркирай като завършена" to "COMPLETED"
        )
        else -> emptyList()
    }
}
