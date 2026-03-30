package com.example.frontendzmabt.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.frontendzmabt.data.repository.CommentRepository

@Composable
fun CommentList(navController: NavController,id: Int) {

    val context = LocalContext.current
    val repo = remember { CommentRepository(context) }

    val pagerFlow = remember { repo.getCommentPager(id) }
    val lazyPagingItems = pagerFlow.collectAsLazyPagingItems()

    Column {
        LazyColumn {
            items(lazyPagingItems.itemCount) { index ->
                val comment = lazyPagingItems[index]

                val key = comment?.id ?: index
                Text(comment?.content ?:"nenasiel sa text" )
            }
        }
        when (lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> Text("Loading...")
            is LoadState.Error -> Text("Error loading comments")
            else -> {}
        }
    }
}
