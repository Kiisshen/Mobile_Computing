package com.example.hw1_mobile_computing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hw1_mobile_computing.ui.theme.HW1_Mobile_computingTheme
import kotlinx.serialization.Serializable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

@Serializable object Main
@Serializable object More
@Serializable object Change
@Serializable data class GalleryItem(val title: String, val imageUri: String)

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var shakeDetector: ShakeDetect? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        createNotificationChannel(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        shakeDetector = ShakeDetect(this) {
            sendShakeNotification(this)
        }

        setContent {

            HW1_Mobile_computingTheme {

                var splashAnimFinished by remember { mutableStateOf(true) }

                if (splashAnimFinished) {
                    StartingSplashScreen {
                        splashAnimFinished = false
                    }
                } else {
                    AppWithNavigator(
                        onEnableSensor = {
                            startListening()
                        }
                    )
                }
            }
        }
    }
    private fun startListening() {
        accelerometer?.let {
            sensorManager.registerListener(shakeDetector, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(shakeDetector)
    }
}

@Composable
fun AppWithNavigator(onEnableSensor: () -> Unit) {

    val navController = rememberNavController()

    NavHost(navController, startDestination = Main) {

        composable<Main> {
            MainScreen(
                onNavigateToMore = {
                    navController.navigate(More)
                },
                onNavigateToChangeInfo = {
                    navController.navigate(Change)
                },
                onEnableSensor = onEnableSensor
            )
        }

        composable<More> {
            MoreScreen { navController.popBackStack() }
        }

        composable<Change> {
            ChangeInfoScreen { navController.popBackStack() }
        }
    }
}