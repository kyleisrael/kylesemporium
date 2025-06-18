package com.example.kylesemporium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kylesemporium.app.ui.AddProductScreen
import com.kylesemporium.app.ui.ForgotPasswordScreen
import com.example.kylesemporium.ui.screens.HomeScreen
import com.kylesemporium.app.ui.LoginScreen
import com.kylesemporium.app.ui.PaymentScreen
import com.example.kylesemporium.ui.screens.SignUpScreen
import com.kylesemporium.app.ui.theme.KylesEmporiumTheme

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        setContent {
            KylesEmporiumTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
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
    val currentUser by remember { mutableStateOf(auth.currentUser) }
    NavHost(navController = navController, startDestination = if (currentUser == null) "login" else "home") {
        composable("login") { LoginScreen(navController, auth) }
        composable("signup") { SignUpScreen(navController, auth) }
        composable("forgot_password") { ForgotPasswordScreen(navController, auth) }
        composable("home") { HomeScreen(navController, auth) }
        composable("add_product") { AddProductScreen(navController) }
        composable("payment/{productId}") { PaymentScreen(navController, it.arguments?.getString("productId") ?: "") }
    }
}