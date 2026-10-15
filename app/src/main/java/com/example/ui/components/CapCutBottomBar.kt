package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdjustSettings
import com.example.model.AspectRatioOption
import com.example.model.FilterItem
import com.example.model.MainTool
import com.example.model.SubMenuType
import com.example.ui.theme.CapCutBackground
import com.example.ui.theme.CapCutPurple
import com.example.ui.theme.CapCutTeal
import com.example.ui.theme.CapCutTextDisabled
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary

@Composable
fun CapCutBottomBar(
  activeSubMenu: SubMenuType,
  selectedTool: MainTool?,
  availableFilters: List<FilterItem>,
  currentFilter: FilterItem,
  filterIntensity: Float,
  adjustSettings: AdjustSettings,
  aspectRatio: AspectRatioOption,
  currentSpeed: Float = 1.0f,
  currentVolume: Float = 1.0f,
  onToolClick: (MainTool) -> Unit,
  onCloseSubMenu: () -> Unit,
  onSplitClick: () -> Unit,
  onSpeedClick: () -> Unit = {},
  onVolumeClick: () -> Unit = {},
  onDuplicateClick: () -> Unit = {},
  onSpeedChange: (Float) -> Unit = {},
  onVolumeChange: (Float) -> Unit = {},
  onDeleteClick: () -> Unit,
  onFilterSelect: (FilterItem) -> Unit,
  onFilterIntensityChange: (Float) -> Unit,
  onAdjustChange: (AdjustSettings) -> Unit,
  onAdjustReset: () -> Unit,
  onAspectRatioSelect: (AspectRatioOption) -> Unit,
  onAddTextClick: () -> Unit,
  onAddAudioClick: () -> Unit,
  onAutoCaptionsClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(68.dp)
      .background(CapCutBackground)
  ) {
    when (activeSubMenu) {
      SubMenuType.NONE -> {
        MainMenuToolbar(onToolClick = onToolClick)
      }
      SubMenuType.CLIP_EDIT -> {
        ClipEditSubToolbar(
          onBack = onCloseSubMenu,
          onSplit = onSplitClick,
          onSpeed = onSpeedClick,
          onVolume = onVolumeClick,
          onDuplicate = onDuplicateClick,
          onDelete = onDeleteClick,
          onFilters = { onToolClick(MainTool.FILTERS) },
          onAdjust = { onToolClick(MainTool.ADJUST) }
        )
      }
      SubMenuType.SPEED -> {
        SpeedSubToolbar(
          currentSpeed = currentSpeed,
          onSpeedChange = onSpeedChange,
          onConfirm = onCloseSubMenu
        )
      }
      SubMenuType.VOLUME -> {
        VolumeSubToolbar(
          currentVolume = currentVolume,
          onVolumeChange = onVolumeChange,
          onConfirm = onCloseSubMenu
        )
      }
      SubMenuType.FILTERS -> {
        FiltersSubToolbar(
          filters = availableFilters,
          currentFilter = currentFilter,
          intensity = filterIntensity,
          onFilterSelect = onFilterSelect,
          onIntensityChange = onFilterIntensityChange,
          onConfirm = onCloseSubMenu
        )
      }
      SubMenuType.ADJUST -> {
        AdjustSubToolbar(
          adjustSettings = adjustSettings,
          onAdjustChange = onAdjustChange,
          onReset = onAdjustReset,
          onConfirm = onCloseSubMenu
        )
      }
      SubMenuType.ASPECT_RATIO -> {
        AspectRatioSubToolbar(
          currentRatio = aspectRatio,
          onSelectRatio = onAspectRatioSelect,
          onConfirm = onCloseSubMenu
        )
      }
      SubMenuType.TEXT -> {
        TextSubToolbar(
          onBack = onCloseSubMenu,
          onAddText = onAddTextClick,
          onAutoCaptions = onAutoCaptionsClick
        )
      }
      SubMenuType.AUDIO -> {
        AudioSubToolbar(
          onBack = onCloseSubMenu,
          onAddAudio = onAddAudioClick
        )
      }
      SubMenuType.EFFECTS -> {
        EffectsSubToolbar(
          onBack = onCloseSubMenu
        )
      }
    }
  }
}

// 1. Primary Bottom Toolbar (scrolling horizontal bar)
@Composable
private fun MainMenuToolbar(
  onToolClick: (MainTool) -> Unit
) {
  val scrollState = rememberScrollState()

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(68.dp)
      .horizontalScroll(scrollState)
      .padding(horizontal = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    MainTool.entries.forEach { tool ->
      ToolButton(
        label = tool.label,
        icon = tool.icon,
        badge = tool.badge,
        onClick = { onToolClick(tool) },
        testTag = "main_tool_${tool.id}"
      )
    }
  }
}

@Composable
private fun ToolButton(
  label: String,
  icon: ImageVector,
  badge: String? = null,
  onClick: () -> Unit,
  testTag: String
) {
  Column(
    modifier = Modifier
      .width(52.dp)
      .clickable { onClick() }
      .padding(vertical = 4.dp)
      .testTag(testTag),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(contentAlignment = Alignment.TopEnd) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = CapCutTextPrimary,
        modifier = Modifier.size(24.dp)
      )

      // Badges: "AI" (Cyan) or "PRO" (Purple Diamond)
      if (badge == "AI") {
        Box(
          modifier = Modifier
            .offset(x = 10.dp, y = (-6).dp)
            .clip(RoundedCornerShape(4.dp))
            .background(CapCutTeal)
            .padding(horizontal = 3.dp, vertical = 1.dp)
        ) {
          Text(
            text = "AI",
            color = Color.Black,
            fontSize = 7.sp,
            fontWeight = FontWeight.Black
          )
        }
      } else if (badge == "PRO") {
        Box(
          modifier = Modifier
            .offset(x = 8.dp, y = (-6).dp)
            .size(12.dp)
            .clip(CircleShape)
            .background(CapCutPurple),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Diamond,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(8.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = label,
      color = CapCutTextPrimary,
      fontSize = 10.sp,
      fontWeight = FontWeight.Normal,
      maxLines = 1
    )
  }
}

// 2. Sub-Toolbar: Clip Edit
@Composable
private fun ClipEditSubToolbar(
  onBack: () -> Unit,
  onSplit: () -> Unit,
  onSpeed: () -> Unit,
  onVolume: () -> Unit,
  onDuplicate: () -> Unit,
  onDelete: () -> Unit,
  onFilters: () -> Unit,
  onAdjust: () -> Unit
) {
  val scrollState = rememberScrollState()

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(68.dp)
      .padding(horizontal = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    IconButton(
      onClick = onBack,
      modifier = Modifier.size(40.dp).testTag("clip_edit_back_button")
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Back",
        tint = CapCutTextPrimary,
        modifier = Modifier.size(20.dp)
      )
    }

    Row(
      modifier = Modifier
        .weight(1f)
        .horizontalScroll(scrollState),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      ToolButton(label = "Split", icon = Icons.Default.GraphicEq, onClick = onSplit, testTag = "subtool_split")
      ToolButton(label = "Speed", icon = Icons.Default.Speed, onClick = onSpeed, testTag = "subtool_speed")
      ToolButton(label = "Volume", icon = Icons.Default.VolumeUp, onClick = onVolume, testTag = "subtool_volume")
      ToolButton(label = "Duplicate", icon = Icons.Default.Transform, onClick = onDuplicate, testTag = "subtool_duplicate")
      ToolButton(label = "Delete", icon = Icons.Default.Delete, onClick = onDelete, testTag = "subtool_delete")
      ToolButton(label = "Animation", icon = Icons.Default.Animation, onClick = {}, testTag = "subtool_animation")
      ToolButton(label = "Filters", icon = Icons.Default.Person, onClick = onFilters, testTag = "subtool_filters")
      ToolButton(label = "Adjust", icon = Icons.Default.Person, onClick = onAdjust, testTag = "subtool_adjust")
    }
  }
}

// Sub-Toolbar: Speed
@Composable
private fun SpeedSubToolbar(
  currentSpeed: Float,
  onSpeedChange: (Float) -> Unit,
  onConfirm: () -> Unit
) {
  val speedPresets = listOf(0.5f, 1.0f, 1.5f, 2.0f, 5.0f)

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(68.dp)
      .padding(horizontal = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier.weight(1f),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Speed: ${currentSpeed}x",
        color = CapCutTeal,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )

      speedPresets.forEach { speed ->
        val isSelected = (currentSpeed - speed) in -0.05f..0.05f
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) CapCutTeal else Color(0xFF22242B))
            .clickable { onSpeedChange(speed) }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("speed_${speed}x"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${speed}x",
            color = if (isSelected) Color.Black else CapCutTextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    IconButton(
      onClick = onConfirm,
      modifier = Modifier.size(36.dp).testTag("speed_confirm_button")
    ) {
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = "Confirm",
        tint = CapCutTeal,
        modifier = Modifier.size(22.dp)
      )
    }
  }
}

// Sub-Toolbar: Volume
@Composable
private fun VolumeSubToolbar(
  currentVolume: Float,
  onVolumeChange: (Float) -> Unit,
  onConfirm: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(68.dp)
      .padding(horizontal = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = Icons.Default.VolumeUp,
      contentDescription = null,
      tint = CapCutTeal,
      modifier = Modifier.size(22.dp)
    )

    Spacer(modifier = Modifier.width(8.dp))

    Text(
      text = "${(currentVolume * 100).toInt()}%",
      color = CapCutTextPrimary,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.width(42.dp)
    )

    Slider(
      value = currentVolume,
      onValueChange = onVolumeChange,
      valueRange = 0f..2.0f,
      colors = SliderDefaults.colors(
        thumbColor = CapCutTeal,
        activeTrackColor = CapCutTeal,
        inactiveTrackColor = Color(0xFF33353E)
      ),
      modifier = Modifier
        .weight(1f)
        .testTag("volume_slider")
    )

    IconButton(
      onClick = onConfirm,
      modifier = Modifier.size(36.dp).testTag("volume_confirm_button")
    ) {
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = "Confirm",
        tint = CapCutTeal,
        modifier = Modifier.size(22.dp)
      )
    }
  }
}

// 3. Sub-Toolbar: Filters
@Composable
private fun FiltersSubToolbar(
  filters: List<FilterItem>,
  currentFilter: FilterItem,
  intensity: Float,
  onFilterSelect: (FilterItem) -> Unit,
  onIntensityChange: (Float) -> Unit,
  onConfirm: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(68.dp)
      .padding(horizontal = 8.dp),
    verticalArrangement = Arrangement.Center
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Horizontal Filter Pills
      val scrollState = rememberScrollState()
      Row(
        modifier = Modifier
          .weight(1f)
          .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        filters.forEach { filter ->
          val isSelected = filter.id == currentFilter.id
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(if (isSelected) CapCutTeal else Color(0xFF23252B))
              .clickable { onFilterSelect(filter) }
              .padding(horizontal = 12.dp, vertical = 6.dp)
              .testTag("filter_pill_${filter.id}"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = filter.name,
              color = if (isSelected) Color.Black else CapCutTextPrimary,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
          }
        }
      }

      // Confirm Checkmark
      IconButton(
        onClick = onConfirm,
        modifier = Modifier.size(36.dp).testTag("filter_confirm_button")
      ) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = "Confirm filter",
          tint = CapCutTeal,
          modifier = Modifier.size(22.dp)
        )
      }
    }
  }
}

// 4. Sub-Toolbar: Adjust
@Composable
private fun AdjustSubToolbar(
  adjustSettings: AdjustSettings,
  onAdjustChange: (AdjustSettings) -> Unit,
  onReset: () -> Unit,
  onConfirm: () -> Unit
) {
  var selectedTab by remember { mutableStateOf("brightness") }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(68.dp)
      .padding(horizontal = 8.dp),
    verticalArrangement = Arrangement.Center
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Reset button
      IconButton(
        onClick = onReset,
        modifier = Modifier.size(32.dp).testTag("adjust_reset_button")
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Reset adjust",
          tint = CapCutTextSecondary,
          modifier = Modifier.size(18.dp)
        )
      }

      // Slider for the selected parameter
      Slider(
        value = when (selectedTab) {
          "brightness" -> adjustSettings.brightness
          "contrast" -> adjustSettings.contrast
          "saturation" -> adjustSettings.saturation
          "vignette" -> adjustSettings.vignette
          else -> 0f
        },
        onValueChange = { newVal ->
          when (selectedTab) {
            "brightness" -> onAdjustChange(adjustSettings.copy(brightness = newVal))
            "contrast" -> onAdjustChange(adjustSettings.copy(contrast = newVal))
            "saturation" -> onAdjustChange(adjustSettings.copy(saturation = newVal))
            "vignette" -> onAdjustChange(adjustSettings.copy(vignette = newVal))
          }
        },
        valueRange = if (selectedTab == "vignette") 0f..100f else -50f..50f,
        colors = SliderDefaults.colors(
          thumbColor = CapCutTeal,
          activeTrackColor = CapCutTeal,
          inactiveTrackColor = Color(0xFF33353E)
        ),
        modifier = Modifier.weight(1f).testTag("adjust_slider")
      )

      // Confirm Checkmark
      IconButton(
        onClick = onConfirm,
        modifier = Modifier.size(32.dp).testTag("adjust_confirm_button")
      ) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = "Confirm adjust",
          tint = CapCutTeal,
          modifier = Modifier.size(22.dp)
        )
      }
    }
  }
}

// 5. Sub-Toolbar: Aspect Ratio
@Composable
private fun AspectRatioSubToolbar(
  currentRatio: AspectRatioOption,
  onSelectRatio: (AspectRatioOption) -> Unit,
  onConfirm: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(68.dp)
      .padding(horizontal = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier.weight(1f),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      AspectRatioOption.entries.forEach { option ->
        val isSelected = option == currentRatio
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) CapCutTeal else Color(0xFF22242B))
            .clickable { onSelectRatio(option) }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("aspect_ratio_${option.name}"),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = option.label,
              color = if (isSelected) Color.Black else CapCutTextPrimary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = option.displayTag,
              color = if (isSelected) Color.Black else CapCutTextSecondary,
              fontSize = 8.sp
            )
          }
        }
      }
    }

    IconButton(
      onClick = onConfirm,
      modifier = Modifier.size(36.dp).testTag("aspect_ratio_confirm")
    ) {
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = "Done",
        tint = CapCutTeal,
        modifier = Modifier.size(22.dp)
      )
    }
  }
}

// 6. Sub-Toolbar: Text
@Composable
private fun TextSubToolbar(
  onBack: () -> Unit,
  onAddText: () -> Unit,
  onAutoCaptions: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(68.dp)
      .padding(horizontal = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
      Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CapCutTextPrimary)
    }
    ToolButton(label = "Add text", icon = Icons.Default.Person, onClick = onAddText, testTag = "text_add")
    ToolButton(label = "Auto captions", icon = Icons.Default.Person, badge = "AI", onClick = onAutoCaptions, testTag = "text_auto_captions")
    ToolButton(label = "Templates", icon = Icons.Default.Person, onClick = onAddText, testTag = "text_templates")
    ToolButton(label = "Auto lyrics", icon = Icons.Default.Person, onClick = onAutoCaptions, testTag = "text_lyrics")
  }
}

// 7. Sub-Toolbar: Audio
@Composable
private fun AudioSubToolbar(
  onBack: () -> Unit,
  onAddAudio: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(68.dp)
      .padding(horizontal = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
      Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CapCutTextPrimary)
    }
    ToolButton(label = "Sounds", icon = Icons.Default.Person, onClick = onAddAudio, testTag = "audio_sounds")
    ToolButton(label = "Copyright", icon = Icons.Default.Person, onClick = onAddAudio, testTag = "audio_copyright")
    ToolButton(label = "Effects", icon = Icons.Default.Person, onClick = onAddAudio, testTag = "audio_effects")
    ToolButton(label = "Extracted", icon = Icons.Default.Person, onClick = onAddAudio, testTag = "audio_extracted")
    ToolButton(label = "Voiceover", icon = Icons.Default.Person, onClick = onAddAudio, testTag = "audio_voiceover")
  }
}

// 8. Sub-Toolbar: Effects
@Composable
private fun EffectsSubToolbar(
  onBack: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(68.dp)
      .padding(horizontal = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
      Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CapCutTextPrimary)
    }
    ToolButton(label = "Video effects", icon = Icons.Default.Person, onClick = {}, testTag = "fx_video")
    ToolButton(label = "Body effects", icon = Icons.Default.Person, onClick = {}, testTag = "fx_body")
    ToolButton(label = "Photo effects", icon = Icons.Default.Person, onClick = {}, testTag = "fx_photo")
    ToolButton(label = "AI effects", icon = Icons.Default.Person, badge = "AI", onClick = {}, testTag = "fx_ai")
  }
}
