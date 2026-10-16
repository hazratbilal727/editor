package com.example.viewmodel

import android.app.Application
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ProjectEntity
import com.example.model.AdjustSettings
import com.example.model.AspectRatioOption
import com.example.model.AudioTrackItem
import com.example.model.FilterItem
import com.example.model.MainTool
import com.example.model.ResolutionSetting
import com.example.model.SubMenuType
import com.example.model.TextTrackItem
import com.example.model.TransitionType
import com.example.model.VideoClip
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class VideoEditorViewModel(application: Application) : AndroidViewModel(application) {

  private val _uiState = MutableStateFlow(EditorUiState())
  val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

  private var playbackJob: Job? = null
  private val undoStack = mutableListOf<EditorUiState>()
  private val redoStack = mutableListOf<EditorUiState>()

  private val toneGenerator by lazy {
    try {
      ToneGenerator(AudioManager.STREAM_MUSIC, 60)
    } catch (_: Exception) {
      null
    }
  }

  init {
    loadDefaultDemoClips()
  }

  fun loadDefaultDemoClips() {
    val sampleClips = listOf(
      VideoClip(
        id = "demo_clip_1",
        title = "Sunset Coast.mp4",
        startMs = 0L,
        endMs = 4000L,
        originalDurationMs = 4000L,
        isVideo = true,
        speed = 1.0f,
        volume = 1.0f
      ),
      VideoClip(
        id = "demo_clip_2",
        title = "Urban Motion.mp4",
        startMs = 0L,
        endMs = 3500L,
        originalDurationMs = 3500L,
        isVideo = true,
        speed = 1.0f,
        volume = 1.0f,
        transitionAfter = TransitionType.DISSOLVE
      ),
      VideoClip(
        id = "demo_clip_3",
        title = "Night Lights.mp4",
        startMs = 0L,
        endMs = 4500L,
        originalDurationMs = 4500L,
        isVideo = true,
        speed = 1.0f,
        volume = 1.0f
      )
    )
    loadClips(sampleClips)
  }

  private fun saveSnapshot() {
    undoStack.add(_uiState.value.copy())
    if (undoStack.size > 20) undoStack.removeAt(0)
    redoStack.clear()
    updateUndoRedoStatus()
  }

  private fun updateUndoRedoStatus() {
    _uiState.update {
      it.copy(
        canUndo = undoStack.isNotEmpty(),
        canRedo = redoStack.isNotEmpty()
      )
    }
  }

  fun undo() {
    if (undoStack.isEmpty()) return
    val current = _uiState.value
    redoStack.add(current)
    val previous = undoStack.removeAt(undoStack.lastIndex)
    _uiState.value = previous
    updateUndoRedoStatus()
    showToast("Undo performed")
  }

  fun redo() {
    if (redoStack.isEmpty()) return
    val current = _uiState.value
    undoStack.add(current)
    val next = redoStack.removeAt(redoStack.lastIndex)
    _uiState.value = next
    updateUndoRedoStatus()
    showToast("Redo performed")
  }

  fun loadClips(newClips: List<VideoClip>, projectTitle: String = "My Edit") {
    val total = calculateTotalDuration(newClips)
    _uiState.update {
      it.copy(
        projectTitle = projectTitle,
        clips = newClips,
        totalDurationMs = total,
        currentTimeMs = 0L,
        selectedClipId = newClips.firstOrNull()?.id,
        isPlaying = false
      )
    }
  }

  fun appendClips(moreClips: List<VideoClip>) {
    saveSnapshot()
    val updated = _uiState.value.clips + moreClips
    val total = calculateTotalDuration(updated)
    _uiState.update {
      it.copy(
        clips = updated,
        totalDurationMs = total
      )
    }
    showToast("Added ${moreClips.size} media clips")
  }

  private fun calculateTotalDuration(clips: List<VideoClip>): Long {
    return clips.sumOf { (it.durationMs / it.speed).toLong() }.coerceAtLeast(1000L)
  }

  fun togglePlayPause() {
    if (_uiState.value.isPlaying) {
      pausePlayback()
    } else {
      startPlayback()
    }
  }

  fun startPlayback() {
    playbackJob?.cancel()
    _uiState.update { it.copy(isPlaying = true) }
    playbackJob = viewModelScope.launch {
      while (_uiState.value.isPlaying) {
        delay(40)
        val current = _uiState.value.currentTimeMs
        val total = _uiState.value.totalDurationMs
        if (current >= total) {
          _uiState.update { it.copy(currentTimeMs = 0L) }
        } else {
          _uiState.update { it.copy(currentTimeMs = (current + 40).coerceAtMost(total)) }
        }
      }
    }
  }

  fun pausePlayback() {
    playbackJob?.cancel()
    _uiState.update { it.copy(isPlaying = false) }
  }

  fun seekTo(timeMs: Long) {
    val bounded = timeMs.coerceIn(0L, _uiState.value.totalDurationMs)
    _uiState.update { it.copy(currentTimeMs = bounded) }
  }

  fun scrubRelative(deltaMs: Long) {
    seekTo(_uiState.value.currentTimeMs + deltaMs)
  }

  fun toggleMute() {
    _uiState.update { it.copy(isMuted = !it.isMuted) }
    showToast(if (_uiState.value.isMuted) "Project muted" else "Project unmuted")
  }

  fun selectClip(clipId: String) {
    _uiState.update {
      it.copy(
        selectedClipId = clipId,
        activeSubMenu = SubMenuType.CLIP_OPTIONS,
        clips = it.clips.map { c -> c.copy(isSelected = c.id == clipId) }
      )
    }
  }

  fun deselectClip() {
    _uiState.update {
      it.copy(
        selectedClipId = null,
        activeSubMenu = SubMenuType.NONE,
        clips = it.clips.map { c -> c.copy(isSelected = false) }
      )
    }
  }

  fun onMainToolClicked(tool: MainTool) {
    when (tool) {
      MainTool.EDIT -> {
        val selected = _uiState.value.selectedClipId ?: _uiState.value.clips.firstOrNull()?.id
        _uiState.update {
          it.copy(
            selectedClipId = selected,
            activeSubMenu = SubMenuType.CLIP_OPTIONS
          )
        }
      }
      MainTool.AUDIO -> {
        _uiState.update { it.copy(activeSubMenu = SubMenuType.AUDIO_OPTIONS) }
      }
      MainTool.TEXT -> {
        _uiState.update { it.copy(showAddTextDialog = true) }
      }
      MainTool.FILTERS -> {
        _uiState.update { it.copy(activeSubMenu = SubMenuType.FILTER) }
      }
      MainTool.ADJUST -> {
        _uiState.update { it.copy(activeSubMenu = SubMenuType.ADJUST) }
      }
      MainTool.RATIO -> {
        cycleAspectRatio()
      }
      MainTool.EFFECTS -> {
        _uiState.update { it.copy(showVoiceChangerDialog = true) }
      }
      MainTool.CANVAS -> {
        showToast("Canvas background set to fit")
      }
    }
  }

  fun closeSubMenu() {
    _uiState.update { it.copy(activeSubMenu = SubMenuType.NONE) }
  }

  fun splitCurrentClip() {
    saveSnapshot()
    val state = _uiState.value
    val currentTime = state.currentTimeMs
    val clips = state.clips.toMutableList()

    var accumulated = 0L
    var targetIndex = -1
    for (i in clips.indices) {
      val clipDuration = (clips[i].durationMs / clips[i].speed).toLong()
      if (currentTime > accumulated && currentTime < accumulated + clipDuration) {
        targetIndex = i
        break
      }
      accumulated += clipDuration
    }

    if (targetIndex != -1) {
      val target = clips[targetIndex]
      val offsetInClip = ((currentTime - accumulated) * target.speed).toLong()
      val firstEnd = target.startMs + offsetInClip
      val firstPart = target.copy(id = UUID.randomUUID().toString(), endMs = firstEnd)
      val secondPart = target.copy(id = UUID.randomUUID().toString(), startMs = firstEnd)

      clips.removeAt(targetIndex)
      clips.add(targetIndex, firstPart)
      clips.add(targetIndex + 1, secondPart)

      val total = calculateTotalDuration(clips)
      _uiState.update {
        it.copy(
          clips = clips,
          totalDurationMs = total,
          selectedClipId = secondPart.id
        )
      }
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
      showToast("Clip split at current position")
    } else {
      showToast("Move playhead inside a clip to split")
    }
  }

  fun deleteSelectedClip() {
    saveSnapshot()
    val selectedId = _uiState.value.selectedClipId ?: return
    val remaining = _uiState.value.clips.filter { it.id != selectedId }
    if (remaining.isEmpty()) {
      showToast("Cannot delete the only clip")
      return
    }
    val total = calculateTotalDuration(remaining)
    _uiState.update {
      it.copy(
        clips = remaining,
        totalDurationMs = total,
        selectedClipId = remaining.firstOrNull()?.id,
        activeSubMenu = SubMenuType.NONE
      )
    }
    showToast("Clip deleted")
  }

  fun duplicateSelectedClip() {
    saveSnapshot()
    val selected = _uiState.value.selectedClip ?: return
    val duplicate = selected.copy(
      id = UUID.randomUUID().toString(),
      title = "${selected.title} (Copy)"
    )
    val index = _uiState.value.clips.indexOfFirst { it.id == selected.id }
    val mutable = _uiState.value.clips.toMutableList()
    mutable.add(index + 1, duplicate)
    val total = calculateTotalDuration(mutable)
    _uiState.update {
      it.copy(
        clips = mutable,
        totalDurationMs = total,
        selectedClipId = duplicate.id
      )
    }
    showToast("Clip duplicated")
  }

  fun setClipSpeed(newSpeed: Float) {
    saveSnapshot()
    val selectedId = _uiState.value.selectedClipId ?: return
    val updated = _uiState.value.clips.map {
      if (it.id == selectedId) it.copy(speed = newSpeed) else it
    }
    val total = calculateTotalDuration(updated)
    _uiState.update { it.copy(clips = updated, totalDurationMs = total) }
    showToast("Speed set to ${newSpeed}x")
  }

  fun setClipVolume(newVolume: Float) {
    val selectedId = _uiState.value.selectedClipId ?: return
    val updated = _uiState.value.clips.map {
      if (it.id == selectedId) it.copy(volume = newVolume) else it
    }
    _uiState.update { it.copy(clips = updated) }
  }

  fun rotateSelectedClip() {
    saveSnapshot()
    val selectedId = _uiState.value.selectedClipId ?: return
    val updated = _uiState.value.clips.map {
      if (it.id == selectedId) {
        val newRotation = (it.rotationDegrees + 90f) % 360f
        it.copy(rotationDegrees = newRotation)
      } else it
    }
    _uiState.update { it.copy(clips = updated) }
    showToast("Rotated 90°")
  }

  fun reverseSelectedClip() {
    saveSnapshot()
    val selectedId = _uiState.value.selectedClipId ?: return
    val updated = _uiState.value.clips.map {
      if (it.id == selectedId) it.copy(isReversed = !it.isReversed) else it
    }
    _uiState.update { it.copy(clips = updated) }
    val isRev = updated.firstOrNull { it.id == selectedId }?.isReversed == true
    showToast(if (isRev) "Clip reversed (offline playback)" else "Clip normal playback")
  }

  fun freezeFrame() {
    saveSnapshot()
    val selected = _uiState.value.selectedClip ?: return
    val frozenClip = selected.copy(
      id = UUID.randomUUID().toString(),
      title = "${selected.title} [Freeze]",
      startMs = 0L,
      endMs = 3000L,
      originalDurationMs = 3000L,
      isFrozen = true
    )
    val index = _uiState.value.clips.indexOfFirst { it.id == selected.id }
    val mutable = _uiState.value.clips.toMutableList()
    mutable.add(index + 1, frozenClip)
    val total = calculateTotalDuration(mutable)
    _uiState.update {
      it.copy(
        clips = mutable,
        totalDurationMs = total,
        selectedClipId = frozenClip.id
      )
    }
    showToast("Inserted 3.0s frozen frame")
  }

  fun setFilter(filter: FilterItem) {
    saveSnapshot()
    _uiState.update { it.copy(selectedFilter = filter) }
    showToast("Filter applied: ${filter.name}")
  }

  fun setFilterIntensity(intensity: Float) {
    _uiState.update { it.copy(filterIntensity = intensity) }
  }

  fun updateAdjustSettings(adjustSettings: AdjustSettings) {
    _uiState.update { it.copy(adjustSettings = adjustSettings) }
  }

  fun resetAdjustSettings() {
    saveSnapshot()
    _uiState.update { it.copy(adjustSettings = AdjustSettings()) }
    showToast("Adjustments reset")
  }

  fun setAspectRatio(ratio: AspectRatioOption) {
    saveSnapshot()
    _uiState.update { it.copy(aspectRatio = ratio) }
    showToast("Canvas aspect ratio: ${ratio.label}")
  }

  private fun cycleAspectRatio() {
    val current = _uiState.value.aspectRatio
    val options = AspectRatioOption.entries
    val nextIndex = (options.indexOf(current) + 1) % options.size
    setAspectRatio(options[nextIndex])
  }

  fun addAudioTrack(title: String, category: String = "BGM") {
    saveSnapshot()
    val newTrack = AudioTrackItem(
      id = UUID.randomUUID().toString(),
      title = title,
      startMs = _uiState.value.currentTimeMs,
      endMs = (_uiState.value.currentTimeMs + 6000L).coerceAtMost(_uiState.value.totalDurationMs),
      category = category
    )
    _uiState.update { it.copy(audioTracks = it.audioTracks + newTrack) }
    toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 70)
    showToast("Added audio track: $title")
  }

  fun removeAudioTrack(id: String) {
    saveSnapshot()
    _uiState.update { it.copy(audioTracks = it.audioTracks.filter { t -> t.id != id }) }
    showToast("Removed audio track")
  }

  fun addSoundEffect(title: String, durationMs: Long) {
    saveSnapshot()
    val track = AudioTrackItem(
      id = UUID.randomUUID().toString(),
      title = title,
      startMs = _uiState.value.currentTimeMs,
      endMs = _uiState.value.currentTimeMs + durationMs,
      category = "SFX"
    )
    _uiState.update { it.copy(audioTracks = it.audioTracks + track, showSfxDialog = false) }
    toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 100)
    showToast("Added SFX: $title")
  }

  fun addTextTrack(text: String, colorHex: String = "#FFFFFF", fontSizeSp: Float = 26f) {
    saveSnapshot()
    val newTrack = TextTrackItem(
      id = UUID.randomUUID().toString(),
      text = text,
      startMs = _uiState.value.currentTimeMs,
      endMs = (_uiState.value.currentTimeMs + 3500L).coerceAtMost(_uiState.value.totalDurationMs),
      colorHex = colorHex,
      fontSizeSp = fontSizeSp
    )
    _uiState.update { it.copy(textTracks = it.textTracks + newTrack, showAddTextDialog = false) }
    showToast("Added text overlay")
  }

  fun removeTextTrack(id: String) {
    saveSnapshot()
    _uiState.update { it.copy(textTracks = it.textTracks.filter { t -> t.id != id }) }
    showToast("Removed text track")
  }

  fun addAutoCaptions() {
    saveSnapshot()
    val captions = listOf(
      TextTrackItem(UUID.randomUUID().toString(), "Welcome to my vlog", 500L, 2500L),
      TextTrackItem(UUID.randomUUID().toString(), "Creating cinematic memories", 2700L, 5000L),
      TextTrackItem(UUID.randomUUID().toString(), "Edited with CapCut Offline", 5200L, 8000L)
    )
    _uiState.update { it.copy(textTracks = it.textTracks + captions) }
    showToast("Generated offline captions")
  }

  fun openTransitionDialog(clipId: String) {
    _uiState.update {
      it.copy(
        selectedTransitionClipId = clipId,
        showTransitionDialog = true
      )
    }
  }

  fun closeTransitionDialog() {
    _uiState.update { it.copy(showTransitionDialog = false) }
  }

  fun applyTransition(type: TransitionType, durationMs: Long) {
    saveSnapshot()
    val targetId = _uiState.value.selectedTransitionClipId ?: return
    val updated = _uiState.value.clips.map {
      if (it.id == targetId) it.copy(transitionAfter = type, transitionDurationMs = durationMs) else it
    }
    _uiState.update {
      it.copy(
        clips = updated,
        showTransitionDialog = false
      )
    }
    showToast("Transition: ${type.displayName}")
  }

  fun openVoiceChanger() {
    _uiState.update { it.copy(showVoiceChangerDialog = true) }
  }

  fun closeVoiceChanger() {
    _uiState.update { it.copy(showVoiceChangerDialog = false) }
  }

  fun applyVoiceEffect(effect: String) {
    saveSnapshot()
    val selectedId = _uiState.value.selectedClipId ?: return
    val updated = _uiState.value.clips.map {
      if (it.id == selectedId) it.copy(voiceEffect = effect) else it
    }
    _uiState.update {
      it.copy(
        clips = updated,
        showVoiceChangerDialog = false
      )
    }
    showToast("Applied voice effect: $effect")
  }

  fun openSfxDialog() {
    _uiState.update { it.copy(showSfxDialog = true) }
  }

  fun closeSfxDialog() {
    _uiState.update { it.copy(showSfxDialog = false) }
  }

  fun openResolutionSheet(show: Boolean) {
    _uiState.update { it.copy(showResolutionSheet = show) }
  }

  fun setResolution(label: String, fps: Int, smartHdr: Boolean) {
    val estSize = when (label) {
      "720P" -> 14.2f
      "1080P" -> 28.5f
      "2K/4K" -> 64.0f
      else -> 25.0f
    }
    _uiState.update {
      it.copy(
        resolutionSetting = ResolutionSetting(label, fps, smartHdr, estSize),
        showResolutionSheet = false
      )
    }
    showToast("Export set to $label ${fps}fps")
  }

  fun triggerExport() {
    pausePlayback()
    _uiState.update {
      it.copy(
        showExportDialog = true,
        exportProgress = 0f,
        isExportFinished = false
      )
    }

    viewModelScope.launch {
      for (step in 1..20) {
        delay(100)
        _uiState.update { it.copy(exportProgress = step / 20f) }
      }
      _uiState.update { it.copy(isExportFinished = true) }
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
      saveProjectLocally()
    }
  }

  private fun saveProjectLocally() {
    viewModelScope.launch {
      try {
        val db = AppDatabase.getDatabase(getApplication())
        val entity = ProjectEntity(
          id = _uiState.value.currentProjectId ?: UUID.randomUUID().toString(),
          title = _uiState.value.projectTitle,
          durationMs = _uiState.value.totalDurationMs,
          thumbnailUri = _uiState.value.clips.firstOrNull()?.uriString,
          clipCount = _uiState.value.clips.size,
          aspectRatio = _uiState.value.aspectRatio.name,
          lastModified = System.currentTimeMillis(),
          clipsJson = ""
        )
        db.projectDao().insertProject(entity)
      } catch (_: Exception) {}
    }
  }

  fun dismissExportDialog() {
    _uiState.update { it.copy(showExportDialog = false) }
  }

  fun toggleFullscreen() {
    _uiState.update { it.copy(isFullscreen = !it.isFullscreen) }
  }

  fun togglePipOverlay() {
    _uiState.update { it.copy(pipOverlayEnabled = !it.pipOverlayEnabled) }
    showToast(if (_uiState.value.pipOverlayEnabled) "PIP overlay enabled" else "PIP overlay hidden")
  }

  fun showAddTextModal(show: Boolean) {
    _uiState.update { it.copy(showAddTextDialog = show) }
  }

  fun showAddAudioModal(show: Boolean) {
    _uiState.update { it.copy(showAddAudioDialog = show) }
  }

  fun clearToast() {
    _uiState.update { it.copy(toastMessage = null) }
  }

  fun showToast(msg: String) {
    _uiState.update { it.copy(toastMessage = msg) }
  }
}
