package com.example.frontendzmabt.ui.screens.main

import android.content.Context
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.frontendzmabt.BuildConfig
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.SocketManager
import com.example.frontendzmabt.data.repository.Comment
import com.example.frontendzmabt.data.repository.CommentRepository
import com.example.frontendzmabt.data.repository.Post
import com.example.frontendzmabt.data.repository.PostImage
import com.example.frontendzmabt.data.repository.PostRepository
import com.example.frontendzmabt.ui.components.RatingPicker
import com.example.frontendzmabt.ui.screens.AppScreenTemplate
import com.example.frontendzmabt.ui.screens.ProfileNavArgs
import com.example.frontendzmabt.ui.screens.Screen
import com.example.frontendzmabt.ui.screens.toRoute
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.json.JSONObject

private val avatarColors = listOf(
    Color(0xFF00695C), Color(0xFF00838F), Color(0xFF1565C0),
    Color(0xFF6A1B9A), Color(0xFFAD1457), Color(0xFF558B2F)
)

@Composable
fun PostScreen(navController: NavController, id: Int, isUser: Boolean) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme

    var post by remember { mutableStateOf<Post?>(null) }
    var images by remember { mutableStateOf<List<PostImage>>(emptyList()) }
    var rating by remember { mutableStateOf(0) }
    var commentText by remember { mutableStateOf("") }
    var ownUserId by remember { mutableStateOf(0) }
    var isGuest by remember { mutableStateOf(false) }
    val liveComments = remember { mutableStateListOf<Comment>() }

    val commentRepo = remember { CommentRepository(context) }
    val commentFlow = remember { commentRepo.getCommentPager(id) }
    val pagedComments = commentFlow.collectAsLazyPagingItems()

    LaunchedEffect(id) {
        isGuest = !SessionManager(context).isLoggedIn()
        ownUserId = SessionManager(context).getUser().id?.toInt() ?: 0
        val repo = PostRepository(context)
        val response = repo.get(id)
        post = response?.post
        images = response?.postImages ?: emptyList()

        SocketManager.joinPost(id)
        val socket = SocketManager.getSocket()
        socket.on("newComment") { args ->
            val json = args[0] as JSONObject
            val comment = Gson().fromJson(
                json.getJSONObject("comment").toString(), Comment::class.java
            )
            liveComments.add(0, comment)
        }
    }

    DisposableEffect(id) {
        onDispose { SocketManager.getSocket().off("newComment") }
    }

    AppScreenTemplate(
        navController = navController,
        header = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.primary)
                }
                Text("Post", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.onBackground)
            }
        },
        content = {
            if (post == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colors.primary)
                }
                return@AppScreenTemplate
            }

            val currentPost = post!!

            LazyColumn(
                modifier = Modifier.fillMaxSize().background(colors.background),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = colors.surface,
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    if (ownUserId != 0 && currentPost.userId == ownUserId)
                                        navController.navigate(Screen.UserProfileScreen.route)
                                    else
                                        navController.navigate(ProfileNavArgs(currentPost.userId).toRoute())
                                }
                            ) {
                                val avatarColor = avatarColors[currentPost.userId % avatarColors.size]
                                val displayName = currentPost.user?.username ?: "User #${currentPost.userId}"
                                Box(
                                    modifier = Modifier.size(44.dp).background(avatarColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        displayName.take(2).uppercase(),
                                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(displayName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = colors.onBackground)
                                    Text(currentPost.createdAt.take(10), fontSize = 12.sp, color = colors.onSurfaceVariant)
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(currentPost.description, fontSize = 15.sp, color = colors.onBackground, lineHeight = 22.sp)
                            Spacer(Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("★", color = colors.tertiary, fontSize = 18.sp)
                                Spacer(Modifier.width(4.dp))
                                Text("${currentPost.stars}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colors.onBackground)
                            }
                        }
                    }
                }

                if (images.isNotEmpty()) {
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(images.size) { index ->
                                AsyncImage(
                                    model = "${BuildConfig.BACKEND_API_URL}/${images[index].imagePath}",
                                    contentDescription = null,
                                    modifier = Modifier.width(280.dp).height(200.dp).clip(RoundedCornerShape(14.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                if (!isUser && !isGuest) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = colors.surface,
                            shadowElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Rate this place", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = colors.onSurfaceVariant)
                                Spacer(Modifier.height(8.dp))
                                RatingPicker(
                                    rating = rating,
                                    onRatingChanged = { newRating ->
                                        rating = newRating
                                        scope.launch { ChangeRating(context, rating = newRating, postId = id) }
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text("Comments", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.onBackground)
                        Spacer(Modifier.height(10.dp))
                        if (isGuest) {
                            Text(
                                "Sign in to leave a comment.",
                                fontSize = 13.sp,
                                color = colors.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = commentText,
                                    onValueChange = { commentText = it },
                                    placeholder = { Text("Write a comment...", color = colors.onSurfaceVariant) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = colors.primary,
                                        unfocusedBorderColor = colors.outline
                                    ),
                                    maxLines = 3
                                )
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(colors.primary, CircleShape)
                                        .clickable {
                                            if (commentText.isNotBlank()) {
                                                scope.launch {
                                                    commentRepo.create(commentText, id)
                                                    commentText = ""
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }

                items(liveComments.size) { index ->
                    CommentItem(
                        comment = liveComments[index],
                        context = context,
                        navController = navController,
                        ownUserId = ownUserId,
                        isGuest = isGuest,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                items(pagedComments.itemCount) { index ->
                    val comment = pagedComments[index]
                    if (comment != null) {
                        CommentItem(
                            comment = comment,
                            context = context,
                            navController = navController,
                            isGuest = isGuest,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                item {
                    when (pagedComments.loadState.refresh) {
                        is LoadState.Loading -> Box(
                            Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
                        is LoadState.Error -> Text("Failed to load comments", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(16.dp))
                        else -> {}
                    }
                }
            }
        }
    )
}

@Composable
private fun CommentItem(comment: Comment, context: Context, navController: NavController, ownUserId: Int = 0, isGuest: Boolean = false, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    var isLiked by remember(comment.id) { mutableStateOf(comment.isLiked ?: false) }
    var likeCount by remember(comment.id) { mutableStateOf(comment.likeCount) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.surface,
        shadowElevation = 1.dp
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            val avatarColor = avatarColors[comment.userId % avatarColors.size]
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        if (ownUserId != 0 && comment.userId == ownUserId)
                            navController.navigate(Screen.UserProfileScreen.route)
                        else
                            navController.navigate(ProfileNavArgs(comment.userId).toRoute())
                    },
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier.size(34.dp).background(avatarColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("#${comment.userId}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("User #${comment.userId}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = colors.onBackground)
                    Text(comment.createdAt.take(10), fontSize = 11.sp, color = colors.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text(comment.content, fontSize = 14.sp, color = colors.onBackground, lineHeight = 20.sp)
                }
            }
            if (!isGuest) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                CommentRepository(context).ChangeLikeStatus(context, action = isLiked, commentId = comment.id)
                            }
                            isLiked = !isLiked
                            likeCount += if (isLiked) 1 else -1
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) colors.error else colors.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text("$likeCount", fontSize = 11.sp, color = colors.onSurfaceVariant)
                }
            }
        }
    }
}

suspend fun ChangeRating(context: Context, rating: Int, postId: Int): Boolean {
    return PostRepository(context).rate(rating, postId)
}

@Composable
fun EditPostButton(navController: NavController) {}
