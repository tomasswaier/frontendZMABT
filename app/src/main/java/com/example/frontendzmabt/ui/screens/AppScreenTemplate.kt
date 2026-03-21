package com.example.frontendzmabt.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AppScreenTemplate(
    navController: NavController,
    header: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = { header() },
        bottomBar = { NavigationBar(navController) }
    ) { padding ->

        Box(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}
@Composable
fun NavigationBar(navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth()) {

        AppNavigation.entries.forEach {
            IconButton(
                onClick ={
                    navController.navigate(it.route) {
                        launchSingleTop = true
                    }
                         }
            ){
                Icon(
                    painter =painterResource( it.icon),
                    contentDescription = it.label
                )
            }
        }
    }
}

