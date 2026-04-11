package com.example.frontendzmabt.ui.screens.main

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.frontendzmabt.BuildConfig
import com.example.frontendzmabt.data.API
import com.example.frontendzmabt.ui.screens.AppScreenTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.frontendzmabt.ui.components.PostList

@Composable
fun HomeScreen(navController: NavController) {
    AppScreenTemplate(
        navController= navController,
        header={Text("Header")}
        ,content={Column{
            Text("Body")
            PostList(navController,0,false)
            }
        }
    )
}
