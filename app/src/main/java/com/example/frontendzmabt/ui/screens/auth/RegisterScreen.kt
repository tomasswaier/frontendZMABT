package com.example.frontendzmabt.ui.screens.auth

import android.content.Context
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = colors.surface,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(colors.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Explore,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Trail & Share",
                        fontSize = 13.sp,
                        color = colors.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Begin your journey",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Create an account to start pinning your discoveries.",
                        fontSize = 14.sp,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))

                    AuthField(
                        label = "USERNAME",
                        value = username,
                        onValueChange = { username = it },
                        placeholder = "alex_explorer",
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = colors.primary)
                        }
                    )
                    Spacer(Modifier.height(14.dp))

                    AuthField(
                        label = "EMAIL",
                        value = email,
                        onValueChange = { email = it },
                        leadingIcon = {
                            Icon(Icons.Default.Share, contentDescription = null, tint = colors.primary)
                        }
                    )
                    Spacer(Modifier.height(14.dp))

                    AuthField(
                        label = "PASSWORD",
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "••••••••",
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = colors.primary)
                        },
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onTogglePassword = { passwordVisible = !passwordVisible }
                    )
                    Spacer(Modifier.height(14.dp))

                    AuthField(
                        label = "CONFIRM PASSWORD",
                        value = passwordConfirmation,
                        onValueChange = { passwordConfirmation = it },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = colors.primary)
                        },
                        isPassword = true,
                        passwordVisible = confirmVisible,
                        onTogglePassword = { confirmVisible = !confirmVisible }
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it },
                            colors = CheckboxDefaults.colors(checkedColor = colors.primary)
                        )
                        Text(
                            "I agree to the Terms of Service and acknowledge the Privacy Policy.",
                            fontSize = 12.sp,
                            color = colors.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    Spacer(Modifier.height(20.dp))

                    RegisterButton(username, email, password, passwordConfirmation, navController)

                    Spacer(Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Already a member? ", fontSize = 14.sp, color = colors.onSurfaceVariant)
                        Text(
                            "Sign In",
                            fontSize = 14.sp,
                            color = colors.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.LoginScreen.route)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AuthField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    leadingIcon: @Composable () -> Unit,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    Text(
        label,
        fontSize = 11.sp,
        color = colors.onBackground,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.8.sp,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = leadingIcon,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
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
}

@Composable
fun RegisterButton(
    username: String,
    email: String,
    password: String,
    passwordConfirmation: String,
    navController: NavController
) {
    val context: Context = LocalContext.current
    val scope: CoroutineScope = rememberCoroutineScope()
    val primary = MaterialTheme.colorScheme.primary
    Button(
        onClick = {
            scope.launch {
                val repo = AuthRepository(context)
                val success = repo.register(username, email, password, passwordConfirmation)
                if (success) {
                    navController.navigate(Screen.HomeScreen.route)
                } else {
                    Toast.makeText(context, "Registration failed", Toast.LENGTH_LONG).show()
                }
            }
        },
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primary)
    ) {
        Text("Create Account →", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun MoveToLoginButton(text: MutableState<String>, navController: NavController) {
    Button(onClick = { navController.navigate(route = Screen.LoginScreen.route) }) {
        Text(text = "have an account? Log IN", fontSize = 16.sp)
    }
}
