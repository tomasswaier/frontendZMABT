package com.example.frontendzmabt.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

private val PickerTeal = Color(0xFF00535A)

@Composable
fun LocationPickerScreen(navController: NavController) {
    val defaultPosition = LatLng(48.1486, 17.1077) // Bratislava
    var selectedPosition by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 10f)
    }
    val markerState = rememberMarkerState()
    LaunchedEffect(selectedPosition) {
        selectedPosition?.let { markerState.position = it }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng -> selectedPosition = latLng }
        ) {
            if (selectedPosition != null) {
                Marker(state = markerState, title = "Vybraná poloha")
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Text(
                "Klepni na mapu pre výber miesta",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                fontSize = 14.sp,
                color = Color(0xFF0D2C2E)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            selectedPosition?.let { pos ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        "%.5f, %.5f".format(pos.latitude, pos.longitude),
                        fontSize = 12.sp,
                        color = Color(0xFF0D2C2E),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Button(
                onClick = {
                    selectedPosition?.let { pos ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("latitude", pos.latitude)
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("longitude", pos.longitude)
                    }
                    navController.popBackStack()
                },
                enabled = selectedPosition != null,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PickerTeal)
            ) {
                Text(
                    if (selectedPosition != null) "Potvrdiť polohu" else "Vyber miesto na mape",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
