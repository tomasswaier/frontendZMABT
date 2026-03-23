package com.example.frontendzmabt.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontendzmabt.R
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.User
import com.example.frontendzmabt.ui.screens.AppScreenTemplate
import com.example.frontendzmabt.ui.screens.Screen
import com.example.frontendzmabt.ui.components.PostList


@Composable
fun ProfileScreen(navController: NavController, id: Int,isUser:Boolean) {
    AppScreenTemplate(
        navController=navController,header= { ProfileHeader(id,isUser,navController) },
    content={Column(modifier = Modifier.background(
        color =Color.Gray
    ).fillMaxSize()

    ) {
       PostList(id,isUser)

    }}
    )
}


@Composable
fun ProfileHeader(id: Number,isUser:Boolean,navController: NavController) {
    var username: String="";
    if (isUser){
        val context = LocalContext.current
        val session = SessionManager(context)

        var user by remember { mutableStateOf<User?>(null) }
        LaunchedEffect(Unit) {
            user = session.getUser()
        }
        if (user != null) {
            username= user!!.username.toString()
        }
    }
    return Row(modifier = Modifier.height(80.dp)) {
        Icon(painter= painterResource(R.drawable.ic_account_box),
                contentDescription = "",
                tint = Color.Green)
        Text("Profile of: $username")
        AddPostButton(navController )

    }
}
@Composable
fun AddPostButton(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Button(onClick = {
            navController.navigate(Screen.PostScreen.route)
    }) {
        Text("Add post")
    }
}

