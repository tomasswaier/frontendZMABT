package com.example.frontendzmabt.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RatingPicker(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    maxStars: Int = 5
) {
    /*
        Tuto funkciu mi vygeneroval chat
     */
    Row {
        for (i in 1..maxStars) {
            Icon(
                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Star $i",
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        onRatingChanged(i)
                    }
            )
        }
    }
}
