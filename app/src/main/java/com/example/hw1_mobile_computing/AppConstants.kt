package com.example.hw1_mobile_computing

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable


const val CHANNEL_ID = "shake_notification_channel"
const val NOTIFICATION_ID = 1
const val PREFS_NAME = "com.example.hw1_mobile_computing.PREFERENCES"
const val KEY_GALLERY_ITEMS = "gallery_items"
@Serializable object Main
@Serializable object More
@Serializable object Change
@Serializable data class GalleryItem(val title: String, val imageUri: String)

// Color palette generated using AI and the main image of the app as a parameter. (GPT 5.2)
val ArmorGrey = Color(0xFF121417)
val PlateGrey = Color(0xFF2A2D32)
val NeonGreen = Color(0xFF39FF14)
val CyberWhite = Color(0xFFE0E0E0)