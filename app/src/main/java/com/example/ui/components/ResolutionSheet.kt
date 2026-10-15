package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ResolutionSetting
import com.example.ui.theme.CapCutBackground
import com.example.ui.theme.CapCutSurfaceVariant
import com.example.ui.theme.CapCutTeal
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResolutionSheet(
  currentSetting: ResolutionSetting,
  onDismiss: () -> Unit,
  onApply: (label: String, fps: Int, smartHdr: Boolean) -> Unit
) {
  var selectedResolution by remember { mutableStateOf(currentSetting.label) }
  var selectedFps by remember { mutableIntStateOf(currentSetting.fps) }
  var smartHdrEnabled by remember { mutableStateOf(currentSetting.smartHdr) }

  val resolutions = listOf("480p", "720p", "1080p", "4K", "AI UHD")
  val frameRates = listOf(24, 30, 50, 60)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    containerColor = Color(0xFF1E2026),
    contentColor = CapCutTextPrimary
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Export Settings",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = CapCutTextPrimary
        )
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.size(32.dp).testTag("resolution_sheet_close")
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = CapCutTextSecondary)
        }
      }

      // Resolution section
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "Resolution",
          fontSize = 13.sp,
          color = CapCutTextSecondary,
          fontWeight = FontWeight.Medium
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          resolutions.forEach { res ->
            val isSelected = res == selectedResolution
            Box(
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) CapCutTeal else Color(0xFF2B2D35))
                .clickable { selectedResolution = res }
                .testTag("res_option_$res"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = res,
                color = if (isSelected) Color.Black else CapCutTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Frame Rate section
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "Frame Rate (fps)",
          fontSize = 13.sp,
          color = CapCutTextSecondary,
          fontWeight = FontWeight.Medium
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          frameRates.forEach { fps ->
            val isSelected = fps == selectedFps
            Box(
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) CapCutTeal else Color(0xFF2B2D35))
                .clickable { selectedFps = fps }
                .testTag("fps_option_$fps"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$fps",
                color = if (isSelected) Color.Black else CapCutTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Smart HDR toggle
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF2B2D35))
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = "Smart HDR", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = CapCutTextPrimary)
          Text(text = "Boost color range with CapCut AI engine", fontSize = 11.sp, color = CapCutTextSecondary)
        }
        Switch(
          checked = smartHdrEnabled,
          onCheckedChange = { smartHdrEnabled = it },
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = CapCutTeal,
            uncheckedTrackColor = Color(0xFF40434C)
          ),
          modifier = Modifier.testTag("switch_smart_hdr")
        )
      }

      // Estimated size
      val estimated = when (selectedResolution) {
        "AI UHD" -> 72.4f
        "4K" -> 118.2f
        "1080p" -> 42.1f
        "720p" -> 22.8f
        else -> 12.0f
      }
      Text(
        text = "Estimated file size: %.1f MB".format(estimated),
        fontSize = 12.sp,
        color = CapCutTextSecondary
      )

      // Apply button
      Button(
        onClick = { onApply(selectedResolution, selectedFps, smartHdrEnabled) },
        colors = ButtonDefaults.buttonColors(
          containerColor = CapCutTeal,
          contentColor = Color.Black
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(46.dp)
          .testTag("apply_resolution_button"),
        shape = RoundedCornerShape(23.dp)
      ) {
        Text(text = "Apply Resolution", fontWeight = FontWeight.Bold, fontSize = 14.sp)
      }
    }
  }
}
