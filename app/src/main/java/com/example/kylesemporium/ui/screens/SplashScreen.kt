package com.example.kylesemporium.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.example.kylesemporium.ui.theme.KylesEmporiumTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, auth: FirebaseAuth) {
    val scale = remember { Animatable(initialValue = 0.0f) }
    val alpha = remember { Animatable(initialValue = 0.0f) }

    // Animation effect
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 800)
        )
        alpha.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 800)
        )
        delay(2000) // 2-second delay
        val destination = if (auth.currentUser == null) "login" else "home"
        navController.navigate(destination) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }

    KylesEmporiumTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Kyle's Emporium",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value)
            )
        }
    }
}