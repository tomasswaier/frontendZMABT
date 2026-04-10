package com.example.frontendzmabt.ui.components

import android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ChangeStatusBoolean(
    IconTrue: ImageVector,
    IconFalse: ImageVector,
    IsFollowing: Boolean,
    onClick: (Boolean) -> Unit,
) {
    /*
        Tuto funkciu mi vygeneroval chat
     */
    return Icon(
            imageVector = if (IsFollowing) IconTrue else IconFalse,
            contentDescription = "IsFollowing",
            modifier = Modifier
                .size(64.dp)
                .clickable {
                    println("inside onClick isFollowing:"+IsFollowing)
                    onClick(IsFollowing)
                }
        )
}
