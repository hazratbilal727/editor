package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.model.AdjustSettings
import com.example.model.AspectRatioOption
import com.example.model.AudioTrackItem
import com.example.model.FilterItem
import com.example.model.MainTool
import com.example.model.ResolutionSetting
import com.example.model.SubMenuType
import com.example.model.TextTrackItem
import com.example.model.VideoClip
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditorUiState(
  val currentTimeMs: Long = 0L,
  val totalDurationMs: Long = 28000L,
  val isPlaying: Boolean = false,
  val isMuted: Boolean = false,
  val activeSubMenu: SubMenuType = SubMenuType.NONE,
  val selectedClipId: String? = null,
  val clips: List<VideoClip> = listOf(
    VideoClip(
      id = "clip_1",
      title = "Mecca Footage",
      startMs = 0L,
      endMs = 28000L,
      originalDurationMs = 28000L,
      thumbnailRes = R.drawable.img_video_clip,
      isSelected = false
    )
  ),
  val audioTracks: List<AudioTrackItem> = emptyList(),
  val textTracks: List<TextTrackItem> = emptyList(),
  val aspectRatio: AspectRatioOption = AspectRatioOption.RATIO_9_16,
  val selectedFilter: FilterItem = FilterItem("none", "Original", 0f),
  val filterIntensity: Float = 0.8f,
  val adjustSettings: AdjustSettings = AdjustSettings(),
  val resolutionSetting: ResolutionSetting = ResolutionSetting(),
  val showResolutionSheet: Boolean = false,
  val showExportDialog: Boolean = false,
  val exportProgress: Float = 0f,
  val isExportFinished: Boolean = false,
  val showAddTextDialog: Boolean = false,
  val showAddAudioDialog: Boolean = false,
  val isFullscreen: Boolean = false,
  val pipOverlayEnabled: Boolean = true,
  val canUndo: Boolean = false,
  val canRedo: Boolean = false,
  val toastMessage: String? = null
)

class VideoEditorViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(EditorUiState())
  val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

  private var playbackJob: Job? = null
  private val historyStack = mutableListOf<EditorUiState>()
  private val redoStack = mutableListOf<EditorUiState>()

  val availableFilters = listOf(
    FilterItem("none", "Original", 0f, 0xFFFFFFFF),
    FilterItem("cinema", "Cinematic", 0.7f, 0xFFFFE8D6),
    FilterItem("teal_orange", "Teal & Gold", 0.85f, 0xFF70D6FF),
    FilterItem("warm_glow", "Warm Glow", 0.6f, 0xFFFFBE0B),
    FilterItem("monochrome", "B & W", 1.0f, 0xFFE0E0E0),
    FilterItem("moody", "Moody Night", 0.75f, 0xFF8338EC),
    FilterItem("vibrant", "Vibrant", 0.8f, 0xFFFF006E)
  )

  fun togglePlayPause() {
    val willPlay = !_uiState.value.isPlaying
    if (willPlay) {
      if (_uiState.value.currentTimeMs >= _uiState.value.totalDurationMs) {
        _uiState.update { it.copy(currentTimeMs = 0L) }
      }
      startPlayback()
    } else {
      pausePlayback()
    }
  }

  private fun startPlayback() {
    _uiState.update { it.copy(isPlaying = true) }
    playbackJob?.cancel()
    playbackJob = viewModelScope.launch {
      while (true) {
        delay(50)
        val current = _uiState.value.currentTimeMs
        val total = _uiState.value.totalDurationMs
        if (current + 50 >= total) {
          _uiState.update { it.copy(currentTimeMs = total, isPlaying = false) }
          break
        } else {
          _uiState.update { it.copy(currentTimeMs = current + 50) }
        }
      }
    }
  }

  fun pausePlayback() {
    playbackJob?.cancel()
    playbackJob = null
    _uiState.update { it.copy(isPlaying = false) }
  }

  fun seekTo(timeMs: Long) {
    val clamped = timeMs.coerceIn(0L, _uiState.value.totalDurationMs)
    _uiState.update { it.copy(currentTimeMs = clamped) }
  }

  fun scrubRelative(deltaMs: Long) {
    seekTo(_uiState.value.currentTimeMs + deltaMs)
  }

  fun toggleMute() {
    _uiState.update { it.copy(isMuted = !it.isMuted) }
  }

  fun selectClip(clipId: String?) {
    _uiState.update { state ->
      val updated = state.clips.map { clip ->
        clip.copy(isSelected = clip.id == clipId)
      }
      state.copy(
        selectedClipId = clipId,
        clips = updated,
        activeSubMenu = if (clipId != null) SubMenuType.CLIP_EDIT else SubMenuType.NONE
      )
    }
  }

  fun loadClips(newClips: List<VideoClip>) {
    if (newClips.isEmpty()) return
    var accumulated = 0L
    val sequentialClips = newClips.mapIndexed { index, clip ->
      val dur = clip.durationMs.coerceAtLeast(1000L)
      val start = accumulated
      accumulated += dur
      clip.copy(
        id = "clip_${index}_${System.currentTimeMillis()}",
        startMs = start,
        endMs = start + dur,
        isSelected = false
      )
    }
    _uiState.update {
      it.copy(
        clips = sequentialClips,
        totalDurationMs = accumulated,
        currentTimeMs = 0L,
        isPlaying = false,
        selectedClipId = null,
        activeSubMenu = SubMenuType.NONE,
        toastMessage = "Imported ${sequentialClips.size} media item(s)"
      )
    }
  }

  fun appendClips(moreClips: List<VideoClip>) {
    if (moreClips.isEmpty()) return
    var accumulated = _uiState.value.totalDurationMs
    val sequential = moreClips.mapIndexed { index, clip ->
      val dur = clip.durationMs.coerceAtLeast(1000L)
      val start = accumulated
      accumulated += dur
      clip.copy(
        id = "clip_${System.currentTimeMillis()}_$index",
        startMs = start,
        endMs = start + dur,
        isSelected = false
      )
    }
    _uiState.update {
      it.copy(
        clips = it.clips + sequential,
        totalDurationMs = accumulated,
        toastMessage = "Added ${sequential.size} clip(s)"
      )
    }
  }

  fun deselectClip() {
    selectClip(null)
  }

  fun onMainToolClicked(tool: MainTool) {
    when (tool) {
      MainTool.EDIT -> {
        // Toggle clip edit mode
        if (_uiState.value.clips.isNotEmpty()) {
          selectClip(_uiState.value.clips.first().id)
        }
      }
      MainTool.AUDIO -> {
        _uiState.update { it.copy(activeSubMenu = SubMenuType.AUDIO) }
      }
      MainTool.TEXT -> {
        _uiState.update { it.copy(activeSubMenu = SubMenuType.TEXT) }
      }
      MainTool.FILTERS -> {
        _uiState.update { it.copy(activeSubMenu = SubMenuType.FILTERS) }
      }
      MainTool.ADJUST -> {
        _uiState.update { it.copy(activeSubMenu = SubMenuType.ADJUST) }
      }
      MainTool.ASPECT_RATIO -> {
        _uiState.update { it.copy(activeSubMenu = SubMenuType.ASPECT_RATIO) }
      }
      MainTool.EFFECTS -> {
        _uiState.update { it.copy(activeSubMenu = SubMenuType.EFFECTS) }
      }
      MainTool.OVERLAY -> {
        showToast("Overlay media picker ready")
      }
      MainTool.CAPTIONS -> {
        // Automatically add auto-captions
        addAutoCaptions()
      }
      MainTool.STICKERS -> {
        showToast("Stickers browser ready")
      }
      MainTool.GENERATE_MEDIA -> {
        showToast("AI Media Generator activated")
      }
      MainTool.AI_AVATAR -> {
        showToast("CapCut Pro AI Avatar ready")
      }
      MainTool.BACKGROUND -> {
        showToast("Background canvas selector")
      }
    }
  }

  fun closeSubMenu() {
    _uiState.update {
      it.copy(
        activeSubMenu = SubMenuType.NONE,
        selectedClipId = null,
        clips = it.clips.map { c -> c.copy(isSelected = false) }
      )
    }
  }

  fun splitCurrentClip() {
    saveToHistory()
    val currentMs = _uiState.value.currentTimeMs
    val clips = _uiState.value.clips
    val targetIndex = clips.indexOfFirst { currentMs > it.startMs && currentMs < it.endMs }
    if (targetIndex != -1) {
      val target = clips[targetIndex]
      val firstPart = target.copy(id = "${target.id}_a", endMs = currentMs, isSelected = false)
      val secondPart = target.copy(id = "${target.id}_b", startMs = currentMs, isSelected = true)

      val newClips = clips.toMutableList()
      newClips.removeAt(targetIndex)
      newClips.add(targetIndex, secondPart)
      newClips.add(targetIndex, firstPart)

      _uiState.update {
        it.copy(
          clips = newClips,
          selectedClipId = secondPart.id,
          toastMessage = "Split clip at ${formatTime(currentMs)}"
        )
      }
    } else {
      showToast("Cannot split at beginning or end of clip")
    }
  }

  fun deleteSelectedClip() {
    saveToHistory()
    val selectedId = _uiState.value.selectedClipId ?: return
    val remaining = _uiState.value.clips.filter { it.id != selectedId }
    if (remaining.isNotEmpty()) {
      val newTotal = remaining.sumOf { it.durationMs }
      _uiState.update {
        it.copy(
          clips = remaining,
          selectedClipId = null,
          totalDurationMs = newTotal,
          activeSubMenu = SubMenuType.NONE,
          toastMessage = "Clip deleted"
        )
      }
    } else {
      showToast("At least one clip must remain")
    }
  }

  fun openSpeedMenu() {
    _uiState.update { it.copy(activeSubMenu = SubMenuType.SPEED) }
  }

  fun openVolumeMenu() {
    _uiState.update { it.copy(activeSubMenu = SubMenuType.VOLUME) }
  }

  fun setClipSpeed(newSpeed: Float) {
    saveToHistory()
    val selectedId = _uiState.value.selectedClipId ?: return
    val clips = _uiState.value.clips
    val updated = clips.map { clip ->
      if (clip.id == selectedId) {
        val newDuration = (clip.originalDurationMs / newSpeed).toLong().coerceAtLeast(500L)
        clip.copy(speed = newSpeed, endMs = clip.startMs + newDuration)
      } else {
        clip
      }
    }
    var currentOffset = 0L
    val realigned = updated.map { clip ->
      val dur = clip.durationMs
      val aligned = clip.copy(startMs = currentOffset, endMs = currentOffset + dur)
      currentOffset += dur
      aligned
    }
    _uiState.update {
      it.copy(
        clips = realigned,
        totalDurationMs = currentOffset,
        toastMessage = "Speed set to ${newSpeed}x"
      )
    }
  }

  fun setClipVolume(newVolume: Float) {
    val selectedId = _uiState.value.selectedClipId ?: return
    val updated = _uiState.value.clips.map { clip ->
      if (clip.id == selectedId) clip.copy(volume = newVolume) else clip
    }
    _uiState.update {
      it.copy(
        clips = updated,
        toastMessage = "Volume set to ${(newVolume * 100).toInt()}%"
      )
    }
  }

  fun duplicateSelectedClip() {
    saveToHistory()
    val selectedId = _uiState.value.selectedClipId ?: return
    val clips = _uiState.value.clips
    val targetIndex = clips.indexOfFirst { it.id == selectedId }
    if (targetIndex != -1) {
      val target = clips[targetIndex]
      val duplicate = target.copy(
        id = "clip_copy_${System.currentTimeMillis()}",
        isSelected = false
      )
      val newClips = clips.toMutableList()
      newClips.add(targetIndex + 1, duplicate)

      var offset = 0L
      val realigned = newClips.map { c ->
        val dur = c.durationMs
        val aligned = c.copy(startMs = offset, endMs = offset + dur)
        offset += dur
        aligned
      }
      _uiState.update {
        it.copy(
          clips = realigned,
          totalDurationMs = offset,
          toastMessage = "Duplicated clip"
        )
      }
    }
  }

  fun setFilter(filter: FilterItem) {
    saveToHistory()
    _uiState.update { it.copy(selectedFilter = filter) }
  }

  fun setFilterIntensity(intensity: Float) {
    _uiState.update { it.copy(filterIntensity = intensity) }
  }

  fun updateAdjustSettings(adjustSettings: AdjustSettings) {
    _uiState.update { it.copy(adjustSettings = adjustSettings) }
  }

  fun resetAdjustSettings() {
    _uiState.update { it.copy(adjustSettings = AdjustSettings()) }
    showToast("Adjustments reset")
  }

  fun setAspectRatio(ratio: AspectRatioOption) {
    saveToHistory()
    _uiState.update { it.copy(aspectRatio = ratio) }
  }

  fun addAudioTrack(title: String) {
    saveToHistory()
    val newAudio = AudioTrackItem(
      id = "audio_${System.currentTimeMillis()}",
      title = title,
      startMs = 0L,
      endMs = _uiState.value.totalDurationMs
    )
    _uiState.update {
      it.copy(
        audioTracks = it.audioTracks + newAudio,
        showAddAudioDialog = false,
        toastMessage = "Added soundtrack: $title"
      )
    }
  }

  fun removeAudioTrack(id: String) {
    saveToHistory()
    _uiState.update { it.copy(audioTracks = it.audioTracks.filter { a -> a.id != id }) }
  }

  fun addTextTrack(text: String) {
    saveToHistory()
    val newText = TextTrackItem(
      id = "text_${System.currentTimeMillis()}",
      text = text,
      startMs = _uiState.value.currentTimeMs,
      endMs = (_uiState.value.currentTimeMs + 4000L).coerceAtMost(_uiState.value.totalDurationMs)
    )
    _uiState.update {
      it.copy(
        textTracks = it.textTracks + newText,
        showAddTextDialog = false,
        toastMessage = "Added caption: \"$text\""
      )
    }
  }

  fun addAutoCaptions() {
    saveToHistory()
    val autoCaptions = listOf(
      TextTrackItem("cap_1", "Peaceful moments at the Holy Kaaba 🕋", 0L, 7000L),
      TextTrackItem("cap_2", "Millions united in devotion and prayer ✨", 7000L, 16000L),
      TextTrackItem("cap_3", "A breathtaking spiritual journey in Mecca 🤍", 16000L, 28000L)
    )
    _uiState.update {
      it.copy(
        textTracks = autoCaptions,
        toastMessage = "Generated AI auto-captions"
      )
    }
  }

  fun openResolutionSheet(show: Boolean) {
    _uiState.update { it.copy(showResolutionSheet = show) }
  }

  fun setResolution(label: String, fps: Int, smartHdr: Boolean) {
    val estimated = when (label) {
      "AI UHD" -> 72.4f
      "4K" -> 118.2f
      "1080p" -> 42.1f
      "720p" -> 22.8f
      else -> 12.0f
    }
    _uiState.update {
      it.copy(
        resolutionSetting = ResolutionSetting(
          label = label,
          fps = fps,
          smartHdr = smartHdr,
          estimatedSizeMb = estimated
        ),
        showResolutionSheet = false,
        toastMessage = "Selected $label • $fps fps"
      )
    }
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
      var progress = 0f
      while (progress < 1.0f) {
        delay(120)
        progress += (0.05f + (Math.random() * 0.08f).toFloat())
        if (progress > 1f) progress = 1f
        _uiState.update { it.copy(exportProgress = progress) }
      }
      delay(300)
      _uiState.update { it.copy(isExportFinished = true) }
    }
  }

  fun dismissExportDialog() {
    _uiState.update { it.copy(showExportDialog = false) }
  }

  fun toggleFullscreen() {
    _uiState.update { it.copy(isFullscreen = !it.isFullscreen) }
  }

  fun togglePipOverlay() {
    _uiState.update {
      val newState = !it.pipOverlayEnabled
      it.copy(
        pipOverlayEnabled = newState,
        toastMessage = if (newState) "PIP Preview enabled" else "PIP Preview disabled"
      )
    }
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

  private fun saveToHistory() {
    historyStack.add(_uiState.value)
    redoStack.clear()
    _uiState.update { it.copy(canUndo = true, canRedo = false) }
  }

  fun undo() {
    if (historyStack.isNotEmpty()) {
      val previous = historyStack.removeAt(historyStack.lastIndex)
      redoStack.add(_uiState.value)
      _uiState.update {
        previous.copy(
          canUndo = historyStack.isNotEmpty(),
          canRedo = true,
          toastMessage = "Action undone"
        )
      }
    }
  }

  fun redo() {
    if (redoStack.isNotEmpty()) {
      val next = redoStack.removeAt(redoStack.lastIndex)
      historyStack.add(_uiState.value)
      _uiState.update {
        next.copy(
          canUndo = true,
          canRedo = redoStack.isNotEmpty(),
          toastMessage = "Action redone"
        )
      }
    }
  }

  companion object {
    fun formatTime(ms: Long): String {
      val totalSec = ms / 1000
      val minutes = totalSec / 60
      val seconds = totalSec % 60
      return "%02d:%02d".format(minutes, seconds)
    }
  }
}
