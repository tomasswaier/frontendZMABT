package com.example.frontendzmabt.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable

@Composable
fun ContinueGuestButton(navController: NavController) {
    Button(
        onClick = {
            println("ContinueAs Guest")
            navController.navigate("main") {
                popUpTo("auth") { inclusive = true }
            }
        }, modifier = Modifier.fillMaxWidth().height(50.dp)

    ) {
        Text(text = "Continue As Guest", fontSize = 16.sp)
    }

}