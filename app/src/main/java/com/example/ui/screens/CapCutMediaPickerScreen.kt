package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.VideoClip
import com.example.ui.theme.CapCutBackground
import com.example.ui.theme.CapCutTeal
import com.example.ui.theme.CapCutTextDisabled
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary

data class MediaPickerItem(
  val id: String,
  val title: String,
  val durationStr: String,
  val durationMs: Long,
  @DrawableRes val drawableRes: Int? = null,
  val uri: Uri? = null,
  val isVideo: Boolean = true
)

@Composable
fun CapCutMediaPickerScreen(
  onClose: () -> Unit,
  onMediaSelected: (List<VideoClip>) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf("Videos") }
  var isHdEnabled by remember { mutableStateOf(false) }
  val selectedMediaIds = remember { mutableStateListOf<String>() }

  // Initial preset media items matching the screenshot
  val mediaList = remember {
    mutableStateListOf(
      MediaPickerItem("m1", "Mecca Courtyard", "00:27", 27000L, drawableRes = R.drawable.img_video_preview),
      MediaPickerItem("m2", "Kaaba Pilgrimage", "00:30", 30000L, drawableRes = R.drawable.img_video_clip),
      MediaPickerItem("m3", "GMC Luxury SUV", "00:11", 11000L, drawableRes = R.drawable.img_suv_car),
      MediaPickerItem("m4", "Arches of Mecca", "00:31", 31000L, drawableRes = R.drawable.img_video_clip),
      MediaPickerItem("m5", "Pilgrims Prayer", "00:34", 34000L, drawableRes = R.drawable.img_video_preview),
      MediaPickerItem("m6", "Mountain Valley", "11:09", 669000L, drawableRes = R.drawable.img_nature_mountains),
      MediaPickerItem("m7", "Courtyard Gathering", "00:41", 41000L, drawableRes = R.drawable.img_video_clip),
      MediaPickerItem("m8", "Tawaf Panorama", "00:27", 27000L, drawableRes = R.drawable.img_video_preview),
      MediaPickerItem("m9", "Black SUV Ride", "00:11", 11000L, drawableRes = R.drawable.img_suv_car),
      MediaPickerItem("m10", "Scenic Travel", "00:13", 13000L, drawableRes = R.drawable.img_nature_mountains),
      MediaPickerItem("m11", "Mecca Grand Mosque", "00:31", 31000L, drawableRes = R.drawable.img_video_preview),
      MediaPickerItem("m12", "Green Valley Hills", "00:19", 19000L, drawableRes = R.drawable.img_nature_mountains)
    )
  }

  // System zero-permission photo/video picker integration
  val systemPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickMultipleVisualMedia()
  ) { uris ->
    if (uris.isNotEmpty()) {
      uris.forEachIndexed { index, uri ->
        val newItem = MediaPickerItem(
          id = "device_${System.currentTimeMillis()}_$index",
          title = "Device Media",
          durationStr = "00:25",
          durationMs = 25000L,
          uri = uri,
          isVideo = true
        )
        mediaList.add(0, newItem)
        if (!selectedMediaIds.contains(newItem.id)) {
          selectedMediaIds.add(newItem.id)
        }
      }
    }
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(CapCutBackground),
    containerColor = CapCutBackground,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.statusBars)
          .background(CapCutBackground)
      ) {
        // TOP BAR (Close button + Albums, Generated, Spaces, Library)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onClose,
            modifier = Modifier
              .size(36.dp)
              .testTag("media_picker_close")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = CapCutTextPrimary,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Category tabs (Albums ▾, Generated, Spaces ▾, Library)
          val scrollState = rememberScrollState()
          Row(
            modifier = Modifier
              .weight(1f)
              .horizontalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
          ) {
            // Albums ▾
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clickable {
                  systemPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                  )
                }
                .testTag("albums_dropdown")
            ) {
              Text(
                text = "Albums",
                color = CapCutTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = CapCutTextPrimary,
                modifier = Modifier.size(20.dp)
              )
            }

            Text(
              text = "Generated",
              color = CapCutTextSecondary,
              fontSize = 15.sp,
              fontWeight = FontWeight.Medium
            )

            // Spaces ▾ with yellow notification dot
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Spaces",
                color = CapCutTextSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
              )
              Box(
                modifier = Modifier
                  .padding(start = 2.dp, bottom = 6.dp)
                  .size(5.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFFFD166))
              )
              Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = CapCutTextSecondary,
                modifier = Modifier.size(20.dp)
              )
            }

            Text(
              text = "Library",
              color = CapCutTextSecondary,
              fontSize = 15.sp,
              fontWeight = FontWeight.Medium
            )
          }

          // Button to trigger Android device system gallery picker
          IconButton(
            onClick = {
              systemPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
              )
            },
            modifier = Modifier.testTag("open_system_picker_button")
          ) {
            Icon(
              imageVector = Icons.Default.AddPhotoAlternate,
              contentDescription = "Pick from device",
              tint = CapCutTeal,
              modifier = Modifier.size(22.dp)
            )
          }
        }

        // SUB TABS ROW ("Videos", "Photos")
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(40.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Videos tab
          Column(
            modifier = Modifier
              .clickable { selectedTab = "Videos" }
              .testTag("tab_videos"),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "Videos",
              color = if (selectedTab == "Videos") CapCutTeal else CapCutTextSecondary,
              fontSize = 15.sp,
              fontWeight = if (selectedTab == "Videos") FontWeight.Bold else FontWeight.Normal
            )
            if (selectedTab == "Videos") {
              Spacer(modifier = Modifier.height(4.dp))
              Box(
                modifier = Modifier
                  .width(28.dp)
                  .height(2.dp)
                  .background(CapCutTeal)
              )
            }
          }

          // Photos tab
          Column(
            modifier = Modifier
              .clickable { selectedTab = "Photos" }
              .testTag("tab_photos"),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "Photos",
              color = if (selectedTab == "Photos") CapCutTeal else CapCutTextSecondary,
              fontSize = 15.sp,
              fontWeight = if (selectedTab == "Photos") FontWeight.Bold else FontWeight.Normal
            )
            if (selectedTab == "Photos") {
              Spacer(modifier = Modifier.height(4.dp))
              Box(
                modifier = Modifier
                  .width(28.dp)
                  .height(2.dp)
                  .background(CapCutTeal)
              )
            }
          }
        }
      }
    },
    bottomBar = {
      // BOTTOM BAR (HD toggle + "Add" button)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.navigationBars)
          .height(60.dp)
          .background(Color(0xFF141518))
          .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
      ) {
        // HD radio toggle
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clickable { isHdEnabled = !isHdEnabled }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("hd_toggle")
        ) {
          Box(
            modifier = Modifier
              .size(16.dp)
              .clip(CircleShape)
              .border(
                1.5.dp,
                if (isHdEnabled) CapCutTeal else CapCutTextDisabled,
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            if (isHdEnabled) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(CapCutTeal)
              )
            }
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "HD",
            color = if (isHdEnabled) CapCutTeal else CapCutTextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // "Add" or "Add (N)" button
        val hasSelection = selectedMediaIds.isNotEmpty()
        Box(
          modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (hasSelection) CapCutTeal else Color(0xFF132F2F))
            .clickable(enabled = hasSelection) {
              // Convert selected items into VideoClips for editor
              val selectedClips = selectedMediaIds.mapNotNull { id ->
                mediaList.find { it.id == id }?.let { item ->
                  VideoClip(
                    id = item.id,
                    title = item.title,
                    startMs = 0L,
                    endMs = item.durationMs,
                    originalDurationMs = item.durationMs,
                    thumbnailRes = item.drawableRes ?: R.drawable.img_video_preview
                  )
                }
              }
              onMediaSelected(selectedClips)
            }
            .padding(horizontal = 22.dp)
            .testTag("media_picker_add_button"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (hasSelection) "Add (${selectedMediaIds.size})" else "Add",
            color = if (hasSelection) Color.Black else Color(0xFF2E6565),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  ) { paddingValues ->
    // 3-COLUMN MEDIA GRID
    LazyVerticalGrid(
      columns = GridCells.Fixed(3),
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(2.dp),
      horizontalArrangement = Arrangement.spacedBy(2.dp),
      verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      itemsIndexed(mediaList) { index, item ->
        val selectedIndex = selectedMediaIds.indexOf(item.id)
        val isSelected = selectedIndex != -1

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable {
              if (isSelected) {
                selectedMediaIds.remove(item.id)
              } else {
                selectedMediaIds.add(item.id)
              }
            }
            .testTag("media_grid_item_$index"),
          contentAlignment = Alignment.Center
        ) {
          // Thumbnail image
          if (item.uri != null) {
            AsyncImage(
              model = item.uri,
              contentDescription = item.title,
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
          } else if (item.drawableRes != null) {
            Image(
              painter = painterResource(id = item.drawableRes),
              contentDescription = item.title,
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
          }

          // Slight dark tint when selected
          if (isSelected) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .border(2.dp, CapCutTeal)
            )
          }

          // Top-right selection circle badge (shows number "1", "2" or empty circle)
          Box(
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(6.dp)
              .size(24.dp)
              .clip(CircleShape)
              .background(if (isSelected) CapCutTeal else Color.Black.copy(alpha = 0.35f))
              .border(1.5.dp, if (isSelected) CapCutTeal else Color.White, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            if (isSelected) {
              Text(
                text = "${selectedIndex + 1}",
                color = Color.Black,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
              )
            }
          }

          // Bottom-right duration text (e.g., "00:27")
          Box(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(4.dp)
              .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(2.dp))
              .padding(horizontal = 4.dp, vertical = 1.dp)
          ) {
            Text(
              text = item.durationStr,
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Medium,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }
    }
  }
}
