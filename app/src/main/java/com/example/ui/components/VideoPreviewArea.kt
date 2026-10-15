package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AdjustSettings
import com.example.model.AspectRatioOption
import com.example.model.FilterItem
import com.example.model.TextTrackItem
import com.example.ui.theme.CapCutBackground
import com.example.ui.theme.CapCutTeal
import com.example.ui.theme.CapCutTextDisabled
import com.example.ui.theme.CapCutTextPrimary

@Composable
fun VideoPreviewArea(
  currentTimeMs: Long,
  isPlaying: Boolean,
  aspectRatio: AspectRatioOption,
  filter: FilterItem,
  filterIntensity: Float,
  adjustSettings: AdjustSettings,
  activeCaptions: List<TextTrackItem>,
  pipEnabled: Boolean,
  canUndo: Boolean,
  canRedo: Boolean,
  onPlayPauseToggle: () -> Unit,
  onFullscreenToggle: () -> Unit,
  onPipToggle: () -> Unit,
  onUndoClick: () -> Unit,
  onRedoClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  // Current active caption
  val currentCaption = activeCaptions.find { currentTimeMs in it.startMs..it.endMs }

  // Build color matrix for adjustments
  val colorMatrix = ColorMatrix().apply {
    val b = adjustSettings.brightness * 2f
    val c = 1f + (adjustSettings.contrast / 50f)
    val s = 1f + (adjustSettings.saturation / 50f)
    setToSaturation(s)
    // Apply contrast and brightness offset
    val scale = c
    val translate = b
    val array = values
    array[0] = array[0] * scale
    array[4] = array[4] + translate
    array[6] = array[6] * scale
    array[9] = array[9] + translate
    array[12] = array[12] * scale
    array[14] = array[14] + translate
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(CapCutBackground),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Video viewport container
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(horizontal = 16.dp, vertical = 4.dp),
      contentAlignment = Alignment.Center
    ) {
      // The video player screen with selected aspect ratio
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .aspectRatio(aspectRatio.ratio)
          .clip(RoundedCornerShape(4.dp))
          .background(Color.Black)
          .pointerInput(Unit) {
            detectTapGestures(onTap = { onPlayPauseToggle() })
          }
          .testTag("video_player_canvas"),
        contentAlignment = Alignment.Center
      ) {
        // Video footage preview image
        Image(
          painter = painterResource(id = R.drawable.img_video_preview),
          contentDescription = "Mecca Video Preview",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop,
          colorFilter = ColorFilter.colorMatrix(colorMatrix)
        )

        // Filter tone overlay
        if (filter.id != "none" && filterIntensity > 0f) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color(filter.colorTone).copy(alpha = filterIntensity * 0.25f))
          )
        }

        // Vignette overlay
        if (adjustSettings.vignette > 0f) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = (adjustSettings.vignette / 100f) * 0.45f))
          )
        }

        // Subtitle / Caption text overlay
        currentCaption?.let { cap ->
          Box(
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color.Black.copy(alpha = 0.65f))
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = cap.text,
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              textAlign = TextAlign.Center
            )
          }
        }

        // Play icon overlay when paused
        if (!isPlaying) {
          Box(
            modifier = Modifier
              .align(Alignment.Center)
              .size(52.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Play",
              tint = Color.White,
              modifier = Modifier.size(32.dp)
            )
          }
        }
      }
    }

    // Controls bar under video (Fullscreen, Play/Pause, PIP, Undo, Redo)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(44.dp)
        .padding(horizontal = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left: Fullscreen icon
      IconButton(
        onClick = onFullscreenToggle,
        modifier = Modifier
          .size(36.dp)
          .testTag("preview_fullscreen_button")
      ) {
        Icon(
          imageVector = Icons.Default.CropFree,
          contentDescription = "Fullscreen preview",
          tint = CapCutTextPrimary,
          modifier = Modifier.size(20.dp)
        )
      }

      // Center: Play / Pause toggle
      IconButton(
        onClick = onPlayPauseToggle,
        modifier = Modifier
          .size(38.dp)
          .testTag("preview_play_pause_button")
      ) {
        Icon(
          imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = if (isPlaying) "Pause" else "Play",
          tint = CapCutTextPrimary,
          modifier = Modifier.size(26.dp)
        )
      }

      // Right: PIP, Undo, Redo
      Row(verticalAlignment = Alignment.CenterVertically) {
        // PIP indicator button
        Box(
          modifier = Modifier
            .height(28.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable { onPipToggle() }
            .padding(horizontal = 6.dp)
            .testTag("preview_pip_button"),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Tv,
              contentDescription = "Picture in picture overlay",
              tint = if (pipEnabled) CapCutTeal else CapCutTextDisabled,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = if (pipEnabled) "ON" else "OFF",
              color = if (pipEnabled) CapCutTeal else CapCutTextDisabled,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Undo
        IconButton(
          onClick = onUndoClick,
          enabled = canUndo,
          modifier = Modifier
            .size(36.dp)
            .testTag("preview_undo_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Undo,
            contentDescription = "Undo",
            tint = if (canUndo) CapCutTextPrimary else CapCutTextDisabled,
            modifier = Modifier.size(20.dp)
          )
        }

        // Redo
        IconButton(
          onClick = onRedoClick,
          enabled = canRedo,
          modifier = Modifier
            .size(36.dp)
            .testTag("preview_redo_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Redo,
            contentDescription = "Redo",
            tint = if (canRedo) CapCutTextPrimary else CapCutTextDisabled,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}
