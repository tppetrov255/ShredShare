package com.example.shredshare.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shredshare.R
import com.example.shredshare.data.dto.UserBookingResponse
import com.example.shredshare.ui.theme.components.AppScreenHeader
import com.example.shredshare.viewmodel.BookingsViewModel

@Composable
fun BookingsRoute(
    userId: Int,
    viewModel: BookingsViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadBookings(userId)
    }

    BookingsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onProfileClick = onProfileClick,
        onCartClick = onCartClick,
        onLogoutClick = onLogoutClick,
        onCancelBookingClick = viewModel::cancelBooking
    )
}

@Composable
fun BookingsScreen(
    uiState: BookingsViewModel.BookingsUiState,
    onBackClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onCancelBookingClick: (Int) -> Unit = {}
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
            AppScreenHeader(
                title = "Моите резервации",
                onBackClick = onBackClick,
                onProfileClick = onProfileClick,
                onCartClick = onCartClick,
                onBookingsClick = {},
                onLogoutClick = onLogoutClick
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
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
                            text = "Все още нямате направени резервации.",
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
                            BookingCard(
                                booking = booking,
                                onCancelClick = { onCancelBookingClick(booking.bookingId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingCard(
    booking: UserBookingResponse,
    onCancelClick: () -> Unit
) {
    val statusUpper = booking.status.uppercase()

    val statusColor = when (statusUpper) {
        "APPROVED" -> Color(0xFF4ADE80)
        "COMPLETED" -> Color(0xFF60A5FA)
        "PICKED_UP" -> Color(0xFFA78BFA)
        "CANCELLED" -> Color(0xFFFF6B6B)
        "REJECTED" -> Color(0xFFFF6B6B)
        else -> Color(0xFFFACC15)
    }

    val canBeCancelled = statusUpper == "PENDING" || statusUpper == "APPROVED"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xE61E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Резервация #${booking.bookingId}",
                color = Color.White,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Период: ${booking.startDate} → ${booking.endDate}",
                color = Color.White
            )

            Text(
                text = "Обща цена: €${"%.2f".format(booking.totalPrice)}",
                color = Color.White
            )

            Text(
                text = "Статус: ${bookingStatusText(booking.status)}",
                color = statusColor
            )

            if (booking.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Артикули:",
                    color = Color.White,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                booking.items.forEach { item ->
                    Text(
                        text = "• ${item.equipmentName} x${item.quantity} (€${"%.2f".format(item.unitPrice)}/ден)",
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            if (canBeCancelled) {
                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White
                    )
                ) {
                    Text("Откажи резервацията")
                }
            }
        }
    }
}

private fun bookingStatusText(status: String): String {
    return when (status.uppercase()) {
        "PENDING" -> "Очаква потвърждение"
        "APPROVED" -> "Одобрена и готова за взимане от гардероб"
        "REJECTED" -> "Отказана"
        "PICKED_UP" -> "Взета"
        "COMPLETED" -> "Завършена"
        "CANCELLED" -> "Отменена"
        else -> status
    }
}