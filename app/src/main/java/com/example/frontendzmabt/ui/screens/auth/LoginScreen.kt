package com.example.frontendzmabt.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontendzmabt.data.repository.AuthRepository
import com.example.frontendzmabt.ui.components.ContinueGuestButton
import com.example.frontendzmabt.ui.screens.Screen
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController){
    //var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val text: MutableState<String> = remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username= it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            singleLine = true
        )
        LoginButton(username,password,navController)
        ContinueGuestButton(navController)
        MoveToRegisterButton(text,navController);
    }
}

@Composable
fun LoginButton(username:String,password:String,navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Button(onClick = {

        scope.launch {
            val repo = AuthRepository(context)
            val success = repo.logIn(username, password)

            if (success) {
                navController.navigate(Screen.HomeScreen.route)
            } else {
                Toast.makeText(context, "Login failed", Toast.LENGTH_LONG).show()
            }
        }
    },modifier = Modifier.fillMaxWidth().height(50.dp)

    ) {
        Text(text = "Submit", fontSize = 16.sp)

    }
}
@Composable
fun MoveToRegisterButton(text:MutableState<String>,navController: NavController) {
    Button(
        onClick = { navController.navigate(route = Screen.RegisterScreen.route) }
    ) {
        Text(text = "Don't have an account? Register", fontSize = 16.sp)
    }
}
