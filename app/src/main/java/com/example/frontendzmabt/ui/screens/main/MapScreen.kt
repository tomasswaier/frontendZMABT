package com.example.frontendzmabt.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.frontendzmabt.data.repository.Place
import com.example.frontendzmabt.data.repository.PlacesRepository
import com.example.frontendzmabt.ui.screens.AppScreenTemplate
import com.example.frontendzmabt.ui.screens.PlaceNavArgs
import com.example.frontendzmabt.ui.screens.toRoute
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun MapScreen(navController: NavController) {
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
    AppScreenTemplate(
        navController= navController,
        header={
            HomeHeader() }
        ,content={
            Column(modifier=Modifier.fillMaxSize()){
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                //cameraPositionState = cameraPositionState
            ) {

                markers?.forEach { place ->
                    val latLng = LatLng(
                        place.latitude.toDouble(),
                        place.longitude.toDouble()
                    )

                    Marker(
                        state = MarkerState(position = latLng),
                        title = "Place #${place.id}",
                        snippet = place.aiDescription,
                        onClick = {
                            navController.navigate(
                                PlaceNavArgs(place.id).toRoute()
                            )
                            true
                        }
                    )
                }
            }
            }

        }
    )
}
