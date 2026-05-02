package com.example.frontendzmabt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.frontendzmabt.BuildConfig
import com.example.frontendzmabt.data.repository.PostImage
import com.example.frontendzmabt.data.repository.PostRepository
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.ui.screens.PostNavArgs
import com.example.frontendzmabt.ui.screens.ProfileNavArgs
import com.example.frontendzmabt.ui.screens.Screen
import com.example.frontendzmabt.ui.screens.toRoute
import kotlinx.coroutines.launch

private val avatarColors = listOf(
    Color(0xFF00695C),
    Color(0xFF00838F),
    Color(0xFF1565C0),
    Color(0xFF6A1B9A),
    Color(0xFFAD1457),
    Color(0xFF558B2F)
)

@Composable
fun PostList(
    navController: NavController,
    id: Int,
    isUser: Boolean,
    headerContent: (@Composable () -> Unit)? = null
) {
    val context = LocalContext.current
    val repo = remember { PostRepository(context) }
    val pagerFlow = remember { repo.getPostsPager(id, isUser) }
    val lazyPagingItems = pagerFlow.collectAsLazyPagingItems()
    var ownUserId by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        ownUserId = SessionManager(context).getUser().id?.toInt() ?: 0
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (headerContent != null) {
            item("profile_header") { headerContent() }
        }

        item {
            when (lazyPagingItems.loadState.refresh) {
                is LoadState.Loading -> Box(
                    Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
                is LoadState.Error -> Text(
                    "Failed to load posts",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                else -> {}
            }
        }

        items(lazyPagingItems.itemCount) { index ->
            val post = lazyPagingItems[index]
            key(post?.id ?: index) {
                if (post != null) {
                    PostCard(
                        postId = post.id,
                        userId = post.userId,
                        username = post.user?.username,
                        description = post.description,
                        stars = post.stars,
                        navController = navController,
                        isUser = isUser,
                        ownUserId = ownUserId
                    )
                }
            }
        }

        item {
            if (lazyPagingItems.loadState.append is LoadState.Loading) {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
internal fun PostCard(
    postId: Int,
    userId: Int,
    username: String?,
    description: String,
    stars: Int,
    navController: NavController,
    isUser: Boolean,
    ownUserId: Int = 0
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    var images by remember { mutableStateOf<List<PostImage>>(emptyList()) }
    var currentDescription by remember(postId) { mutableStateOf(description) }
    var currentStars by remember(postId) { mutableIntStateOf(stars) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(description) }
    var editRating by remember { mutableIntStateOf(stars) }

    LaunchedEffect(postId) {
        val result = PostRepository(context).get(postId)
        images = result?.postImages ?: emptyList()
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit post", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editText,
                        onValueChange = { editText = it },
                        placeholder = { Text("Post text...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 6,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.outline
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Rating", fontSize = 13.sp, color = colors.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    RatingPicker(rating = editRating, onRatingChanged = { editRating = it })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val text = editText
                        val rating = editRating
                        scope.launch {
                            val ok = PostRepository(context).update(postId, text, rating)
                            if (ok) {
                                currentDescription = text
                                currentStars = rating
                            }
                        }
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = colors.onSurfaceVariant)
                }
            }
        )
    }

    val avatarColor = avatarColors[userId % avatarColors.size]
    val displayName = username ?: "User #$userId"
    val initials = displayName.take(2).uppercase()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { navController.navigate(PostNavArgs(postId, isUser).toRoute()) },
        shape = RoundedCornerShape(16.dp),
        color = colors.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (ownUserId != 0 && userId == ownUserId)
                                navController.navigate(Screen.UserProfileScreen.route)
                            else
                                navController.navigate(ProfileNavArgs(userId).toRoute())
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(avatarColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        displayName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = colors.onBackground
                    )
                }
                if (isUser) {
                    IconButton(onClick = {
                        editText = currentDescription
                        editRating = currentStars
                        showEditDialog = true
                    }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = colors.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = currentDescription,
                fontSize = 14.sp,
                color = colors.onBackground,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            if (images.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    images.take(2).forEach { img ->
                        AsyncImage(
                            model = "${BuildConfig.BACKEND_API_URL}/${img.imagePath}",
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .height(140.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    if (images.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
