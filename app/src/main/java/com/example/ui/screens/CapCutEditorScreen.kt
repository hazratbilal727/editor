package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.model.SubMenuType
import com.example.ui.components.AddTextDialog
import com.example.ui.components.CapCutBottomBar
import com.example.ui.components.CapCutTopBar
import com.example.ui.components.ExportDialog
import com.example.ui.components.ResolutionSheet
import com.example.ui.components.SfxDialog
import com.example.ui.components.TimelineView
import com.example.ui.components.TransitionsDialog
import com.example.ui.components.VideoPreviewArea
import com.example.ui.components.VoiceChangerDialog
import com.example.ui.theme.CapCutBackground
import com.example.viewmodel.VideoEditorViewModel

@Composable
fun CapCutEditorScreen(
  viewModel: VideoEditorViewModel,
  onNavigateBack: () -> Unit,
  onOpenMediaPickerForAppend: () -> Unit
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  // Intercept back key: close submenu first, otherwise return to home
  BackHandler {
    if (uiState.activeSubMenu != SubMenuType.NONE) {
      viewModel.closeSubMenu()
    } else {
      onNavigateBack()
    }
  }

  // Toast message trigger
  LaunchedEffect(uiState.toastMessage) {
    uiState.toastMessage?.let { msg ->
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
      viewModel.clearToast()
    }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      CapCutTopBar(
        uiState = uiState,
        onBack = onNavigateBack,
        onUndo = { viewModel.undo() },
        onRedo = { viewModel.redo() },
        onOpenResolutionSheet = { viewModel.openResolutionSheet(true) },
        onExport = { viewModel.triggerExport() }
      )
    },
    bottomBar = {
      CapCutBottomBar(
        uiState = uiState,
        onMainToolClick = { viewModel.onMainToolClicked(it) },
        onCloseSubMenu = { viewModel.closeSubMenu() },
        onSplitClip = { viewModel.splitCurrentClip() },
        onDeleteClip = { viewModel.deleteSelectedClip() },
        onDuplicateClip = { viewModel.duplicateSelectedClip() },
        onRotateClip = { viewModel.rotateSelectedClip() },
        onReverseClip = { viewModel.reverseSelectedClip() },
        onFreezeClip = { viewModel.freezeFrame() },
        onOpenSpeedMenu = { viewModel.openSpeedMenu() },
        onOpenVolumeMenu = { viewModel.openVolumeMenu() },
        onSetSpeed = { viewModel.setClipSpeed(it) },
        onSetVolume = { viewModel.setClipVolume(it) },
        onSetFilter = { viewModel.setFilter(it) },
        onSetFilterIntensity = { viewModel.setFilterIntensity(it) },
        onUpdateAdjust = { viewModel.updateAdjustSettings(it) },
        onResetAdjust = { viewModel.resetAdjustSettings() },
        onAddAudio = { title, cat -> viewModel.addAudioTrack(title, cat) },
        onAddCaptions = { viewModel.addAutoCaptions() },
        onOpenSfxDialog = { viewModel.openSfxDialog() },
        onOpenVoiceoverDialog = { viewModel.openVoiceChanger() }
      )
    },
    containerColor = CapCutBackground
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .background(CapCutBackground)
    ) {
      // 1. Video Preview Area (Weighted 1f)
      VideoPreviewArea(
        uiState = uiState,
        onTogglePlayPause = { viewModel.togglePlayPause() },
        onToggleMute = { viewModel.toggleMute() },
        onToggleFullscreen = { viewModel.toggleFullscreen() },
        modifier = Modifier.weight(1f)
      )

      // 2. Interactive Precision Timeline View
      TimelineView(
        uiState = uiState,
        onSeekTo = { viewModel.seekTo(it) },
        onScrubRelative = { viewModel.scrubRelative(it) },
        onSelectClip = { viewModel.selectClip(it) },
        onDeselectClip = { viewModel.deselectClip() },
        onOpenTransition = { viewModel.openTransitionDialog(it) },
        onAppendMediaClick = onOpenMediaPickerForAppend,
        onTogglePlayPause = { viewModel.togglePlayPause() },
        onToggleMute = { viewModel.toggleMute() },
        onRemoveAudio = { viewModel.removeAudioTrack(it) },
        onRemoveText = { viewModel.removeTextTrack(it) },
        modifier = Modifier.fillMaxWidth()
      )
    }

    // Modal Overlays
    if (uiState.showResolutionSheet) {
      ResolutionSheet(
        uiState = uiState,
        onDismiss = { viewModel.openResolutionSheet(false) },
        onConfirm = { label, fps, hdr -> viewModel.setResolution(label, fps, hdr) }
      )
    }

    if (uiState.showTransitionDialog) {
      TransitionsDialog(
        onDismiss = { viewModel.closeTransitionDialog() },
        onApply = { type, dur -> viewModel.applyTransition(type, dur) }
      )
    }

    if (uiState.showExportDialog) {
      ExportDialog(
        uiState = uiState,
        onDismiss = { viewModel.dismissExportDialog() }
      )
    }

    if (uiState.showSfxDialog) {
      SfxDialog(
        onDismiss = { viewModel.closeSfxDialog() },
        onSelectSfx = { title, dur -> viewModel.addSoundEffect(title, dur) }
      )
    }

    if (uiState.showVoiceChangerDialog) {
      VoiceChangerDialog(
        onDismiss = { viewModel.closeVoiceChanger() },
        onSelectEffect = { viewModel.applyVoiceEffect(it) }
      )
    }

    if (uiState.showAddTextDialog) {
      AddTextDialog(
        onDismiss = { viewModel.showAddTextModal(false) },
        onConfirm = { text, colorHex, size -> viewModel.addTextTrack(text, colorHex, size) }
      )
    }
  }
}

// Helpers on ViewModel
fun VideoEditorViewModel.openSpeedMenu() {
  onMainToolClicked(com.example.model.MainTool.EDIT)
}

fun VideoEditorViewModel.openVolumeMenu() {
  onMainToolClicked(com.example.model.MainTool.EDIT)
}
