package com.example.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.ui.theme.CapCutBackground
import com.example.ui.theme.CapCutSurface
import com.example.ui.theme.CapCutTeal
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary
import com.example.viewmodel.EditorUiState

@Composable
fun VideoPreviewArea(
  uiState: EditorUiState,
  onTogglePlayPause: () -> Unit,
  onToggleMute: () -> Unit,
  onToggleFullscreen: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val currentClip = uiState.currentClipAtPlayhead

  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(CapCutBackground)
      .padding(8.dp),
    contentAlignment = Alignment.Center
  ) {
    // Aspect Ratio Container
    BoxWithConstraints(
      modifier = Modifier
        .aspectRatio(uiState.aspectRatio.ratioFloat)
        .clip(RoundedCornerShape(8.dp))
        .background(Color.Black)
        .clickable { onTogglePlayPause() }
        .testTag("video_preview_canvas"),
      contentAlignment = Alignment.Center
    ) {
      // 1. Media Content (Real Video URI or Real Image URI or Aesthetic Demo Canvas)
      if (currentClip?.uriString != null) {
        val uri = Uri.parse(currentClip.uriString)
        if (currentClip.isVideo) {
          // Video Playback via AndroidView VideoView
          AndroidView(
            factory = { ctx ->
              VideoView(ctx).apply {
                setVideoURI(uri)
                setOnPreparedListener { mp ->
                  mp.isLooping = true
                  if (uiState.isMuted) mp.setVolume(0f, 0f) else mp.setVolume(currentClip.volume, currentClip.volume)
                  if (uiState.isPlaying) start()
                }
              }
            },
            update = { videoView ->
              if (uiState.isPlaying) {
                if (!videoView.isPlaying) videoView.start()
              } else {
                if (videoView.isPlaying) videoView.pause()
              }
            },
            modifier = Modifier
              .fillMaxSize()
              .rotate(currentClip.rotationDegrees)
          )
        } else {
          // Photo Content via Coil AsyncImage
          AsyncImage(
            model = uri,
            contentDescription = "Selected Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .fillMaxSize()
              .rotate(currentClip.rotationDegrees)
          )
        }
      } else {
        // Built-in Demo Canvas with gradient and motion
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              androidx.compose.ui.graphics.Brush.verticalGradient(
                colors = listOf(
                  Color(0xFF1F1C2C),
                  Color(0xFF928DAB).copy(alpha = 0.5f),
                  Color(0xFF0F2027)
                )
              )
            )
            .rotate(currentClip?.rotationDegrees ?: 0f),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = currentClip?.title ?: "No Media Selected",
              color = CapCutTextPrimary,
              fontWeight = FontWeight.SemiBold,
              fontSize = 15.sp
            )
            Text(
              text = if (currentClip?.isVideo == true) "Video Clip" else "Photo",
              color = CapCutTextSecondary,
              fontSize = 12.sp
            )
          }
        }
      }

      // 2. Filter Tone Tint Overlay
      if (uiState.selectedFilter.colorTone != 0L) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(uiState.selectedFilter.colorTone).copy(alpha = 0.35f * uiState.filterIntensity))
        )
      }

      // 3. Text Overlay Tracks active at currentTimeMs
      val activeTexts = uiState.textTracks.filter {
        uiState.currentTimeMs in it.startMs..it.endMs
      }
      activeTexts.forEach { textTrack ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.BottomCenter
        ) {
          Text(
            text = textTrack.text,
            color = try { Color(android.graphics.Color.parseColor(textTrack.colorHex)) } catch (_: Exception) { Color.White },
            fontSize = textTrack.fontSizeSp.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
              .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      // 4. Center Play Button overlay when paused
      if (!uiState.isPlaying) {
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.65f)),
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

      // 5. Bottom Controls Overlay inside preview: Mute, Time indicator, Fullscreen
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .background(Color.Black.copy(alpha = 0.45f))
          .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Mute button
        IconButton(
          onClick = onToggleMute,
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = if (uiState.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
            contentDescription = if (uiState.isMuted) "Unmute" else "Mute",
            tint = if (uiState.isMuted) CapCutTeal else Color.White,
            modifier = Modifier.size(18.dp)
          )
        }

        // Time code: 00:02 / 00:12
        Text(
          text = "${formatTime(uiState.currentTimeMs)} / ${formatTime(uiState.totalDurationMs)}",
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )

        // Fullscreen toggle
        IconButton(
          onClick = onToggleFullscreen,
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Fullscreen,
            contentDescription = "Fullscreen",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

fun formatTime(ms: Long): String {
  val totalSeconds = (ms / 1000).coerceAtLeast(0)
  val minutes = totalSeconds / 60
  val seconds = totalSeconds % 60
  return String.format("%02d:%02d", minutes, seconds)
}
