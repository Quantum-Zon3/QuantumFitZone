package com.qz.quantumfitzone

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qz.quantumfitzone.ui.screens.BottomNavBar
import com.qz.quantumfitzone.ui.screens.BottomNavDestination

// ── Colores ───────────────────────────────────────────────────────────────────
private val BgDeep        = Color(0xFF080E1A)
private val BgCard        = Color(0xFF0D1726)
private val CyanPrimary   = Color(0xFF00D4FF)
private val CyanGlow      = Color(0x3300D4FF)
private val TextPrimary   = Color(0xFFE8F4FF)
private val TextSecondary = Color(0xFF6B8FAB)
private val DividerColor  = Color(0xFF1A2F45)

// ── Modelos ───────────────────────────────────────────────────────────────────
private data class SupportCategory(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val content: String
)

private data class PopularArticle(
    val title: String,
    val content: String
)

private val categories = listOf(
    SupportCategory(
        title    = "Technical Support",
        subtitle = "App issues, bugs, sync problems",
        icon     = Icons.Default.Build,
        content  = "For technical issues, try restarting the app first. If the problem persists, check your internet connection and make sure the app is up to date. Contact us at support@quantumfitzone.com for further assistance."
    ),
    SupportCategory(
        title    = "Billing",
        subtitle = "Subscriptions, payments, receipts",
        icon     = Icons.Default.CreditCard,
        content  = "Manage your subscription from the Profile > Account section. Receipts are sent automatically to your registered email. For refund requests, contact billing@quantumfitzone.com within 14 days of purchase."
    ),
    SupportCategory(
        title    = "Workout Tips",
        subtitle = "Exercises, routines, progress",
        icon     = Icons.Default.FitnessCenter,
        content  = "Start with routines matched to your level. Track your progress weekly in the Progress tab. Rest at least 48 hours between strength sessions targeting the same muscle groups."
    ),
    SupportCategory(
        title    = "Account Management",
        subtitle = "Profile, privacy, settings",
        icon     = Icons.Default.ManageAccounts,
        content  = "Update your profile info from the Profile tab. To delete your account, go to Profile > Account > Delete Account. Your data is stored securely and never shared with third parties."
    )
)

private val articles = listOf(
    PopularArticle(
        title   = "How to connect Apple Health?",
        content = "Go to Profile > Connect Devices > Apple Health and tap Enable. Make sure Health permissions are granted in your iPhone Settings > Privacy > Health > QuantumFitZone."
    ),
    PopularArticle(
        title   = "Change my subscription plan",
        content = "Go to Profile > Account > Subscription. Select your new plan and confirm. Changes take effect at the start of your next billing cycle."
    ),
    PopularArticle(
        title   = "Resetting my password",
        content = "On the Login screen tap 'Forgot Password' and enter your email. You'll receive a reset link within a few minutes. Check your spam folder if it doesn't arrive."
    )
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun SupportScreen(
    onBack: () -> Unit = {},
    onNavigate: (BottomNavDestination) -> Unit = {}) {
    var searchQuery         by remember { mutableStateOf("") }
    var expandedCategory    by remember { mutableStateOf<String?>(null) }
    var expandedArticle     by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // ── Top Bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* back */ }) {
                    Icon(
                        imageVector        = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint               = CyanPrimary
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text          = "Help & Support",
                    color         = TextPrimary,
                    fontSize      = 18.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.weight(1f))
                Box(Modifier.size(48.dp))
            }

            // ── Search Bar ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BgCard)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector        = Icons.Default.Search,
                    contentDescription = null,
                    tint               = TextSecondary,
                    modifier           = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value         = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier      = Modifier.weight(1f),
                    textStyle     = androidx.compose.ui.text.TextStyle(
                        color    = TextPrimary,
                        fontSize = 14.sp
                    ),
                    decorationBox = { inner ->
                        if (searchQuery.isEmpty()) {
                            Text("Search FAQs...", color = TextSecondary, fontSize = 14.sp)
                        }
                        inner()
                    }
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Categories label ──────────────────────────────────────────────
            Text(
                text          = "CATEGORIES",
                color         = CyanPrimary,
                fontSize      = 11.sp,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier      = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(12.dp))

            // ── Category rows ─────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BgCard)
            ) {
                categories.forEachIndexed { index, category ->
                    CategoryRow(
                        category   = category,
                        isExpanded = expandedCategory == category.title,
                        onClick    = {
                            expandedCategory =
                                if (expandedCategory == category.title) null
                                else category.title
                        }
                    )
                    if (index < categories.lastIndex) {
                        HorizontalDivider(
                            modifier  = Modifier.padding(horizontal = 20.dp),
                            color     = DividerColor,
                            thickness = 0.5.dp
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Popular Articles label ────────────────────────────────────────
            Text(
                text          = "POPULAR ARTICLES",
                color         = CyanPrimary,
                fontSize      = 11.sp,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier      = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(12.dp))

            // ── Article rows ──────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BgCard)
            ) {
                articles.forEachIndexed { index, article ->
                    ArticleRow(
                        article    = article,
                        isExpanded = expandedArticle == article.title,
                        onClick    = {
                            expandedArticle =
                                if (expandedArticle == article.title) null
                                else article.title
                        }
                    )
                    if (index < articles.lastIndex) {
                        HorizontalDivider(
                            modifier  = Modifier.padding(horizontal = 20.dp),
                            color     = DividerColor,
                            thickness = 0.5.dp
                        )
                    }
                }
            }

            Spacer(Modifier.height(200.dp))
        }

        // ── FAB chat ─────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 120.dp)
                .size(72.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                CyanPrimary.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(CyanPrimary)
                    .clickable { /* próximamente */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.Chat,
                    contentDescription = "Chat",
                    tint               = BgDeep,
                    modifier           = Modifier.size(24.dp)
                )
            }
        }

        // ── Bottom Nav ────────────────────────────────────────────────────────
        BottomNavBar(
            selected = BottomNavDestination.SUPPORT,
            onItemClick = onNavigate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ── Category Row ──────────────────────────────────────────────────────────────
@Composable
private fun CategoryRow(
    category: SupportCategory,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyanGlow),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = category.icon,
                    contentDescription = null,
                    tint               = CyanPrimary,
                    modifier           = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = category.title,
                    color      = TextPrimary,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text     = category.subtitle,
                    color    = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector        = if (isExpanded) Icons.Default.KeyboardArrowUp
                                     else Icons.Default.ChevronRight,
                contentDescription = null,
                tint               = TextSecondary,
                modifier           = Modifier.size(18.dp)
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter   = expandVertically() + fadeIn(),
            exit    = shrinkVertically() + fadeOut()
        ) {
            Text(
                text     = category.content,
                color    = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 68.dp, end = 16.dp, bottom = 14.dp),
                lineHeight = 20.sp
            )
        }
    }
}

// ── Article Row ───────────────────────────────────────────────────────────────
@Composable
private fun ArticleRow(
    article: PopularArticle,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text       = article.title,
                color      = TextPrimary,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier   = Modifier.weight(1f)
            )
            Icon(
                imageVector        = if (isExpanded) Icons.Default.KeyboardArrowUp
                                     else Icons.Default.ChevronRight,
                contentDescription = null,
                tint               = CyanPrimary,
                modifier           = Modifier.size(18.dp)
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter   = expandVertically() + fadeIn(),
            exit    = shrinkVertically() + fadeOut()
        ) {
            Text(
                text     = article.content,
                color    = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
                lineHeight = 20.sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080E1A)
@Composable
fun SupportScreenPreview() {
    SupportScreen()
}
