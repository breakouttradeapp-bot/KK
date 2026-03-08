package com.aikundli.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aikundli.model.HoroscopeResult
import com.aikundli.model.PlanetPosition
import com.aikundli.ui.components.GlassCard
import com.aikundli.ui.theme.*
import com.aikundli.viewmodel.KundliViewModel

@Composable
fun KundliResultScreen(
    navController: NavController,
    viewModel    : KundliViewModel = viewModel()
) {
    val state = viewModel.kundliState.collectAsState().value

    val result = state.kundliResult
    if (result == null) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔮", fontSize = 64.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    "No Kundli data available",
                    color = StarlightWhite.copy(0.7f),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = MysticViolet)
                ) {
                    Text("← Go Back")
                }
            }
        }
        return
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.resetKundliState() }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "result")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "shimmer"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo, Color(0xFF120025))))
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 44.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, null, tint = GoldenStar)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Kundli Report",
                    style = MaterialTheme.typography.headlineMedium,
                    color = GoldenStar,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    state.currentName,
                    color = StarlightWhite.copy(0.6f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
        ) {

            GlassCard(Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    CosmicPurple.copy(0.6f),
                                    CelestialBlue.copy(0.4f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            "☽✦☾",
                            fontSize = 32.sp,
                            color = GoldenStar.copy(0.8f + 0.2f * shimmer),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            SignChip("☀️ Sun", result.sunSign)
                            SignChip("🌙 Moon", result.moonSign)
                            SignChip("⬆️ Lagna", result.ascendant)
                        }

                        if (result.nakshatra.isNotBlank()) {
                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                SignChip("⭐ Nakshatra", result.nakshatra)

                                if (result.yoga.isNotBlank())
                                    SignChip("🔥 Yoga", result.yoga)
                            }
                        }
                    }
                }
            }

            PlanetPositionsCard(planets = result.planets)

            if (result.houses.isNotEmpty()) {
                HousesCard(houses = result.houses.take(12))
            }

            if (state.horoscope != null) {
                HoroscopeCard(state.horoscope)
            } else {

                GlassCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        val loadingAnim by rememberInfiniteTransition(label = "loading")
                            .animateFloat(
                                0f,
                                1f,
                                infiniteRepeatable(tween(1000), RepeatMode.Reverse),
                                "la"
                            )

                        Text(
                            "🤖",
                            fontSize = 36.sp,
                            modifier = Modifier.alpha(0.5f + 0.5f * loadingAnim)
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Generating AI Horoscope Reading...",
                            color = StarlightWhite.copy(0.7f),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(12.dp))

                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(0.6f),
                            color = MysticViolet,
                            trackColor = GlassBorder
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun SignChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = StarlightWhite.copy(alpha = 0.55f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = if (value.isNotBlank()) value else "—",
            style = MaterialTheme.typography.titleMedium,
            color = GoldenStar,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InfoChip(label: String, value: String) = SignChip(label, value)

@Composable
fun PlanetPositionsCard(planets: List<PlanetPosition>) {

    if (planets.isEmpty()) return

    GlassCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

        Column(Modifier.padding(16.dp)) {

            Text(
                "🪐 Planetary Positions",
                style = MaterialTheme.typography.titleLarge,
                color = GoldenStar,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            planets.forEachIndexed { index, planet ->

                var visible by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(index * 80L)
                    visible = true
                }

                val alpha by animateFloatAsState(
                    targetValue = if (visible) 1f else 0f,
                    animationSpec = tween(300),
                    label = "pa$index"
                )

                Row(
                    modifier = Modifier.fillMaxWidth().alpha(alpha).padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(MysticViolet.copy(0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(planetEmoji(planet.planet), fontSize = 16.sp)
                        }

                        Spacer(Modifier.width(10.dp))

                        Text(
                            planet.planet,
                            color = StarlightWhite,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {

                        Text(
                            "${planet.sign} ${String.format("%.1f", planet.degree)}°",
                            color = GoldenStar.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            "House ${planet.house}${if (planet.isRetro) " ℞" else ""}",
                            color = StarlightWhite.copy(0.5f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                if (index < planets.lastIndex) {
                    Divider(color = GlassBorder, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun HousesCard(houses: List<com.aikundli.model.HouseInfo>) {

    GlassCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

        Column(Modifier.padding(16.dp)) {

            Text(
                "🏠 House Positions",
                style = MaterialTheme.typography.titleLarge,
                color = GoldenStar,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            val chunked = houses.chunked(2)

            chunked.forEach { row ->

                Row(modifier = Modifier.fillMaxWidth()) {

                    row.forEach { house ->

                        Column(
                            modifier = Modifier.weight(1f).padding(4.dp)
                        ) {

                            Text(
                                "House ${house.house}",
                                color = MysticViolet,
                                style = MaterialTheme.typography.labelSmall
                            )

                            Text(
                                house.sign,
                                color = StarlightWhite,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun HoroscopeCard(horoscope: HoroscopeResult) {

    val sections = buildList {

        if (horoscope.personality.isNotBlank())
            add("👤 Personality" to horoscope.personality)

        if (horoscope.career.isNotBlank())
            add("💼 Career" to horoscope.career)

        if (horoscope.marriage.isNotBlank())
            add("💑 Relationships" to horoscope.marriage)

        if (horoscope.finance.isNotBlank())
            add("💰 Finance" to horoscope.finance)

        if (horoscope.health.isNotBlank())
            add("🏥 Health" to horoscope.health)

        if (horoscope.luckyNumbers.isNotBlank())
            add("🔢 Lucky Numbers" to horoscope.luckyNumbers)

        if (horoscope.luckyColors.isNotBlank())
            add("🎨 Lucky Colors" to horoscope.luckyColors)
    }

    if (sections.isEmpty()) return

    GlassCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

        Column(Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    "✨ AI Horoscope Reading",
                    style = MaterialTheme.typography.titleLarge,
                    color = GoldenStar,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text("🤖", fontSize = 20.sp)
            }

            Spacer(Modifier.height(4.dp))

            Text(
                "Powered by Cerebras AI",
                color = MysticViolet.copy(0.8f),
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(Modifier.height(12.dp))

            sections.forEach { (title, content) ->
                ExpandableSection(title = title, content = content)
            }
        }
    }
}

@Composable
fun ExpandableSection(title: String, content: String) {

    var expanded by remember { mutableStateOf(false) }

    Column {

        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                title,
                color = StarlightWhite,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )

            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = MysticViolet
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {

            Text(
                text = content,
                color = StarlightWhite.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlassWhite, RoundedCornerShape(8.dp))
                    .padding(12.dp)
                    .padding(bottom = 8.dp)
            )
        }

        Divider(color = GlassBorder, thickness = 0.5.dp)
    }
}

fun planetEmoji(name: String) = when (name.lowercase()) {
    "sun" -> "☀️"
    "moon" -> "🌙"
    "mars" -> "♂️"
    "mercury" -> "☿"
    "jupiter" -> "♃"
    "venus" -> "♀️"
    "saturn" -> "♄"
    "rahu" -> "☊"
    "ketu" -> "☋"
    else -> "⭐"
}
