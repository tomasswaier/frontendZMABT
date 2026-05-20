package com.example.frontendzmabt.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpOffAlt
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.SocketManager
import com.example.frontendzmabt.data.repository.Comment
import com.example.frontendzmabt.data.repository.CommentRepository
import com.example.frontendzmabt.data.repository.CommentRepositoryInterface
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.jvm.java


@Composable
fun CommentList(
    navController: NavController,id: Int,
    repo: CommentRepositoryInterface,

) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerFlow = remember { repo.getCommentPager(id) }
    val lazyPagingItems = pagerFlow.collectAsLazyPagingItems()
    var isLoggedIn by remember { mutableStateOf(false) }

    val liveComments = remember { mutableStateListOf<Comment>() }
    LaunchedEffect(id) {
        if (SocketManager.isInitialized()) {
            SocketManager.joinPost(id)
        }
    }
    LaunchedEffect(Unit) {
        if(SessionManager(context).getToken()!=null) {
            isLoggedIn=true
            if (SocketManager.isInitialized()) {
                val socket = SocketManager.getSocket()
                socket.on("newComment") { args ->
                    val json = args[0] as JSONObject

                    val gson = Gson()
                    val comment =
                        gson.fromJson(json.getJSONObject("comment").toString(), Comment::class.java)
                    println(comment)

                    // 🔥 update UI state
                    liveComments.add(0, comment)
                }
            }
        }
        println(Unit)
    }


    Column {
        LazyColumn {
            items(liveComments.count()) { index ->
                val comment = liveComments[index]

                //val key = comment?.id ?: index
                Text(comment?.content ?:"nenasiel sa text" )

            }
        }
        LazyColumn {
            //TODO fix funkcionalitu
            items(lazyPagingItems.itemCount) { index ->
                val comment = lazyPagingItems[index];
                var isLiked by remember { mutableStateOf(false) }
                LaunchedEffect(comment) {
                    if (comment!=null && comment.isLiked!=null) {
                        isLiked = comment.isLiked;
                    }
                }
                if (comment!=null ) {
                    Row(
                        modifier= Modifier.testTag("comment_content"),
                    ) {
                        Text(comment.content)
                        if (isLoggedIn) {
                            ChangeStatusBoolean(
                                Icons.Default.ThumbUp, Icons.Default.ThumbUpOffAlt, isLiked,
                                modifier= Modifier.testTag("like_button"),
                                onClick = {
                                    scope.launch {
                                        ChangeStatus(
                                            context,
                                            action = isLiked,
                                            commentId = comment.id
                                        )
                                    }
                                    isLiked = !isLiked;

                                }
                            )
                        }
                    }
                }
            }
        }
        when (lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> Text("Loading...")
            is LoadState.Error -> Text("Error loading comments")
            else -> {}
        }

        DisposableEffect(Unit) {
            if (SocketManager.isInitialized()) {
                val socket = SocketManager.getSocket()

                onDispose {
                    socket.off("newComment")
                }
            }else {
                onDispose {}

            }
        }
    }
}
suspend fun ChangeStatus(context: Context,action:Boolean,commentId:Int): Boolean {

    val repo = CommentRepository(context)
    repo.ChangeLikeStatus(context=context,action=action,commentId=commentId)
    return true

}
