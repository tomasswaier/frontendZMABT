package com.example.frontendzmabt.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
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
    modifier: Modifier,
) {
    /*
        Tuto funkciu mi vygeneroval chat
     */
    return Icon(
            imageVector = if (IsFollowing) IconTrue else IconFalse,
            contentDescription = "IsFollowing",
            modifier = modifier
                .size(64.dp)
                .clickable {
                    println("inside onClick isFollowing:"+IsFollowing)
                    onClick(IsFollowing)
                }
        )
}
