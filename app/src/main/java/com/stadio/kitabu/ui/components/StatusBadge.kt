package com.stadio.kitabu.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val BadgeGreen = Color(0xFF2E7D32)
val BadgeRed = Color(0xFFC62828)
val BadgeOrange = Color(0xFFEF6C00)

/**
 * Small coloured pill used for availability (catalog) and booking status (dashboard).
 */
@Composable
fun StatusBadge(label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = color,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
