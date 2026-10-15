package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CapCutPurple
import com.example.ui.theme.CapCutTeal
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary

data class MeMenuItem(
  val id: String,
  val label: String,
  val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeScreen(
  onSettingsClick: () -> Unit,
  onProClaimClick: () -> Unit,
  onMenuItemClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var showProSheet by remember { mutableStateOf(false) }
  var showSettingsSheet by remember { mutableStateOf(false) }
  var isCloudSyncEnabled by remember { mutableStateOf(true) }

  val firstGroup = listOf(
    MeMenuItem("events", "Events", Icons.Default.EmojiEvents),
    MeMenuItem("purchased", "Purchased", Icons.Default.ShoppingCart),
    MeMenuItem("likes", "Likes & Favorites", Icons.Default.BookmarkBorder),
    MeMenuItem("history", "View history", Icons.Default.History)
  )

  val secondGroup = listOf(
    MeMenuItem("help", "Help Center", Icons.AutoMirrored.Filled.HelpOutline),
    MeMenuItem("rewards", "Reward Center", Icons.Default.CardGiftcard),
    MeMenuItem("scan", "Scan", Icons.Default.QrCodeScanner)
  )

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = Color.White,
    contentWindowInsets = WindowInsets(0, 0, 0, 0)
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(bottom = 80.dp)
    ) {
      // 1. TOP BAR
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // "🎁 Up to 1 FREE year of Pro >"
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(Color(0xFFF1F3F7))
              .clickable { showProSheet = true }
              .padding(horizontal = 12.dp, vertical = 6.dp)
              .testTag("me_free_pro_pill"),
            contentAlignment = Alignment.CenterStart
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(text = "🎁", fontSize = 12.sp)
              Text(
                text = "Up to 1 FREE year of Pro",
                color = Color(0xFF1E1E24),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFF757D8A),
                modifier = Modifier.size(10.dp)
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { Toast.makeText(context, "No unread notifications", Toast.LENGTH_SHORT).show() },
              modifier = Modifier.size(38.dp)
            ) {
              Icon(Icons.Default.NotificationsNone, contentDescription = "Notifications", tint = Color(0xFF1E1E24))
            }
            IconButton(
              onClick = { showSettingsSheet = true },
              modifier = Modifier.size(38.dp).testTag("me_settings_button")
            ) {
              Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF1E1E24))
            }
          }
        }
      }

      // 2. USER PROFILE HEADER
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Box(
            modifier = Modifier
              .size(68.dp)
              .clip(CircleShape)
              .background(Color(0xFFE2E6EC)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = "Profile",
              tint = Color(0xFF6B7280),
              modifier = Modifier.size(44.dp)
            )
          }

          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = "hazratbilal727",
                color = Color(0xFF1E1E24),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .border(1.dp, Color(0xFFB0B7C3), RoundedCornerShape(6.dp))
                  .padding(horizontal = 6.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "Pro",
                  color = Color(0xFF757D8A),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.clickable {
                Toast.makeText(context, "Profile: @hazratbilal727 • Pro Member", Toast.LENGTH_SHORT).show()
              }
            ) {
              Text(text = "View profile", color = Color(0xFF757D8A), fontSize = 13.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFF757D8A),
                modifier = Modifier.size(10.dp)
              )
            }
          }
        }
      }

      // 3. PRO MEMBERSHIP BANNER CARD
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
              Brush.linearGradient(
                colors = listOf(
                  Color(0xFFEBE5FF),
                  Color(0xFFDCEBFF),
                  Color(0xFFE4F1FE)
                )
              )
            )
            .padding(18.dp)
            .testTag("me_pro_banner")
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = null,
                    tint = CapCutPurple,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Pro",
                    color = Color(0xFF1E1E24),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Restart Pro for only Rs200",
                  color = Color(0xFF555B66),
                  fontSize = 12.sp
                )
              }

              Button(
                onClick = { showProSheet = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp)
              ) {
                Text(text = "Claim offer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }

            // 4 Pro Tools Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceAround,
              verticalAlignment = Alignment.CenterVertically
            ) {
              ProFeatureItem("Materials", Icons.Default.PhotoLibrary) { showProSheet = true }
              ProFeatureItem("Pro tools", Icons.Default.GridView) { showProSheet = true }
              ProFeatureItem("AI effects", Icons.Default.AutoAwesome) { showProSheet = true }
              ProFeatureItem("Retouch", Icons.Default.Face) { showProSheet = true }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(8.dp))
      }

      // 4. FIRST MENU GROUP
      items(firstGroup.size) { index ->
        val item = firstGroup[index]
        MeMenuRow(
          item = item,
          onClick = {
            when (item.id) {
              "events" -> Toast.makeText(context, "CapCut Creator Challenge 2026", Toast.LENGTH_SHORT).show()
              "purchased" -> Toast.makeText(context, "CapCut Pro VIP (Active)", Toast.LENGTH_SHORT).show()
              "likes" -> Toast.makeText(context, "12 Saved Templates & Sounds", Toast.LENGTH_SHORT).show()
              "history" -> Toast.makeText(context, "Viewing recent project history", Toast.LENGTH_SHORT).show()
            }
          }
        )
      }

      // Divider Space
      item {
        Spacer(modifier = Modifier.height(16.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(Color(0xFFF7F8FA))
        )
        Spacer(modifier = Modifier.height(8.dp))
      }

      // 5. SECOND MENU GROUP
      items(secondGroup.size) { index ->
        val item = secondGroup[index]
        MeMenuRow(
          item = item,
          onClick = {
            when (item.id) {
              "help" -> Toast.makeText(context, "CapCut Support & Video Tutorials", Toast.LENGTH_SHORT).show()
              "rewards" -> Toast.makeText(context, "Claim 500 Creator Points", Toast.LENGTH_SHORT).show()
              "scan" -> Toast.makeText(context, "Scan QR code to pair with Desktop", Toast.LENGTH_SHORT).show()
            }
          }
        )
      }
    }
  }

  // CapCut Pro Subscription Sheet
  if (showProSheet) {
    ModalBottomSheet(
      onDismissRequest = { showProSheet = false },
      containerColor = Color(0xFF1E2026),
      contentColor = CapCutTextPrimary
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp)
          .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Diamond, contentDescription = null, tint = CapCutPurple, modifier = Modifier.size(24.dp))
            Text("CapCut Pro VIP", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
          IconButton(onClick = { showProSheet = false }) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = CapCutTextSecondary)
          }
        }

        val perks = listOf(
          "Export in Ultra HD 4K & AI UHD (60 fps)",
          "100+ Exclusive VIP Transitions & AI Effects",
          "AI Body & Face Beauty Auto-Retouch",
          "Unlimited 100GB Cloud Space backup",
          "Remove watermarks and commercial licenses"
        )

        perks.forEach { perk ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(CapCutTeal),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
            }
            Text(text = perk, color = Color.White, fontSize = 13.sp)
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
          onClick = {
            showProSheet = false
            Toast.makeText(context, "Subscribed to CapCut Pro! All features unlocked.", Toast.LENGTH_LONG).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = CapCutTeal, contentColor = Color.Black),
          shape = RoundedCornerShape(22.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
        ) {
          Text("Claim Offer (Rs200/month)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
      }
    }
  }

  // Settings Sheet
  if (showSettingsSheet) {
    ModalBottomSheet(
      onDismissRequest = { showSettingsSheet = false },
      containerColor = Color.White,
      contentColor = Color(0xFF1E1E24)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp)
          .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E24))
          IconButton(onClick = { showSettingsSheet = false }) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF757D8A))
          }
        }

        // Cloud sync switch
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Auto Cloud Sync", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E1E24))
            Text("Automatically backup project drafts to Space", fontSize = 11.sp, color = Color(0xFF757D8A))
          }
          Switch(
            checked = isCloudSyncEnabled,
            onCheckedChange = { isCloudSyncEnabled = it },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CapCutTeal)
          )
        }

        // Clear cache
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF6F7F9))
            .clickable {
              Toast.makeText(context, "Cache cleared: 142MB freed", Toast.LENGTH_SHORT).show()
            }
            .padding(horizontal = 14.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Clear Cache", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E1E24))
          Text("142 MB", fontSize = 13.sp, color = Color(0xFF757D8A))
        }

        // App version
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Version", fontSize = 14.sp, color = Color(0xFF1E1E24))
          Text("v12.8.0 (Build 36)", fontSize = 13.sp, color = Color(0xFF757D8A))
        }
      }
    }
  }
}

@Composable
private fun ProFeatureItem(label: String, icon: ImageVector, onClick: () -> Unit) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.clickable { onClick() }
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = Color(0xFF1E1E24),
      modifier = Modifier.size(22.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = label,
      color = Color(0xFF1E1E24),
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

@Composable
private fun MeMenuRow(item: MeMenuItem, onClick: () -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 20.dp, vertical = 14.dp)
      .testTag("me_menu_${item.id}"),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Icon(
        imageVector = item.icon,
        contentDescription = item.label,
        tint = Color(0xFF1E1E24),
        modifier = Modifier.size(22.dp)
      )
      Text(
        text = item.label,
        color = Color(0xFF1E1E24),
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium
      )
    }

    Icon(
      imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
      contentDescription = null,
      tint = Color(0xFFC0C6D0),
      modifier = Modifier.size(14.dp)
    )
  }
}
