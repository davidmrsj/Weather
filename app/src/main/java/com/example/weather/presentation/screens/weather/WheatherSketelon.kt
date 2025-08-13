package com.example.weather.presentation.screens.weather

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.weather.presentation.ui.theme.BluePrimary

@Composable
fun WeatherSkeletonScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BluePrimary)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            SkeletonPill(width = 64.dp, height = 18.dp, corner = 6.dp)
        }

        SkeletonBlock(height = 28.dp, corner = 8.dp)

        SkeletonBlock(height = 132.dp, corner = 12.dp)

        SkeletonBlock(height = 88.dp, corner = 12.dp)

        SkeletonBlock(height = 148.dp, corner = 12.dp)

        SkeletonBlock(height = 120.dp, corner = 12.dp)

        Spacer(Modifier.height(8.dp))
    }
}


@Composable
private fun shimmerBrush(
    base: Color = Color.White.copy(alpha = 0.15f),
    highlight: Color = Color.White.copy(alpha = 0.35f),
): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val anim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1100, easing = LinearEasing)
        ),
        label = "offset"
    )

    val startX = -400f + anim * 800f
    val endX = startX + 300f

    return Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset(startX, 0f),
        end = Offset(endX, 0f)
    )
}

@Composable
private fun SkeletonBlock(
    modifier: Modifier = Modifier,
    height: Dp,
    corner: Dp = 10.dp,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(corner))
            .background(shimmerBrush())
    )
}

@Composable
private fun SkeletonPill(
    width: Dp,
    height: Dp = 24.dp,
    corner: Dp = 8.dp,
) {
    Box(
        Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(corner))
            .background(shimmerBrush())
    )
}

@Preview(showSystemUi = true, name = "Skeleton Home – Tiempo")
@Composable
private fun WeatherSkeletonPreview() {
    MaterialTheme {
        WeatherSkeletonScreen()
    }
}