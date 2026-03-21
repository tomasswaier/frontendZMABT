package com.example.frontendzmabt.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.frontendzmabt.ui.screens.AppScreenTemplate

@Composable
fun MapScreen(navController: NavController) {
    AppScreenTemplate(
        navController= navController,
        header={Text("Header")}
        ,content={Column{
            Text("Map goes here. TBD.")
            Text("Placeholder rn")

        }
        }
    )
}
