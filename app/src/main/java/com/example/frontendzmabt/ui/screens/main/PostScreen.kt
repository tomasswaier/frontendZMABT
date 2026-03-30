package com.example.frontendzmabt.ui.screens.main


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.frontendzmabt.R
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.repository.Post
import com.example.frontendzmabt.data.repository.CommentRepository
import com.example.frontendzmabt.data.repository.PostRepository
import com.example.frontendzmabt.ui.components.CommentList
import com.example.frontendzmabt.ui.screens.AppScreenTemplate
import com.example.frontendzmabt.ui.screens.Screen
import com.example.frontendzmabt.ui.screens.ProfileNavArgs
import com.example.frontendzmabt.ui.screens.toRoute
import kotlinx.coroutines.launch

@Composable
fun PostScreen(navController: NavController, id: Int,isUser:Boolean) {

    val context = LocalContext.current
    val session = SessionManager(context)

    var post by remember { mutableStateOf<Post?>(null) }
    LaunchedEffect(Unit) {
        val repo = PostRepository(context)
        post= repo.get(id)
    }
    /*if (post) {
        username= user!!.username.toString()
    }*/
    AppScreenTemplate(
        navController=navController,header= {},
        content={Column(modifier = Modifier.background(
            color =Color.Gray
        ).fillMaxSize()

        ) {
            //PostList(id,isUser)
            if (post==null){
                Text("Failed to load post")
            }else {
                Row {
                    IconButton(
                        onClick = {
                            navController.navigate(ProfileNavArgs(post!!.userId).toRoute()) {
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_account_box),
                            contentDescription = "",
                            tint = Color.Green,

                            )
                    }
                    Text("userId:" + post?.userId)
                }
                Text("userId:" + post?.description)
                Text("MAPA SEM :")
                if (isUser) {
                    EditPostButton(navController)
                }
                CommentForm(id)
                CommentList(navController, id)
            }



        }}
    )
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
                val success = repo.create(commentText,postId)

                if (success) {
                    println("comment sucessfully posted");
                    //navController.navigate(Screen.HomeScreen.route)
                } else {
                    Toast.makeText(context, "Login failed", Toast.LENGTH_LONG).show()
                }
            }
            println("Longitude and Latitude set to 1.0")

        }) {
            Text("postComment")
        }
    }


}


@Composable
fun EditPostButton(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Button(onClick = {
        //TODO fix
        navController.navigate(Screen.PostScreen.route)
    }) {
        Text("Edit post")
    }
}