package com.example.frontendzmabt.ui.screens.main
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
import com.example.frontendzmabt.data.model.User
import com.example.frontendzmabt.data.repository.AuthRepository
import com.example.frontendzmabt.data.repository.GetUserResponse
import com.example.frontendzmabt.data.repository.Place
import com.example.frontendzmabt.data.repository.PlacesRepository
import com.example.frontendzmabt.data.repository.PostRepository
import com.example.frontendzmabt.data.repository.UserRepository
import com.example.frontendzmabt.ui.components.PostList
import com.example.frontendzmabt.ui.screens.AppScreenTemplate
import com.example.frontendzmabt.ui.screens.Screen
import com.example.frontendzmabt.ui.theme.ThemeManager
import kotlinx.coroutines.launch


@Composable
fun PlaceScreen(navController: NavController, id: Int) {
    val context = LocalContext.current
    var place by remember { mutableStateOf<Place?>(null) }

    LaunchedEffect(Unit) {
        val repo = PlacesRepository(context)
        place = repo.getInfo(context, id)
        println(place!!.aiDescription)
        println("HUH")
    }

    AppScreenTemplate(
        navController = navController,
        header = { HomeHeader() },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                PostList(
                    navController = navController,
                    id = 0,
                    placeId = id,
                    isUser = false,
                    headerContent = {
                        Text(place?.aiDescription ?: "Loading...")
                    }
                )
            }
        }
    )
}
@Composable
private fun PlaceHeaderContent(
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
            }
            Button(
                onClick = { navController.navigate(Screen.PostCreateScreen.route) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
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

        // Bio sekcia
        if (isUser) {
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

        if (isUser) {
            Button(
                onClick = onLogOut,
                modifier = Modifier.height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
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
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.tertiary)
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
            Text("Timeline", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFCFD8DC))
        }

        Spacer(Modifier.height(4.dp))
    }
}
