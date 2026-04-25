package com.example.shredshare.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalTextStyle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.shredshare.R
import com.example.shredshare.data.dto.cart.CartItemDto
import com.example.shredshare.ui.theme.components.AppScreenHeader
import com.example.shredshare.viewmodel.CartViewModel
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
private val CartBlue = Color(0xDD2196F3)

@Composable
fun CartRoute(
    viewModel: CartViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onBookingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    CartScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onProfileClick = onProfileClick,
        onLogoutClick = onLogoutClick,
        onIncreaseClick = viewModel::increaseQuantity,
        onDecreaseClick = viewModel::decreaseQuantity,
        onRemoveClick = viewModel::removeItem,
        onStartDateChange = viewModel::onStartDateChange,
        onEndDateChange = viewModel::onEndDateChange,
        onSubmitBooking = { viewModel.submitBooking() },
        onBookingsClick = onBookingsClick
    )
}

@Composable
fun CartScreen(
    uiState: CartViewModel.CartUiState,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onIncreaseClick: (Int) -> Unit,
    onDecreaseClick: (Int) -> Unit,
    onRemoveClick: (Int) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onSubmitBooking: () -> Unit,
    onBookingsClick: () -> Unit = {},
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
                .padding(20.dp)
        ) {
            AppScreenHeader(
                title = "Количка",
                onBackClick = onBackClick,
                onProfileClick = onProfileClick,
                onCartClick = {},
                onBookingsClick = onBookingsClick,
                onLogoutClick = onLogoutClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isEmpty) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xE61E293B))
                ) {
                    Text(
                        text = "Количката е празна.",
                        color = Color.White,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(uiState.items) { item ->
                        CartItemCard(
                            item = item,
                            onIncreaseClick = { onIncreaseClick(item.equipmentId) },
                            onDecreaseClick = { onDecreaseClick(item.equipmentId) },
                            onRemoveClick = { onRemoveClick(item.equipmentId) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xE61E293B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        BookingDateField(
                            value = uiState.startDate,
                            label = "Начална дата",
                            onDateSelected = onStartDateChange
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        BookingDateField(
                            value = uiState.endDate,
                            label = "Крайна дата",
                            onDateSelected = onEndDateChange
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Общо: €${"%.2f".format(uiState.totalPrice)} / ден",
                            color = Color.White
                        )

                        uiState.error?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }

                        uiState.message?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(it, color = Color(0xFF4ADE80))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onSubmitBooking,
                            enabled = !uiState.isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CartBlue,
                                contentColor = Color.White
                            )
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Text("Потвърди резервация")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItemDto,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xE61E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!item.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = "http://10.0.2.2:8080${item.imageUrl}",
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = item.name,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text("Размер: ${item.size ?: "-"}", color = Color.White)
            Text("Цена на ден: €${"%.2f".format(item.pricePerDay)}", color = Color.White)
            Text("Количество: ${item.quantity}", color = Color.White)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onDecreaseClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CartBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text("-")
                }

                Button(
                    onClick = onIncreaseClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CartBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text("+")
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = onRemoveClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Премахни",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingDateField(
    value: String,
    label: String,
    onDateSelected: (String) -> Unit
) {
    val todayMillis = remember {
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        calendar.timeInMillis
    }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= todayMillis
            }
        }
    )

    var showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        DatePickerDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        val formattedDate = formatDateToApi(selectedMillis)

                        if (formattedDate.isNotBlank()) {
                            onDateSelected(formattedDate)
                        }

                        showDialog.value = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text("Отказ")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Избери дата",
                    tint = Color.White
                )
            },
            colors = cartTextFieldColors()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable {
                    showDialog.value = true
                }
        )
    }
}

@Composable
private fun cartTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    disabledTextColor = Color.White.copy(alpha = 0.6f),

    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    errorContainerColor = Color.Transparent,

    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White.copy(alpha = 0.75f),
    disabledLabelColor = Color.White.copy(alpha = 0.5f),
    errorLabelColor = MaterialTheme.colorScheme.error,

    cursorColor = Color.White,
    errorCursorColor = MaterialTheme.colorScheme.error,

    focusedBorderColor = Color.White,
    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
    disabledBorderColor = Color.White.copy(alpha = 0.3f),
    errorBorderColor = MaterialTheme.colorScheme.error
)

private fun formatDateToApi(millis: Long?): String {
    if (millis == null) return ""
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date(millis))
}