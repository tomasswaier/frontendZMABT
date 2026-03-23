package com.example.frontendzmabt.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.frontendzmabt.data.repository.PostRepository

@Composable
fun PostList(id: Int, isUser: Boolean) {
    //if isUser == true then display posts of the users who's logged in
    //if id is set then display posts of user specified in id
    //if both false then display posts of all users

    val context = LocalContext.current
    val repo = remember { PostRepository(context) }

    val pagerFlow = remember { repo.getPostsPager(id,isUser) }
    val lazyPagingItems = pagerFlow.collectAsLazyPagingItems()

    Column {

        LazyColumn {
            items(lazyPagingItems.itemCount) { index ->
                val post = lazyPagingItems[index]

                val key = post?.id ?: index
                key(key) {
                    if (post != null) {
                        Text(post.description)
                        Text(post.createdAt)
                    } else {
                        Text("Loading...")
                    }
                }
            }
        }
        when (lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> Text("Loading...")
            is LoadState.Error -> Text("Error loading posts")
            else -> {}
        }
    }
}
