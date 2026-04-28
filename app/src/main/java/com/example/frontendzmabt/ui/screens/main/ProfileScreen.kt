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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import kotlinx.coroutines.launch

private val ProfileBg     = Color(0xFFF0F9FA)
private val ProfileTeal   = Color(0xFF00535A)
private val ProfileAvatar = Color(0xFF26C6DA)
private val ProfileRed    = Color(0xFFE53935)
private val ProfileText   = Color(0xFF0D2C2E)
private val ProfileGray   = Color(0xFF78909C)

@Composable
fun ProfileScreen(navController: NavController, id: Int, isUser: Boolean) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var userResponse by remember { mutableStateOf<GetUserResponse?>(null) }
    var ownUserId by remember { mutableStateOf(0) }
    var userInitials by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val repo = UserRepository(context)
        if (isUser) {
            val session = SessionManager(context)
            val localUser = session.getUser()
            ownUserId = localUser.id?.toInt() ?: 0
            userResponse = repo.getOwnProfile()
            userInitials = userResponse?.user?.username?.take(2)?.uppercase() ?: ""
        } else {
            userResponse = repo.get(id)
            userInitials = userResponse?.user?.username?.take(2)?.uppercase() ?: ""
        }
    }

    AppScreenTemplate(
        navController = navController,
        header = { HomeHeader(userInitials = userInitials) },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ProfileBg)
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
                                        Toast.makeText(context, "Bio sa nepodarilo uložiť", Toast.LENGTH_SHORT).show()
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
    var showBioDialog by remember { mutableStateOf(false) }
    var bioInput by remember(user?.bio) { mutableStateOf(user?.bio ?: "") }

    if (showBioDialog) {
        AlertDialog(
            onDismissRequest = { showBioDialog = false },
            title = { Text("Upraviť bio", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = bioInput,
                    onValueChange = { bioInput = it },
                    placeholder = { Text("Napíš niečo o sebe...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ProfileTeal,
                        unfocusedBorderColor = Color(0xFFB0DDE6)
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onBioUpdate(bioInput)
                        showBioDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ProfileTeal)
                ) { Text("Uložiť") }
            },
            dismissButton = {
                TextButton(onClick = { showBioDialog = false }) { Text("Zrušiť", color = ProfileGray) }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ProfileBg)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isUser) {
            Button(
                onClick = { navController.navigate(Screen.PostCreateScreen.route) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ProfileTeal)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Create New Post", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(24.dp))
        }

        // Avatar — len iniciály, bez fajky
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(ProfileAvatar, CircleShape),
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

        // Meno (username)
        Text(
            user?.username ?: "...",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = ProfileText
        )

        Spacer(Modifier.height(4.dp))

        // Email
        Text(
            user?.email ?: "",
            fontSize = 13.sp,
            color = ProfileGray
        )

        Spacer(Modifier.height(12.dp))

        // Bio sekcia
        if (isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    if (user?.bio.isNullOrBlank()) "Pridaj bio..." else user?.bio ?: "",
                    fontSize = 14.sp,
                    color = if (user?.bio.isNullOrBlank()) ProfileGray.copy(alpha = 0.6f) else ProfileGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Upraviť bio",
                    tint = ProfileTeal,
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
                color = ProfileGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        if (isUser) {
            Button(
                onClick = onLogOut,
                modifier = Modifier.height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ProfileRed)
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
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ProfileTeal)
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
            Text("Timeline", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ProfileText)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFCFD8DC))
        }

        Spacer(Modifier.height(4.dp))
    }
}

// Zachované pre spätnú kompatibilitu
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
