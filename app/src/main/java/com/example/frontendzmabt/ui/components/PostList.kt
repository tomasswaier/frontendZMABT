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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.frontendzmabt.ui.screens.PostNavArgs
import com.example.frontendzmabt.ui.screens.toRoute
import com.google.firebase.messaging.FirebaseMessaging

private val CardBg      = Color(0xFFFFFFFF)
private val CardText    = Color(0xFF37474F)
private val CardName    = Color(0xFF0D2C2E)
private val CardSubtle  = Color(0xFF78909C)

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
    //To be clear. I know how to do this. I choose not ot do it correctly because It's 3:02 AM and I'm watching banana channel
    placeId: Int,
    isUser: Boolean,
    headerContent: (@Composable () -> Unit)? = null
) {
    /*FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
        println("FCM TOKEN: $token")
    }*/
    val context = LocalContext.current
    val repo = remember { PostRepository(context) }
    val pagerFlow = remember { repo.getPostsPager(id,placeId, isUser) }
    val lazyPagingItems = pagerFlow.collectAsLazyPagingItems()
    val cachedPosts by repo.getCachedPosts(id, placeId, isUser)
        .collectAsState(initial = emptyList())

    val isNetworkError = lazyPagingItems.loadState.refresh is LoadState.Error
    val isOffline = isNetworkError && cachedPosts.isNotEmpty()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (headerContent != null) {
            item("profile_header") { headerContent() }
        }

        item {
            when {
                lazyPagingItems.loadState.refresh is LoadState.Loading -> {
                    Box(
                        Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = Color(0xFF00535A)) }
                }
                isOffline -> {
                    Text(
                        "Offline – zobrazujú sa uložené dáta",
                        modifier = Modifier.padding(16.dp),
                        color = CardSubtle
                    )
                }
                isNetworkError -> {
                    Text(
                        "Offline – pre lepšie fungovanie aplikácie sa pripoj na internet",
                        modifier = Modifier.padding(16.dp),
                        color = CardSubtle
                    )
                }
                else -> {}
            }
        }


        if (isOffline) {
            items(cachedPosts.size) { index ->
                val post = cachedPosts[index]
                PostCard(
                    postId = post.id,
                    userId = post.userId,
                    username = null,
                    description = post.description,
                    navController = navController,
                    isUser = isUser
                )
            }
        } else {
            items(lazyPagingItems.itemCount) { index ->
                val post = lazyPagingItems[index]
                key(post?.id ?: index) {
                    if (post != null) {
                        PostCard(
                            postId = post.id,
                            userId = post.userId,
                            username = post.user?.username,
                            description = post.description,
                            navController = navController,
                            isUser = isUser
                        )
                    }
                }
            }

            item {
                if (lazyPagingItems.loadState.append is LoadState.Loading) {
                    Box(
                        Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF00535A),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PostCard(
    postId: Int,
    userId: Int,
    username: String?,
    description: String,
    navController: NavController,
    isUser: Boolean
) {
    val context = LocalContext.current
    var images by remember { mutableStateOf<List<PostImage>>(emptyList()) }

    LaunchedEffect(postId) {
        val result = PostRepository(context).get(postId)
        images = result?.postImages ?: emptyList()
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
        color = CardBg,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Avatar + meno
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    color = CardName
                )
            }

            Spacer(Modifier.height(10.dp))

            // Text postu — max 4 riadky
            Text(
                text = description,
                fontSize = 14.sp,
                color = CardText,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            // Obrázky — max 2
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
