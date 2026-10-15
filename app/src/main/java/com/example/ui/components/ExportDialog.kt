package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CapCutTeal
import com.example.ui.theme.CapCutTextDisabled
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary

@Composable
fun ExportDialog(
  progress: Float,
  isFinished: Boolean,
  resolution: String,
  onDismiss: () -> Unit,
  onShare: (String) -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = Color(0xFF1E2026),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .testTag("export_dialog_surface")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isFinished) "Ready to Share" else "Exporting Video",
            color = CapCutTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp).testTag("export_dialog_close")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = CapCutTextSecondary)
          }
        }

        if (!isFinished) {
          // Progress State
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "${(progress * 100).toInt()}%",
            color = CapCutTeal,
            fontSize = 42.sp,
            fontWeight = FontWeight.Black
          )

          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = CapCutTeal,
            trackColor = Color(0xFF2E313A)
          )

          Text(
            text = "Exporting at $resolution...\nDon't lock your screen or switch to other apps.",
            color = CapCutTextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
          )
        } else {
          // Finished State
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(CapCutTeal),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Success",
              tint = Color.Black,
              modifier = Modifier.size(36.dp)
            )
          }

          Text(
            text = "Saved to Device & Ready!",
            color = CapCutTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )

          // Share buttons
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = { onShare("TikTok") },
              colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(22.dp)),
              shape = RoundedCornerShape(22.dp)
            ) {
              Text(
                text = "Share to TikTok",
                color = Color.White,
                fontWeight = FontWeight.Bold
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = { onShare("Instagram") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2F38)),
                modifier = Modifier
                  .weight(1f)
                  .height(40.dp),
                shape = RoundedCornerShape(20.dp)
              ) {
                Text(text = "Instagram", color = CapCutTextPrimary, fontSize = 12.sp)
              }
              Button(
                onClick = { onShare("WhatsApp") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2F38)),
                modifier = Modifier
                  .weight(1f)
                  .height(40.dp),
                shape = RoundedCornerShape(20.dp)
              ) {
                Text(text = "WhatsApp", color = CapCutTextPrimary, fontSize = 12.sp)
              }
            }
          }

          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(
              containerColor = CapCutTeal,
              contentColor = Color.Black
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp),
            shape = RoundedCornerShape(22.dp)
          ) {
            Text(text = "Done", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
