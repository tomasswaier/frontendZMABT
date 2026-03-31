package com.example.frontendzmabt.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.navigation.NavController
import com.example.frontendzmabt.ui.screens.AppScreenTemplate
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker

@Composable
fun MapScreen(navController: NavController) {
    AppScreenTemplate(
        navController= navController,
        header={Text("Header")}
        ,content={
            Column(modifier=Modifier.fillMaxSize()){
            Text("Map goes here. TBD.")
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                //cameraPositionState = cameraPositionState
            ) {
                Marker(
                    //state = singaporeMarkerState,
                    title = "YourPlace",
                    draggable = true,
                    snippet = ""
                )

            }
            }

        }
    )
}
