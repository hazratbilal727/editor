package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CapCutTeal
import com.example.ui.theme.CapCutTextDisabled
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary

data class ProjectItemData(
  val id: String,
  val title: String,
  val dateStr: String,
  val sizeStr: String,
  val durationStr: String,
  val thumbnailRes: Int,
  val type: String = "Video"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
  onOpenProject: (String) -> Unit,
  onCreateProject: () -> Unit,
  onSearchClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableStateOf("Local") }
  var selectedFilter by remember { mutableStateOf("All") }
  var isSelectMode by remember { mutableStateOf(false) }
  val selectedProjectIds = remember { mutableStateListOf<String>() }

  var projectToRename by remember { mutableStateOf<ProjectItemData?>(null) }
  var renameInput by remember { mutableStateOf("") }
  var projectMenuTarget by remember { mutableStateOf<ProjectItemData?>(null) }

  val tabs = listOf("Local", "Generated", "Spaces", "Gallery", "Trash")
  val filters = listOf("All", "Video", "Photo", "Templates", "Camera")

  val projectsList = remember {
    mutableStateListOf(
      ProjectItemData(
        id = "1015-01",
        title = "1015-01",
        dateStr = "15/10/2026 10:50",
        sizeStr = "24MB",
        durationStr = "00:28",
        thumbnailRes = R.drawable.img_video_clip,
        type = "Video"
      ),
      ProjectItemData(
        id = "1014-02",
        title = "Mecca Ihram Journey",
        dateStr = "14/10/2026 18:22",
        sizeStr = "48MB",
        durationStr = "00:45",
        thumbnailRes = R.drawable.img_video_preview,
        type = "Video"
      ),
      ProjectItemData(
        id = "1012-03",
        title = "GMC SUV Cinematic",
        dateStr = "12/10/2026 14:10",
        sizeStr = "18MB",
        durationStr = "00:11",
        thumbnailRes = R.drawable.img_suv_car,
        type = "Video"
      )
    )
  }

  // Filter projects
  val displayedProjects = projectsList.filter {
    if (selectedFilter == "All") true
    else it.type.equals(selectedFilter, ignoreCase = true)
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = Color.White,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    floatingActionButton = {
      if (!isSelectMode) {
        FloatingActionButton(
          onClick = onCreateProject,
          containerColor = CapCutTeal,
          contentColor = Color.White,
          shape = RoundedCornerShape(28.dp),
          elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
          modifier = Modifier
            .padding(bottom = 16.dp, end = 8.dp)
            .height(50.dp)
            .testTag("projects_create_fab")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = "Create", tint = Color.White, modifier = Modifier.size(20.dp))
            Text(text = "Create", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    },
    bottomBar = {
      if (isSelectMode && selectedProjectIds.isNotEmpty()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E2026))
            .padding(horizontal = 20.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "${selectedProjectIds.size} selected",
            color = Color.White,
            fontWeight = FontWeight.Bold
          )
          Button(
            onClick = {
              projectsList.removeAll { selectedProjectIds.contains(it.id) }
              selectedProjectIds.clear()
              isSelectMode = false
              Toast.makeText(context, "Deleted selected projects", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D4F))
          ) {
            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Delete")
          }
        }
      }
    }
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
            .padding(horizontal = 16.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Projects",
            color = Color(0xFF1E1E24),
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
          )

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onSearchClick, modifier = Modifier.size(38.dp)) {
              Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF1E1E24))
            }
            IconButton(
              onClick = {
                // Reverse sorting
                projectsList.reverse()
                Toast.makeText(context, "Sorted projects", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier.size(38.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort", tint = Color(0xFF1E1E24))
            }
            IconButton(
              onClick = {
                isSelectMode = !isSelectMode
                if (!isSelectMode) selectedProjectIds.clear()
              },
              modifier = Modifier.size(38.dp)
            ) {
              Icon(Icons.Default.MoreHoriz, contentDescription = "Options", tint = Color(0xFF1E1E24))
            }
          }
        }
      }

      // 2. HERO BANNER: "Create AI character"
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF6F7F9))
            .clickable {
              Toast.makeText(context, "CapCut AI Character Studio", Toast.LENGTH_SHORT).show()
            }
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("banner_ai_character"),
          contentAlignment = Alignment.CenterStart
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFE2E6EC)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null,
                  tint = Color(0xFF9EA7B5),
                  modifier = Modifier.size(28.dp)
                )
              }

              Column {
                Text(
                  text = "Create AI character",
                  color = Color(0xFF1E1E24),
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "Star yourself, pets, and more in videos",
                  color = Color(0xFF757D8A),
                  fontSize = 12.sp
                )
              }
            }

            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
              contentDescription = null,
              tint = Color(0xFFB0B7C3),
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      // 3. CATEGORY TABS
      item {
        val scrollState = rememberScrollState()
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(24.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          tabs.forEach { tab ->
            val isSelected = tab == selectedTab
            Column(
              modifier = Modifier
                .clickable { selectedTab = tab }
                .padding(vertical = 4.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = tab,
                color = if (isSelected) Color(0xFF1E1E24) else Color(0xFF757D8A),
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
              Spacer(modifier = Modifier.height(4.dp))
              if (isSelected) {
                Box(
                  modifier = Modifier
                    .width(26.dp)
                    .height(2.5.dp)
                    .background(Color(0xFF1E1E24), RoundedCornerShape(1.dp))
                )
              } else {
                Spacer(modifier = Modifier.height(2.5.dp))
              }
            }
          }
        }
      }

      // 4. FILTER PILLS
      item {
        val scrollState = rememberScrollState()
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          filters.forEach { filter ->
            val isSelected = filter == selectedFilter
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(
                  width = 1.dp,
                  color = if (isSelected) Color(0xFF1E1E24) else Color(0xFFE5E7EB),
                  shape = RoundedCornerShape(8.dp)
                )
                .background(Color.White)
                .clickable { selectedFilter = filter }
                .padding(horizontal = 18.dp, vertical = 7.dp)
                .testTag("project_filter_$filter"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = filter,
                color = if (isSelected) Color(0xFF1E1E24) else Color(0xFF555B66),
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        }
      }

      // 5. PROJECT COUNT & "Select" ACTION
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${displayedProjects.size} project${if (displayedProjects.size != 1) "s" else ""}",
            color = Color(0xFF1E1E24),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = if (isSelectMode) "Cancel" else "Select",
            color = Color(0xFF555B66),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable {
              isSelectMode = !isSelectMode
              if (!isSelectMode) selectedProjectIds.clear()
            }
          )
        }
      }

      // 6. SPECIAL "Space" CARD
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
              Toast.makeText(context, "CapCut Cloud Space Storage", Toast.LENGTH_SHORT).show()
            }
            .padding(vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE8F6F8)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CloudQueue,
                contentDescription = "Space",
                tint = Color(0xFF00B4D8),
                modifier = Modifier.size(32.dp)
              )
            }

            Column {
              Text(
                text = "Space",
                color = Color(0xFF1E1E24),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Upload and manage your projects >",
                color = Color(0xFF757D8A),
                fontSize = 12.sp
              )
            }
          }

          IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = Color(0xFF9EA7B5))
          }
        }
      }

      // 7. PROJECTS LIST
      items(displayedProjects, key = { it.id }) { project ->
        val isChecked = selectedProjectIds.contains(project.id)

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
              if (isSelectMode) {
                if (isChecked) selectedProjectIds.remove(project.id) else selectedProjectIds.add(project.id)
              } else {
                onOpenProject(project.id)
              }
            }
            .padding(vertical = 6.dp)
            .testTag("project_item_${project.id}"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            // Checkbox in select mode
            if (isSelectMode) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(if (isChecked) CapCutTeal else Color.White)
                  .border(2.dp, if (isChecked) CapCutTeal else Color(0xFFC0C6D0), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                if (isChecked) {
                  Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                }
              }
            }

            // Thumbnail
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF2B2D35)),
              contentAlignment = Alignment.Center
            ) {
              Image(
                painter = painterResource(id = project.thumbnailRes),
                contentDescription = project.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )
            }

            Column {
              Text(
                text = project.title,
                color = Color(0xFF1E1E24),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = project.dateStr,
                color = Color(0xFF8E99A8),
                fontSize = 11.sp
              )
              Spacer(modifier = Modifier.height(3.dp))
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.ContentCut,
                  contentDescription = null,
                  tint = Color(0xFF8E99A8),
                  modifier = Modifier.size(11.dp)
                )
                Text(
                  text = "${project.sizeStr}  |  ${project.durationStr}",
                  color = Color(0xFF8E99A8),
                  fontSize = 11.sp
                )
              }
            }
          }

          // Options button
          Box {
            IconButton(
              onClick = { projectMenuTarget = project },
              modifier = Modifier.size(32.dp).testTag("project_menu_${project.id}")
            ) {
              Icon(Icons.Default.MoreHoriz, contentDescription = "Project options", tint = Color(0xFF9EA7B5))
            }

            DropdownMenu(
              expanded = projectMenuTarget?.id == project.id,
              onDismissRequest = { projectMenuTarget = null }
            ) {
              DropdownMenuItem(
                text = { Text("Rename") },
                onClick = {
                  projectToRename = project
                  renameInput = project.title
                  projectMenuTarget = null
                }
              )
              DropdownMenuItem(
                text = { Text("Duplicate") },
                onClick = {
                  val copy = project.copy(
                    id = "copy_${System.currentTimeMillis()}",
                    title = "${project.title} (Copy)",
                    dateStr = "Today"
                  )
                  projectsList.add(0, copy)
                  projectMenuTarget = null
                  Toast.makeText(context, "Duplicated project", Toast.LENGTH_SHORT).show()
                }
              )
              DropdownMenuItem(
                text = { Text("Delete", color = Color(0xFFFF4D4F)) },
                onClick = {
                  projectsList.remove(project)
                  projectMenuTarget = null
                  Toast.makeText(context, "Deleted project", Toast.LENGTH_SHORT).show()
                }
              )
            }
          }
        }
      }
    }
  }

  // Rename Dialog
  projectToRename?.let { target ->
    AlertDialog(
      onDismissRequest = { projectToRename = null },
      title = { Text("Rename Project") },
      text = {
        OutlinedTextField(
          value = renameInput,
          onValueChange = { renameInput = it },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      },
      confirmButton = {
        Button(
          onClick = {
            if (renameInput.isNotBlank()) {
              val idx = projectsList.indexOfFirst { it.id == target.id }
              if (idx != -1) {
                projectsList[idx] = target.copy(title = renameInput)
              }
              projectToRename = null
              Toast.makeText(context, "Project renamed", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = CapCutTeal, contentColor = Color.Black)
        ) {
          Text("Save")
        }
      },
      dismissButton = {
        TextButton(onClick = { projectToRename = null }) {
          Text("Cancel")
        }
      }
    )
  }
}
