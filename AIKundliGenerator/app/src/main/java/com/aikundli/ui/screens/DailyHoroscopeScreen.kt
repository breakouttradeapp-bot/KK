package com.aikundli.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aikundli.model.ZodiacHoroscope
import com.aikundli.ui.components.GlassCard
import com.aikundli.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyHoroscopeScreen(
    navController: NavController
) {
    var selected by remember { mutableStateOf<ZodiacHoroscope?>(null) }
    val horoscopes = remember { defaultZodiacList() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo, Color(0xFF120025))))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 44.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = GoldenStar)
                }
                Column {
                    Text(
                        "Daily Horoscope",
                        style = MaterialTheme.typography.headlineMedium,
                        color = GoldenStar,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Tap your sign for today's reading",
                        color = StarlightWhite.copy(0.5f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(horoscopes) { index, sign ->
                    ZodiacCard(
                        sign = sign,
                        delay = index * 55,
                        selected = selected?.sign == sign.sign,
                        onClick = { selected = if (selected?.sign == sign.sign) null else sign }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = selected != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selected?.let { sign ->
                SignDetailCard(sign = sign, onClose = { selected = null })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignDetailCard(sign: ZodiacHoroscope, onClose: () -> Unit) {

    val gradientColors = zodiacGradient(sign.sign)

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(gradientColors.first, gradientColors.second, DeepIndigo))
                )
                .padding(20.dp)
        ) {
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(sign.symbol, fontSize = 40.sp)

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text(
                                sign.sign,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Today's Cosmic Forecast",
                                color = Color.White.copy(0.6f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, null, tint = Color.White.copy(0.7f))
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    sign.text,
                    color = Color.White.copy(0.9f),
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LuckyChip("🔢 ${sign.luckyNumber}", "Lucky Number")
                    LuckyChip("🎨 ${sign.luckyColor}", "Lucky Color")
                }
            }
        }
    }
}

@Composable
fun LuckyChip(value: String, label: String) {
    Column(
        modifier = Modifier
            .background(Color.White.copy(0.12f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = GoldenStar, fontWeight = FontWeight.Bold)
        Text(label, color = Color.White.copy(0.6f), style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun ZodiacCard(sign: ZodiacHoroscope, delay: Int, selected: Boolean, onClick: () -> Unit) {

    val colors = zodiacGradient(sign.sign)

    Card(
        onClick = onClick,
        modifier = Modifier.aspectRatio(0.88f),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(colors.first.copy(0.3f), colors.second.copy(0.15f)))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(sign.symbol, fontSize = 30.sp)

                Spacer(Modifier.height(6.dp))

                Text(
                    sign.sign,
                    color = if (selected) GoldenStar else StarlightWhite,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )

                Text(
                    "🔢 ${sign.luckyNumber}",
                    color = GoldenStar.copy(0.7f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

private fun zodiacGradient(sign: String): Pair<Color, Color> = when (sign) {
    "Aries" -> Color(0xFFFF5722) to Color(0xFFE91E63)
    "Taurus" -> Color(0xFF4CAF50) to Color(0xFF2196F3)
    "Gemini" -> Color(0xFFFFD700) to Color(0xFFFF9800)
    "Cancer" -> Color(0xFF9C27B0) to Color(0xFF3F51B5)
    "Leo" -> Color(0xFFFF9800) to Color(0xFFFF5722)
    "Virgo" -> Color(0xFF009688) to Color(0xFF4CAF50)
    "Libra" -> Color(0xFFE91E63) to Color(0xFF9C27B0)
    "Scorpio" -> Color(0xFF8B0000) to Color(0xFF6A0572)
    "Sagittarius" -> Color(0xFF1565C0) to Color(0xFF9C27B0)
    "Capricorn" -> Color(0xFF37474F) to Color(0xFF1565C0)
    "Aquarius" -> Color(0xFF1976D2) to Color(0xFF00BCD4)
    "Pisces" -> Color(0xFF5C6BC0) to Color(0xFF00BCD4)
    else -> Color(0xFF6A0572) to Color(0xFF1565C0)
}

fun defaultZodiacList(): List<ZodiacHoroscope> = listOf(
    ZodiacHoroscope("Aries","♈","Energy surrounds you today.",9,"Red"),
    ZodiacHoroscope("Taurus","♉","Patience brings rewards.",6,"Green"),
    ZodiacHoroscope("Gemini","♊","Communication is strong.",5,"Yellow"),
    ZodiacHoroscope("Cancer","♋","Trust your intuition.",2,"Silver"),
    ZodiacHoroscope("Leo","♌","Confidence attracts success.",1,"Gold"),
    ZodiacHoroscope("Virgo","♍","Details matter today.",4,"Navy"),
    ZodiacHoroscope("Libra","♎","Balance creates harmony.",7,"Pink"),
    ZodiacHoroscope("Scorpio","♏","Transformation begins.",8,"Crimson"),
    ZodiacHoroscope("Sagittarius","♐","Adventure calls.",3,"Purple"),
    ZodiacHoroscope("Capricorn","♑","Hard work pays off.",10,"Brown"),
    ZodiacHoroscope("Aquarius","♒","Innovation shines.",11,"Blue"),
    ZodiacHoroscope("Pisces","♓","Creativity flows.",12,"Sea Green")
)
