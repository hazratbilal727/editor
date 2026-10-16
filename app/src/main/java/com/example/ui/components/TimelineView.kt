package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioTrackItem
import com.example.model.TextTrackItem
import com.example.model.TransitionType
import com.example.model.VideoClip
import com.example.ui.theme.CapCutAudioTrack
import com.example.ui.theme.CapCutAudioWave
import com.example.ui.theme.CapCutPlayheadLine
import com.example.ui.theme.CapCutPurple
import com.example.ui.theme.CapCutSurface
import com.example.ui.theme.CapCutTeal
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary
import com.example.ui.theme.CapCutTextTrack
import com.example.ui.theme.CapCutTimelineBg
import com.example.ui.theme.CapCutTrackBorder
import com.example.viewmodel.EditorUiState

private const val PIXELS_PER_SECOND = 80f // 80dp per second

@Composable
fun TimelineView(
  uiState: EditorUiState,
  onSeekTo: (Long) -> Unit,
  onScrubRelative: (Long) -> Unit,
  onSelectClip: (String) -> Unit,
  onDeselectClip: () -> Unit,
  onOpenTransition: (String) -> Unit,
  onAppendMediaClick: () -> Unit,
  onTogglePlayPause: () -> Unit,
  onToggleMute: () -> Unit,
  onRemoveAudio: (String) -> Unit,
  onRemoveText: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  // Sync scroll position with currentTimeMs
  LaunchedEffect(uiState.currentTimeMs) {
    val targetPx = ((uiState.currentTimeMs / 1000f) * PIXELS_PER_SECOND * 2).toInt()
    if (!scrollState.isScrollInProgress && kotlin.math.abs(scrollState.value - targetPx) > 50) {
      scrollState.scrollTo(targetPx)
    }
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(CapCutTimelineBg)
      .padding(vertical = 4.dp)
  ) {
    // 1. Controls Header: Time code + Mute toggle + Play/Pause button
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Mute project button
        Box(
          modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(if (uiState.isMuted) CapCutTeal else CapCutSurface)
            .clickable { onToggleMute() }
            .testTag("timeline_mute_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (uiState.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
            contentDescription = "Mute Project",
            tint = if (uiState.isMuted) Color.Black else CapCutTextPrimary,
            modifier = Modifier.size(16.dp)
          )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = formatTime(uiState.currentTimeMs),
          color = CapCutTextPrimary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
      }

      // Play / Pause round pill
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(CapCutTeal)
          .clickable { onTogglePlayPause() }
          .testTag("timeline_play_pause_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = "Play/Pause",
          tint = Color.Black,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    // 2. Timeline Canvas with Playhead Line
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .pointerInput(Unit) {
          detectDragGestures { change, dragAmount ->
            change.consume()
            val deltaMs = (-dragAmount.x / PIXELS_PER_SECOND * 1000f).toLong()
            onScrubRelative(deltaMs)
          }
        }
    ) {
      // Horizontal Scrollable Tracks
      Row(
        modifier = Modifier
          .fillMaxSize()
          .horizontalScroll(scrollState)
          .padding(start = 160.dp, end = 250.dp)
      ) {
        Column(modifier = Modifier.fillMaxHeight()) {
          // Time Ruler (Tick marks)
          TimeRuler(totalDurationMs = uiState.totalDurationMs)

          Spacer(modifier = Modifier.height(6.dp))

          // Track 1: Main Video Clips
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(68.dp)
          ) {
            uiState.clips.forEachIndexed { index, clip ->
              val clipWidth = ((clip.durationMs / 1000f) * PIXELS_PER_SECOND).dp.coerceAtLeast(60.dp)
              val isSelected = clip.id == uiState.selectedClipId

              // Video Clip Item
              Box(
                modifier = Modifier
                  .width(clipWidth)
                  .fillMaxHeight()
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isSelected) CapCutTeal.copy(alpha = 0.25f) else CapCutSurface)
                  .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color.White else CapCutTrackBorder,
                    shape = RoundedCornerShape(6.dp)
                  )
                  .clickable { onSelectClip(clip.id) }
                  .padding(horizontal = 6.dp, vertical = 4.dp)
                  .testTag("timeline_clip_${clip.id}")
              ) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                  Text(
                    text = clip.title,
                    color = CapCutTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = String.format("%.1fs", clip.durationMs / 1000f),
                      color = CapCutTextSecondary,
                      fontSize = 10.sp
                    )
                    if (clip.speed != 1.0f) {
                      Text(
                        text = "${clip.speed}x",
                        color = CapCutTeal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
              }

              // Transition Icon button between clips
              if (index < uiState.clips.size - 1) {
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (clip.transitionAfter != TransitionType.NONE) CapCutTeal else CapCutTrackBorder)
                    .clickable { onOpenTransition(clip.id) }
                    .testTag("transition_button_${clip.id}"),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.AutoFixHigh,
                    contentDescription = "Transition",
                    tint = if (clip.transitionAfter != TransitionType.NONE) Color.Black else CapCutTextSecondary,
                    modifier = Modifier.size(14.dp)
                  )
                }
              }
            }

            // "+" Append Media Button on track
            Box(
              modifier = Modifier
                .padding(start = 12.dp)
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CapCutSurface)
                .border(1.dp, CapCutTeal, RoundedCornerShape(8.dp))
                .clickable { onAppendMediaClick() }
                .testTag("timeline_append_media_button"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Media",
                tint = CapCutTeal,
                modifier = Modifier.size(22.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Track 2: Audio Tracks
          if (uiState.audioTracks.isNotEmpty()) {
            Row(modifier = Modifier.height(30.dp), verticalAlignment = Alignment.CenterVertically) {
              uiState.audioTracks.forEach { audio ->
                val audioWidth = ((audio.durationMs / 1000f) * PIXELS_PER_SECOND).dp.coerceAtLeast(60.dp)
                Box(
                  modifier = Modifier
                    .width(audioWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(CapCutAudioTrack)
                    .border(1.dp, CapCutAudioWave, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp),
                  contentAlignment = Alignment.CenterStart
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Audiotrack, contentDescription = null, tint = CapCutAudioWave, modifier = Modifier.size(12.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(text = audio.title, color = CapCutTextPrimary, fontSize = 10.sp, maxLines = 1)
                    }
                    IconButton(onClick = { onRemoveAudio(audio.id) }, modifier = Modifier.size(16.dp)) {
                      Icon(Icons.Default.Close, contentDescription = "Delete Audio", tint = CapCutTextSecondary, modifier = Modifier.size(12.dp))
                    }
                  }
                }
                Spacer(modifier = Modifier.width(4.dp))
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Track 3: Text Tracks
          if (uiState.textTracks.isNotEmpty()) {
            Row(modifier = Modifier.height(28.dp), verticalAlignment = Alignment.CenterVertically) {
              uiState.textTracks.forEach { textTrack ->
                val textWidth = ((textTrack.durationMs / 1000f) * PIXELS_PER_SECOND).dp.coerceAtLeast(50.dp)
                Box(
                  modifier = Modifier
                    .width(textWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(CapCutTextTrack)
                    .border(1.dp, CapCutPurple, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp),
                  contentAlignment = Alignment.CenterStart
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.TextFields, contentDescription = null, tint = CapCutPurple, modifier = Modifier.size(12.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(text = textTrack.text, color = CapCutTextPrimary, fontSize = 10.sp, maxLines = 1)
                    }
                    IconButton(onClick = { onRemoveText(textTrack.id) }, modifier = Modifier.size(16.dp)) {
                      Icon(Icons.Default.Close, contentDescription = "Delete Text", tint = CapCutTextSecondary, modifier = Modifier.size(12.dp))
                    }
                  }
                }
                Spacer(modifier = Modifier.width(4.dp))
              }
            }
          }
        }
      }

      // Vertical White Playhead Line fixed at center (or start offset)
      Box(
        modifier = Modifier
          .align(Alignment.CenterStart)
          .padding(start = 160.dp)
          .width(2.dp)
          .fillMaxHeight()
          .background(CapCutPlayheadLine)
          .testTag("timeline_playhead_cursor")
      )
    }
  }
}

@Composable
private fun TimeRuler(totalDurationMs: Long) {
  val seconds = ((totalDurationMs / 1000L) + 5).toInt()
  Row(
    modifier = Modifier.height(18.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    for (sec in 0..seconds) {
      Box(
        modifier = Modifier.width(PIXELS_PER_SECOND.dp),
        contentAlignment = Alignment.CenterStart
      ) {
        Text(
          text = String.format("%02d:00", sec),
          color = CapCutTextSecondary.copy(alpha = 0.7f),
          fontSize = 9.sp
        )
      }
    }
  }
}
