package com.example.shredshare.ui.theme.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.shredshare.R
import com.example.shredshare.viewmodel.CreateWardrobeViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWardrobeScreen(
    ownerId: Int,
    navController: NavController,
    onPickLocationClick: () -> Unit = {},
    onCreateSuccess: () -> Unit = {},
    viewModel: CreateWardrobeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Image Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onImageSelected(uri)
    }

    // Camera State and Launcher
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.onImageSelected(tempImageUri)
        }
    }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri(context)
            tempImageUri = uri
            cameraLauncher.launch(uri)
        }
    }

    // Listen for results from the map picker using SavedStateHandle
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    
    savedStateHandle?.let { handle ->
        val pickedLat by handle.getLiveData<Double>("picked_lat").observeAsState()
        val pickedLng by handle.getLiveData<Double>("picked_lng").observeAsState()

        LaunchedEffect(pickedLat, pickedLng) {
            val lat = pickedLat
            val lng = pickedLng
            if (lat != null && lng != null) {
                viewModel.updateLocation(
                    latitude = lat,
                    longitude = lng,
                    address = "Локация: $lat, $lng"
                )
                handle.remove<Double>("picked_lat")
                handle.remove<Double>("picked_lng")
            }
        }
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onCreateSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.create_wardrobe_title),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xE61E293B)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Image Picker Section
                    FieldLabel("Снимка на гардероба")
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable { galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(uiState.imageUri),
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
                                Text("Избери от галерия", color = Color.White.copy(alpha = 0.7f))
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = {
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
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF475569)
                        )
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Направи снимка")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Resort Selection
                    FieldLabel("Изберете ски курорт")
                    Spacer(modifier = Modifier.height(8.dp))

                    var expanded by remember { mutableStateOf(false) }
                    var searchQuery by remember { mutableStateOf(uiState.selectedResort?.name ?: "") }

                    val filteredResorts = remember(searchQuery, uiState.resorts) {
                        if (searchQuery.isBlank()) {
                            uiState.resorts
                        } else {
                            uiState.resorts.filter {
                                it.name.contains(searchQuery, ignoreCase = true)
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                expanded = true
                            },
                            readOnly = false,
                            singleLine = true,
                            placeholder = {
                                Text("Изберете или потърсете курорт")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryEditable)
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedBorderColor = Color(0xFF94A3B8),
                                unfocusedBorderColor = Color(0xFF94A3B8),
                                focusedPlaceholderColor = Color(0xFFAEB7C2),
                                unfocusedPlaceholderColor = Color(0xFFAEB7C2),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            if (filteredResorts.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Няма намерени курорти") },
                                    onClick = { }
                                )
                            } else {
                                filteredResorts.forEach { resort ->
                                    DropdownMenuItem(
                                        text = { Text(resort.name) },
                                        onClick = {
                                            searchQuery = resort.name
                                            viewModel.onResortSelected(resort)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    if (uiState.validationErrors.containsKey("resort")) {
                        Text(text = uiState.validationErrors["resort"]!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    FieldLabel(stringResource(R.string.wardrobe_name_label))
                    Spacer(modifier = Modifier.height(8.dp))
                    StyledTextField(
                        value = uiState.wardrobeName,
                        onValueChange = { viewModel.onWardrobeNameChange(it) },
                        placeholder = "Въведете име на гардероба",
                        isError = uiState.validationErrors.containsKey("wardrobeName"),
                        supportingText = uiState.validationErrors["wardrobeName"]
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    FieldLabel(stringResource(R.string.address_label))
                    Spacer(modifier = Modifier.height(8.dp))
                    StyledTextField(
                        value = uiState.address,
                        onValueChange = { viewModel.onAddressChange(it) },
                        placeholder = "Въведете адрес",
                        isError = uiState.validationErrors.containsKey("address"),
                        supportingText = uiState.validationErrors["address"]
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    FieldLabel(stringResource(R.string.phone_label))
                    Spacer(modifier = Modifier.height(8.dp))
                    StyledTextField(
                        value = uiState.phone,
                        onValueChange = { viewModel.onPhoneChange(it) },
                        placeholder = "Въведете телефонен номер",
                        isError = uiState.validationErrors.containsKey("phone"),
                        supportingText = uiState.validationErrors["phone"]
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    FieldLabel(stringResource(R.string.description_label))
                    Spacer(modifier = Modifier.height(8.dp))
                    StyledTextField(
                        value = uiState.description,
                        onValueChange = { viewModel.onDescriptionChange(it) },
                        placeholder = "Опишете вашия гардероб...",
                        singleLine = false,
                        minLines = 4,
                        isError = uiState.validationErrors.containsKey("description"),
                        supportingText = uiState.validationErrors["description"]
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    FieldLabel(stringResource(R.string.location_label))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.selectedLocationText,
                        color = if (uiState.validationErrors.containsKey("location")) MaterialTheme.colorScheme.error else Color(0xFFD1D5DB),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onPickLocationClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF475569),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = stringResource(R.string.pick_on_map), fontSize = 16.sp)
                    }

                    uiState.error?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = { viewModel.createWardrobe(context, ownerId) },
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xDD2196F3),
                            contentColor = Color.White
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text(text = stringResource(R.string.create_listing_button), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

fun createImageUri(context: Context): Uri {
    val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

@Composable
fun FieldLabel(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
}

@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        placeholder = {
            Text(text = placeholder, color = Color(0xFFAEB7C2))
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else Color(0xFF94A3B8),
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else Color(0xFF94A3B8),
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedPlaceholderColor = Color(0xFFAEB7C2),
            unfocusedPlaceholderColor = Color(0xFFAEB7C2),
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    )
}
