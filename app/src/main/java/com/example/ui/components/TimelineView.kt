package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AudioTrackItem
import com.example.model.TextTrackItem
import com.example.model.VideoClip
import com.example.ui.theme.CapCutBackground
import com.example.ui.theme.CapCutSurfaceVariant
import com.example.ui.theme.CapCutTeal
import com.example.ui.theme.CapCutTextDisabled
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary
import com.example.viewmodel.VideoEditorViewModel

@Composable
fun TimelineView(
  currentTimeMs: Long,
  totalDurationMs: Long,
  isMuted: Boolean,
  clips: List<VideoClip>,
  audioTracks: List<AudioTrackItem>,
  textTracks: List<TextTrackItem>,
  selectedClipId: String?,
  onMuteToggle: () -> Unit,
  onCoverClick: () -> Unit,
  onClipSelect: (String) -> Unit,
  onAddMediaClick: () -> Unit,
  onAddAudioClick: () -> Unit,
  onAddTextClick: () -> Unit,
  onScrubRelative: (Long) -> Unit,
  onSeekTo: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  // Timeline scale: 60 dp per second (1000 ms)
  val dpPerSecond = 60f
  val trackScrollState = rememberScrollState()

  // Track header width
  val headerWidth = 110.dp

  // When playing, optionally auto-scroll to keep playhead centered
  LaunchedEffect(currentTimeMs) {
    val scrollTarget = ((currentTimeMs / 1000f) * dpPerSecond - 120f).coerceAtLeast(0f)
    if (trackScrollState.value != scrollTarget.toInt()) {
      trackScrollState.scrollTo(scrollTarget.toInt())
    }
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(Color(0xFF141518))
  ) {
    // 1. Timecode & Ruler Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(28.dp)
        .background(Color(0xFF141518))
        .padding(horizontal = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Monospace time: "00:00 / 00:28"
      Row(
        modifier = Modifier.width(headerWidth),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = VideoEditorViewModel.formatTime(currentTimeMs),
          color = CapCutTextPrimary,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = " / " + VideoEditorViewModel.formatTime(totalDurationMs),
          color = CapCutTextSecondary,
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace
        )
      }

      // Ruler ticks
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(28.dp)
          .horizontalScroll(trackScrollState, enabled = false)
      ) {
        Row(
          modifier = Modifier.padding(start = 16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
          val secondsCount = (totalDurationMs / 1000).toInt() + 4
          for (sec in 0..secondsCount step 2) {
            val min = sec / 60
            val s = sec % 60
            val timeText = "%02d:%02d".format(min, s)

            Text(
              text = timeText,
              color = CapCutTextSecondary,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "·",
              color = CapCutTextDisabled,
              fontSize = 14.sp
            )
          }
        }
      }
    }

    // 2. Multi-track Timeline Area with Fixed Left Column and Scrollable Right Area
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
    ) {
      Row(modifier = Modifier.fillMaxSize()) {
        // Left Column: Sticky controls ("Mute clip", "Cover", Track badges)
        Column(
          modifier = Modifier
            .width(headerWidth)
            .fillMaxHeight()
            .background(Color(0xFF141518))
            .padding(vertical = 4.dp, horizontal = 8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Top controls row: Mute clip + Cover
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Mute clip
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clickable { onMuteToggle() }
                .padding(4.dp)
                .testTag("timeline_mute_clip_button")
            ) {
              Icon(
                imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                contentDescription = "Mute clip",
                tint = if (isMuted) CapCutTeal else CapCutTextPrimary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Mute\nclip",
                color = if (isMuted) CapCutTeal else CapCutTextSecondary,
                fontSize = 9.sp,
                lineHeight = 10.sp,
                fontWeight = FontWeight.Medium
              )
            }

            // Cover thumbnail
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, Color(0xFF33353D), RoundedCornerShape(6.dp))
                .clickable { onCoverClick() }
                .testTag("timeline_cover_button"),
              contentAlignment = Alignment.Center
            ) {
              Image(
                painter = painterResource(id = R.drawable.img_video_clip),
                contentDescription = "Video cover",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(Color.Black.copy(alpha = 0.35f))
              )
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Edit,
                  contentDescription = "Edit cover",
                  tint = Color.White,
                  modifier = Modifier.size(12.dp)
                )
                Text(
                  text = "Cover",
                  color = Color.White,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          // Small track badges aligned with secondary tracks
          Spacer(modifier = Modifier.height(2.dp))

          // Audio track badge
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFF22242B)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Audiotrack,
              contentDescription = "Audio track",
              tint = CapCutTextSecondary,
              modifier = Modifier.size(14.dp)
            )
          }

          // Text track badge
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFF22242B)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.TextFields,
              contentDescription = "Text track",
              tint = CapCutTextSecondary,
              modifier = Modifier.size(14.dp)
            )
          }
        }

        // Right Column: Scrollable Tracks (Filmstrip, Audio Track, Text Track)
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .pointerInput(Unit) {
              detectDragGestures { change, dragAmount ->
                change.consume()
                // Convert px drag into ms scrub
                val msDelta = (-dragAmount.x / dpPerSecond * 1000f).toLong()
                onScrubRelative(msDelta)
              }
            }
        ) {
          Row(
            modifier = Modifier
              .fillMaxSize()
              .horizontalScroll(trackScrollState)
              .padding(start = 12.dp, end = 200.dp),
            verticalAlignment = Alignment.Top
          ) {
            Column(
              verticalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.padding(vertical = 4.dp)
            ) {
              // 1. VIDEO TRACK (Filmstrip)
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                clips.forEach { clip ->
                  val clipWidthDp = ((clip.durationMs / 1000f) * dpPerSecond).dp.coerceAtLeast(60.dp)
                  val isSelected = clip.id == selectedClipId

                  Box(
                    modifier = Modifier
                      .width(clipWidthDp)
                      .height(50.dp)
                      .clip(RoundedCornerShape(6.dp))
                      .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Color.White else Color(0xFF2E313A),
                        shape = RoundedCornerShape(6.dp)
                      )
                      .clickable { onClipSelect(clip.id) }
                      .testTag("timeline_clip_${clip.id}")
                  ) {
                    // Repeating frame thumbnails in the filmstrip
                    Row(modifier = Modifier.fillMaxSize()) {
                      val frameCount = ((clip.durationMs / 1000f) / 3f).toInt().coerceAtLeast(1)
                      for (i in 0 until frameCount) {
                        Image(
                          painter = painterResource(id = clip.thumbnailRes),
                          contentDescription = "Filmstrip frame",
                          modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                          contentScale = ContentScale.Crop
                        )
                      }
                    }

                    // Clip header / duration tag
                    Box(
                      modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = "%.1fs".format(clip.durationMs / 1000f),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                      )
                    }

                    // Left & Right drag handles if selected
                    if (isSelected) {
                      Box(
                        modifier = Modifier
                          .align(Alignment.CenterStart)
                          .width(6.dp)
                          .fillMaxHeight()
                          .background(Color.White)
                      )
                      Box(
                        modifier = Modifier
                          .align(Alignment.CenterEnd)
                          .width(6.dp)
                          .fillMaxHeight()
                          .background(Color.White)
                      )
                    }
                  }
                }

                // Add Media (+) Button at the end of video track
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .clickable { onAddMediaClick() }
                    .testTag("timeline_add_media_button"),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add media clip",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                  )
                }
              }

              // 2. AUDIO TRACK
              if (audioTracks.isEmpty()) {
                // "+ Add audio" placeholder bar
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1E2026))
                    .clickable { onAddAudioClick() }
                    .padding(horizontal = 12.dp)
                    .testTag("timeline_add_audio_row"),
                  contentAlignment = Alignment.CenterStart
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Add,
                      contentDescription = "Add audio",
                      tint = CapCutTextSecondary,
                      modifier = Modifier.size(14.dp)
                    )
                    Text(
                      text = "Add audio",
                      color = CapCutTextSecondary,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Medium
                    )
                  }
                }
              } else {
                // Render active audio tracks
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  audioTracks.forEach { audio ->
                    val audioWidth = ((audio.durationMs / 1000f) * dpPerSecond).dp.coerceAtLeast(80.dp)
                    Box(
                      modifier = Modifier
                        .width(audioWidth)
                        .height(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF104F55))
                        .padding(horizontal = 8.dp),
                      contentAlignment = Alignment.CenterStart
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.Audiotrack,
                          contentDescription = null,
                          tint = CapCutTeal,
                          modifier = Modifier.size(12.dp)
                        )
                        Text(
                          text = audio.title,
                          color = Color.White,
                          fontSize = 10.sp,
                          fontWeight = FontWeight.SemiBold,
                          maxLines = 1
                        )
                      }
                    }
                  }
                }
              }

              // 3. TEXT TRACK
              if (textTracks.isEmpty()) {
                // "+ Add text" placeholder bar
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1E2026))
                    .clickable { onAddTextClick() }
                    .padding(horizontal = 12.dp)
                    .testTag("timeline_add_text_row"),
                  contentAlignment = Alignment.CenterStart
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Add,
                      contentDescription = "Add text",
                      tint = CapCutTextSecondary,
                      modifier = Modifier.size(14.dp)
                    )
                    Text(
                      text = "Add text",
                      color = CapCutTextSecondary,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Medium
                    )
                  }
                }
              } else {
                // Render active text / subtitle tracks
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  textTracks.forEach { item ->
                    val textWidth = (( (item.endMs - item.startMs) / 1000f) * dpPerSecond).dp.coerceAtLeast(60.dp)
                    Box(
                      modifier = Modifier
                        .width(textWidth)
                        .height(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF5A189A))
                        .padding(horizontal = 8.dp),
                      contentAlignment = Alignment.CenterStart
                    ) {
                      Text(
                        text = item.text,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                      )
                    }
                  }
                }
              }
            }
          }

          // VERTICAL WHITE PLAYHEAD NEEDLE (Center Playhead Indicator)
          Box(
            modifier = Modifier
              .align(Alignment.CenterStart)
              .offset(x = 18.dp)
              .width(2.dp)
              .fillMaxHeight()
              .background(Color.White)
          ) {
            // White diamond or triangle anchor at top of needle
            Box(
              modifier = Modifier
                .align(Alignment.TopCenter)
                .size(6.dp)
                .background(Color.White, CircleShape)
            )
          }
        }
      }
    }
  }
}
