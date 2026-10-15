package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CapCutTeal
import com.example.ui.theme.CapCutTextPrimary

@Composable
fun CapCutTopBar(
  currentResolution: String,
  onCloseClick: () -> Unit,
  onSearchClick: () -> Unit,
  onProClick: () -> Unit,
  onResolutionClick: () -> Unit,
  onExportClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(56.dp)
      .padding(horizontal = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // Left controls
    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(
        onClick = onCloseClick,
        modifier = Modifier
          .size(40.dp)
          .testTag("topbar_close_button")
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Close editor",
          tint = CapCutTextPrimary,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(4.dp))

      IconButton(
        onClick = onSearchClick,
        modifier = Modifier
          .size(40.dp)
          .testTag("topbar_search_button")
      ) {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search tools and audio",
          tint = CapCutTextPrimary,
          modifier = Modifier.size(24.dp)
        )
      }
    }

    // Right controls
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Pro Diamond Badge
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(
            Brush.linearGradient(
              colors = listOf(Color(0xFF8A3FFC), Color(0xFF6728C2))
            )
          )
          .clickable { onProClick() }
          .testTag("topbar_pro_badge"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Diamond,
          contentDescription = "CapCut Pro features",
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
      }

      // Resolution pill ("AI UHD v")
      Box(
        modifier = Modifier
          .height(32.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF23252B))
          .clickable { onResolutionClick() }
          .padding(horizontal = 10.dp)
          .testTag("topbar_resolution_button"),
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = currentResolution,
            color = CapCutTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(2.dp))
          Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Select resolution",
            tint = CapCutTextPrimary,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      // Export button (bright teal pill with AD icon)
      Box(
        modifier = Modifier
          .height(32.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(CapCutTeal)
          .clickable { onExportClick() }
          .padding(horizontal = 12.dp)
          .testTag("topbar_export_button"),
        contentAlignment = Alignment.Center
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Small framed AD icon
          Box(
            modifier = Modifier
              .size(16.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(Color.Black),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "AD",
              color = CapCutTeal,
              fontSize = 7.sp,
              fontWeight = FontWeight.Black
            )
          }

          Text(
            text = "Export",
            color = Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
