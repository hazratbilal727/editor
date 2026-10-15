package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MotionPhotosAuto
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CapCutPurple
import com.example.ui.theme.CapCutTeal

data class HomeToolItem(
  val id: String,
  val label: String,
  val icon: ImageVector,
  val hasSubBadge: Boolean = false
)

@Composable
fun CapCutHomeScreen(
  onNewVideoClick: () -> Unit,
  onEditPhotoClick: () -> Unit,
  onOpenProject: (String) -> Unit,
  onToolClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val gridState = rememberLazyGridState()

  // Detect if scrolled down to display sticky collapsed header bar (as in Screenshot 1)
  val isScrolled by remember {
    derivedStateOf {
      gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 180
    }
  }

  val homeTools = remember {
    listOf(
      HomeToolItem("autocut", "AutoCut", Icons.Default.SmartDisplay),
      HomeToolItem("retouch", "Retouch", Icons.Default.Face),
      HomeToolItem("ai_generator", "AI generator", Icons.Default.AutoAwesome, hasSubBadge = true),
      HomeToolItem("photo_tools", "Photo tools", Icons.Default.PhotoLibrary),
      HomeToolItem("shoot_record", "Shoot and\nrecord", Icons.Default.CameraAlt, hasSubBadge = true),
      HomeToolItem("auto_enhance", "Auto enhance", Icons.Default.AutoFixHigh),
      HomeToolItem("cover_maker", "Cover maker", Icons.Default.PhotoAlbum),
      HomeToolItem("auto_captions", "Auto captions", Icons.Default.Subtitles),
      HomeToolItem("remove_bg", "Remove\nbackground", Icons.Default.Transform),
      HomeToolItem("ai_photo_editor", "AI photo editor", Icons.Default.Image),
      HomeToolItem("space", "Space", Icons.Default.CloudQueue),
      HomeToolItem("marketing_tools", "Marketing tools", Icons.Default.ShoppingBag, hasSubBadge = true),
      HomeToolItem("record", "Record", Icons.Default.Mic),
      HomeToolItem("avatar_tools", "Avatar tools", Icons.Default.Person, hasSubBadge = true),
      HomeToolItem("generate_media", "Generate media", Icons.Default.MotionPhotosAuto),
      HomeToolItem("desktop_editor", "Desktop editor", Icons.Default.Laptop),
      HomeToolItem("all_tools", "All tools (28)", Icons.Default.GridView)
    )
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = Color.White,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    bottomBar = {
      CapCutHomeBottomBar(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it }
      )
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      when (selectedTab) {
        0 -> {
          // TAB 0: EDIT / HOME SCREEN
          EditTabContent(
            gridState = gridState,
            homeTools = homeTools,
            isScrolled = isScrolled,
            onNewVideoClick = onNewVideoClick,
            onEditPhotoClick = onEditPhotoClick,
            onOpenProject = onOpenProject,
            onToolClick = onToolClick
          )
        }
        1 -> {
          // TAB 1: TEMPLATES
          TemplatesTabContent(onTemplateClick = onNewVideoClick)
        }
        2 -> {
          // TAB 2: AI LAB
          AiLabTabContent(onToolClick = onToolClick)
        }
        3 -> {
          // TAB 3: PROJECTS (SCREENSHOT 1)
          ProjectsScreen(
            onOpenProject = onOpenProject,
            onCreateProject = onNewVideoClick,
            onSearchClick = { onToolClick("search_projects") }
          )
        }
        4 -> {
          // TAB 4: ME (SCREENSHOT 2)
          MeScreen(
            onSettingsClick = { onToolClick("settings") },
            onProClaimClick = { onToolClick("pro_subscription") },
            onMenuItemClick = onToolClick
          )
        }
      }
    }
  }
}

@Composable
private fun EditTabContent(
  gridState: androidx.compose.foundation.lazy.grid.LazyGridState,
  homeTools: List<HomeToolItem>,
  isScrolled: Boolean,
  onNewVideoClick: () -> Unit,
  onEditPhotoClick: () -> Unit,
  onOpenProject: (String) -> Unit,
  onToolClick: (String) -> Unit
) {
  Box(modifier = Modifier.fillMaxSize()) {
    // Top gradient
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(340.dp)
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color(0xFF0052D4),
              Color(0xFF4364F7),
              Color(0xFF6FB1FC),
              Color.White
            )
          )
        )
    )

    // Scrollable Grid
    LazyVerticalGrid(
      columns = GridCells.Fixed(3),
      state = gridState,
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      // Header item
      item(span = { GridItemSpan(3) }) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top = 4.dp, bottom = 8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Pro promotion pill
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.95f))
                .clickable { onToolClick("pro_subscription") }
                .padding(horizontal = 12.dp, vertical = 7.dp)
                .testTag("home_pro_pill"),
              contentAlignment = Alignment.CenterStart
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Diamond,
                  contentDescription = "Pro",
                  tint = CapCutPurple,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "Restart Pro for only Rs200",
                  color = Color(0xFF1E1E24),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            // Search button
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF0D47A1).copy(alpha = 0.65f))
                .clickable { onToolClick("search") }
                .testTag("home_search_button"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(24.dp))

          // Subtitle & Title: "Video create", "Get started >"
          Text(
            text = "Video create",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clickable { onNewVideoClick() }
              .testTag("home_get_started_row")
          ) {
            Text(
              text = "Get started",
              color = Color.White,
              fontSize = 26.sp,
              fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.3f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(10.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // HERO ACTION CARDS ROW
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // 1. "New video" Card
            Box(
              modifier = Modifier
                .weight(1.35f)
                .height(130.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFDFF0FC))
                .clickable { onNewVideoClick() }
                .testTag("home_new_video_card"),
              contentAlignment = Alignment.Center
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
              ) {
                Box(
                  modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New video",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                  )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = "New video",
                  color = Color.Black,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            // 2. "Edit photo" Card
            Box(
              modifier = Modifier
                .weight(1.0f)
                .height(130.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFDFF0FC))
                .clickable { onEditPhotoClick() }
                .testTag("home_edit_photo_card"),
              contentAlignment = Alignment.Center
            ) {
              Box(
                modifier = Modifier
                  .align(Alignment.TopCenter)
                  .padding(top = 8.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color.White.copy(alpha = 0.8f))
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "Nano Banana 2.1",
                  color = Color(0xFF2C3E50),
                  fontSize = 9.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }

              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
              ) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                  modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Edit photo",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                  )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "Edit photo",
                  color = Color.Black,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // RECENT PROJECTS STRIP ("1015-01")
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "1015-01",
              color = Color(0xFF8E99A8),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.width(60.dp)
            )

            // Draft preview cards
            repeat(3) { index ->
              Box(
                modifier = Modifier
                  .size(62.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color(0xFFF1F3F6))
                  .clickable { onOpenProject("1015-01") }
                  .testTag("home_draft_card_$index"),
                contentAlignment = Alignment.Center
              ) {
                if (index == 0) {
                  Image(
                    painter = painterResource(id = R.drawable.img_video_clip),
                    contentDescription = "Project draft",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                  )
                }
              }
            }
          }
        }
      }

      // Quick tools items
      items(homeTools) { tool ->
        HomeToolGridItem(
          tool = tool,
          onClick = { onToolClick(tool.id) }
        )
      }
    }

    // Sticky Top Bar when scrolled down
    if (isScrolled) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.statusBars)
          .height(58.dp)
          .background(Color.White)
          .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .weight(1.35f)
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFDFF0FC))
            .clickable { onNewVideoClick() }
            .testTag("sticky_new_video_pill"),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(Color.Black),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
          }
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFDFF0FC))
            .clickable { onEditPhotoClick() }
            .testTag("sticky_edit_photo_pill"),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(Color.Black),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Image, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  }
}

@Composable
private fun TemplatesTabContent(onTemplateClick: () -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .windowInsetsPadding(WindowInsets.statusBars)
      .padding(16.dp)
  ) {
    Text(
      text = "Templates",
      color = Color(0xFF1E1E24),
      fontSize = 24.sp,
      fontWeight = FontWeight.Black
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Sample template cards
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(6) { index ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E2026))
            .clickable { onTemplateClick() },
          contentAlignment = Alignment.BottomStart
        ) {
          Image(
            painter = painterResource(id = if (index % 2 == 0) R.drawable.img_video_preview else R.drawable.img_nature_mountains),
            contentDescription = "Template",
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
          )
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color.Black.copy(alpha = 0.5f))
              .padding(8.dp)
          ) {
            Text(
              text = "Viral TikTok Beat #$index",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
private fun AiLabTabContent(onToolClick: (String) -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .windowInsetsPadding(WindowInsets.statusBars)
      .padding(16.dp)
  ) {
    Text(
      text = "AI Lab",
      color = Color(0xFF1E1E24),
      fontSize = 24.sp,
      fontWeight = FontWeight.Black
    )
    Spacer(modifier = Modifier.height(16.dp))

    val aiTools = listOf(
      "AI Avatar Generator" to Icons.Default.Face,
      "AI Smart Expand" to Icons.Default.AutoFixHigh,
      "AI Background Remover" to Icons.Default.Transform,
      "AI Hug & Motion" to Icons.Default.AutoAwesome
    )

    aiTools.forEach { (title, icon) ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFFF6F7F9))
          .clickable { onToolClick(title) }
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Icon(icon, contentDescription = null, tint = CapCutPurple, modifier = Modifier.size(28.dp))
          Text(text = title, color = Color(0xFF1E1E24), fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color(0xFF9EA7B5), modifier = Modifier.size(14.dp))
      }
    }
  }
}

@Composable
private fun HomeToolGridItem(
  tool: HomeToolItem,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(vertical = 4.dp)
      .testTag("home_tool_${tool.id}"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(contentAlignment = Alignment.BottomEnd) {
      Icon(
        imageVector = tool.icon,
        contentDescription = tool.label,
        tint = Color(0xFF1E1E24),
        modifier = Modifier.size(32.dp)
      )

      if (tool.hasSubBadge) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E1E24))
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = tool.label,
      color = Color(0xFF1E1E24),
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium,
      textAlign = TextAlign.Center,
      lineHeight = 14.sp
    )
  }
}

@Composable
private fun CapCutHomeBottomBar(
  selectedTab: Int,
  onTabSelected: (Int) -> Unit
) {
  Surface(
    color = Color.White,
    shadowElevation = 8.dp,
    modifier = Modifier
      .fillMaxWidth()
      .windowInsetsPadding(WindowInsets.navigationBars)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      val tabs = listOf(
        Triple("Edit", Icons.Default.Transform, 0),
        Triple("Templates", Icons.Default.VideoLibrary, 1),
        Triple("AI Lab", Icons.Default.AutoAwesome, 2),
        Triple("Projects", Icons.Default.Folder, 3),
        Triple("Me", Icons.Default.Person, 4)
      )

      tabs.forEach { (label, icon, index) ->
        val isSelected = selectedTab == index
        Column(
          modifier = Modifier
            .weight(1f)
            .clickable { onTabSelected(index) }
            .padding(vertical = 4.dp)
            .testTag("bottom_tab_$label"),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color.Black else Color(0xFF8E99A8),
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = label,
            color = if (isSelected) Color.Black else Color(0xFF8E99A8),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
          )
        }
      }
    }
  }
}
