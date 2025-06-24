package com.example.kylesemporium.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val BlackGoldColorScheme = darkColorScheme(
    primary = Color(0xFFFFD700), // Gold
    onPrimary = Color(0xFF000000), // Black
    secondary = Color(0xFF1C2526), // Dark Gray
    onSecondary = Color(0xFFFFD700), // Gold
    background = Color(0xFF121212), // Black
    onBackground = Color(0xFFE0E0E0), // Light Gray
    surface = Color(0xFF1E1E1E), // Darker Black
    onSurface = Color(0xFFE0E0E0), // Light Gray
    error = Color(0xFFCF6679), // Material3 default error
    onError = Color(0xFF000000) // Black
)

private val AppTypography = androidx.compose.material3.Typography(
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.5.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.4.sp
    )
)

@Composable
fun KylesEmporiumTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BlackGoldColorScheme,
        typography = AppTypography,
        content = content
    )
}