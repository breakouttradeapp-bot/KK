package com.aikundli.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aikundli.model.KundliRequest
import com.aikundli.ui.components.GlassCard
import com.aikundli.ui.navigation.Screen
import com.aikundli.ui.theme.*
import com.aikundli.viewmodel.KundliViewModel
import java.util.*

// ── Generate Kundli Screen ────────────────────────────────────────────────────

@Composable
fun GenerateKundliScreen(
    navController : NavController,
    viewModel     : KundliViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.kundliState.collectAsState()

    var name   by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var dob    by remember { mutableStateOf("") }
    var tob    by remember { mutableStateOf("") }
    var place  by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()

    // Date picker — dismissed on composition disposal to prevent WindowLeaked crash
    val datePicker = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, y: Int, m: Int, d: Int ->
                dob = "%04d-%02d-%02d".format(y, m + 1, d)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
    DisposableEffect(Unit) {
        onDispose { if (datePicker.isShowing) datePicker.dismiss() }
    }

    // Time picker — same disposal guard
    val timePicker = remember {
        TimePickerDialog(context, { _, h, min -> tob = "%02d:%02d".format(h, min) }, 12, 0, true)
    }
    DisposableEffect(Unit) {
        onDispose { if (timePicker.isShowing) timePicker.dismiss() }
    }

    // Navigate to result once — flag prevents double-navigation on recomposition
    var hasNavigated by remember { mutableStateOf(false) }
    LaunchedEffect(state.kundliResult) {
        if (state.kundliResult != null && !hasNavigated) {
            hasNavigated = true
            navController.navigate(Screen.KundliResult.route) { launchSingleTop = true }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "genAnim")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue  = 0.85f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label         = "glow"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Main Form ─────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo, Color(0xFF120025))))
                .verticalScroll(rememberScrollState())
        ) {

            // Header
            Row(
                modifier          = Modifier.fillMaxWidth().padding(16.dp).padding(top = 44.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldenStar)
                }
                Column {
                    Text(
                        "Generate Kundli",
                        style      = MaterialTheme.typography.headlineMedium,
                        color      = GoldenStar,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Enter birth details",
                        color = StarlightWhite.copy(0.5f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Zodiac banner
            GlassCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(CosmicPurple.copy(0.5f), CelestialBlue.copy(0.5f))
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text     = "♈ ♉ ♊ ♋ ♌ ♍ ♎ ♏ ♐ ♑ ♒ ♓",
                        color    = GoldenStar.copy(alpha = 0.7f * glowPulse),
                        fontSize = 18.sp,
                        modifier = Modifier.scale(glowPulse * 0.98f)
                    )
                }
            }

            // Form card
            GlassCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Column(
                    modifier            = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "🔮 Birth Details",
                        style      = MaterialTheme.typography.titleLarge,
                        color      = GoldenStar,
                        fontWeight = FontWeight.Bold
                    )

                    KundliTextField(
                        value         = name,
                        onValueChange = { name = it },
                        label         = "Full Name",
                        icon          = "👤"
                    )

                    // Gender selector
                    Column {
                        Text(
                            "Gender",
                            color = StarlightWhite.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.labelSmall
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf("Male" to "♂", "Female" to "♀", "Other" to "✦").forEach { (g, sym) ->
                                FilterChip(
                                    selected = gender == g,
                                    onClick  = { gender = g },
                                    label    = { Text("$sym $g") },
                                    colors   = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MysticViolet,
                                        selectedLabelColor     = Color.White,
                                        containerColor         = GlassWhite,
                                        labelColor             = StarlightWhite
                                    )
                                )
                            }
                        }
                    }

                    // Date of birth picker
                    OutlinedButton(
                        onClick  = { datePicker.show() },
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(12.dp),
                        border   = BorderStroke(1.dp, if (dob.isNotEmpty()) MysticViolet else GlassBorder)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = GoldenStar)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text  = if (dob.isEmpty()) "📅 Date of Birth" else "📅 $dob",
                            color = if (dob.isEmpty()) StarlightWhite.copy(0.5f) else GoldenStar
                        )
                    }

                    // Time of birth picker
                    OutlinedButton(
                        onClick  = { timePicker.show() },
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(12.dp),
                        border   = BorderStroke(1.dp, if (tob.isNotEmpty()) MysticViolet else GlassBorder)
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = GoldenStar)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text  = if (tob.isEmpty()) "⏰ Time of Birth" else "⏰ $tob",
                            color = if (tob.isEmpty()) StarlightWhite.copy(0.5f) else GoldenStar
                        )
                    }

                    // Place of birth
                    KundliTextField(
                        value         = place,
                        onValueChange = { place = it },
                        label         = "Place of Birth (City, Country)",
                        icon          = "📍"
                    )

                    val formValid = name.isNotBlank() && dob.isNotBlank() &&
                                    tob.isNotBlank() && place.isNotBlank()

                    // Validation hint
                    AnimatedVisibility(visible = !formValid && (name.isNotBlank() || dob.isNotBlank())) {
                        Text(
                            text     = "⚠️ Please fill in all fields to generate your Kundli",
                            color    = GoldenStar.copy(0.8f),
                            style    = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GoldenStar.copy(0.08f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        )
                    }

                    // Error display
                    AnimatedVisibility(visible = state.error != null) {
                        state.error?.let { errorMsg ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0x33FF5722)),
                                shape  = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier          = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("⚠️", fontSize = 18.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        errorMsg,
                                        color = Color(0xFFFF8A65),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    // Generate button
                    val buttonScale by animateFloatAsState(
                        targetValue   = if (state.isLoading) 0.97f else glowPulse,
                        animationSpec = tween(300),
                        label         = "btnScale"
                    )
                    val formValidCapture = name.isNotBlank() && dob.isNotBlank() &&
                                           tob.isNotBlank() && place.isNotBlank()

                    Button(
                        onClick        = {
                            if (formValidCapture && !state.isLoading) {
                                viewModel.generateKundli(
                                    KundliRequest(
                                        name         = name.trim(),
                                        gender       = gender,
                                        dateOfBirth  = dob,
                                        timeOfBirth  = tob,
                                        placeOfBirth = place.trim()
                                    )
                                )
                            }
                        },
                        modifier       = Modifier.fillMaxWidth().height(58.dp).scale(buttonScale),
                        shape          = RoundedCornerShape(18.dp),
                        colors         = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        enabled        = !state.isLoading
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        if (formValidCapture)
                                            listOf(CosmicPurple, MysticViolet, CelestialBlue)
                                        else
                                            listOf(Color(0xFF555555), Color(0xFF444444))
                                    ),
                                    RoundedCornerShape(18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "✨ Generate Kundli",
                                color      = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 17.sp
                            )
                        }
                    }
                }
            }

            // AI info banner
            GlassCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier          = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🤖", fontSize = 28.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Powered by Cerebras AI",
                            color      = GoldenStar,
                            fontWeight = FontWeight.Bold,
                            style      = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "Advanced Vedic astrology calculations",
                            color = StarlightWhite.copy(0.6f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        } // end Column

        // ── Loading Splash Overlay ────────────────────────────────────────
        AnimatedVisibility(
            visible = state.isLoading,
            enter   = fadeIn(animationSpec = tween(400)),
            exit    = fadeOut(animationSpec = tween(400))
        ) {
            KundliLoadingSplash()
        }

    } // end Box
}

// ── Cosmic Loading Splash ─────────────────────────────────────────────────────

private val loadingMessages = listOf(
    "🔭 Consulting the stars...",
    "🪐 Calculating planetary positions...",
    "📜 Reading your birth chart...",
    "✨ Analyzing cosmic influences...",
    "🌙 Mapping lunar nodes...",
    "☀️ Aligning solar energies...",
    "🔮 Decoding your destiny...",
    "🌟 Preparing your cosmic blueprint..."
)

@Composable
fun KundliLoadingSplash() {
    val infiniteTransition = rememberInfiniteTransition(label = "loadAnim")

    val outerRot by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label         = "outerRot"
    )
    val innerRot by infiniteTransition.animateFloat(
        initialValue  = 360f,
        targetValue   = 0f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Restart),
        label         = "innerRot"
    )
    val glow by infiniteTransition.animateFloat(
        initialValue  = 0.5f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label         = "glow"
    )
    val twinkle by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label         = "twinkle"
    )

    // Cycling messages with fade
    var msgIndex by remember { mutableStateOf(0) }
    var msgAlpha by remember { mutableFloatStateOf(1f) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1800)
            animate(1f, 0f, animationSpec = tween(300)) { v, _ -> msgAlpha = v }
            msgIndex = (msgIndex + 1) % loadingMessages.size
            animate(0f, 1f, animationSpec = tween(300)) { v, _ -> msgAlpha = v }
        }
    }

    val stars = remember {
        List(120) {
            Star(
                x      = kotlin.random.Random.nextFloat(),
                y      = kotlin.random.Random.nextFloat(),
                radius = kotlin.random.Random.nextFloat() * 2f + 0.5f,
                alpha  = kotlin.random.Random.nextFloat() * 0.8f + 0.2f
            )
        }
    }

    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(Color(0xEE060014)),
        contentAlignment = Alignment.Center
    ) {
        // Starfield canvas
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            drawStars(stars, twinkle)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(32.dp)
        ) {
            // Zodiac wheel
            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier.size(220.dp)
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    drawZodiacWheel(outerRot, innerRot, glow)
                }

                // Orbiting planet symbols (clamped to prevent off-screen)
                Box(
                    modifier         = Modifier.size(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val orb1X = (kotlin.math.cos(Math.toRadians(outerRot.toDouble())) * 55)
                        .toFloat().coerceIn(-70f, 70f)
                    val orb1Y = (kotlin.math.sin(Math.toRadians(outerRot.toDouble())) * 55)
                        .toFloat().coerceIn(-70f, 70f)
                    Text(
                        "♄", fontSize = 16.sp,
                        color    = GoldenStar.copy(alpha = glow),
                        modifier = Modifier.offset(x = orb1X.dp, y = orb1Y.dp)
                    )

                    val orb2X = (kotlin.math.cos(Math.toRadians((innerRot + 120.0))) * 40)
                        .toFloat().coerceIn(-55f, 55f)
                    val orb2Y = (kotlin.math.sin(Math.toRadians((innerRot + 120.0))) * 40)
                        .toFloat().coerceIn(-55f, 55f)
                    Text(
                        "♃", fontSize = 14.sp,
                        color    = MysticViolet.copy(alpha = glow),
                        modifier = Modifier.offset(x = orb2X.dp, y = orb2Y.dp)
                    )
                }

                // Center emblem
                Text(
                    text     = "☽✦☾",
                    fontSize = 36.sp,
                    modifier = Modifier.alpha(glow).scale(0.92f + glow * 0.08f)
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text       = "Generating Your Kundli",
                fontSize   = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = GoldenStar,
                textAlign  = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text      = loadingMessages[msgIndex],
                fontSize  = 14.sp,
                color     = StarlightWhite.copy(alpha = 0.75f * msgAlpha),
                textAlign = TextAlign.Center,
                modifier  = Modifier.alpha(msgAlpha)
            )

            Spacer(Modifier.height(28.dp))

            // Animated equalizer bars
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                repeat(5) { i ->
                    val dotPhase by infiniteTransition.animateFloat(
                        initialValue  = 0f,
                        targetValue   = 1f,
                        animationSpec = infiniteRepeatable(
                            tween(1000, delayMillis = i * 160, easing = FastOutSlowInEasing),
                            RepeatMode.Reverse
                        ),
                        label = "dot$i"
                    )
                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .height((8 + dotPhase * 14).dp)
                            .background(
                                Brush.verticalGradient(listOf(GoldenStar, MysticViolet)),
                                RoundedCornerShape(4.dp)
                            )
                            .alpha(0.5f + dotPhase * 0.5f)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text  = "Powered by Cerebras AI 🤖",
                fontSize = 12.sp,
                color = MysticViolet.copy(alpha = 0.7f * glow)
            )
        }
    }
}

// ── Reusable TextField ────────────────────────────────────────────────────────

@Composable
fun KundliTextField(
    value         : String,
    onValueChange : (String) -> Unit,
    label         : String,
    icon          : String,
    modifier      : Modifier      = Modifier.fillMaxWidth(),
    keyboardType  : KeyboardType  = KeyboardType.Text
) {
    OutlinedTextField(
        value           = value,
        onValueChange   = onValueChange,
        label           = { Text(label, color = StarlightWhite.copy(alpha = 0.6f)) },
        leadingIcon     = { Text(icon, fontSize = 18.sp) },
        modifier        = modifier,
        shape           = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine      = true,
        colors          = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = MysticViolet,
            unfocusedBorderColor = GlassBorder,
            focusedTextColor     = StarlightWhite,
            unfocusedTextColor   = StarlightWhite,
            cursorColor          = GoldenStar
        )
    )
}
