package com.example.frontendzmabt.ui.screens.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.frontendzmabt.data.API
import com.example.frontendzmabt.data.model.Post
import com.example.frontendzmabt.data.model.WeatherResponse
import com.example.frontendzmabt.data.repository.GetPostResponse
import com.example.frontendzmabt.data.repository.PostRepository
import com.example.frontendzmabt.ui.components.RatingPicker
import com.example.frontendzmabt.ui.screens.AppScreenTemplate
import com.example.frontendzmabt.ui.screens.Screen
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.jvm.java


@Composable
fun PostCreateScreen(
    navController: NavController,
    postId: Int,
    repo: PostRepository = PostRepository(LocalContext.current)
) {
    val context = LocalContext.current

    var postText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(3) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var post by remember { mutableStateOf<Post?>(null) }
    var response by remember { mutableStateOf<GetPostResponse?>(null) }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val latitude  = savedStateHandle?.getStateFlow("latitude", 0.0)?.collectAsState()?.value ?: 0.0
    val longitude = savedStateHandle?.getStateFlow("longitude", 0.0)?.collectAsState()?.value ?: 0.0

    var online by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        online = API().isOnline(context)
    }

    LaunchedEffect(postId) {
        response = repo.get(postId)
        response?.post?.let {
            post = it
            rating = it.stars
            postText = it.description
        }
    }

    val onSubmit: () -> Unit = {
        scope.launch {
            PostCreateScreenContentActions.handleSubmit(
                context = context,
                navController = navController,
                repo = repo,
                postId = postId,
                postText = postText,
                rating = rating,
                longitude = longitude,
                latitude = latitude,
                imageUri = imageUri,
                online = online
            )
        }
    }

    PostCreateScreenContent(
        navController = navController,
        postId = postId,
        postText = postText,
        rating = rating,
        imageUri = imageUri,
        latitude = latitude,
        longitude = longitude,
        online = online,
        onPostTextChange = { postText = it },
        onRatingChange = { rating = it },
        onImageChange = { imageUri = it },
        onSubmit = onSubmit
    )
}
@Composable
fun PostCreateScreenContent(
    navController: NavController,
    postId: Int,

    postText: String,
    rating: Int,
    imageUri: Uri?,
    latitude: Double,
    longitude: Double,
    online: Boolean,

    onPostTextChange: (String) -> Unit,
    onRatingChange: (Int) -> Unit,
    onImageChange: (Uri?) -> Unit,
    onSubmit: () -> Unit
) {

    AppScreenTemplate(
        navController = navController,
        header = {
            CreatePostHeader(
                onBack = { navController.popBackStack() },
                onPost = onSubmit
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondary)
                    .verticalScroll(rememberScrollState())
            ) {

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 6.dp
                ) {

                    Column(modifier = Modifier.padding(20.dp)) {

                        PostSectionLabel("THE STORY")

                        OutlinedTextField(
                            value = postText,
                            onValueChange = onPostTextChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp).testTag("the_story_text_field")
                        )

                        if (online && postId == 0) {

                            PostSectionLabel("ADD PHOTOS")

                            PostImageUploader(
                                imageUri = imageUri,
                                onImageSelected = onImageChange
                            )

                            Button(onClick = {
                                // still UI action
                            }) {
                                Text("Add Current Wether")
                            }
                        }

                        if (postId == 0) {
                            PostSectionLabel("LOCATION")

                            PostLocationSection(
                                navController = navController,
                                latitude = latitude,
                                longitude = longitude
                            )
                        }

                        PostSectionLabel("RATING")

                        RatingPicker(
                            rating = rating,
                            onRatingChanged = onRatingChange
                        )

                        Button(onClick = onSubmit) {
                            Text("Publish to Trail  ▷")
                        }
                    }
                }
            }
        }
    )
}
object PostCreateScreenContentActions {

    suspend fun handleSubmit(
        context: Context,
        navController: NavController,
        repo: PostRepository,
        postId: Int,
        postText: String,
        rating: Int,
        longitude: Double,
        latitude: Double,
        imageUri: Uri?,
        online: Boolean
    ) {
        if (postText.length >= 100) {
            Toast.makeText(context, "Too long", Toast.LENGTH_SHORT).show()
            return
        }

        if (postId == 0) {
            if (longitude == 0.0 && latitude == 0.0) {
                Toast.makeText(context, "Pick location", Toast.LENGTH_SHORT).show()
                return
            }

            val success = repo.create(postText, rating, longitude, latitude, imageUri, online)
            if (success) navController.navigate(Screen.UserProfileScreen.route)
        } else {
            val success = repo.edit(postText, rating, longitude, latitude, postId)
            if (success) navController.navigate(Screen.UserProfileScreen.route)
        }
    }
}


@Composable
private fun CreatePostHeader(onBack: () -> Unit, onPost: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.Close, contentDescription = "Back", tint = MaterialTheme.colorScheme.tertiary)
        }
        Text("Create New Post", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
        TextButton(onClick = onPost) {
            Text("Post", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun PostSectionLabel(text: String) {
    Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.surface, letterSpacing = 0.8.sp)
}

@Composable
private fun PostImageUploader(imageUri: Uri?, onImageSelected: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onImageSelected(it) }
    }
    val dashedTeal = MaterialTheme.colorScheme.tertiary

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background( MaterialTheme.colorScheme.background)
                .drawBehind {
                    drawRoundRect(
                        color = dashedTeal,
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
                        ),
                        cornerRadius = CornerRadius(12.dp.toPx())
                    )
                }
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(28.dp))
            }
        }

        /*repeat(4) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.background)
            )
        }*/
    }
}
//generated by ai from here
fun weatherCodeToText(code: Int): String {
    return when (code) {
        0 -> "Clear sky"
        1, 2, 3 -> "Partly cloudy"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rain"
        71, 73, 75 -> "Snow"
        95 -> "Thunderstorm"
        else -> "Unknown"
    }
}

fun weatherEmoji(code: Int): String {
    return when (code) {
        0 -> "☀️"
        1, 2, 3 -> "🌤"
        45, 48 -> "🌫"
        51, 53, 55 -> "🌦"
        61, 63, 65 -> "🌧"
        71, 73, 75 -> "❄️"
        95 -> "⛈"
        else -> "🌍"
    }
}

suspend fun addWeather(
    context: Context,
    longitude: Double,
    latitude: Double
): String {

    if (longitude == 0.0 || latitude == 0.0) {

        Toast.makeText(
            context,
            "Pick a location first",
            Toast.LENGTH_SHORT
        ).show()

        return ""
    }

    val url =
        "https://api.open-meteo.com/v1/forecast" +
                "?latitude=$latitude" +
                "&longitude=$longitude" +
                "&current=temperature_2m,weather_code"

    val result = withContext(Dispatchers.IO) {
        API.callApi(
            apiUrl = url,
            token = null,
            httpMethod = "GET"
        )
    }

    return try {

        val gson = Gson()

        val response = gson.fromJson(
            result,
            WeatherResponse::class.java
        )

        val temp = response.current.temperature_2m.toInt()
        val code = response.current.weather_code

        val emoji = weatherEmoji(code)
        val text = weatherCodeToText(code)

        "$emoji ${temp}°C • $text"

    } catch (e: Exception) {

        e.printStackTrace()

        Toast.makeText(
            context,
            "Failed to load weather",
            Toast.LENGTH_SHORT
        ).show()

        ""
    }
}//to here. It was assissted writing but most of it ai. simply api call

@Composable
private fun PostLocationSection(navController: NavController, latitude: Double, longitude: Double) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var currentLatitude by remember { mutableStateOf(0.0) }
    var currentLongitude by remember { mutableStateOf(0.0) }
    val hasLocation = latitude != 0.0 || longitude != 0.0
    var showPicker by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->

        if (
            granted &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                }

                showPicker = true
            }
        }
    }

    if (showPicker) {
        LocationPickerDialog(
            initialLatitude = currentLatitude,
            initialLongitude = currentLongitude,
            onDismiss = { showPicker = false },
            onLocationSelected = { lat, lon ->

                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("latitude", lat)

                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("longitude", lon)

                showPicker = false
            }
        )
    }

    if (hasLocation) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("%.4f,  %.4f".format(latitude, longitude), fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.weight(1f))
            TextButton(onClick = {
                permissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }) {
                Text("Zmeniť", color = MaterialTheme.colorScheme.tertiary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    } else {
        Button(
            onClick = {
                permissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text("Pick a location", fontSize = 14.sp)
        }
    }

}