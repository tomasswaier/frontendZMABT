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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.frontendzmabt.BuildConfig
import com.example.frontendzmabt.data.SocketManager
import com.example.frontendzmabt.data.repository.Comment
import com.example.frontendzmabt.data.repository.CommentRepository
import com.example.frontendzmabt.data.repository.Post
import com.example.frontendzmabt.data.repository.PostImage
import com.example.frontendzmabt.data.repository.PostRepository
import com.example.frontendzmabt.ui.components.RatingPicker
import com.example.frontendzmabt.ui.screens.AppScreenTemplate
import com.example.frontendzmabt.ui.screens.ProfileNavArgs
import com.example.frontendzmabt.ui.screens.toRoute
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.json.JSONObject

private val PostBg      = Color(0xFFF0F9FA)
private val PostTeal    = Color(0xFF00535A)
private val PostText    = Color(0xFF0D2C2E)
private val PostGray    = Color(0xFF78909C)
private val PostCard    = Color(0xFFFFFFFF)
private val StarYellow  = Color(0xFFFFA726)

private val avatarColors = listOf(
    Color(0xFF00695C), Color(0xFF00838F), Color(0xFF1565C0),
    Color(0xFF6A1B9A), Color(0xFFAD1457), Color(0xFF558B2F)
)

@Composable
fun PostScreen(navController: NavController, id: Int, isUser: Boolean) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var post by remember { mutableStateOf<Post?>(null) }
    var images by remember { mutableStateOf<List<PostImage>>(emptyList()) }
    var rating by remember { mutableStateOf(0) }
    var commentText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<Comment?>(null) }
    val expandedReplies = remember { mutableStateOf<Map<Int, List<Comment>>>(emptyMap()) }
    val liveComments = remember { mutableStateListOf<Comment>() }

    val commentRepo = remember { CommentRepository(context) }
    val commentFlow = remember { commentRepo.getCommentPager(id) }
    val pagedComments = commentFlow.collectAsLazyPagingItems()

    LaunchedEffect(id) {
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
                    .background(PostCard)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Späť",
                        tint = PostTeal
                    )
                }
                Text(
                    "Post",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PostText
                )
            }
        },
        content = {
            if (post == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PostTeal)
                }
                return@AppScreenTemplate
            }

            val currentPost = post!!

            LazyColumn(
                modifier = Modifier.fillMaxSize().background(PostBg),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── Autor + obsah ──────────────────────────────────────
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = PostCard,
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Autor
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    navController.navigate(
                                        ProfileNavArgs(currentPost.userId).toRoute()
                                    )
                                }
                            ) {
                                val avatarColor = avatarColors[currentPost.userId % avatarColors.size]
                                val displayName = currentPost.user?.username ?: "User #${currentPost.userId}"
                                val initials = displayName.take(2).uppercase()

                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(avatarColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        initials,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        displayName,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        color = PostText
                                    )
                                    Text(
                                        currentPost.createdAt.take(10),
                                        fontSize = 12.sp,
                                        color = PostGray
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // Popis
                            Text(
                                currentPost.description,
                                fontSize = 15.sp,
                                color = PostText,
                                lineHeight = 22.sp
                            )

                            // Hodnotenie
                            Spacer(Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("★", color = StarYellow, fontSize = 18.sp)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "${currentPost.stars}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = PostText
                                )
                            }
                        }
                    }
                }

                // ── Obrázky ───────────────────────────────────────────
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
                                    modifier = Modifier
                                        .width(280.dp)
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(14.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                // ── Rating picker (pre cudzí post) ────────────────────
                if (!isUser) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = PostCard,
                            shadowElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Ohodnoť miesto",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = PostGray
                                )
                                Spacer(Modifier.height(8.dp))
                                RatingPicker(
                                    rating = rating,
                                    onRatingChanged = { newRating ->
                                        rating = newRating
                                        scope.launch {
                                            ChangeRating(context, rating = newRating, postId = id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // ── Komentáre — hlavička + form ───────────────────────
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            "Komentáre",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PostText
                        )
                        Spacer(Modifier.height(10.dp))

                        // Reply banner
                        if (replyingTo != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFE0F4F5), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Odpovedáš na User #${replyingTo!!.userId}",
                                    fontSize = 12.sp,
                                    color = PostTeal,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { replyingTo = null },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Zrušiť odpoveď",
                                        tint = PostGray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = commentText,
                                onValueChange = { commentText = it },
                                placeholder = {
                                    Text(
                                        if (replyingTo != null) "Napíš odpoveď..." else "Napíš komentár...",
                                        color = PostGray
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PostTeal,
                                    unfocusedBorderColor = Color(0xFFB0DDE6)
                                ),
                                maxLines = 3
                            )
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(PostTeal, CircleShape)
                                    .clickable {
                                        if (commentText.isNotBlank()) {
                                            scope.launch {
                                                commentRepo.create(commentText, id, replyingTo?.id)
                                                commentText = ""
                                                replyingTo = null
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Odoslať",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // ── Live komentáre (socket) ───────────────────────────
                items(liveComments.size) { index ->
                    val comment = liveComments[index]
                    CommentItem(
                        comment = comment,
                        postId = id,
                        context = context,
                        replies = expandedReplies.value[comment.id],
                        onReply = { replyingTo = it },
                        onLoadReplies = { commentId ->
                            scope.launch {
                                val replies = commentRepo.getReplies(id, commentId)
                                expandedReplies.value = expandedReplies.value + (commentId to replies)
                            }
                        },
                        onHideReplies = { commentId ->
                            expandedReplies.value = expandedReplies.value - commentId
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // ── Paged komentáre ───────────────────────────────────
                items(pagedComments.itemCount) { index ->
                    val comment = pagedComments[index]
                    if (comment != null) {
                        CommentItem(
                            comment = comment,
                            postId = id,
                            context = context,
                            replies = expandedReplies.value[comment.id],
                            onReply = { replyingTo = it },
                            onLoadReplies = { commentId ->
                                scope.launch {
                                    val replies = commentRepo.getReplies(id, commentId)
                                    expandedReplies.value = expandedReplies.value + (commentId to replies)
                                }
                            },
                            onHideReplies = { commentId ->
                                expandedReplies.value = expandedReplies.value - commentId
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                item {
                    when (pagedComments.loadState.refresh) {
                        is LoadState.Loading -> Box(
                            Modifier.fillMaxWidth().padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = PostTeal) }
                        is LoadState.Error -> Text(
                            "Nepodarilo sa načítať komentáre",
                            color = PostGray,
                            modifier = Modifier.padding(16.dp)
                        )
                        else -> {}
                    }
                }
            }
        }
    )
}

@Composable
private fun CommentItem(
    comment: Comment,
    postId: Int,
    context: Context,
    replies: List<Comment>?,
    onReply: (Comment) -> Unit,
    onLoadReplies: (Int) -> Unit,
    onHideReplies: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    isReply: Boolean = false
) {
    val scope = rememberCoroutineScope()
    var isLiked by remember(comment.id) { mutableStateOf(comment.isLiked ?: false) }
    var likeCount by remember(comment.id) { mutableStateOf(comment.likeCount) }

    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (isReply) Color(0xFFE8F5F6) else PostCard,
            shadowElevation = if (isReply) 0.dp else 1.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    // Avatar s userId
                    val avatarColor = avatarColors[comment.userId % avatarColors.size]
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(avatarColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "#${comment.userId}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "User #${comment.userId}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = PostText
                        )
                        Text(
                            comment.createdAt.take(10),
                            fontSize = 11.sp,
                            color = PostGray
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            comment.content,
                            fontSize = 14.sp,
                            color = PostText,
                            lineHeight = 20.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    CommentRepository(context).ChangeLikeStatus(
                                        context, action = isLiked, commentId = comment.id
                                    )
                                }
                                isLiked = !isLiked
                                likeCount += if (isLiked) 1 else -1
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (isLiked) Color(0xFFE53935) else PostGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text("$likeCount", fontSize = 11.sp, color = PostGray)
                    }
                }

                // Akcie
                Row(modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        "Odpovedať",
                        fontSize = 12.sp,
                        color = PostTeal,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onReply(comment) }
                    )
                    if (!isReply) {
                        Spacer(Modifier.width(16.dp))
                        Text(
                            if (replies == null) "Zobraziť odpovede" else "Skryť odpovede",
                            fontSize = 12.sp,
                            color = PostGray,
                            modifier = Modifier.clickable {
                                if (replies == null) onLoadReplies(comment.id)
                                else onHideReplies(comment.id)
                            }
                        )
                    }
                }
            }
        }

        // Odpovede (indentované)
        if (replies != null && replies.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .padding(start = 20.dp, top = 4.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                replies.forEach { reply ->
                    CommentItem(
                        comment = reply,
                        postId = postId,
                        context = context,
                        replies = null,
                        onReply = onReply,
                        onLoadReplies = {},
                        isReply = true
                    )
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
