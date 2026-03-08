package com.aikundli.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aikundli.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

data class Star(
    val x      : Float,
    val y      : Float,
    val radius : Float,
    val alpha  : Float
)

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {

    var starsAlpha  by remember { mutableStateOf(0f) }
    var zodiacAlpha by remember { mutableStateOf(0f) }
    var logoAlpha   by remember { mutableStateOf(0f) }
    var titleAlpha  by remember { mutableStateOf(0f) }
    var logoScale   by remember { mutableStateOf(0.3f) }

    val zodiacRotation by rememberInfiniteTransition(label = "zodiac").animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart),
        label         = "rotation"
    )
    val innerRotation by rememberInfiniteTransition(label = "inner").animateFloat(
        initialValue  = 360f,
        targetValue   = 0f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Restart),
        label         = "innerRot"
    )

    val glowPulse by rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue  = 0.6f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label         = "glow"
    )

    val stars = remember {
        List(150) {
            Star(
                x      = Random.nextFloat(),
                y      = Random.nextFloat(),
                radius = Random.nextFloat() * 2.5f + 0.5f,
                alpha  = Random.nextFloat() * 0.8f + 0.2f
            )
        }
    }

    val twinkle by rememberInfiniteTransition(label = "twinkle").animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label         = "twinkle"
    )

    LaunchedEffect(Unit) {
        animate(0f, 1f, animationSpec = tween(800)) { v, _ -> starsAlpha = v }
        animate(0f, 1f, animationSpec = tween(600)) { v, _ -> zodiacAlpha = v }
        animate(0f, 1f, animationSpec = tween(700, easing = OvershootInterpolator().toEasing())) { v, _ ->
            logoAlpha = v; logoScale = 0.3f + v * 0.7f
        }
        delay(200)
        animate(0f, 1f, animationSpec = tween(600)) { v, _ -> titleAlpha = v }
        delay(1200)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo, Color(0xFF120025)))),
        contentAlignment = Alignment.Center
    ) {
        // Starfield
        Canvas(modifier = Modifier.fillMaxSize().alpha(starsAlpha)) {
            drawStars(stars, twinkle)
        }

        // Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier.size(240.dp).alpha(zodiacAlpha)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawZodiacWheel(zodiacRotation, innerRotation, glowPulse)
                }

                Text(
                    text     = "☽✦☾",
                    fontSize = 52.sp,
                    modifier = Modifier
                        .alpha(logoAlpha * glowPulse)
                        .scale(logoScale)
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text       = "AI Kundli Generator",
                fontSize   = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = GoldenStar,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.alpha(titleAlpha)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text     = "Your Cosmic Blueprint Awaits",
                fontSize = 14.sp,
                color    = StarlightWhite.copy(alpha = 0.65f),
                modifier = Modifier.alpha(titleAlpha * 0.8f)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text     = "Powered by Cerebras AI 🤖",
                fontSize = 12.sp,
                color    = MysticViolet.copy(alpha = 0.8f),
                modifier = Modifier.alpha(titleAlpha * 0.7f)
            )
        }
    }
}

fun DrawScope.drawStars(stars: List<Star>, twinkle: Float) {
    stars.forEach { star ->
        val a = (star.alpha * (0.6f + 0.4f * twinkle)).coerceIn(0f, 1f)
        drawCircle(
            color  = Color.White.copy(alpha = a),
            radius = star.radius,
            center = Offset(star.x * size.width, star.y * size.height)
        )
    }
}

fun DrawScope.drawZodiacWheel(outerRot: Float, innerRot: Float, glow: Float) {
    val cx     = size.width / 2
    val cy     = size.height / 2
    val radius = size.minDimension / 2 - 12f
    val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)))

    // Glow rings
    listOf(radius + 30f to 0.15f, radius + 15f to 0.25f, radius to 0.4f).forEach { (r, alpha) ->
        drawCircle(
            brush  = Brush.radialGradient(
                listOf(MysticViolet.copy(alpha * glow), Color.Transparent),
                Offset(cx, cy), r + 10
            ),
            radius = r,
            center = Offset(cx, cy)
        )
    }

    // Outer dashed ring
    drawCircle(color = MysticViolet.copy(0.5f), radius = radius, center = Offset(cx, cy), style = stroke)

    // 12 zodiac ticks (rotating)
    for (i in 0..11) {
        val angle  = Math.toRadians((outerRot + i * 30.0))
        val startX = cx + (radius - 22) * cos(angle).toFloat()
        val startY = cy + (radius - 22) * sin(angle).toFloat()
        val endX   = cx + radius * cos(angle).toFloat()
        val endY   = cy + radius * sin(angle).toFloat()
        drawLine(
            color       = GoldenStar.copy(alpha = 0.7f + 0.3f * glow),
            start       = Offset(startX, startY),
            end         = Offset(endX, endY),
            strokeWidth = 2.5f
        )
    }

    // Inner counter-rotating ring
    drawCircle(
        color  = CosmicPurple.copy(alpha = 0.35f * glow),
        radius = radius * 0.65f,
        center = Offset(cx, cy)
    )
    for (i in 0..5) {
        val angle = Math.toRadians((innerRot + i * 60.0))
        val r2    = radius * 0.65f
        drawLine(
            color       = GoldenStar.copy(0.3f),
            start       = Offset(cx + (r2 - 14) * cos(angle).toFloat(), cy + (r2 - 14) * sin(angle).toFloat()),
            end         = Offset(cx + r2 * cos(angle).toFloat(), cy + r2 * sin(angle).toFloat()),
            strokeWidth = 1.5f
        )
    }

    // Center glowing circle
    drawCircle(
        brush  = Brush.radialGradient(
            listOf(MysticViolet.copy(0.4f * glow), Color.Transparent),
            Offset(cx, cy), radius * 0.3f
        ),
        radius = radius * 0.3f,
        center = Offset(cx, cy)
    )
}

private fun android.view.animation.Interpolator.toEasing() = Easing { x ->
    getInterpolation(x)
}

private fun OvershootInterpolator() = android.view.animation.OvershootInterpolator(2f)
