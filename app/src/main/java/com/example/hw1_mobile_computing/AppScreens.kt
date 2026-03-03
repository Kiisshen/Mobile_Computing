package com.example.hw1_mobile_computing

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun StartingSplashScreen(onFinished: () -> Unit) {

    var startAnimation by remember { mutableStateOf(false) }

    val scale = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(1500)
    )

    val alpha = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1500)
    )

    LaunchedEffect(true) {
        startAnimation = true
        delay(2000)
        onFinished()
    }

    EpicBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.android_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        this.alpha = alpha.value
                    }
            )
        }
    }
}

@Composable
fun MainScreen(
    onNavigateToMore: () -> Unit,
    onNavigateToChangeInfo: () -> Unit,
    onEnableSensor: () -> Unit
) {
    val context = LocalContext.current
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                onEnableSensor()
            }
        }
    var items by remember { mutableStateOf(loadGalleryItems(context)) }

    LaunchedEffect(true) {
        items = loadGalleryItems(context)
    }

    EpicBackground {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            IconButton(
                onClick = onNavigateToChangeInfo,
                modifier = Modifier
                    .padding(top = 40.dp, start = 16.dp)
                    .background(PlateGrey, RoundedCornerShape(50))
            ) {
                Text("⚙", color = NeonGreen, fontSize = 20.sp)
            }

            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                TextNormal("THE EPIC GALLERY OF")
                GreetingBold("THE EPIC ANDROID MAN")

                Spacer(Modifier.height(12.dp))

                Image(
                    painter = painterResource(R.drawable.epicandroid),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            2.dp,
                            NeonGreen.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.height(32.dp))

                GreetingBold("EPIC IMAGES!")

                items.forEach { item ->

                    Column(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        TextNormal(item.title.uppercase())

                        Image(
                            painter = rememberAsyncImagePainter(item.imageUri.toUri()),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    2.dp,
                                    Color.Gray,
                                    RoundedCornerShape(8.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(Modifier.height(15.dp))
                EpicButton("READ EPIC TEXT!") { onNavigateToMore() }

                Spacer(Modifier.height(15.dp))

                TextNormal(
                    "The Epic Android man is so fast! " +
                            "See if you have any chance to compare by allowing the accelerometer below."
                )

                EpicButton("ENABLE ACCELEROMETER") {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                Spacer(Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun ChangeInfoScreen(onNavigateBack: () -> Unit) {

    val context = LocalContext.current

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var titleText by remember { mutableStateOf("") }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                selectedImageUri = cameraUri
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                val file = File(context.cacheDir, "temp_camera.jpg")
                cameraUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                cameraLauncher.launch(cameraUri!!)
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
        }

    EpicBackground {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            GreetingBold("ADD IMAGE")

            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Row {
                EpicButton("CAMERA") {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }

                Spacer(Modifier.width(10.dp))

                EpicButton("GALLERY") {
                    galleryLauncher.launch("image/*")
                }
            }

            selectedImageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
            }

            EpicButton("SAVE") {

                if (selectedImageUri != null && titleText.isNotBlank()) {

                    val permanentUri =
                        makeImagePermanent(context, selectedImageUri!!)

                    val list = loadGalleryItems(context).toMutableList()
                    list.add(GalleryItem(titleText, permanentUri.toString()))

                    saveGalleryItems(context, list)

                    onNavigateBack()
                }
            }

            EpicButton("RETURN") {
                onNavigateBack()
            }
        }
    }
}

@Composable
fun MoreScreen(onNavigateBack: () -> Unit) {

    EpicBackground {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            GreetingBold("EPIC STORY")

            TextNormal(
                "One morning, when Gregor Samsa woke from troubled dreams..."
            )

            Spacer(Modifier.height(24.dp))

            EpicButton("RETURN") {
                onNavigateBack()
            }
        }
    }
}