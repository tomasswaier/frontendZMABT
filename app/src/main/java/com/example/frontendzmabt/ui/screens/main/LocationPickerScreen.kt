package com.example.frontendzmabt.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontendzmabt.data.repository.Place
import com.example.frontendzmabt.data.repository.PlaceRepository
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun LocationPickerScreen(navController: NavController) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val defaultPosition = LatLng(48.1486, 17.1077)
    var selectedPosition by remember { mutableStateOf<LatLng?>(null) }
    var selectedPlaceName by remember { mutableStateOf<String?>(null) }
    var places by remember { mutableStateOf<List<Place>>(emptyList()) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 10f)
    }
    val customMarkerState = rememberMarkerState()

    LaunchedEffect(Unit) {
        places = PlaceRepository(context).getAll()
    }

    LaunchedEffect(selectedPosition) {
        selectedPosition?.let { customMarkerState.position = it }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                selectedPosition = latLng
                selectedPlaceName = null
            }
        ) {
            places.forEach { place ->
                Marker(
                    state = MarkerState(position = LatLng(place.latitude, place.longitude)),
                    title = place.aiDescription ?: "Place #${place.id}",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN),
                    onClick = {
                        selectedPosition = LatLng(place.latitude, place.longitude)
                        selectedPlaceName = place.aiDescription ?: "Place #${place.id}"
                        true
                    }
                )
            }

            if (selectedPosition != null && selectedPlaceName == null) {
                Marker(state = customMarkerState, title = "Selected location")
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Text(
                "Tap on the map or select an existing place",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                fontSize = 14.sp,
                color = colors.onBackground
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            selectedPosition?.let {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = selectedPlaceName
                                ?: "%.5f, %.5f".format(it.latitude, it.longitude),
                            fontSize = 13.sp,
                            color = colors.onBackground,
                            fontWeight = if (selectedPlaceName != null) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Button(
                onClick = {
                    selectedPosition?.let { pos ->
                        navController.previousBackStackEntry?.savedStateHandle?.apply {
                            set("latitude", pos.latitude)
                            set("longitude", pos.longitude)
                            set("placeName", selectedPlaceName)
                        }
                    }
                    navController.popBackStack()
                },
                enabled = selectedPosition != null,
                modifier = Modifier.fillMaxWidth(0.75f).height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Text(
                    if (selectedPosition != null) "Confirm location" else "Select a location on the map",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
