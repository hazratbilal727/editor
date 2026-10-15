package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.SubMenuType
import com.example.ui.components.AddAudioSheet
import com.example.ui.components.AddTextSheet
import com.example.ui.components.CapCutBottomBar
import com.example.ui.components.CapCutTopBar
import com.example.ui.components.ExportDialog
import com.example.ui.components.FullscreenVideoModal
import com.example.ui.components.ResolutionSheet
import com.example.ui.components.TimelineView
import com.example.ui.components.VideoPreviewArea
import com.example.ui.theme.CapCutBackground
import com.example.viewmodel.VideoEditorViewModel

@Composable
fun CapCutEditorScreen(
  viewModel: VideoEditorViewModel = viewModel(),
  onCloseClick: () -> Unit = {},
  onAddMediaClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  // Show toasts
  LaunchedEffect(uiState.toastMessage) {
    uiState.toastMessage?.let { msg ->
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
      viewModel.clearToast()
    }
  }

  // Handle system back button to exit sub-menus or return to home
  BackHandler(enabled = true) {
    if (uiState.activeSubMenu != SubMenuType.NONE) {
      viewModel.closeSubMenu()
    } else {
      onCloseClick()
    }
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(CapCutBackground),
    containerColor = CapCutBackground,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.statusBars)
      ) {
        CapCutTopBar(
          currentResolution = uiState.resolutionSetting.label,
          onCloseClick = onCloseClick,
          onSearchClick = { viewModel.showToast("Search effects and sounds") },
          onProClick = { viewModel.showToast("CapCut Pro: All VIP features unlocked") },
          onResolutionClick = { viewModel.openResolutionSheet(true) },
          onExportClick = { viewModel.triggerExport() }
        )
      }
    },
    bottomBar = {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.navigationBars)
      ) {
        val selectedClip = uiState.clips.find { it.id == uiState.selectedClipId }
        CapCutBottomBar(
          activeSubMenu = uiState.activeSubMenu,
          selectedTool = null,
          availableFilters = viewModel.availableFilters,
          currentFilter = uiState.selectedFilter,
          filterIntensity = uiState.filterIntensity,
          adjustSettings = uiState.adjustSettings,
          aspectRatio = uiState.aspectRatio,
          currentSpeed = selectedClip?.speed ?: 1.0f,
          currentVolume = selectedClip?.volume ?: 1.0f,
          onToolClick = { tool -> viewModel.onMainToolClicked(tool) },
          onCloseSubMenu = { viewModel.closeSubMenu() },
          onSplitClick = { viewModel.splitCurrentClip() },
          onSpeedClick = { viewModel.openSpeedMenu() },
          onVolumeClick = { viewModel.openVolumeMenu() },
          onDuplicateClick = { viewModel.duplicateSelectedClip() },
          onSpeedChange = { speed -> viewModel.setClipSpeed(speed) },
          onVolumeChange = { vol -> viewModel.setClipVolume(vol) },
          onDeleteClick = { viewModel.deleteSelectedClip() },
          onFilterSelect = { filter -> viewModel.setFilter(filter) },
          onFilterIntensityChange = { intensity -> viewModel.setFilterIntensity(intensity) },
          onAdjustChange = { adjust -> viewModel.updateAdjustSettings(adjust) },
          onAdjustReset = { viewModel.resetAdjustSettings() },
          onAspectRatioSelect = { ratio -> viewModel.setAspectRatio(ratio) },
          onAddTextClick = { viewModel.showAddTextModal(true) },
          onAddAudioClick = { viewModel.showAddAudioModal(true) },
          onAutoCaptionsClick = { viewModel.addAutoCaptions() }
        )
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // 1. VIDEO PREVIEW PLAYER (occupies upper half)
      VideoPreviewArea(
        currentTimeMs = uiState.currentTimeMs,
        isPlaying = uiState.isPlaying,
        aspectRatio = uiState.aspectRatio,
        filter = uiState.selectedFilter,
        filterIntensity = uiState.filterIntensity,
        adjustSettings = uiState.adjustSettings,
        activeCaptions = uiState.textTracks,
        pipEnabled = uiState.pipOverlayEnabled,
        canUndo = uiState.canUndo,
        canRedo = uiState.canRedo,
        onPlayPauseToggle = { viewModel.togglePlayPause() },
        onFullscreenToggle = { viewModel.toggleFullscreen() },
        onPipToggle = { viewModel.togglePipOverlay() },
        onUndoClick = { viewModel.undo() },
        onRedoClick = { viewModel.redo() },
        modifier = Modifier
          .fillMaxWidth()
          .weight(1.25f)
      )

      HorizontalDivider(color = Color(0xFF1E2026), thickness = 1.dp)

      // 2. TIMELINE & SCRUBBER AREA (occupies lower half)
      TimelineView(
        currentTimeMs = uiState.currentTimeMs,
        totalDurationMs = uiState.totalDurationMs,
        isMuted = uiState.isMuted,
        clips = uiState.clips,
        audioTracks = uiState.audioTracks,
        textTracks = uiState.textTracks,
        selectedClipId = uiState.selectedClipId,
        onMuteToggle = { viewModel.toggleMute() },
        onCoverClick = { viewModel.showToast("Cover selected") },
        onClipSelect = { clipId -> viewModel.selectClip(clipId) },
        onAddMediaClick = onAddMediaClick,
        onAddAudioClick = { viewModel.showAddAudioModal(true) },
        onAddTextClick = { viewModel.showAddTextModal(true) },
        onScrubRelative = { delta -> viewModel.scrubRelative(delta) },
        onSeekTo = { time -> viewModel.seekTo(time) },
        modifier = Modifier
          .fillMaxWidth()
          .weight(1.0f)
      )

      HorizontalDivider(color = Color(0xFF1E2026), thickness = 1.dp)
    }
  }

  // Bottom Sheets & Dialogs
  if (uiState.showResolutionSheet) {
    ResolutionSheet(
      currentSetting = uiState.resolutionSetting,
      onDismiss = { viewModel.openResolutionSheet(false) },
      onApply = { label, fps, hdr ->
        viewModel.setResolution(label, fps, hdr)
      }
    )
  }

  if (uiState.showExportDialog) {
    ExportDialog(
      progress = uiState.exportProgress,
      isFinished = uiState.isExportFinished,
      resolution = uiState.resolutionSetting.label,
      onDismiss = { viewModel.dismissExportDialog() },
      onShare = { platform ->
        viewModel.showToast("Shared to $platform!")
        viewModel.dismissExportDialog()
      }
    )
  }

  if (uiState.showAddTextDialog) {
    AddTextSheet(
      onDismiss = { viewModel.showAddTextModal(false) },
      onAddText = { text -> viewModel.addTextTrack(text) }
    )
  }

  if (uiState.showAddAudioDialog) {
    AddAudioSheet(
      onDismiss = { viewModel.showAddAudioModal(false) },
      onSelectTrack = { track -> viewModel.addAudioTrack(track) }
    )
  }

  if (uiState.isFullscreen) {
    FullscreenVideoModal(
      isPlaying = uiState.isPlaying,
      currentTimeMs = uiState.currentTimeMs,
      activeCaptions = uiState.textTracks,
      onPlayPauseToggle = { viewModel.togglePlayPause() },
      onDismiss = { viewModel.toggleFullscreen() }
    )
  }
}
