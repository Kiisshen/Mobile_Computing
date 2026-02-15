package com.example.hw1_mobile_computing

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.hw1_mobile_computing.ui.theme.HW1_Mobile_computingTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import androidx.compose.foundation.layout.height
import android.content.Context
import androidx.compose.material3.OutlinedTextField
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import coil3.compose.rememberAsyncImagePainter
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.app.PendingIntent
import android.app.TaskStackBuilder

@Serializable
object Main

@Serializable
object More

@Serializable
object Change

const val CHANNEL_ID = "shake_notification_channel"
const val NOTIFICATION_ID = 1
const val PREFS_NAME = "com.example.hw1_mobile_computing.PREFERENCES"
const val KEY_MAIN_TITLE = "main_title"
const val KEY_MAIN_IMAGE_URI  = "main_image_uri"

private var lastX = 0f
private var lastY = 0f
private var lastZ = 0f
private var isFirstReading = true
private var lastShakeTime: Long = 0

fun saveMainTitle(context: Context, title: String) {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString(KEY_MAIN_TITLE, title)
        apply()
    }
}

fun loadMainTitle(context: Context): String {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return sharedPref.getString(KEY_MAIN_TITLE, "Modifiable Android Text!") ?: "Modifiable Android Text!"
}

fun saveMainImageUri(context: Context, uri: String) {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString(KEY_MAIN_IMAGE_URI, uri)
        apply()
    }
}

fun loadMainImageUri(context: Context): String? {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return sharedPref.getString(KEY_MAIN_IMAGE_URI, null)
}

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            HW1_Mobile_computingTheme {
                AppWithNavigator(
                    onEnableSensor = {
                        sendShakeNotification(this)
                        startListening()
                    }
                )
            }
        }
    }

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (isShake(event)) {
                sendShakeNotification(this@MainActivity)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private fun startListening() {
        accelerometer?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(sensorListener)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier
    )
}

@Composable
fun ImageCard(backgroundColor: Color) {
    Image(
        painter = painterResource(R.drawable.android_logo),
        contentDescription = "Android logo picture",
        modifier = Modifier.padding(top=40.dp).background(color=backgroundColor).clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun GreetingBold(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        fontSize = TextUnit(5.0f, TextUnitType.Em)
    )
}

@Composable
fun Button(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Green)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = Color.Black
        )
    }
}

@Composable
fun SettingsButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(53.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(Color.DarkGray)
            .clickable(onClick = onClick)
            .padding(
                start = 10.dp,
                end=10.dp,
                bottom=2.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = TextUnit(8.0f, TextUnitType.Em)
        )
    }
}

fun isShake(event: SensorEvent): Boolean {
    val x = event.values[0]
    val y = event.values[1]
    val z = event.values[2]

    if (isFirstReading) {
        lastX = x
        lastY = y
        lastZ = z
        isFirstReading = false
        return false
    }

    val deltaX = Math.abs(lastX - x)
    val deltaY = Math.abs(lastY - y)
    val deltaZ = Math.abs(lastZ - z)

    lastX = x
    lastY = y
    lastZ = z

    val currentTime = System.currentTimeMillis()

    if ((deltaX > 0.5f || deltaY > 0.5f || deltaZ > 0.5f) &&
        currentTime - lastShakeTime > 1000) {

        lastShakeTime = currentTime
        return true
    }

    return false
}

private fun sendShakeNotification(context: Context) {
    val resultIntent = Intent(context, MainActivity::class.java)

    val resultPendingIntent: PendingIntent? =
        TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.android_logo)
        .setContentTitle("Shake Detected!")
        .setContentText("You moved your device quickly.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(resultPendingIntent)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as ComponentActivity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
            return@with
        }
        notify(NOTIFICATION_ID, builder.build())
    }
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.channel_name)
        val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

@Composable
fun MainScreen(
    onNavigateToMore: () -> Unit,
    onNavigateToChangeInfo: () -> Unit,
    onEnableSensor: () -> Unit
) {
    HW1_Mobile_computingTheme {

        val context = androidx.compose.ui.platform.LocalContext.current
        var imageColor by remember { mutableStateOf(Color.Red) }
        var mainTitle by remember { mutableStateOf(loadMainTitle(context)) }
        var mainImageUri by remember {
            mutableStateOf(if (loadMainImageUri(context) != null) Uri.parse(loadMainImageUri(context)) else null)
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()
                ).padding(
                    bottom = 20.dp
                )
        ) {
            Column(
                modifier = Modifier.padding(
                    top = 25.dp,
                    start = 10.dp,
                    end = 0.dp
                )
            ){
                SettingsButton("\u2699") {
                    onNavigateToChangeInfo()
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(
                    start = 10.dp,
                    end = 20.dp,
                    bottom = 20.dp
                )
            ){
                Greeting("AndroidFanPage")
                GreetingBold(mainTitle)
                if (mainImageUri != null) {
                    val painter = rememberAsyncImagePainter(
                        model = mainImageUri
                    )
                    Image(
                        painter = painter,
                        contentDescription = "image",
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    ImageCard(imageColor)
                }
                Spacer(modifier = Modifier.padding(top = 40.dp))
                Greeting(
                    "One morning, when Gregor Samsa woke from troubled dreams, he found himself transformed in his bed into a horrible vermin.\n" +
                            "\n" +
                            "He lay on his armour-like back, and if he lifted his head a little he could see his brown belly, slightly domed and divided by arches into stiff sections.\n" +
                            "\n" +
                            "The bedding was hardly able to cover it and seemed ready to slide off any moment.\n" +
                            "\n" +
                            "His many legs, pitifully "
                )
                Spacer(modifier = Modifier.padding(top = 100.dp))
                Button("Click me!") {
                    imageColor = Color.Blue
                }
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Button("See more lorem ipsum!") {
                    onNavigateToMore()
                }
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Button("Allow use of accelerometer sensor.") {
                    onEnableSensor()
                }
            }
        }
    }
}

@Composable
fun MoreScreen(
    onNavigateBack: () -> Unit
) {
    HW1_Mobile_computingTheme {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(all = 80.dp)
                .verticalScroll(rememberScrollState())
        ) {
            GreetingBold("More Android Here!")
            Spacer(modifier = Modifier.padding(top = 5.dp))
            Greeting(
                "One morning, when Gregor Samsa woke from troubled dreams, he found himself transformed in his bed into a horrible vermin.\n" +
                        "\n" +
                        "He lay on his armour-like back, and if he lifted his head a little he could see his brown belly, slightly domed and divided by arches into stiff sections.\n" +
                        "\n" +
                        "The bedding was hardly able to cover it and seemed ready to slide off any moment.\n" +
                        "\n" +
                        "His many legs, pitifully "
            )
            Spacer(modifier = Modifier.padding(top = 15.dp))
            Button("Go Back to Main Page.") {
                onNavigateBack()
            }
        }
    }
}

@Composable
fun ChangeInfoScreen(
    onNavigateBack: () -> Unit
) {
    HW1_Mobile_computingTheme {
        val context = androidx.compose.ui.platform.LocalContext.current

        var text by remember {
            mutableStateOf(loadMainTitle(context))
        }

        var imageUri by remember {
            mutableStateOf(if (loadMainImageUri(context) != null) Uri.parse(loadMainImageUri(context)) else null)
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            if (uri != null) {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                imageUri = uri
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(all = 80.dp)
                .verticalScroll(rememberScrollState())
        ) {
            GreetingBold("Change Picture Here:")
            Spacer(modifier = Modifier.padding(top = 4.dp))
            Button("Pick Image") {
                launcher.launch(arrayOf("image/*"))
            }
            Spacer(modifier = Modifier.padding(top = 10.dp))
            GreetingBold("Change Title Here:")
            Spacer(modifier = Modifier.padding(top = 6.dp))
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Main screen title") },
                singleLine = true
            )
            Spacer(modifier = Modifier.padding(top = 30.dp))
            Button("Save and go back.") {
                if (imageUri != null) saveMainImageUri(context, imageUri.toString())
                saveMainTitle(context, text)
                onNavigateBack()
            }
        }
    }
}
@Composable
fun AppWithNavigator(
    onEnableSensor: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Main) {
        composable<Main> {
            MainScreen(
                onNavigateToMore = {
                    navController.navigate(route = More)
                },
                onNavigateToChangeInfo = {
                    navController.navigate(route = Change)
                },
                onEnableSensor = onEnableSensor
            )
        }
        composable<More> {
            MoreScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Change> {
            ChangeInfoScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}