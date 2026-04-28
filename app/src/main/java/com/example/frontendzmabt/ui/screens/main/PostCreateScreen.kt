package com.example.frontendzmabt.ui.screens.main

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.frontendzmabt.data.repository.PostRepository
import com.example.frontendzmabt.ui.components.RatingPicker
import com.example.frontendzmabt.ui.screens.AppScreenTemplate
import com.example.frontendzmabt.ui.screens.Screen
import kotlinx.coroutines.launch

private val PostBg      = Color(0xFFB2EBF2)
private val PostCard    = Color(0xFFFFFFFF)
private val PostTeal    = Color(0xFF00535A)
private val PostFieldBg = Color(0xFFE8F7F9)
private val PostLabel   = Color(0xFF546E7A)
private val PostText    = Color(0xFF0D2C2E)
private val PostGray    = Color(0xFF78909C)

@Composable
fun PostCreateScreen(navController: NavController) {
    var postText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val latitude  = savedStateHandle?.getStateFlow("latitude",  0.0)?.collectAsState()?.value ?: 0.0
    val longitude = savedStateHandle?.getStateFlow("longitude", 0.0)?.collectAsState()?.value ?: 0.0

    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    val onSubmit: () -> Unit = {
        scope.launch {
            val repo = PostRepository(context)
            val success = repo.create(postText, rating, longitude, latitude, imageUri)
            if (success) navController.navigate(Screen.UserProfileScreen.route)
            else Toast.makeText(context, "Failed to post content", Toast.LENGTH_LONG).show()
        }
    }

    AppScreenTemplate(
        navController = navController,
        header = { CreatePostHeader(onBack = { navController.popBackStack() }, onPost = onSubmit) },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PostBg)
                    .verticalScroll(rememberScrollState())
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = PostCard,
                    shadowElevation = 6.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        // THE STORY
                        PostSectionLabel("THE STORY")
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = postText,
                            onValueChange = { postText = it },
                            placeholder = { Text("Tell the story behind this place...", color = PostGray, fontSize = 14.sp) },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = PostFieldBg,
                                focusedContainerColor = PostFieldBg,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = PostTeal
                            )
                        )

                        Spacer(Modifier.height(20.dp))

                        // ADD PHOTOS
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PostSectionLabel("ADD PHOTOS")
                            Text("UP TO 5 PHOTOS", fontSize = 10.sp, color = PostGray, letterSpacing = 0.5.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        PostImageUploader(imageUri = imageUri, onImageSelected = { imageUri = it })

                        Spacer(Modifier.height(20.dp))

                        // LOCATION
                        PostSectionLabel("LOCATION")
                        Spacer(Modifier.height(8.dp))
                        PostLocationSection(navController = navController, latitude = latitude, longitude = longitude)

                        Spacer(Modifier.height(20.dp))

                        // RATING
                        PostSectionLabel("RATING")
                        Spacer(Modifier.height(8.dp))
                        RatingPicker(rating = rating, onRatingChanged = { rating = it })

                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = onSubmit,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PostTeal)
                        ) {
                            Text("Publish to Trail  ▷", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    )
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
            Icon(Icons.Default.Close, contentDescription = "Back", tint = PostTeal)
        }
        Text("Create New Post", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PostText)
        TextButton(onClick = onPost) {
            Text("Post", color = PostTeal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun PostSectionLabel(text: String) {
    Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = PostLabel, letterSpacing = 0.8.sp)
}

@Composable
private fun PostImageUploader(imageUri: Uri?, onImageSelected: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onImageSelected(it) }
    }
    val dashedTeal = PostTeal

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Hlavný slot — kamera alebo náhľad
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PostFieldBg)
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
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = PostTeal, modifier = Modifier.size(28.dp))
            }
        }

        // Prázdne sloty
        repeat(4) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PostFieldBg)
            )
        }
    }
}

@Composable
private fun PostLocationSection(navController: NavController, latitude: Double, longitude: Double) {
    val hasLocation = latitude != 0.0 || longitude != 0.0

    if (hasLocation) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(PostFieldBg)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = PostTeal, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("%.4f,  %.4f".format(latitude, longitude), fontSize = 13.sp, color = PostText, modifier = Modifier.weight(1f))
            TextButton(onClick = { navController.navigate(Screen.LocationPickerScreen.route) }) {
                Text("Zmeniť", color = PostTeal, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    } else {
        Button(
            onClick = { navController.navigate(Screen.LocationPickerScreen.route) },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PostTeal)
        ) {
            Text("Pick a location", fontSize = 14.sp)
        }
    }
}

// Zachované pre spätnú kompatibilitu
@Composable
fun PostForm(navController: NavController) {}

@Composable
fun ImageUploader(onImageSelected: (Uri) -> Unit) {}

@Composable
fun PickLocationButton(navController: NavController, latitude: Double, longitude: Double) {}

@Composable
fun SubmitPostButton(navController: NavController, postText: String, rating: Int, longitude: Double, latitude: Double, imageUri: Uri?) {}
