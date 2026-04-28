package com.example.frontendzmabt.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontendzmabt.data.repository.AuthRepository
import com.example.frontendzmabt.ui.screens.Screen
import kotlinx.coroutines.launch

private val BgColor     = Color(0xFFB2EBF2)
private val CardColor   = Color(0xFFFFFFFF)
private val PrimaryTeal = Color(0xFF00535A)
private val FieldBg     = Color(0xFFE8F7F9)
private val TextDark    = Color(0xFF0D2C2E)
private val TextGray    = Color(0xFF78909C)
private val BorderColor = Color(0xFFB0DDE6)

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = CardColor,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Welcome Back",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Sign in to continue your journey through the map.",
                        fontSize = 14.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(28.dp))

                    Text(
                        "Username",
                        fontSize = 13.sp,
                        color = TextDark,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryTeal)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = FieldBg,
                            focusedContainerColor = FieldBg,
                            unfocusedBorderColor = BorderColor,
                            focusedBorderColor = PrimaryTeal
                        )
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Password", fontSize = 13.sp, color = TextDark, fontWeight = FontWeight.Medium)
                        Text("Forgot?", fontSize = 13.sp, color = PrimaryTeal, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryTeal)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = TextGray
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = FieldBg,
                            focusedContainerColor = FieldBg,
                            unfocusedBorderColor = BorderColor,
                            focusedBorderColor = PrimaryTeal
                        )
                    )
                    Spacer(Modifier.height(24.dp))

                    LoginButton(username, password, navController)

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
                        Text(
                            "  OR CONTINUE WITH  ",
                            fontSize = 11.sp,
                            color = TextGray,
                            fontWeight = FontWeight.Medium
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
                    }

                    Spacer(Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark)
                    ) {
                        Text("Google", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            navController.navigate("main") {
                                popUpTo("auth") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark)
                    ) {
                        Text("Guest", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(horizontalArrangement = Arrangement.Center) {
                        Text("New to the maps? ", fontSize = 14.sp, color = TextGray)
                        Text(
                            "Register new account",
                            fontSize = 14.sp,
                            color = PrimaryTeal,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.RegisterScreen.route)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "© 2024 SHARE & TRAIL DIGITAL STUDIO",
                fontSize = 10.sp,
                color = TextGray.copy(alpha = 0.7f),
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun LoginButton(username: String, password: String, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Button(
        onClick = {
            scope.launch {
                val repo = AuthRepository(context)
                val success = repo.logIn(username, password)
                if (success) {
                    navController.navigate(Screen.HomeScreen.route)
                } else {
                    Toast.makeText(context, "Login failed", Toast.LENGTH_LONG).show()
                }
            }
        },
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
    ) {
        Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun MoveToRegisterButton(text: MutableState<String>, navController: NavController) {
    Button(onClick = { navController.navigate(route = Screen.RegisterScreen.route) }) {
        Text(text = "Don't have an account? Register", fontSize = 16.sp)
    }
}
