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
import androidx.compose.material.icons.filled.Login
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
import androidx.paging.LoadState
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.model.User
import com.example.frontendzmabt.data.repository.AuthRepository
import com.example.frontendzmabt.data.repository.GetUserResponse
import com.example.frontendzmabt.data.repository.UserRepository
import com.example.frontendzmabt.ui.components.PostList
import com.example.frontendzmabt.ui.screens.AppScreenTemplate
import com.example.frontendzmabt.ui.screens.Screen
import com.example.frontendzmabt.ui.theme.ThemeManager
import kotlinx.coroutines.launch
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.frontendzmabt.data.repository.PostRepository


@Composable
fun ProfileScreen(navController: NavController, id: Int, isUserIn: Boolean) {
    val context = LocalContext.current
    val repository= remember{PostRepository(context)}
    val scope = rememberCoroutineScope()
    var userResponse by remember { mutableStateOf<GetUserResponse?>(null) }
    //can be changed later

    var isUser by remember { mutableStateOf(isUserIn) }
    LaunchedEffect(Unit) {
        val repo = UserRepository(context)
        if (isUserIn) {
            userResponse = repo.getOwnProfile()
        } else {
            val session = SessionManager(context)
            val localUser = session.getUser()
            val ownUserId = localUser.id?.toInt() ?: 0
            isUser=ownUserId==id
            userResponse = repo.get(id)
        }
        println(Unit)
    }
    AppScreenTemplate(
        navController = navController,
        header = {
            HomeHeader() },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                PostList(
                    navController = navController,
                    id =  id,
                    placeId=0,
                    repository,
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
                                        userId= id
                                    )
                                    userResponse = repo.get(id)
                                }
                            },
                            onLogOut = {
                                scope.launch {
                                    val repo = AuthRepository(context)
                                    val success = repo.logout()
                                    if (success) navController.navigate(Screen.LoginScreen.route)
                                    else Toast.makeText(
                                        context,
                                        "Logout failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onBioUpdate = { newBio ->
                                scope.launch {
                                    val repo = UserRepository(context)
                                    val success = repo.updateBio(newBio)
                                    if (success) {
                                        userResponse = repo.getOwnProfile()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Bio sa nepodarilo uložiť",
                                            Toast.LENGTH_SHORT
                                        ).show()
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
    val userInitials by remember(user) {
        mutableStateOf(user?.username?.take(2)?.uppercase() ?: "")
    }
    val context = LocalContext.current
    var isLoggedIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if(SessionManager(context).getToken()!=null) {
            isLoggedIn=true
        }
    }

    if (showBioDialog && isLoggedIn) {
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
                        focusedBorderColor = MaterialTheme.colorScheme.tertiary,
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) { Text("Uložiť") }
            },
            dismissButton = {
                TextButton(onClick = { showBioDialog = false }) { Text("Zrušiť", color = MaterialTheme.colorScheme.onSecondary) }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isUser) {
            Button(
                onClick = {
                    ThemeManager.isDarkMode = !ThemeManager.isDarkMode
                }
            ) {
                Text(
                    if (ThemeManager.isDarkMode) "Switch to Light Mode"
                    else "Switch to Dark Mode"
                )
            }//CRASHLITICS TEST
            /*Button(onClick = { throw RuntimeException("Test crash") }) {
                Text("Test crash")
            }*/
            if (isLoggedIn) {
                Button(
                    onClick = { navController.navigate(Screen.PostCreateScreen.route) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Create New Post", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // Avatar — len iniciály, bez fajky
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(MaterialTheme.colorScheme.onSecondary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                userInitials,
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
            color = MaterialTheme.colorScheme.onPrimary
        )

        Spacer(Modifier.height(4.dp))

        // Email
        Text(
            user?.email ?: "",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSecondary
        )

        Spacer(Modifier.height(12.dp))

        if (isUser && isLoggedIn) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    if (user?.bio.isNullOrBlank()) "Pridaj bio..." else user?.bio ?: "",
                    fontSize = 14.sp,
                    color = if (user?.bio.isNullOrBlank()) MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Upraviť bio",
                    tint = MaterialTheme.colorScheme.tertiary,
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
                color = MaterialTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        if (isUser ) {
            Button(
                onClick = onLogOut,
                modifier = Modifier.height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(if(isLoggedIn)"Log Out" else "Log In", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        } else if (isLoggedIn) {
            OutlinedButton(
                onClick = onFollowToggle,
                modifier = Modifier.height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text(
                    if (isFollowing) "Unfollow" else "Follow",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }else
        {
            Text("Something went wrong")

        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        Text("Timeline", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFCFD8DC))
        }

        Spacer(Modifier.height(4.dp))
    }
}
