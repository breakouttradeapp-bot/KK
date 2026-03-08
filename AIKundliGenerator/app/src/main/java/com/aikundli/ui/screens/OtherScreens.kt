package com.aikundli.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aikundli.ui.components.GlassCard
import com.aikundli.ui.navigation.Screen
import com.aikundli.ui.theme.*

// ── Saved Reports ──────────────────────────────────────────────────────────

@Composable
fun SavedReportsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo)))
    ) {
        ScreenHeader("Saved Reports", navController)
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📋", style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(16.dp))
                Text("No saved reports yet",
                    color = StarlightWhite.copy(0.7f),
                    style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text("Generate your first Kundli to save it here",
                    color = StarlightWhite.copy(0.4f),
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// ── Settings ───────────────────────────────────────────────────────────────

@Composable
fun SettingsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo)))
    ) {
        ScreenHeader("Settings", navController)

        LazyColumn(
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SettingsGroup(title = "Account") {
                    SettingsRow("👑 Premium Plan", "Upgrade for unlimited access") {
                        navController.navigate(Screen.Premium.route)
                    }
                }
            }
            item {
                SettingsGroup(title = "App") {
                    SettingsRow("🌙 Theme", "Dark / Light")
                    SettingsRow("🔔 Notifications", "Daily horoscope alerts")
                    SettingsRow("🌐 Language", "English")
                }
            }
            item {
                SettingsGroup(title = "Legal") {
                    SettingsRow("🔒 Privacy Policy", "") {
                        navController.navigate(Screen.PrivacyPolicy.route)
                    }
                    SettingsRow("📄 Terms & Conditions", "") {
                        navController.navigate(Screen.Terms.route)
                    }
                }
            }
            item {
                SettingsGroup(title = "About") {
                    SettingsRow("ℹ️ App Version", "1.0.0")
                    SettingsRow("📧 Contact Support", "support@aikundli.app")
                }
            }
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    GlassCard {
        Column(Modifier.padding(16.dp)) {
            Text(title,
                color      = GoldenStar,
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun SettingsRow(title: String, subtitle: String, onClick: (() -> Unit)? = null) {
    val modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    Row(
        modifier              = modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column {
            Text(title, color = StarlightWhite, style = MaterialTheme.typography.bodyLarge)
            if (subtitle.isNotEmpty())
                Text(subtitle, color = StarlightWhite.copy(0.5f), style = MaterialTheme.typography.bodySmall)
        }
        if (onClick != null)
            Icon(Icons.Default.ChevronRight, null, tint = GoldenStar.copy(0.6f))
    }
}

// ── Premium Screen ─────────────────────────────────────────────────────────

@Composable
fun PremiumScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkSpace, Color(0xFF1A0010))))
            .verticalScroll(rememberScrollState())
    ) {
        ScreenHeader("Premium Plans", navController)

        Column(
            modifier            = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("👑 Unlock Your Full Cosmic Potential",
                style      = MaterialTheme.typography.headlineMedium,
                color      = GoldenStar,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(vertical = 8.dp))

            PremiumCard(
                plan      = "Basic Plan",
                price     = "₹99 / month",
                color1    = CosmicPurple,
                color2    = CelestialBlue,
                features  = listOf(
                    "✅ Unlimited Kundli Generation",
                    "✅ AI-Powered Horoscope Readings",
                    "✅ Daily Zodiac Forecasts",
                    "❌ PDF Download",
                    "❌ Priority Support",
                ),
                productId = "premium_basic_monthly"
            )

            PremiumCard(
                plan        = "Pro Plan",
                price       = "₹199 / month",
                color1      = NebulaPink,
                color2      = GoldenStar,
                features    = listOf(
                    "✅ Everything in Basic",
                    "✅ Advanced AI Predictions",
                    "✅ PDF Download (No Ads)",
                    "✅ Priority AI Analysis",
                    "✅ Priority Support",
                ),
                productId   = "premium_pro_monthly",
                isHighlight = true
            )

            Text(
                "Subscriptions managed via Google Play.\nCancel anytime.",
                color    = StarlightWhite.copy(0.4f),
                style    = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun PremiumCard(
    plan       : String,
    price      : String,
    color1     : Color,
    color2     : Color,
    features   : List<String>,
    productId  : String,
    isHighlight: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(20.dp),
        border   = if (isHighlight) BorderStroke(2.dp, GoldenStar) else null,
        colors   = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(color1, color2)))
        ) {
            Column(Modifier.padding(20.dp)) {
                if (isHighlight) {
                    Surface(
                        shape    = RoundedCornerShape(8.dp),
                        color    = GoldenStar,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text("  MOST POPULAR  ",
                            color      = DarkSpace,
                            fontWeight = FontWeight.Bold,
                            style      = MaterialTheme.typography.labelSmall,
                            modifier   = Modifier.padding(4.dp))
                    }
                }
                Text(plan, color = Color.White,
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Text(price, color = GoldenStar,
                    style      = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.padding(vertical = 4.dp))
                Spacer(Modifier.height(8.dp))
                features.forEach { f ->
                    Text(f, color = Color.White.copy(0.9f),
                        style    = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 2.dp))
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick  = { /* TODO: trigger Google Play Billing */ },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Subscribe Now", color = color1, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ── Privacy Policy ─────────────────────────────────────────────────────────

@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo)))
    ) {
        ScreenHeader("Privacy Policy", navController)
        Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
            GlassCard {
                Column(Modifier.padding(20.dp)) {
                    Text(PRIVACY_POLICY_TEXT,
                        color = StarlightWhite.copy(0.85f),
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// ── Terms & Conditions ─────────────────────────────────────────────────────

@Composable
fun TermsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo)))
    ) {
        ScreenHeader("Terms & Conditions", navController)
        Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
            GlassCard {
                Column(Modifier.padding(20.dp)) {
                    Text(TERMS_TEXT,
                        color = StarlightWhite.copy(0.85f),
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// ── Common screen header ───────────────────────────────────────────────────

@Composable
fun ScreenHeader(title: String, navController: NavController) {
    Row(
        modifier          = Modifier.fillMaxWidth().padding(16.dp).padding(top = 44.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, null, tint = GoldenStar)
        }
        Text(title,
            style      = MaterialTheme.typography.headlineMedium,
            color      = GoldenStar,
            fontWeight = FontWeight.Bold)
    }
}

// ── Policy text ────────────────────────────────────────────────────────────

private const val PRIVACY_POLICY_TEXT = """
Privacy Policy — AI Kundli Generator

Last Updated: January 2025

1. INFORMATION WE COLLECT
We collect birth details (name, date, time, and place of birth) solely to generate your Kundli report. No sensitive personal data is stored on our servers.

2. HOW WE USE YOUR DATA
• Birth data is sent to the Cerebras AI API to generate astrological charts and readings.
• We do not sell or share your personal information with third parties.

3. DATA RETENTION
Generated reports are stored locally on your device. You can delete them at any time.

4. CONTACT
For privacy inquiries: privacy@aikundli.app
"""

private const val TERMS_TEXT = """
Terms & Conditions — AI Kundli Generator

1. DISCLAIMER
AI Kundli Generator is an entertainment application. Astrological readings are for informational and entertainment purposes only.

2. SUBSCRIPTIONS
Premium subscriptions are billed monthly through Google Play. Cancel anytime through your Google Play account.

3. CONTENT
AI-generated horoscope content is produced by Cerebras AI and should not be used as a substitute for professional advice.

4. LIMITATION OF LIABILITY
The app and its creators are not liable for any decisions made based on astrological readings.

Contact: legal@aikundli.app
"""
