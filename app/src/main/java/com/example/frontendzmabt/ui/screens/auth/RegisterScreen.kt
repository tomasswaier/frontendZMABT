package com.example.frontendzmabt.ui.screens.auth

import android.content.Context
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController){
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // UI components go here
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
            value = email,
            onValueChange = { email= it },
            label = { Text("Email") },
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
        OutlinedTextField(
            value = passwordConfirmation,
            onValueChange = { passwordConfirmation = it },
            label = { Text("PasswordConfirmation") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            singleLine = true
        )
        RegisterButton(username,email,password,passwordConfirmation,navController)
        ContinueGuestButton(navController)
        /*Button(
            onClick = {
                // Handle login logic here
                println("Username: $username")
                println("Password: $password")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Submit", fontSize = 16.sp)
        }*/
    }
}
@Composable
fun RegisterButton(username:String,email:String,password:String,passwordConfirmation:String,navController: NavController) { val context: Context = LocalContext.current

    // Coroutine scope tied to the composable
    val scope: CoroutineScope = rememberCoroutineScope()
    Button(onClick = {

        scope.launch {
            val repo = AuthRepository(context)
            val success = repo.register(username,email ,password,passwordConfirmation)

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
fun MoveToLoginButton(text:MutableState<String>,navController: NavController) {
    Button(
        onClick = { navController.navigate(route = Screen.LoginScreen.route) }
    ) {
        Text(text = "have an account? Log IN", fontSize = 16.sp)
    }
}
