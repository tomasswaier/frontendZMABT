package com.example.frontendzmabt.ui.screens.main

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.User
import com.example.frontendzmabt.data.repository.AuthRepository
import com.example.frontendzmabt.data.repository.GetUserResponse
import com.example.frontendzmabt.data.repository.UserRepository
import com.example.frontendzmabt.ui.components.PostList
import com.example.frontendzmabt.ui.screens.AppScreenTemplate
import com.example.frontendzmabt.ui.screens.Screen
import com.example.frontendzmabt.ui.theme.LocalDarkMode
import com.example.frontendzmabt.ui.theme.LocalSetDarkMode
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController, id: Int, isUser: Boolean) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var userResponse by remember { mutableStateOf<GetUserResponse?>(null) }
    var ownUserId by remember { mutableStateOf(0) }
    var userInitials by remember { mutableStateOf("") }

    var isGuest by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val session = SessionManager(context)
        isGuest = !session.isLoggedIn()
        if (isGuest && isUser) return@LaunchedEffect

        val repo = UserRepository(context)
        if (isUser) {
            val localUser = session.getUser()
            ownUserId = localUser.id?.toInt() ?: 0
            userResponse = repo.getOwnProfile()
            userInitials = userResponse?.user?.username?.take(2)?.uppercase() ?: ""
        } else {
            userResponse = repo.get(id)
            userInitials = userResponse?.user?.username?.take(2)?.uppercase() ?: ""
        }
    }

    if (isGuest && isUser) {
        GuestProfileScreen(navController)
        return
    }

    AppScreenTemplate(
        navController = navController,
        header = { HomeHeader(userInitials = userInitials) },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                PostList(
                    navController = navController,
                    id = if (isUser) ownUserId else id,
                    isUser = isUser,
                    headerContent = {
                        ProfileHeaderContent(
                            user = userResponse?.user,
                            isUser = isUser,
                            isFollowing = userResponse?.isFollowing ?: false,
                            navController = navController,
                            onFollowToggle = {
                                scope.launch {
                                    val repo = UserRepository(context)
                                    repo.ChangeFollowStatus(
                                        userResponse?.isFollowing ?: false,
                                        if (isUser) ownUserId else id
                                    )
                                    userResponse = repo.get(if (isUser) ownUserId else id)
                                }
                            },
                            onLogOut = {
                                scope.launch {
                                    val repo = AuthRepository(context)
                                    val success = repo.logout()
                                    if (success) navController.navigate(Screen.LoginScreen.route)
                                    else Toast.makeText(context, "Logout failed", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onBioUpdate = { newBio ->
                                scope.launch {
                                    val repo = UserRepository(context)
                                    val success = repo.updateBio(newBio)
                                    if (success) {
                                        userResponse = repo.getOwnProfile()
                                    } else {
                                        Toast.makeText(context, "Failed to save bio", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                )
            }
        }
    )
}

@Composable
private fun ProfileHeaderContent(
    user: User?,
    isUser: Boolean,
    isFollowing: Boolean,
    navController: NavController,
    onFollowToggle: () -> Unit,
    onLogOut: () -> Unit,
    onBioUpdate: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val isDark = LocalDarkMode.current
    val setDark = LocalSetDarkMode.current
    var showBioDialog by remember { mutableStateOf(false) }
    var bioInput by remember(user?.bio) { mutableStateOf(user?.bio ?: "") }

    if (showBioDialog) {
        AlertDialog(
            onDismissRequest = { showBioDialog = false },
            title = { Text("Edit bio", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = bioInput,
                    onValueChange = { bioInput = it },
                    placeholder = { Text("Write something about yourself...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onBioUpdate(bioInput)
                        showBioDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showBioDialog = false }) { Text("Cancel", color = colors.onSurfaceVariant) }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isUser) {
            Button(
                onClick = { navController.navigate(Screen.PostCreateScreen.route) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Create New Post", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(24.dp))
        }

        Box(
            modifier = Modifier
                .size(96.dp)
                .background(colors.secondary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                user?.username?.take(2)?.uppercase() ?: "??",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp
            )
        }

        Spacer(Modifier.height(14.dp))

        Text(
            user?.username ?: "...",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground
        )

        Spacer(Modifier.height(4.dp))

        Text(
            user?.email ?: "",
            fontSize = 13.sp,
            color = colors.onSurfaceVariant
        )

        Spacer(Modifier.height(12.dp))

        if (isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    if (user?.bio.isNullOrBlank()) "Add a bio..." else user?.bio ?: "",
                    fontSize = 14.sp,
                    color = if (user?.bio.isNullOrBlank()) colors.onSurfaceVariant.copy(alpha = 0.6f) else colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit bio",
                    tint = colors.primary,
                    modifier = Modifier
                        .size(18.dp)
                        .offset(y = 1.dp)
                        .then(androidx.compose.ui.Modifier.clickable { showBioDialog = true })
                )
            }
        } else if (!user?.bio.isNullOrBlank()) {
            Text(
                user?.bio ?: "",
                fontSize = 14.sp,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        if (isUser) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dark mode", fontSize = 14.sp, color = colors.onBackground, fontWeight = FontWeight.Medium)
                Switch(
                    checked = isDark,
                    onCheckedChange = { setDark(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.surface,
                        checkedTrackColor = colors.primary,
                        uncheckedThumbColor = colors.surface,
                        uncheckedTrackColor = colors.onSurfaceVariant
                    )
                )
            }
            Spacer(Modifier.height(12.dp))
        }

        if (isUser) {
            Button(
                onClick = onLogOut,
                modifier = Modifier.height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.error)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Log Out", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        } else {
            OutlinedButton(
                onClick = onFollowToggle,
                modifier = Modifier.height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
            ) {
                Text(
                    if (isFollowing) "Unfollow" else "Follow",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Timeline", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
            HorizontalDivider(modifier = Modifier.weight(1f), color = colors.outlineVariant)
        }

        Spacer(Modifier.height(4.dp))
    }
}


@Composable
private fun GuestProfileScreen(navController: NavController) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text("👤", fontSize = 56.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                "You're browsing as a guest",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Sign in to access your profile, post trails, and interact with the community.",
                fontSize = 14.sp,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = { navController.navigate(Screen.LoginScreen.route) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { navController.navigate(Screen.RegisterScreen.route) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
            ) {
                Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ProfileHeader(id: Int, isUser: Boolean, navController: NavController) {}

@Composable
fun AddPostButton(navController: NavController) {}

@Composable
fun LogOutButton(navController: NavController) {}

suspend fun changeStatus(context: Context, action: Boolean, userId: Int): Boolean {
    UserRepository(context).ChangeFollowStatus(action, userId = userId)
    return true
}
