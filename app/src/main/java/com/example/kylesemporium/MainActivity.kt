package com.example.kylesemporium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kylesemporium.ui.screens.AddProductScreen
import com.example.kylesemporium.ui.screens.ForgotPasswordScreen
import com.example.kylesemporium.ui.screens.HomeScreen
import com.example.kylesemporium.ui.screens.LoginScreen
import com.example.kylesemporium.ui.screens.PaymentScreen
import com.example.kylesemporium.ui.screens.SignUpScreen
import com.example.kylesemporium.ui.screens.SplashScreen
import com.example.kylesemporium.ui.theme.KylesEmporiumTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        setContent {
            KylesEmporiumTheme {
                Surface(
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                ) {
                    AppNavigation(auth)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(auth: FirebaseAuth) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController, auth) }
        composable("login") { LoginScreen(navController, auth) }
        composable("signup") { SignUpScreen(navController, auth) }
        composable("forgot_password") { ForgotPasswordScreen(navController, auth) }
        composable("home") { HomeScreen(navController, auth) }
        composable("add_product") { AddProductScreen(navController) }
        composable("payment/{productId}") { PaymentScreen(navController, it.arguments?.getString("productId") ?: "") }
    }
}