package com.example.frontendzmabt.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.frontendzmabt.data.repository.Place
import com.example.frontendzmabt.data.repository.PlaceRepository
import com.example.frontendzmabt.data.repository.PostRepository
import com.example.frontendzmabt.ui.components.PostCard
import com.example.frontendzmabt.ui.screens.AppScreenTemplate
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

private val SheetBg   = Color(0xFFF0F9FA)
private val TealColor = Color(0xFF00535A)
private val TextMain  = Color(0xFF0D2C2E)
private val TextGray  = Color(0xFF78909C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    var places by remember { mutableStateOf<List<Place>>(emptyList()) }
    var selectedPlace by remember { mutableStateOf<Place?>(null) }

    LaunchedEffect(Unit) {
        places = PlaceRepository(context).getAll()
    }

    if (selectedPlace != null) {
        val place = selectedPlace!!
        val repo = remember(place.id) { PostRepository(context) }
        val pagerFlow = remember(place.id) { repo.getPlacePostsPager(place.id) }
        val lazyPagingItems = pagerFlow.collectAsLazyPagingItems()

        ModalBottomSheet(
            onDismissRequest = { selectedPlace = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = SheetBg
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        "Place #${place.id}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextMain,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }

                item {
                    when (lazyPagingItems.loadState.refresh) {
                        is LoadState.Loading -> Box(
                            Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = TealColor) }
                        is LoadState.Error -> Text(
                            "Nepodarilo sa načítať posty",
                            modifier = Modifier.padding(16.dp),
                            color = TextGray
                        )
                        else -> {}
                    }
                }

                items(lazyPagingItems.itemCount) { index ->
                    val post = lazyPagingItems[index]
                    if (post != null) {
                        PostCard(
                            postId = post.id,
                            userId = post.userId,
                            username = post.user?.username,
                            description = post.description,
                            navController = navController,
                            isUser = false
                        )
                    }
                }

                item {
                    if (lazyPagingItems.loadState.append is LoadState.Loading) {
                        Box(
                            Modifier.fillMaxWidth().padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = TealColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    AppScreenTemplate(
        navController = navController,
        header = { Text("Map") },
        content = {
            GoogleMap(modifier = Modifier.fillMaxSize()) {
                places.forEach { place ->
                    Marker(
                        state = MarkerState(position = LatLng(place.latitude, place.longitude)),
                        onClick = {
                            selectedPlace = place
                            true
                        }
                    )
                }
            }
        }
    )
}
