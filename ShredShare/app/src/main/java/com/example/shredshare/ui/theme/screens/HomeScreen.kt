package com.example.shredshare.ui.theme.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shredshare.R
import com.example.shredshare.data.dto.resort.Resort
import com.example.shredshare.ui.theme.themes.ShredShareTheme
import com.example.shredshare.viewmodel.HomeViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.android.gms.location.LocationServices
@Composable
fun HomeScreen(
    onMyLocationClick: () -> Unit = {},
    onViewWardrobesClick: (Resort?) -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var selectedResort by remember { mutableStateOf<Resort?>(null) }
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var locationPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions.values.all { it }
    }

    LaunchedEffect(Unit) {
        if (!locationPermissionGranted) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val defaultLocation = LatLng(54.5260, 15.2551) //central europe ish

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 3.8f)
    }

    val mapProperties = remember(locationPermissionGranted) {
        MapProperties(isMyLocationEnabled = locationPermissionGranted)
    }

    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false
        )
    }

    // Zoom to selected resort when it changes
    LaunchedEffect(selectedResort) {
        selectedResort?.let { resort ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    LatLng(resort.latitude, resort.longitude),
                    12f
                ),
                durationMs = 1000
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings
        ) {
            uiState.resorts.forEach { resort ->
                val markerState = rememberMarkerState(
                    key = resort.resortId.toString(),
                    position = LatLng(resort.latitude, resort.longitude)
                )

                Marker(
                    state = markerState,
                    title = resort.name,
                    snippet = resort.country ?: "",
                    onClick = {
                        selectedResort = resort
                        false
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.08f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xB31E293B)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.welcome_title),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.home_subtitle),
                        color = Color(0xFFE5E7EB),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xCCFFFFFF)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF374151)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    BasicTextField(
                        value = searchText,
                        onValueChange = { newValue ->
                            searchText = newValue
                            // To prevent jumping, only update selection on exact match
                            val match = uiState.resorts.find { 
                                it.name.equals(newValue, ignoreCase = true) 
                            }
                            if (match != null) {
                                selectedResort = match
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color(0xFF111827)),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (searchText.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.search_placeholder),
                                    color = Color(0xFF6B7280),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        FloatingActionButton(
            onClick = {
                if (
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val userLatLng = LatLng(location.latitude, location.longitude)
                            selectedResort = null

                            cameraPositionState.move(
                                CameraUpdateFactory.newLatLngZoom(userLatLng, 14f)
                            )
                        }
                    }
                } else {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }

                onMyLocationClick()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 440.dp),
            containerColor = Color.White.copy(alpha = 0.72f),
            contentColor = Color.Black,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "My location"
            )
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xB31E293B)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = selectedResort?.name ?: stringResource(R.string.no_resort_selected),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                selectedResort?.let { resort ->
                    ResortDetailRow(label = stringResource(R.string.location_label), value = resort.country ?: "Unknown")
                    ResortDetailRow(label = "Сезон", value = resort.season ?: "Unknown")
                    ResortDetailRow(
                        label = "Писти", 
                        value = "${resort.totalSlopes ?: 0} km (B: ${resort.beginnerSlopes ?: 0}, I: ${resort.intermediateSlopes ?: 0}, D: ${resort.difficultSlopes ?: 0})"
                    )
                    ResortDetailRow(label = "Най-висока точка", value = "${resort.highestPoint ?: 0}m peak")
                    
                    // Display boolean-like fields as Yes/No (Да/Не)
                    ResortDetailRow(
                        label = "Гондола", 
                        value = if (!resort.gondola.isNullOrBlank() && resort.gondola != "0") "Да" else "Не"
                    )
                    ResortDetailRow(
                        label = "Сноупаркове", 
                        value = if (!resort.snowparks.isNullOrBlank() && resort.snowparks != "0") "Да" else "Не"
                    )
                    ResortDetailRow(
                        label = "Подходящо за деца",
                        value = if (!resort.childFriendly.isNullOrBlank() && resort.childFriendly != "0") "Да" else "Не"
                    )
                } ?: run {
                    Text(
                        text = stringResource(R.string.select_marker_hint),
                        color = Color(0xFFE5E7EB),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { onViewWardrobesClick(selectedResort) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xDD2196F3),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = stringResource(R.string.view_wardrobes),
                        fontWeight = FontWeight.Bold
                    )
                }

                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = uiState.error ?: "",
                        color = AppErrorRed,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun ResortDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color(0xFF9CA3AF),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    ShredShareTheme {
        Surface {
            HomeScreen()
        }
    }
}