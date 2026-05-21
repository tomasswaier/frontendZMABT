package com.example.frontendzmabt.ui.screens.main


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.frontendzmabt.BuildConfig
import com.example.frontendzmabt.R
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.model.Post
import com.example.frontendzmabt.data.repository.CommentRepository
import com.example.frontendzmabt.data.repository.CommentRepositoryInterface
import com.example.frontendzmabt.data.repository.GetPostResponse
import com.example.frontendzmabt.data.repository.PostImage
import com.example.frontendzmabt.data.repository.PostRepository
import com.example.frontendzmabt.ui.components.CommentList
import com.example.frontendzmabt.ui.components.RatingPicker
import com.example.frontendzmabt.ui.screens.AppScreenTemplate
import com.example.frontendzmabt.ui.screens.EditPostNavArgs
import com.example.frontendzmabt.ui.screens.Screen
import com.example.frontendzmabt.ui.screens.ProfileNavArgs
import com.example.frontendzmabt.ui.screens.toRoute
import kotlinx.coroutines.launch

@Composable
fun PostScreen(navController: NavController, id: Int, isUser: Boolean) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var rating by remember { mutableStateOf(0) }
    var response by remember { mutableStateOf<GetPostResponse?>(null) }
    var post by remember { mutableStateOf<Post?>(null) }
    var images by remember { mutableStateOf<List<PostImage>?>(null) }
    var isLoggedIn by remember { mutableStateOf(false) }
    val commentRepo = remember { CommentRepository(context) }

    LaunchedEffect(Unit) {
        if (SessionManager(context).getToken() != null) {
            isLoggedIn = true
        }
    }
    LaunchedEffect(Unit) {
        val repo = PostRepository(context)
        println(id)
        response = repo.get(id)
        if (response != null) {
            post = response?.post
            images = response?.postImages
        }
    }

    PostScreenContent(
        navController = navController,
        id = id,
        isUser = isUser,
        isLoggedIn = isLoggedIn,
        post = post,
        images = images,
        rating = rating,
        commentRepo = commentRepo,
        onRatingChanged = { newRating ->
            rating = newRating
            scope.launch {
                val success = ChangeRating(context, rating = newRating, postId = id)
                println("Rating changed: $success")
            }
        }
    )
}

@Composable
fun PostScreenContent(
    navController: NavController,
    id: Int,
    isUser: Boolean,
    isLoggedIn: Boolean,
    post: Post?,
    images: List<PostImage>?,
    rating: Int,
    commentRepo: CommentRepositoryInterface,
    onRatingChanged: (Int) -> Unit
) {
    AppScreenTemplate(
        navController = navController,
        header = {},
        content = {
            Column(
                modifier = Modifier
                    .background(color = Color.Gray)
                    .fillMaxSize()
            ) {
                if (post == null) {
                    Text("Failed to load post")
                } else {
                    Row {
                        IconButton(
                            onClick = {
                                navController.navigate(ProfileNavArgs(post.userId).toRoute()) {
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_account_box),
                                contentDescription = "Open profile",
                                tint = Color.Green,
                            )
                        }
                        Text("userId:" + post.userId)
                    }
                    Text("userId:" + post.description)

                    images?.takeIf { it.isNotEmpty() }?.let {
                        PostImages(it)
                    }
                    Text("MAPA SEM :")
                    if (isUser) {
                        DeletePostButton(navController, post.id)
                        EditPostButton(navController, post.id)
                    } else if (isLoggedIn) {
                        RatingPicker(
                            rating = rating,
                            onRatingChanged = onRatingChanged
                        )
                        CommentForm(id)
                    }
                    CommentList(navController, id, commentRepo)
                }
            }
        }
    )
}


suspend fun ChangeRating(context: Context, rating: Int,postId:Int): Boolean {
    val repo = PostRepository(context)
    return repo.rate(rating,postId)
}

@Composable
fun PostImages(images: List<PostImage>){
    return LazyRow{
        items(count = images.count()) { index ->
            val item = images[index % images.size]
            println(item)
            AsyncImage(
                model = BuildConfig.BACKEND_API_URL+"/"+item.imagePath,
                contentDescription = "Translated description of what the image contains"
            )

            Text(item.imagePath)
            //ClipData.Item(item)
        }
    }


}
@Composable
fun CommentForm(postId :Int){

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var commentText by remember { mutableStateOf("") }

    return Row() {
        TextField(
            value = commentText,
            onValueChange = { commentText = it },
            //modifier = Modifier.fillMaxWidth(),
            //placeholder = { Text("Write your post...") },
            minLines = 3
        )
        Button(onClick = {
            //onLocationPicked(1.0, 1.0)
            scope.launch {
                val repo = CommentRepository(context)
                if (commentText.length>100) {
                    Toast.makeText(context, "Comment needs to be shorter than 100 characters", Toast.LENGTH_LONG).show()
                }else {
                    val success = repo.create(commentText, postId)
                }

                /*if (success) {
                    println("request successfully sent");
                    //navController.navigate(Screen.HomeScreen.route)
                } else {
                    Toast.makeText(context, "Message failed to send internally", Toast.LENGTH_LONG).show()
                }*/
            }

        }) {
            Text("postComment")
        }
    }


}


@Composable
fun EditPostButton(navController: NavController,postId:Int) {
    Button(
        modifier = Modifier.testTag("edit_button"),
        onClick = {
        navController.navigate(
            EditPostNavArgs(postId).toRoute()
        )
    }) {
        Text("Edit post")
    }
}
@Composable
fun DeletePostButton(navController: NavController,postId:Int) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    Button(
        modifier = Modifier.testTag("delete_button"),
        onClick = {
        //TODO fix

        scope.launch {
            val repo = PostRepository(context)
            val success= repo.delete(postId=postId)
            if (success) {
                navController.navigate(Screen.UserProfileScreen.route)
            }else{
                Toast.makeText(context,"Post couldn't be deleted",Toast.LENGTH_LONG).show()
            }
        }
    }) {
        Text("delete post")
    }
}
