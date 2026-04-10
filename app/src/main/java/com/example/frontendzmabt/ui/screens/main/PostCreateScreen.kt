package com.example.frontendzmabt.ui.screens.main

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.frontendzmabt.data.repository.PostRepository
import com.example.frontendzmabt.ui.components.RatingPicker
import com.example.frontendzmabt.ui.screens.AppScreenTemplate
import com.example.frontendzmabt.ui.screens.Screen
import kotlinx.coroutines.launch


@Composable
fun PostCreateScreen(navController: NavController) {
    AppScreenTemplate(
        navController=navController,header= {},
        content={Column(modifier = Modifier.background(
            color =Color.Gray
        ).fillMaxSize()

        ) {
            PostForm(navController)
        }}
    )
}


@Composable
fun PostForm(navController: NavController) {
    var rating by remember { mutableStateOf(0) }
    var postText by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    Column() {

        TextField(
            value = postText,
            onValueChange = { postText = it },
            modifier = Modifier.fillMaxWidth(),
            //placeholder = { Text("Write your post...") },
            minLines = 3
        )
        PickLocationButton(navController,latitude=latitude,longitude=longitude,onLocationPicked = { lat, lon ->
            latitude = lat
            longitude = lon
        })
        RatingPicker(rating=rating,onRatingChanged = { rating = it;println("changed rating to:"+it) })

        ImageUploader(
            onImageSelected = { uri ->
                imageUri = uri
            }
        )
        SubmitPostButton(navController,postText=postText,rating=rating,longitude=longitude,latitude=latitude,imageUri=imageUri)
    }
}
@Composable
fun ImageUploader(
    onImageSelected: (Uri) -> Unit
) {
    val context = LocalContext.current

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            onImageSelected(it)
        }
    }

    Column {
        Button(onClick = {
            launcher.launch("image/*")
        }) {
            Text("Select Image")
        }

        selectedImageUri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}

@Composable
fun PickLocationButton(navController: NavController,latitude:Number,longitude:Number,onLocationPicked: (Double, Double) -> Unit) {
    Button(onClick = {
        onLocationPicked(1.0, 1.0)
        println("Longitude and Latitude set to 1.0")

    }) {
        Text("Pick Location")
    }
}

@Composable
fun SubmitPostButton(navController: NavController,postText:String,rating:Int,longitude: Double,latitude: Double,imageUri: Uri?) {
    val context = LocalContext.current
    // Coroutine scope tied to the composable
    val scope = rememberCoroutineScope()

    Button(onClick = {

        scope.launch {
            val repo = PostRepository(context)
            val success = repo.create(postText=postText,rating=rating,longitude=longitude,latitude=latitude,imageUri=imageUri)

            if (success) {
                navController.navigate(Screen.UserProfileScreen.route)
            } else {
                Toast.makeText(context, "Failed to post content", Toast.LENGTH_LONG).show()
            }
        }
    }) {
        Text("POST")
    }
}

