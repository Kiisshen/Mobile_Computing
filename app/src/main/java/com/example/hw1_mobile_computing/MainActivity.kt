package com.example.hw1_mobile_computing

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
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil3.compose.rememberAsyncImagePainter
import androidx.lifecycle.compose.LocalLifecycleOwner
import android.content.Intent

@Serializable
object Main

@Serializable
object More

@Serializable
object Change

const val PREFS_NAME = "com.example.hw1_mobile_computing.PREFERENCES"
const val KEY_MAIN_TITLE = "main_title"
const val KEY_MAIN_IMAGE_URI  = "main_image_uri"

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HW1_Mobile_computingTheme {
                AppWithNavigator()
            }
        }
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

@Composable
fun MainScreen(
    onNavigateToMore: () -> Unit,
    onNavigateToChangeInfo: () -> Unit
) {
    HW1_Mobile_computingTheme {

        val context = androidx.compose.ui.platform.LocalContext.current
        var imageColor by remember { mutableStateOf(Color.Red) }
        var mainTitle by remember { mutableStateOf(loadMainTitle(context)) }
        var mainImageUri by remember {
            mutableStateOf(if (loadMainImageUri(context) != null) Uri.parse(loadMainImageUri(context)) else null)
        }

        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    val uriString = loadMainImageUri(context)
                    mainImageUri = if (uriString != null) {
                        Uri.parse(uriString)
                    } else {
                        null
                    }
                    mainTitle = loadMainTitle(context)
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
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
fun AppWithNavigator() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Main) {
        composable<Main> {
            MainScreen (
                onNavigateToMore = {
                    navController.navigate(route = More)
                },
                onNavigateToChangeInfo = {
                    navController.navigate(route = Change)
                }
            )
        }
        composable<More> {
            MoreScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<Change> {
            ChangeInfoScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
