package com.example.shredshare.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
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
import com.example.shredshare.viewmodel.ManageEquipmentViewModel

@Composable
fun ManageEquipmentRoute(
    wardrobeId: Int,
    viewModel: ManageEquipmentViewModel = viewModel(),
    onAddItemClick: (Int) -> Unit = {},
    onEditItemClick: (Int, Int) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(wardrobeId) {
        viewModel.loadItems(wardrobeId)
    }

    ManageEquipmentScreen(
        wardrobeId = wardrobeId,
        items = uiState.items,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onAddItemClick = { onAddItemClick(wardrobeId) },
        onEditItemClick = { equipmentId ->
            onEditItemClick(wardrobeId, equipmentId)
        },
        onDeleteItemClick = { equipmentId ->
            viewModel.deleteItem(wardrobeId, equipmentId)
        },
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageEquipmentScreen(
    wardrobeId: Int,
    items: List<EquipmentResponseDto>,
    isLoading: Boolean = false,
    error: String? = null,
    onAddItemClick: () -> Unit = {},
    onEditItemClick: (Int) -> Unit = {},
    onDeleteItemClick: (Int) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
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

    val itemToDelete = remember { mutableStateOf<Int?>(null) }

    if (itemToDelete.value != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete.value = null },
            title = { Text("Потвърждение") },
            text = { Text("Сигурен ли си, че искаш да изтриеш този артикул?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteItemClick(itemToDelete.value!!)
                        itemToDelete.value = null
                    }
                ) {
                    Text("Да")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { itemToDelete.value = null }
                ) {
                    Text("Не")
                }
            }
        )
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Управление на артикули",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ред с бутони за Филтър, Сорт и Добавяне
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddItemClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xDD2196F3),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Добави", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = { showFilterSheet = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xCC1E293B),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.FilterList, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Филтър", fontSize = 13.sp)
                }

                Box(modifier = Modifier.weight(0.8f)) {
                    Button(
                        onClick = { showSortMenu = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xCC1E293B),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Sort, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Сорт", fontSize = 13.sp)
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

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (error != null) {
                        item {
                            Text(text = error, color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (filteredItems.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xE61E293B))
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text("Няма намерени артикули.", color = Color.White.copy(alpha = 0.9f))
                                }
                            }
                        }
                    } else {
                        items(filteredItems) { item ->
                            ManageEquipmentCard(
                                item = item,
                                onEditClick = { onEditItemClick(item.equipmentId) },
                                onDeleteClick = { itemToDelete.value = item.equipmentId }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
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
                ManageFilterSheetContent(
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
    }
}

@Composable
private fun ManageFilterSheetContent(
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
        Text("Филтри", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(20.dp))

        FilterDropdownItem("Размер", tempSize, sizeOptions) { tempSize = it }
        Spacer(modifier = Modifier.height(12.dp))
        FilterDropdownItem("Тип", tempType, typeOptions) { tempType = it }
        Spacer(modifier = Modifier.height(12.dp))
        FilterDropdownItem("Бранд", tempBrand, brandOptions) { tempBrand = it }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = tempMaxPrice,
            onValueChange = { tempMaxPrice = it },
            label = { Text("Максимална цена (€)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onClear,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) { Text("Изчисти") }

            Button(
                onClick = { onApply(tempSize, tempType, tempBrand, tempMaxPrice) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) { Text("Приложи") }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdownItem(
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
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
            )
        )
        Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.85f).background(Color(0xFF1E293B))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color.White) },
                    onClick = { onValueSelected(option); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun ManageEquipmentCard(
    item: EquipmentResponseDto,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xE61E293B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (!item.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = "http://10.0.2.2:8080${item.imageUrl}",
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            Text(item.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(item.type ?: "-", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) { InfoDisplayRow("Размер", item.size ?: "-") }
                Column(modifier = Modifier.weight(1f)) { InfoDisplayRow("Бранд", item.brand ?: "-") }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) { InfoDisplayRow("Цена на ден (€)", item.pricePerDay?.toString() ?: "-") }
                Column(modifier = Modifier.weight(1f)) { InfoDisplayRow("Количество", item.quantity?.toString() ?: "-") }
            }
            InfoDisplayRow("Описание", item.description ?: "-")

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onEditClick, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))) {
                    Text("Редактирай", fontWeight = FontWeight.Bold)
                }
                Button(onClick = onDeleteClick, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))) {
                    Text("Изтрий", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun InfoDisplayRow(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(label, color = Color.White.copy(alpha = 0.65f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text(value, color = Color.White, fontSize = 15.sp)
    }
}
