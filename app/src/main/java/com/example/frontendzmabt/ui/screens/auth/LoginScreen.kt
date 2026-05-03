package com.example.frontendzmabt.ui.screens.auth

import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.compose.foundation.background
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
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
import androidx.compose.material3.MaterialTheme
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
import com.example.frontendzmabt.BuildConfig
import com.example.frontendzmabt.data.repository.AuthRepository
import com.example.frontendzmabt.ui.screens.Screen
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
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
                color = colors.surface,
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
                        color = colors.onBackground
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Sign in to continue your journey through the map.",
                        fontSize = 14.sp,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(28.dp))

                    Text(
                        "Username",
                        fontSize = 13.sp,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = colors.primary)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = colors.surfaceVariant,
                            focusedContainerColor = colors.surfaceVariant,
                            unfocusedBorderColor = colors.outline,
                            focusedBorderColor = colors.primary
                        )
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Password", fontSize = 13.sp, color = colors.onBackground, fontWeight = FontWeight.Medium)
                        Text("Forgot?", fontSize = 13.sp, color = colors.primary, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = colors.primary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = colors.onSurfaceVariant
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = colors.surfaceVariant,
                            focusedContainerColor = colors.surfaceVariant,
                            unfocusedBorderColor = colors.outline,
                            focusedBorderColor = colors.primary
                        )
                    )
                    Spacer(Modifier.height(24.dp))

                    LoginButton(username, password, navController)

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = colors.outline)
                        Text(
                            "  OR CONTINUE WITH  ",
                            fontSize = 11.sp,
                            color = colors.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = colors.outline)
                    }

                    Spacer(Modifier.height(16.dp))

                    GoogleSignInButton(navController)

                    Spacer(Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            navController.navigate("main") {
                                popUpTo("auth") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onBackground)
                    ) {
                        Text("Guest", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(horizontalArrangement = Arrangement.Center) {
                        Text("New to the maps? ", fontSize = 14.sp, color = colors.onSurfaceVariant)
                        Text(
                            "Register new account",
                            fontSize = 14.sp,
                            color = colors.primary,
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
                color = colors.onSurfaceVariant.copy(alpha = 0.7f),
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun LoginButton(username: String, password: String, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val primary = MaterialTheme.colorScheme.primary
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
        colors = ButtonDefaults.buttonColors(containerColor = primary)
    ) {
        Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun GoogleSignInButton(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    var isLoading by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = {
            isLoading = true
            scope.launch {
                try {
                    val credentialManager = CredentialManager.create(context)
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                        .setAutoSelectEnabled(false)
                        .build()
                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()
                    val result = credentialManager.getCredential(context = context, request = request)
                    val credential = result.credential
                    val idToken: String? = when {
                        credential is GoogleIdTokenCredential -> credential.idToken
                        credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL ->
                            GoogleIdTokenCredential.createFrom(credential.data).idToken
                        else -> null
                    }
                    if (idToken != null) {
                        android.util.Log.d("GoogleSignIn", "idToken obtained, calling backend")
                        val success = AuthRepository(context).signInWithGoogle(idToken)
                        if (success) {
                            navController.navigate(Screen.HomeScreen.route)
                        } else {
                            Toast.makeText(context, "Google sign-in failed", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        android.util.Log.e("GoogleSignIn", "unexpected credential type: ${credential::class.java.name}")
                        Toast.makeText(context, "Unexpected credential type", Toast.LENGTH_LONG).show()
                    }
                } catch (e: GetCredentialException) {
                    android.util.Log.e("GoogleSignIn", "type=${e.type} msg=${e.message}", e)
                    Toast.makeText(context, "Error: ${e.type} - ${e.message}", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    android.util.Log.e("GoogleSignIn", "unexpected: ${e.message}", e)
                    Toast.makeText(context, "Sign-in error: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    isLoading = false
                }
            }
        },
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onBackground)
    ) {
        Text(
            if (isLoading) "Signing in..." else "Google",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MoveToRegisterButton(text: MutableState<String>, navController: NavController) {
    Button(onClick = { navController.navigate(route = Screen.RegisterScreen.route) }) {
        Text(text = "Don't have an account? Register", fontSize = 16.sp)
    }
}
