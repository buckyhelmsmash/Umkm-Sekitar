package com.example.umkm_sekitar.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.umkm_sekitar.ui.theme.RoyalBlue

@Composable
fun OrderProgress(
    currentStep: Int,
    modifier: Modifier = Modifier,
    circleSize: Dp = 40.dp,
    lineWidth: Dp = 4.dp,
    stepSpacing: Dp = 70.dp,
    inactiveColor: Color = Color.LightGray,
    activeColor: Color = RoyalBlue,
    icons: List<ImageVector>
) {
    require(icons.size == 4) { "You must supply exactly 4 icons" }

    val steps = listOf(
        "Berhasil melakukan pemesanan",
        "Menyiapkan Pesananmu",
        "Sedang mengantar Pesananmu",
        "Selesai"
    )

    val descriptions = listOf(
        "Anda telah melakukan pemesanan di Tokodekat",
        "Menyiapkan pesananmu agar siap diambil",
        "Kurir mengantarkan pesananmu",
        "Pesananmu telah selesai"
    )
    val totalHeight = circleSize + stepSpacing * (steps.size - 1) + 20.dp

    Box(modifier = modifier
        .fillMaxWidth()
        .height(totalHeight)
        .padding(horizontal = 16.dp)
    ) {
        Canvas(Modifier
            .fillMaxHeight()
            .width(lineWidth)
            .align(Alignment.TopStart)
            .offset(x = circleSize / 2)
        ) {
            val topOffset = circleSize.toPx() / 2
            val bottomOffset = topOffset + stepSpacing.toPx() * (steps.size - 1)
            drawLine(
                color = inactiveColor,
                strokeWidth = lineWidth.toPx(),
                start = Offset(0f, topOffset),
                end = Offset(0f, bottomOffset)
            )
            val activeEnd = topOffset + stepSpacing.toPx() * currentStep
            drawLine(
                color = activeColor,
                strokeWidth = lineWidth.toPx(),
                start = Offset(0f, topOffset),
                end = Offset(0f, activeEnd)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(circleSize / 2))

            steps.forEachIndexed { index, label ->
                val isDone    = index < currentStep
                val isCurrent = index == currentStep
                val circleColor = when {
                    isDone    -> activeColor
                    isCurrent -> activeColor
                    else      -> Color.DarkGray
                }
                val iconTint = when {
                    isDone    -> Color.White
                    isCurrent -> Color.White
                    else      -> inactiveColor
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(circleSize)
                            .clip(CircleShape)
                            .background(circleColor)
                            .border(1.dp, Color.Gray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = label,
                            tint = iconTint,
                            modifier = Modifier.size(circleSize * 0.6f)
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isDone || isCurrent) Color.Black else Color.Gray
                            )
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = descriptions[index],
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDone || isCurrent) Color.DarkGray else Color.LightGray
                            )
                        )
                    }
                }

                if (index < steps.lastIndex) {
                    Spacer(Modifier.height(stepSpacing - circleSize))
                }
            }
        }
    }
}
