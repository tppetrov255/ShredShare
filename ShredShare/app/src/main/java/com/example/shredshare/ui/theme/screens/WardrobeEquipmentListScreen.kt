package com.example.shredshare.ui.theme.screens

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.shredshare.viewmodel.WardrobeEquipmentListViewModel
import com.google.maps.android.compose.MapUiSettings
import com.example.shredshare.ui.theme.components.AppScreenHeader
enum class EquipmentSortOption(val label: String) {
    NONE("Без сортиране"),
    PRICE_ASC("Цена: ниска към висока"),
    PRICE_DESC("Цена: висока към ниска")
}

@Composable
fun WardrobeEquipmentListRoute(
    wardrobeId: Int,
    latitude: Double?,
    longitude: Double?,
    wardrobeName: String = "Локация на гардероба",
    viewModel: WardrobeEquipmentListViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onBookingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onItemClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(wardrobeId) {
        viewModel.loadEquipment(wardrobeId)
    }

    WardrobeEquipmentListScreen(
        items = uiState.items,
        isLoading = uiState.isLoading,
        error = uiState.error,
        latitude = latitude,
        longitude = longitude,
        wardrobeName = wardrobeName,
        onBackClick = onBackClick,
        onProfileClick = onProfileClick,
        onCartClick = onCartClick,
        onBookingsClick = onBookingsClick,
        onLogoutClick = onLogoutClick,
        onItemClick = onItemClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeEquipmentListScreen(
    items: List<EquipmentResponseDto>,
    isLoading: Boolean = false,
    error: String? = null,
    onBackClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onBookingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    latitude: Double?,
    longitude: Double?,
    wardrobeName: String,
    onItemClick: (Int) -> Unit
){
    var showLocationSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    var appliedSize by remember { mutableStateOf("Всички") }
    var appliedType by remember { mutableStateOf("Всички") }
    var appliedBrand by remember { mutableStateOf("Всички") }
    var appliedMaxPrice by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf(EquipmentSortOption.NONE) }

    val sizeOptions = remember(items) {
        listOf("Всички") + items.mapNotNull { it.size }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    val typeOptions = remember(items) {
        listOf("Всички") + items.mapNotNull { it.type }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    val brandOptions = remember(items) {
        listOf("Всички") + items.mapNotNull { it.brand }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    val filteredItems = remember(
        items,
        appliedSize,
        appliedType,
        appliedBrand,
        appliedMaxPrice,
        selectedSort
    ) {
        val maxPrice = appliedMaxPrice.toDoubleOrNull()

        var result = items.filter { item ->
            val matchesSize = appliedSize == "Всички" || item.size == appliedSize
            val matchesType = appliedType == "Всички" || item.type == appliedType
            val matchesBrand = appliedBrand == "Всички" || item.brand == appliedBrand
            val matchesPrice = maxPrice == null || ((item.pricePerDay ?: Double.MAX_VALUE) <= maxPrice)

            matchesSize && matchesType && matchesBrand && matchesPrice
        }

        result = when (selectedSort) {
            EquipmentSortOption.NONE -> result
            EquipmentSortOption.PRICE_ASC -> result.sortedBy { it.pricePerDay ?: Double.MAX_VALUE }
            EquipmentSortOption.PRICE_DESC -> result.sortedByDescending { it.pricePerDay ?: Double.MIN_VALUE }
        }

        result
    }

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
            Spacer(modifier = Modifier.height(16.dp))

            // Заглавие
            AppScreenHeader(
                title = "Артикули в гардероба",
                onBackClick = onBackClick,
                onProfileClick = onProfileClick,
                onCartClick = onCartClick,
                onBookingsClick = onBookingsClick,
                onLogoutClick = onLogoutClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showFilterSheet = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xCC1E293B),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text("Филтър", maxLines = 1)
                }

                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = { showSortMenu = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xCC1E293B),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Сорт", maxLines = 1)
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        modifier = Modifier.background(Color(0xFF1E293B))
                    ) {
                        EquipmentSortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label, color = Color.White) },
                                onClick = {
                                    selectedSort = option
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = { showLocationSheet = true },
                    enabled = latitude != null && longitude != null,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xCC1E293B),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0x66232F3E),
                        disabledContentColor = Color.White.copy(alpha = 0.55f)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Локация", maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Активни филтри
            if (
                appliedSize != "Всички" ||
                appliedType != "Всички" ||
                appliedBrand != "Всички" ||
                appliedMaxPrice.isNotBlank() ||
                selectedSort != EquipmentSortOption.NONE
            ) {
                Text(
                    text = buildString {
                        append("Активни: ")
                        if (appliedSize != "Всички") append("Размер=$appliedSize  ")
                        if (appliedType != "Всички") append("Тип=$appliedType  ")
                        if (appliedBrand != "Всички") append("Бранд=$appliedBrand  ")
                        if (appliedMaxPrice.isNotBlank()) append("Макс цена=$appliedMaxPrice€  ")
                        if (selectedSort != EquipmentSortOption.NONE) append(selectedSort.label)
                    },
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

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

                filteredItems.isEmpty() -> {
                    Text(
                        text = "Няма артикули, които да отговарят на избраните филтри.",
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(filteredItems) { item ->
                            CustomerEquipmentCard(
                                item = item,
                                onClick = {
                                    onItemClick(item.equipmentId)
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF1E293B),
                contentColor = Color.White
            ) {
                FilterSheetContent(
                    sizeOptions = sizeOptions,
                    typeOptions = typeOptions,
                    brandOptions = brandOptions,
                    initialSize = appliedSize,
                    initialType = appliedType,
                    initialBrand = appliedBrand,
                    initialMaxPrice = appliedMaxPrice,
                    onClear = {
                        appliedSize = "Всички"
                        appliedType = "Всички"
                        appliedBrand = "Всички"
                        appliedMaxPrice = ""
                        showFilterSheet = false
                    },
                    onApply = { size, type, brand, maxPrice ->
                        appliedSize = size
                        appliedType = type
                        appliedBrand = brand
                        appliedMaxPrice = maxPrice
                        showFilterSheet = false
                    }
                )
            }
        }

        if (showLocationSheet) {
            ModalBottomSheet(
                onDismissRequest = { showLocationSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color(0xFF1E293B),
                contentColor = Color.White
            ) {
                WardrobeLocationSheetContent(
                    latitude = latitude,
                    longitude = longitude,
                    wardrobeName = wardrobeName
                )
            }
        }
    }
}

@Composable
private fun FilterSheetContent(
    sizeOptions: List<String>,
    typeOptions: List<String>,
    brandOptions: List<String>,
    initialSize: String,
    initialType: String,
    initialBrand: String,
    initialMaxPrice: String,
    onClear: () -> Unit,
    onApply: (String, String, String, String) -> Unit
) {
    var tempSize by remember { mutableStateOf(initialSize) }
    var tempType by remember { mutableStateOf(initialType) }
    var tempBrand by remember { mutableStateOf(initialBrand) }
    var tempMaxPrice by remember { mutableStateOf(initialMaxPrice) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = "Филтри",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))

        FilterDropdown(
            label = "Размер",
            selectedValue = tempSize,
            options = sizeOptions,
            onValueSelected = { tempSize = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        FilterDropdown(
            label = "Тип",
            selectedValue = tempType,
            options = typeOptions,
            onValueSelected = { tempType = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        FilterDropdown(
            label = "Бранд",
            selectedValue = tempBrand,
            options = brandOptions,
            onValueSelected = { tempBrand = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = tempMaxPrice,
            onValueChange = { tempMaxPrice = it },
            label = { Text("Максимална цена (€)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = filterTextFieldColors()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onClear,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("Изчисти")
            }

            Button(
                onClick = { onApply(tempSize, tempType, tempBrand, tempMaxPrice) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text("Приложи")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = filterTextFieldColors()
        )

        // Overlay to catch clicks
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.85f).background(Color(0xFF1E293B))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color.White) },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CustomerEquipmentCard(
    item: EquipmentResponseDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xE61E293B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (!item.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = "http://10.0.2.2:8080${item.imageUrl}",
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = item.name,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.type ?: "-",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "€${item.pricePerDay ?: "-"} / ден",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Налични: ${item.quantity ?: "-"}",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp
                )
            }

            if (!item.brand.isNullOrBlank() || !item.size.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = listOfNotNull(
                        item.brand?.takeIf { it.isNotBlank() },
                        item.size?.takeIf { it.isNotBlank() }?.let { "Размер: $it" }
                    ).joinToString(" • "),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun EquipmentInfoRow(label: String, value: String) {
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

@Composable
private fun filterTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedBorderColor = Color(0xFF2196F3),
    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
    cursorColor = Color.White
)

@Composable
private fun WardrobeLocationSheetContent(
    latitude: Double?,
    longitude: Double?,
    wardrobeName: String
) {
    if (latitude == null || longitude == null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "Локацията не е налична за този гардероб.",
                color = Color.White
            )
        }
        return
    }

    val location = remember(latitude, longitude) {
        LatLng(latitude, longitude)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 15f)
    }

    val markerState = rememberMarkerState(position = location)

    LaunchedEffect(location) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(location, 15f),
            durationMs = 800
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = wardrobeName,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Lat: $latitude, Lng: $longitude",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Използвай два пръста, за да местиш и увеличаваш картата.",
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(12.dp))


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .clip(RoundedCornerShape(18.dp))
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    mapToolbarEnabled = false,
                    myLocationButtonEnabled = false,
                    scrollGesturesEnabled = false,
                    scrollGesturesEnabledDuringRotateOrZoom = true,
                    zoomGesturesEnabled = true,
                    rotationGesturesEnabled = true,
                    tiltGesturesEnabled = true
                )
            ) {
                Marker(
                    state = markerState,
                    title = wardrobeName
                )
            }
        }
    }
}