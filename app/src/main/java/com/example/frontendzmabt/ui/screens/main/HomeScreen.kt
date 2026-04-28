package com.example.frontendzmabt.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.ui.components.PostList
import com.example.frontendzmabt.ui.screens.AppScreenTemplate

private val HomeTeal = Color(0xFF00535A)
private val HomeBg   = Color(0xFFF0F9FA)

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    var userInitials by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val session = SessionManager(context)
        val user = session.getUser()
        userInitials = user?.username?.take(2)?.uppercase() ?: "??"
    }

    AppScreenTemplate(
        navController = navController,
        header = { HomeHeader(userInitials = userInitials) },
        content = {
            Box(modifier = Modifier.fillMaxSize().background(HomeBg)) {
                PostList(navController, 0, false)
            }
        }
    )
}

@Composable
fun HomeHeader(userInitials: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Share & Trail",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = HomeTeal
        )

        Box(
            modifier = Modifier
                .size(36.dp)
                .background(HomeTeal, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(userInitials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}
