package com.example.shredshare.ui.theme.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun PickLocationScreen(
    onLocationPicked: (LatLng) -> Unit,
    initialLocation: LatLng = LatLng(42.7339, 25.4858) // Bulgaria center
) {
    var pickedLocation by remember { mutableStateOf(initialLocation) }
    val markerState = rememberMarkerState(position = pickedLocation)
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 7f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                pickedLocation = latLng
                markerState.position = latLng
            }
        ) {
            Marker(
                state = markerState,
                title = "Избрано местоположение"
            )
        }

        FloatingActionButton(
            onClick = { onLocationPicked(pickedLocation) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF2196F3),
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Check, contentDescription = "Потвърди")
        }
    }
}