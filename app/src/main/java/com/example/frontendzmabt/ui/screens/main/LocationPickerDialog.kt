package com.example.frontendzmabt.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontendzmabt.data.repository.Place
import com.example.frontendzmabt.data.repository.PlacesRepository
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


private val PickerTeal = Color(0xFF00535A)

@Composable
fun LocationPickerDialog(
    initialLatitude: Double,
    initialLongitude: Double,
    onDismiss: () -> Unit,
    onLocationSelected: (Double, Double) -> Unit
){
    val defaultPosition = LatLng(48.1486, 17.1077)

    var selectedPosition by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(initialLatitude, initialLongitude),
            15f
        )
    }
    var markers by remember { mutableStateOf<List<Place>?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val repo = PlacesRepository(context)
        markers= repo.get(
            context = context,
        )
        println("HERE------------------------------------============================================")
        println(markers)
    }

    val markerState = rememberMarkerState()

    LaunchedEffect(selectedPosition) {
        selectedPosition?.let { markerState.position = it }
    }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(500.dp)
        ) {
            Box(Modifier.fillMaxSize()) {

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { selectedPosition = it }
                ) {

                    markers?.forEach { place ->
                        val latLng = LatLng(
                            place.latitude.toDouble(),
                            place.longitude.toDouble()
                        )

                        Marker(
                            state = MarkerState(position = latLng),
                            title = place.aiDescription,
                            onClick = {
                                selectedPosition = latLng
                                true
                            }
                        )
                    }

                    // 🔹 Selected marker (user-picked)
                    selectedPosition?.let {
                        Marker(
                            state = markerState,
                            title = "Selected location"
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    selectedPosition?.let { pos ->
                        Text(
                            "%.5f, %.5f".format(pos.latitude, pos.longitude),
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            selectedPosition?.let {
                                onLocationSelected(it.latitude, it.longitude)
                            }
                            onDismiss()
                        },
                        enabled = selectedPosition != null
                    ) {
                        Text("Confirm location")
                    }
                }
            }
        }
    }
}