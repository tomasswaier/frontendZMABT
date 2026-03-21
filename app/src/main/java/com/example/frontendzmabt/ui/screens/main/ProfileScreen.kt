package com.example.frontendzmabt.ui.screens.main

import android.R.*
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButtonDefaults.Icon
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
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontendzmabt.BuildConfig
import com.example.frontendzmabt.R
import com.example.frontendzmabt.data.API
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.User
import com.example.frontendzmabt.ui.screens.AppScreenTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text


@Composable
fun ProfileScreen(navController: NavController, id: Number,isUser:Boolean) {
    AppScreenTemplate(
        navController=navController,header= { ProfileHeader(id,isUser) },
    content={Column(modifier = Modifier.background(
        color =Color.Gray
    ).fillMaxSize()

    ) {
        Text("Here will be displayed all the posts that the user has made in the past. Preferably sorted by newest")
        AddPostButton()
    }}
    )
}


@Composable
fun ProfileHeader(id: Number,isUser:Boolean) {
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
            Text("Profile of: $username")
            Icon(painter= painterResource(R.drawable.ic_account_box),
                contentDescription = "",
                tint = Color.Green)

    }
}

@Composable
fun AddPostButton() {
    val context = LocalContext.current

    // Coroutine scope tied to the composable
    val scope = rememberCoroutineScope()

    Button(onClick = {
        scope.launch(Dispatchers.IO) {
            try {
                val apiUrl = BuildConfig.BACKEND_API_URL
                val token = "testValue"
                val requestBody = mapOf(
                    "test" to "fungujem?",
                    "meow" to "meow"
                )

                // Make network request on IO thread
                val result = API.callApi(apiUrl, token, "POST", requestBody)

                // Switch to Main thread to show a Toast or update UI
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Result: $result", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }) {
        Text("CLICK ME")
    }
}

