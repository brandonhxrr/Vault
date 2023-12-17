package com.brandonhxrr.vault

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.brandonhxrr.vault.ui.Home
import com.brandonhxrr.vault.ui.Login
import com.brandonhxrr.vault.ui.Screens
import com.brandonhxrr.vault.ui.SignUp
import com.brandonhxrr.vault.ui.Splash
import com.brandonhxrr.vault.ui.theme.VaultTheme
import com.google.firebase.auth.FirebaseAuth
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VaultTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Start()
                }
            }
        }
    }
}

@Composable
fun Start() {
    val navController = rememberNavController()
    val user = FirebaseAuth.getInstance().currentUser

    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.name
    ) {
        composable(Screens.SplashScreen.name) {
            Splash()
            val timer = object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    if (navController.currentBackStackEntry?.destination?.route == Screens.SplashScreen.name) {
                        if (user != null) {
                            navController.navigate(Screens.HomeScreen.name) {
                                popUpTo(Screens.SplashScreen.name) {
                                    inclusive = true
                                }
                            }
                        } else {
                            navController.navigate(Screens.LoginScreen.name) {
                                popUpTo(Screens.SplashScreen.name) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }
            }

            timer.start()

        }
        composable(Screens.HomeScreen.name) {
            Home()
        }

        composable(Screens.LoginScreen.name) {
            Login(navController = navController)
        }

        composable(Screens.SignUpScreen.name) {
            SignUp(navController = navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    VaultTheme {
        Start()
    }
}