package com.example.model

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainTool(
  val id: String,
  val label: String,
  val icon: ImageVector,
  val badge: String? = null
) {
  EDIT("edit", "Edit", Icons.Default.ContentCut),
  AUDIO("audio", "Audio", Icons.Default.Audiotrack),
  TEXT("text", "Text", Icons.Default.TextFields),
  EFFECTS("effects", "Effects", Icons.Default.AutoAwesome),
  OVERLAY("overlay", "Overlay", Icons.Default.Layers),
  CAPTIONS("captions", "Captions", Icons.Default.Subtitles),
  FILTERS("filters", "Filters", Icons.Default.ColorLens),
  ADJUST("adjust", "Adjust", Icons.Default.Tune),
  STICKERS("stickers", "Stickers", Icons.Default.EmojiEmotions),
  GENERATE_MEDIA("generate_media", "Generate media", Icons.Default.AutoFixHigh, badge = "AI"),
  AI_AVATAR("ai_avatar", "AI avatar", Icons.Default.Face, badge = "PRO"),
  ASPECT_RATIO("aspect_ratio", "Aspect ratio", Icons.Default.AspectRatio),
  BACKGROUND("background", "Background", Icons.Default.Wallpaper)
}

enum class SubMenuType {
  NONE,
  CLIP_EDIT,
  SPEED,
  VOLUME,
  AUDIO,
  TEXT,
  FILTERS,
  ADJUST,
  ASPECT_RATIO,
  EFFECTS
}

data class VideoClip(
  val id: String,
  val title: String,
  val startMs: Long,
  val endMs: Long,
  val originalDurationMs: Long,
  @DrawableRes val thumbnailRes: Int,
  val speed: Float = 1.0f,
  val volume: Float = 1.0f,
  val isSelected: Boolean = false
) {
  val durationMs: Long get() = (endMs - startMs)
}

data class AudioTrackItem(
  val id: String,
  val title: String,
  val startMs: Long,
  val endMs: Long,
  val volume: Float = 1.0f
) {
  val durationMs: Long get() = (endMs - startMs)
}

data class TextTrackItem(
  val id: String,
  val text: String,
  val startMs: Long,
  val endMs: Long
)

data class FilterItem(
  val id: String,
  val name: String,
  val filterFactor: Float = 0f,
  val colorTone: Long = 0xFFFFFFFF
)

data class AdjustSettings(
  val brightness: Float = 0f, // -50 to +50
  val contrast: Float = 0f,   // -50 to +50
  val saturation: Float = 0f, // -50 to +50
  val sharpness: Float = 0f,  // 0 to 100
  val vignette: Float = 0f    // 0 to 100
)

enum class AspectRatioOption(val label: String, val ratio: Float, val displayTag: String) {
  RATIO_9_16("9:16", 9f / 16f, "TikTok"),
  RATIO_16_9("16:9", 16f / 9f, "YouTube"),
  RATIO_1_1("1:1", 1f, "Instagram"),
  RATIO_4_3("4:3", 4f / 3f, "Standard"),
  RATIO_21_9("2.35:1", 2.35f, "Cinema")
}

data class ResolutionSetting(
  val label: String = "AI UHD",
  val fps: Int = 30,
  val smartHdr: Boolean = true,
  val estimatedSizeMb: Float = 48.5f
)

data class CapCutProject(
  val id: String,
  val title: String,
  val dateStr: String,
  val sizeStr: String,
  val durationStr: String,
  @DrawableRes val thumbnailRes: Int,
  val clipsCount: Int = 1
)
