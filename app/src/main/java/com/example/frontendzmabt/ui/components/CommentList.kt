package com.example.frontendzmabt.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.frontendzmabt.data.SocketManager
import com.example.frontendzmabt.data.repository.Comment
import com.example.frontendzmabt.data.repository.CommentRepository
import com.google.gson.Gson
import org.json.JSONObject
import kotlin.jvm.java


@Composable
fun CommentList(navController: NavController,id: Int) {

    val context = LocalContext.current
    val repo = remember { CommentRepository(context) }

    val pagerFlow = remember { repo.getCommentPager(id) }
    val lazyPagingItems = pagerFlow.collectAsLazyPagingItems()
    SocketManager.joinPost(id)
    val liveComments = remember { mutableStateListOf<Comment>() }
    LaunchedEffect(id) {
        SocketManager.joinPost(id)
    }

    LaunchedEffect(id) {

        val socket= SocketManager.getSocket()
        socket.on("newComment") { args ->
            val json = args[0] as JSONObject

            val gson = Gson()
            val comment = gson.fromJson(json.getJSONObject("comment").toString(), Comment::class.java)
            println(comment)

            // 🔥 update UI state
            liveComments.add(0, comment)
        }
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
            items(lazyPagingItems.itemCount) { index ->
                val comment = lazyPagingItems[index]

                //val key = comment?.id ?: index
                Text(comment?.content ?:"nenasiel sa text" )
            }
        }
        when (lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> Text("Loading...")
            is LoadState.Error -> Text("Error loading comments")
            else -> {}
        }

        DisposableEffect(Unit) {
            val socket = SocketManager.getSocket()

            onDispose {
                socket.off("newComment")
            }
        }
    }
}
