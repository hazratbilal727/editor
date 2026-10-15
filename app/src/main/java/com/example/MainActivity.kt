package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.VideoClip
import com.example.ui.screens.CapCutEditorScreen
import com.example.ui.screens.CapCutHomeScreen
import com.example.ui.screens.CapCutMediaPickerScreen
import com.example.ui.theme.CapCutBackground
import com.example.ui.theme.CapCutTheme
import com.example.viewmodel.VideoEditorViewModel

enum class Screen {
  HOME,
  MEDIA_PICKER,
  EDITOR
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      CapCutTheme {
        var currentScreen by remember { mutableStateOf(Screen.HOME) }
        var isAppendingMedia by remember { mutableStateOf(false) }
        val editorViewModel: VideoEditorViewModel = viewModel()
        val context = LocalContext.current

        // Hardware Back handler
        BackHandler(enabled = currentScreen != Screen.HOME) {
          when (currentScreen) {
            Screen.MEDIA_PICKER -> {
              currentScreen = if (isAppendingMedia) Screen.EDITOR else Screen.HOME
              isAppendingMedia = false
            }
            Screen.EDITOR -> {
              currentScreen = Screen.HOME
            }
            Screen.HOME -> {}
          }
        }

        Surface(
          modifier = Modifier.fillMaxSize(),
          color = CapCutBackground
        ) {
          AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
              when {
                targetState == Screen.MEDIA_PICKER -> {
                  (slideInVertically { height -> height } + fadeIn()).togetherWith(
                    fadeOut()
                  )
                }
                initialState == Screen.MEDIA_PICKER && targetState == Screen.EDITOR -> {
                  (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> -width / 2 } + fadeOut()
                  )
                }
                initialState == Screen.MEDIA_PICKER && targetState == Screen.HOME -> {
                  fadeIn().togetherWith(
                    slideOutVertically { height -> height } + fadeOut()
                  )
                }
                targetState == Screen.EDITOR -> {
                  (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> -width / 2 } + fadeOut()
                  )
                }
                else -> {
                  (slideInHorizontally { width -> -width / 2 } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> width } + fadeOut()
                  )
                }
              }
            },
            label = "CapCutScreenNavigation"
          ) { screen ->
            when (screen) {
              Screen.HOME -> {
                CapCutHomeScreen(
                  onNewVideoClick = {
                    isAppendingMedia = false
                    currentScreen = Screen.MEDIA_PICKER
                  },
                  onEditPhotoClick = {
                    isAppendingMedia = false
                    currentScreen = Screen.MEDIA_PICKER
                  },
                  onOpenProject = { projectId ->
                    Toast.makeText(context, "Opening project $projectId...", Toast.LENGTH_SHORT).show()
                    currentScreen = Screen.EDITOR
                  },
                  onToolClick = { toolId ->
                    when (toolId) {
                      "pro_subscription" -> {
                        Toast.makeText(context, "CapCut Pro unlocked for Rs200/month", Toast.LENGTH_SHORT).show()
                      }
                      "search" -> {
                        Toast.makeText(context, "Search templates, effects and music", Toast.LENGTH_SHORT).show()
                      }
                      "autocut", "auto_captions", "ai_generator", "cover_maker", "photo_tools" -> {
                        // Open media picker directly for this tool
                        isAppendingMedia = false
                        currentScreen = Screen.MEDIA_PICKER
                      }
                      else -> {
                        Toast.makeText(context, "Selected: $toolId", Toast.LENGTH_SHORT).show()
                      }
                    }
                  }
                )
              }
              Screen.MEDIA_PICKER -> {
                CapCutMediaPickerScreen(
                  onClose = {
                    currentScreen = if (isAppendingMedia) Screen.EDITOR else Screen.HOME
                    isAppendingMedia = false
                  },
                  onMediaSelected = { selectedClips ->
                    if (isAppendingMedia) {
                      editorViewModel.appendClips(selectedClips)
                    } else {
                      editorViewModel.loadClips(selectedClips)
                    }
                    isAppendingMedia = false
                    currentScreen = Screen.EDITOR
                  }
                )
              }
              Screen.EDITOR -> {
                CapCutEditorScreen(
                  viewModel = editorViewModel,
                  onCloseClick = {
                    currentScreen = Screen.HOME
                  },
                  onAddMediaClick = {
                    isAppendingMedia = true
                    currentScreen = Screen.MEDIA_PICKER
                  }
                )
              }
            }
          }
        }
      }
    }
  }
}
