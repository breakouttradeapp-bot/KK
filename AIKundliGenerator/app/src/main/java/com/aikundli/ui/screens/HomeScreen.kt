package com.aikundli.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aikundli.ui.navigation.Screen
import com.aikundli.ui.theme.*

data class MenuCard(
    val title    : String,
    val subtitle : String,
    val icon     : String,
    val gradient : List<Color>,
    val route    : String
)

@Composable
fun HomeScreen(navController: NavController) {
    // Match Kundli removed
    val menuItems = listOf(
        MenuCard("Generate Kundli", "Birth chart & predictions", "🔮",
            listOf(Color(0xFF6A0572), Color(0xFF1565C0)), Screen.GenerateKundli.route),
        MenuCard("Daily Horoscope", "Today's cosmic insights",  "⭐",
            listOf(Color(0xFFFF6F00), Color(0xFFE91E8C)), Screen.DailyHoroscope.route),
        MenuCard("Saved Reports",   "View past analyses",       "📋",
            listOf(Color(0xFF00695C), Color(0xFF1565C0)), Screen.SavedReports.route),
        MenuCard("Premium",         "Unlock all features",      "👑",
            listOf(Color(0xFFFFD700), Color(0xFFFF6F00)), Screen.Premium.route),
        MenuCard("Settings",        "App preferences",          "⚙️",
            listOf(Color(0xFF37474F), Color(0xFF1A237E)), Screen.Settings.route),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo, Color(0xFF120025))))
    ) {
        // Animated particles
        ParticleBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            HomeHeader()

            LazyVerticalGrid(
                columns               = GridCells.Fixed(2),
                modifier              = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement   = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding        = PaddingValues(vertical = 16.dp)
            ) {
                itemsIndexed(menuItems) { index, item ->
                    AnimatedMenuCard(
                        card    = item,
                        delay   = index * 100,
                        onClick = { navController.navigate(item.route) }
                    )
                }
            }
        }
    }
}

@Composable
fun ParticleBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Reverse),
        label = "particleOffset"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val particlePositions = listOf(
            Offset(size.width * 0.1f, size.height * (0.1f + offset * 0.05f)),
            Offset(size.width * 0.85f, size.height * (0.2f - offset * 0.05f)),
            Offset(size.width * 0.3f, size.height * (0.7f + offset * 0.04f)),
            Offset(size.width * 0.7f, size.height * (0.8f - offset * 0.03f)),
            Offset(size.width * 0.5f, size.height * (0.4f + offset * 0.06f)),
        )
        particlePositions.forEachIndexed { i, pos ->
            val radius = (8f + i * 4f) * (0.8f + offset * 0.4f)
            drawCircle(
                color  = GoldenStar.copy(alpha = 0.06f + i * 0.01f),
                radius = radius,
                center = pos
            )
        }
    }
}

@Composable
fun HomeHeader() {
    val infiniteTransition = rememberInfiniteTransition(label = "header")
    val shimmer by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label         = "shimmer"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.05f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label         = "pulse"
    )

    Column(
        modifier            = Modifier.fillMaxWidth().padding(top = 56.dp, start = 24.dp, end = 24.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text       = "✨ AI Kundli",
            style      = MaterialTheme.typography.displayLarge,
            color      = GoldenStar.copy(alpha = 0.85f + 0.15f * shimmer),
            fontWeight = FontWeight.ExtraBold,
            modifier   = Modifier.scale(pulse)
        )
        Text(
            text     = "Generator",
            fontSize = 34.sp,
            color    = MysticViolet.copy(alpha = 0.9f),
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 2.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text  = "Discover your cosmic blueprint 🌙",
            style = MaterialTheme.typography.bodyLarge,
            color = StarlightWhite.copy(alpha = 0.6f)
        )

        Spacer(Modifier.height(16.dp))
        // Decorative divider
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(3.dp)
                .background(
                    Brush.horizontalGradient(listOf(GoldenStar, MysticViolet, Color.Transparent)),
                    RoundedCornerShape(2.dp)
                )
        )
    }
}

@Composable
fun AnimatedMenuCard(card: MenuCard, delay: Int, onClick: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue   = if (visible) 1f else 0.6f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label         = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(400),
        label         = "alpha"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "cardGlow")
    val glow by infiniteTransition.animateFloat(
        initialValue  = 0.8f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(2000 + delay, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label         = "glow"
    )

    Card(
        onClick   = onClick,
        modifier  = Modifier.fillMaxWidth().aspectRatio(0.88f).scale(scale).alpha(alpha),
        shape     = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = card.gradient + listOf(card.gradient.last().copy(alpha = 0.7f)),
                        start  = Offset.Zero,
                        end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .border(
                    width  = 1.5.dp,
                    brush  = Brush.linearGradient(
                        listOf(Color.White.copy(0.4f), Color.White.copy(0.1f), Color.Transparent)
                    ),
                    shape  = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Top-right glow circle
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 10.dp, y = (-10).dp)
                    .background(
                        Brush.radialGradient(listOf(Color.White.copy(0.15f * glow), Color.Transparent)),
                        shape = RoundedCornerShape(50)
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier            = Modifier.padding(16.dp)
            ) {
                Text(
                    text     = card.icon,
                    fontSize = 44.sp,
                    modifier = Modifier.scale(glow)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text       = card.title,
                    style      = MaterialTheme.typography.titleLarge,
                    color      = Color.White,
                    textAlign  = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text      = card.subtitle,
                    style     = MaterialTheme.typography.bodySmall,
                    color     = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
