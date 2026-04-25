package com.example.shredshare.ui.theme.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.shredshare.R
import com.example.shredshare.viewmodel.AddEquipmentViewModel

private val equipmentTypeOptions = listOf(
    "Ски",
    "Сноуборд",
    "Ски обувки",
    "Сноуборд обувки",
    "Каска",
    "Щеки",
    "Очила"
)

@Composable
fun AddEquipmentRoute(
    wardrobeId: Int,
    equipmentId: Int? = null,
    viewModel: AddEquipmentViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onAddSuccess: () -> Unit = {}
) {
    LaunchedEffect(equipmentId) {
        if (equipmentId != null) {
            viewModel.loadEquipmentForEdit(equipmentId)
        } else {
            viewModel.resetState()
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onImageSelected(uri)
    }

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.onImageSelected(tempImageUri)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri(context)
            tempImageUri = uri
            cameraLauncher.launch(uri)
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onAddSuccess()
            viewModel.clearMessage()
        }
    }

    AddEquipmentScreen(
        wardrobeId = wardrobeId,
        uiState = uiState,
        onPickFromGallery = {
            galleryLauncher.launch("image/*")
        },
        onTakePhoto = {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                    val uri = createImageUri(context)
                    tempImageUri = uri
                    cameraLauncher.launch(uri)
                }
                else -> {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        },
        onNameChange = viewModel::onNameChange,
        onTypeChange = viewModel::onTypeChange,
        onBrandChange = viewModel::onBrandChange,
        onSizeChange = viewModel::onSizeChange,
        onPricePerDayChange = viewModel::onPricePerDayChange,
        onQuantityChange = viewModel::onQuantityChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onSubmit = {
            if (equipmentId != null) {
                viewModel.updateEquipment(
                    context = context,
                    equipmentId = equipmentId
                )
            } else {
                viewModel.createEquipment(
                    context = context,
                    wardrobeId = wardrobeId
                )
            }
        },
        onBackClick = onBackClick
    )
}

@Composable
fun AddEquipmentScreen(
    wardrobeId: Int,
    uiState: AddEquipmentViewModel.AddEquipmentUiState,
    onNameChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onSizeChange: (String) -> Unit,
    onPricePerDayChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onPickFromGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val whiteTextStyle = LocalTextStyle.current.copy(color = Color.White)
    var typeExpanded by remember { mutableStateOf(false) }

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
                .background(Color.Black.copy(alpha = 0.35f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (uiState.isEditMode) "Редактиране на артикул" else "Добавяне на артикул",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Гардероб ID: $wardrobeId",
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                    FieldLabel("Снимка на артикула")
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable { onPickFromGallery() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(uiState.imageUri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Inside
                            )
                        } else if (!uiState.existingImageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = "http://10.0.2.2:8080${uiState.existingImageUrl}",
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "Избери от галерия",
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onTakePhoto,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF475569),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Направи снимка")
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = onNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Име на артикула") },
                        textStyle = whiteTextStyle,
                        isError = uiState.validationErrors.containsKey("name"),
                        supportingText = {
                            uiState.validationErrors["name"]?.let { Text(it) }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Inventory2, contentDescription = null)
                        },
                        colors = registerLikeTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = uiState.type,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Тип") },
                            textStyle = whiteTextStyle,
                            isError = uiState.validationErrors.containsKey("type"),
                            supportingText = {
                                uiState.validationErrors["type"]?.let { Text(it) }
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Category, contentDescription = null)
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Избери тип",
                                    tint = Color.White
                                )
                            },
                            colors = registerLikeTextFieldColors()
                        )

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { typeExpanded = true }
                        )

                        DropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .background(Color(0xFF1E293B))
                        ) {
                            equipmentTypeOptions.forEach { type ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = type,
                                            color = Color.White
                                        )
                                    },
                                    onClick = {
                                        onTypeChange(type)
                                        typeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.brand,
                        onValueChange = onBrandChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Марка") },
                        textStyle = whiteTextStyle,
                        isError = uiState.validationErrors.containsKey("brand"),
                        supportingText = {
                            uiState.validationErrors["brand"]?.let { Text(it) }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Category, contentDescription = null)
                        },
                        colors = registerLikeTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.size,
                        onValueChange = onSizeChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Размер") },
                        textStyle = whiteTextStyle,
                        isError = uiState.validationErrors.containsKey("size"),
                        supportingText = {
                            uiState.validationErrors["size"]?.let { Text(it) }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Straighten, contentDescription = null)
                        },
                        colors = registerLikeTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.pricePerDay,
                        onValueChange = onPricePerDayChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Цена на ден (в евро - €)") },
                        textStyle = whiteTextStyle,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = uiState.validationErrors.containsKey("pricePerDay"),
                        supportingText = {
                            uiState.validationErrors["pricePerDay"]?.let { Text(it) }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Sell, contentDescription = null)
                        },
                        colors = registerLikeTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.quantity,
                        onValueChange = onQuantityChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Количество") },
                        textStyle = whiteTextStyle,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = uiState.validationErrors.containsKey("quantity"),
                        supportingText = {
                            uiState.validationErrors["quantity"]?.let { Text(it) }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Numbers, contentDescription = null)
                        },
                        colors = registerLikeTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = onDescriptionChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Описание") },
                        textStyle = whiteTextStyle,
                        minLines = 3,
                        isError = uiState.validationErrors.containsKey("description"),
                        supportingText = {
                            uiState.validationErrors["description"]?.let { Text(it) }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null)
                        },
                        colors = registerLikeTextFieldColors()
                    )

                    uiState.message?.let {
                        Text(
                            text = it,
                            color = if (uiState.isSuccess) Color(0xFF4ADE80) else MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onSubmit,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(0.88f)
                            .height(54.dp),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xDD2196F3),
                            contentColor = Color.White
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (uiState.isEditMode) "Запази промените" else "Добави артикул",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    TextButton(
                        onClick = onBackClick,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Назад",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun registerLikeTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    disabledTextColor = Color.White.copy(alpha = 0.6f),

    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    errorContainerColor = Color.Transparent,

    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
    disabledLabelColor = Color.White.copy(alpha = 0.5f),
    errorLabelColor = MaterialTheme.colorScheme.error,

    cursorColor = Color.White,
    errorCursorColor = MaterialTheme.colorScheme.error,

    focusedBorderColor = Color(0xFF2196F3),
    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
    disabledBorderColor = Color.White.copy(alpha = 0.3f),
    errorBorderColor = MaterialTheme.colorScheme.error,

    focusedLeadingIconColor = Color.White,
    unfocusedLeadingIconColor = Color.White.copy(alpha = 0.8f),
    disabledLeadingIconColor = Color.White.copy(alpha = 0.5f),
    errorLeadingIconColor = MaterialTheme.colorScheme.error,

    focusedSupportingTextColor = Color.White.copy(alpha = 0.8f),
    unfocusedSupportingTextColor = Color.White.copy(alpha = 0.7f),
    disabledSupportingTextColor = Color.White.copy(alpha = 0.5f),
    errorSupportingTextColor = MaterialTheme.colorScheme.error
)