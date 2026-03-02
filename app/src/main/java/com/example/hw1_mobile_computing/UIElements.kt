package com.example.hw1_mobile_computing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EpicBackground(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(ArmorGrey, Color(0xFF08090A))))) {
        content()
    }
}

@Composable
fun EpicButton(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, NeonGreen),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Box(modifier = Modifier.background(PlateGrey.copy(alpha = 0.5f)).padding(horizontal = 24.dp, vertical = 12.dp)) {
            Text(text = text.uppercase(), color = NeonGreen, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun GreetingBold(name: String) {
    Text(text = name, color = NeonGreen, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center)
}

@Composable
fun TextNormal(text: String) {
    Text(text = text, color = CyberWhite, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 12.dp))
}