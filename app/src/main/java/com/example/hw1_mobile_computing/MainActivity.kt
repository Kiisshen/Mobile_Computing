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

@Serializable
object Main

@Serializable
object More

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
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun ImageCard(backgroundColor: Color) {
    Image(
        painter = painterResource(R.drawable.android_logo),
        contentDescription = "Android logo picture",
        modifier = Modifier.padding(top=40.dp).background(color=backgroundColor)
    )
}

@Composable
fun GreetingBold(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
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
            color = Color.White
        )
    }
}

@Composable
fun MainScreen(
    onNavigateToMore: () -> Unit
) {
    HW1_Mobile_computingTheme {

        var imageColor by remember { mutableStateOf(Color.Red) }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(all = 80.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Greeting("Android")
            GreetingBold("Bold Android")
            ImageCard(imageColor)
            Spacer(modifier = Modifier.padding(top = 100.dp))
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
            Greeting(
                "One morning, when Gregor Samsa woke from troubled dreams, he found himself transformed in his bed into a horrible vermin.\n" +
                        "\n" +
                        "He lay on his armour-like back, and if he lifted his head a little he could see his brown belly, slightly domed and divided by arches into stiff sections.\n" +
                        "\n" +
                        "The bedding was hardly able to cover it and seemed ready to slide off any moment.\n" +
                        "\n" +
                        "His many legs, pitifully "
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Button("Go Back to Main Page.") {
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
    }
}
