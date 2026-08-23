package com.example.ui.screen

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShutterSpeed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.AiPhotoAnalysis
import com.example.data.model.CameraPhoto
import com.example.data.model.EnhancementPreset
import com.example.data.model.EnhancementUiState
import com.example.data.model.QueueItemStatus
import com.example.ui.theme.BentoAiBluePrimary
import com.example.ui.theme.BentoAiBlueText
import com.example.ui.theme.BentoBgLight
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCardAiBlue
import com.example.ui.theme.BentoCardMeta
import com.example.ui.theme.BentoCardMuted
import com.example.ui.theme.BentoGreenActive
import com.example.ui.theme.BentoPurpleContainer
import com.example.ui.theme.BentoPurpleDark
import com.example.ui.theme.BentoPurpleLight
import com.example.ui.theme.BentoPurplePrimary
import com.example.ui.theme.BentoPurpleText
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.viewmodel.CameraAiViewModel
import com.example.ui.viewmodel.StudioTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraAiScreen(
    viewModel: CameraAiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val isCameraOpen by viewModel.isCameraOpen.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val latestPhoto by viewModel.latestPhoto.collectAsStateWithLifecycle()
    val isServiceActive by viewModel.isServiceActive.collectAsStateWithLifecycle()
    val isAutoProcessEnabled by viewModel.isAutoProcessEnabled.collectAsStateWithLifecycle()
    val queueItems by viewModel.queueItems.collectAsStateWithLifecycle()
    val enhancedPhotos by viewModel.enhancedPhotos.collectAsStateWithLifecycle()
    val selectedPreset by viewModel.selectedPreset.collectAsStateWithLifecycle()
    val enhancementState by viewModel.enhancementState.collectAsStateWithLifecycle()
    val isShowingOriginal by viewModel.isShowingOriginal.collectAsStateWithLifecycle()
    val saveStatusMessage by viewModel.saveStatusMessage.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()

    val pendingQueueCount = queueItems.count { it.status is QueueItemStatus.Pending || it.status is QueueItemStatus.InProgress }

    LaunchedEffect(saveStatusMessage) {
        saveStatusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSaveStatus()
        }
    }

    if (isCameraOpen) {
        CameraCaptureScreen(
            onPhotoCaptured = { uri -> viewModel.onPhotoCaptured(uri) },
            onClose = { viewModel.closeCamera() },
            onOpenGallery = {
                viewModel.closeCamera()
                viewModel.selectTab(StudioTab.GALLERY)
            },
            latestPhoto = latestPhoto,
            modifier = modifier
        )
    } else {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .background(BentoBgLight),
            containerColor = BentoBgLight,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.openCamera() },
                    containerColor = BentoPurplePrimary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .size(60.dp)
                        .testTag("floating_camera_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Capture Photo with In-App Camera",
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            bottomBar = {
                StudioBottomNavigationBar(
                    currentTab = currentTab,
                    pendingQueueCount = pendingQueueCount,
                    galleryCount = enhancedPhotos.size,
                    onSelectTab = { tab -> viewModel.selectTab(tab) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    StudioTab.STUDIO -> {
                        StudioWorkspaceContent(
                            viewModel = viewModel,
                            latestPhoto = latestPhoto,
                            isServiceActive = isServiceActive,
                            isAutoProcessEnabled = isAutoProcessEnabled,
                            pendingQueueCount = pendingQueueCount,
                            selectedPreset = selectedPreset,
                            enhancementState = enhancementState,
                            isShowingOriginal = isShowingOriginal,
                            isSaving = isSaving
                        )
                    }
                    StudioTab.GALLERY -> {
                        AiGalleryScreen(viewModel = viewModel)
                    }
                    StudioTab.QUEUE -> {
                        AiQueueScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun StudioBottomNavigationBar(
    currentTab: StudioTab,
    pendingQueueCount: Int,
    galleryCount: Int,
    onSelectTab: (StudioTab) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BentoBorder)
            .testTag("main_bottom_nav")
    ) {
        NavigationBarItem(
            selected = currentTab == StudioTab.STUDIO,
            onClick = { onSelectTab(StudioTab.STUDIO) },
            icon = {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Studio",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Studio", fontWeight = if (currentTab == StudioTab.STUDIO) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BentoPurplePrimary,
                selectedTextColor = BentoPurplePrimary,
                indicatorColor = BentoPurpleContainer,
                unselectedIconColor = BentoTextSecondary,
                unselectedTextColor = BentoTextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_studio")
        )

        NavigationBarItem(
            selected = currentTab == StudioTab.GALLERY,
            onClick = { onSelectTab(StudioTab.GALLERY) },
            icon = {
                BadgedBox(
                    badge = {
                        if (galleryCount > 0) {
                            Badge(containerColor = BentoPurplePrimary) {
                                Text(galleryCount.toString(), color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "AI Gallery",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = { Text("AI Gallery", fontWeight = if (currentTab == StudioTab.GALLERY) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BentoPurplePrimary,
                selectedTextColor = BentoPurplePrimary,
                indicatorColor = BentoPurpleContainer,
                unselectedIconColor = BentoTextSecondary,
                unselectedTextColor = BentoTextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_gallery")
        )

        NavigationBarItem(
            selected = currentTab == StudioTab.QUEUE,
            onClick = { onSelectTab(StudioTab.QUEUE) },
            icon = {
                BadgedBox(
                    badge = {
                        if (pendingQueueCount > 0) {
                            Badge(containerColor = Color(0xFF0288D1)) {
                                Text(pendingQueueCount.toString(), color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "AI Queue",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = { Text("AI Queue", fontWeight = if (currentTab == StudioTab.QUEUE) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0288D1),
                selectedTextColor = Color(0xFF0288D1),
                indicatorColor = Color(0xFFE0F2FE),
                unselectedIconColor = BentoTextSecondary,
                unselectedTextColor = BentoTextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_queue")
        )
    }
}

@Composable
fun StudioWorkspaceContent(
    viewModel: CameraAiViewModel,
    latestPhoto: CameraPhoto?,
    isServiceActive: Boolean,
    isAutoProcessEnabled: Boolean,
    pendingQueueCount: Int,
    selectedPreset: EnhancementPreset,
    enhancementState: EnhancementUiState,
    isShowingOriginal: Boolean,
    isSaving: Boolean
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Bento Header Section
        BentoHeader(
            isServiceActive = isServiceActive,
            onToggleService = { viewModel.toggleBackgroundService() },
            onRefresh = { viewModel.refreshLatestPhoto() }
        )

        // Auto-Process Toggle Bento Card
        AutoProcessToggleCard(
            isAutoProcessEnabled = isAutoProcessEnabled,
            onToggle = { enabled -> viewModel.setAutoProcessEnabled(enabled) }
        )

        // Active Queue Banner (when photos are actively processing)
        if (pendingQueueCount > 0) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectTab(StudioTab.QUEUE) }
                    .testTag("active_queue_banner"),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFE0F2FE),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBAE6FD))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = Color(0xFF0288D1),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$pendingQueueCount photo${if (pendingQueueCount > 1) "s" else ""} processing in AI Queue…",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0369A1)
                        )
                    }
                    Text(
                        text = "VIEW QUEUE →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0288D1)
                    )
                }
            }
        }

        // Main Hero Bento Card
        if (latestPhoto != null) {
            BentoHeroPhotoCard(
                photo = latestPhoto,
                enhancementState = enhancementState,
                isShowingOriginal = isShowingOriginal,
                onToggleOriginal = { showOriginal -> viewModel.toggleShowOriginal(showOriginal) },
                onShare = {
                    val currentSuccess = enhancementState as? EnhancementUiState.Success
                    if (currentSuccess != null) {
                        shareBitmap(context, currentSuccess.enhancedBitmap)
                    } else {
                        shareUri(context, latestPhoto.uri)
                    }
                }
            )

            // Bento 2-Column Grid Row (Metadata & AI Enhance)
            BentoActionGrid(
                photo = latestPhoto,
                enhancementState = enhancementState,
                selectedPreset = selectedPreset,
                onEnhance = { viewModel.enhancePhoto() }
            )

            // Error Card (Visible when Gemini API or network fails - no local fallbacks)
            AnimatedVisibility(
                visible = enhancementState is EnhancementUiState.Error,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                val error = enhancementState as? EnhancementUiState.Error
                if (error != null) {
                    BentoErrorCard(
                        errorMessage = error.message,
                        onRetry = { viewModel.enhancePhoto() },
                        onDismiss = { viewModel.clearError() }
                    )
                }
            }

            // AI Scene Intelligence & Auto Remastering Bento Card
            BentoAiSceneIntelligenceCard(
                enhancementState = enhancementState
            )

            // AI Insights Card (Visible when enhancement succeeds)
            AnimatedVisibility(
                visible = enhancementState is EnhancementUiState.Success,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                val success = enhancementState as? EnhancementUiState.Success
                if (success != null) {
                    BentoAiAnalysisCard(analysis = success.analysis)
                }
            }

            // Studio Action Controls (Save / Reset)
            StudioActionControls(
                enhancementState = enhancementState,
                isSaving = isSaving,
                onSave = { viewModel.saveEnhancedPhoto() },
                onReset = { viewModel.resetEnhancement() }
            )
        } else {
            EmptyCameraState(
                onLoadSample = { viewModel.loadSamplePhoto() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AutoProcessToggleCard(
    isAutoProcessEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("auto_process_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAutoProcessEnabled) Color(0xFFF3E8FF) else Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isAutoProcessEnabled) BentoPurplePrimary.copy(alpha = 0.4f) else BentoBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isAutoProcessEnabled) BentoPurplePrimary else Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (isAutoProcessEnabled) Color.White else Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.auto_process_title),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isAutoProcessEnabled) BentoPurplePrimary else Color(0xFFE2E8F0)
                        ) {
                            Text(
                                text = if (isAutoProcessEnabled) "DCIM ACTIVE" else "DCIM ONLY",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isAutoProcessEnabled) Color.White else Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Auto-enhances with Gemini 3.1 Flash Image for photos captured in native DCIM camera",
                        fontSize = 11.sp,
                        color = BentoTextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = isAutoProcessEnabled,
                onCheckedChange = onToggle,
                modifier = Modifier.testTag("auto_process_switch"),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = BentoPurplePrimary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFCBD5E1)
                )
            )
        }
    }
}

@Composable
fun BentoHeader(
    isServiceActive: Boolean,
    onToggleService: () -> Unit,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Camera AI",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.5).sp,
                color = BentoTextPrimary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .padding(top = 2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleService() }
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isServiceActive) BentoGreenActive else BentoTextSecondary)
                )
                Text(
                    text = if (isServiceActive) "Background Service Active" else "Background Service Paused",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = BentoTextSecondary
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onRefresh,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BentoCardMuted)
                    .testTag("refresh_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = BentoTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BentoPurpleContainer)
                    .clickable { onToggleService() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = BentoPurpleDark,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun BentoHeroPhotoCard(
    photo: CameraPhoto,
    enhancementState: EnhancementUiState,
    isShowingOriginal: Boolean,
    onToggleOriginal: (Boolean) -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = Color(0x1A000000)
            )
            .border(1.dp, BentoBorder, RoundedCornerShape(32.dp))
            .testTag("photo_display_card"),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardMuted)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.15f)
                .background(Color(0xFFEBEBEB))
                .pointerInput(enhancementState) {
                    if (enhancementState is EnhancementUiState.Success) {
                        detectTapGestures(
                            onPress = {
                                onToggleOriginal(true)
                                tryAwaitRelease()
                                onToggleOriginal(false)
                            }
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            when (enhancementState) {
                is EnhancementUiState.Success -> {
                    val bitmapToDisplay = if (isShowingOriginal) enhancementState.originalBitmap else enhancementState.enhancedBitmap
                    Image(
                        bitmap = bitmapToDisplay.asImageBitmap(),
                        contentDescription = "Captured camera photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                else -> {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(photo.uri)
                            .crossfade(true)
                            .build(),
                        contentDescription = photo.displayName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Processing Overlay
            if (enhancementState is EnhancementUiState.Processing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        CircularProgressIndicator(
                            color = BentoPurpleLight,
                            strokeWidth = 3.5.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = enhancementState.stage,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Top-Left Bento Pill: "LATEST CAPTURE" / "AI ENHANCED" / "ERROR"
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                color = when {
                    enhancementState is EnhancementUiState.Error -> Color(0xFFD32F2F).copy(alpha = 0.85f)
                    isShowingOriginal -> Color.Black.copy(alpha = 0.65f)
                    enhancementState is EnhancementUiState.Success -> BentoPurpleDark.copy(alpha = 0.85f)
                    else -> Color.Black.copy(alpha = 0.45f)
                },
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = when {
                            enhancementState is EnhancementUiState.Error -> Icons.Default.Warning
                            isShowingOriginal -> Icons.Default.Compare
                            enhancementState is EnhancementUiState.Success -> Icons.Default.AutoAwesome
                            else -> Icons.Outlined.PhotoCamera
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = when {
                            enhancementState is EnhancementUiState.Error -> "AI ENHANCE FAILED"
                            isShowingOriginal -> "VIEWING ORIGINAL"
                            enhancementState is EnhancementUiState.Success -> "GEMINI 3.1 4K REMASTER"
                            else -> "NATIVE CAMERA CAPTURE"
                        },
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Hold-to-compare instruction tooltip
            if (enhancementState is EnhancementUiState.Success) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 70.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Hold photo to compare with original",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Bottom Overlay Gradient with File Title & Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = photo.displayName,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Captured ${getRelativeTimeSpan(photo.dateTaken)}",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 10.sp
                        )
                    }
                }

                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(BentoPurpleLight)
                        .testTag("share_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = BentoPurpleText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BentoActionGrid(
    photo: CameraPhoto,
    enhancementState: EnhancementUiState,
    selectedPreset: EnhancementPreset,
    onEnhance: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tile 1: Metadata Bento Card (Enhanced with DCIM path badge and clean typography)
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .testTag("metadata_bento_card"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = BentoCardMeta)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.65f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = BentoTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Native Camera Storage Pill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (photo.isNativeCameraPath) Color(0xFFDCFCE7) else Color.White.copy(alpha = 0.8f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (photo.isNativeCameraPath) Color(0xFF86EFAC) else BentoBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (photo.isNativeCameraPath) BentoGreenActive else Color(0xFF94A3B8))
                            )
                            Text(
                                text = if (photo.isNativeCameraPath) "DCIM" else "LOCAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (photo.isNativeCameraPath) Color(0xFF166534) else BentoTextSecondary
                            )
                        }
                    }
                }

                Column {
                    Text(
                        text = "IMAGE METRICS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = BentoTextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = photo.resolutionText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Text(
                        text = "${photo.formattedSize} • ${photo.mimeType.substringAfterLast("/").uppercase()}",
                        fontSize = 11.sp,
                        color = BentoTextSecondary
                    )
                }
            }
        }

        // Tile 2: AI Enhance Action Bento Card
        val isError = enhancementState is EnhancementUiState.Error
        val cardBg = if (isError) Color(0xFFFFECEC) else BentoCardAiBlue
        val iconBg = if (isError) Color(0xFFD32F2F) else BentoAiBluePrimary

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .clickable(enabled = enhancementState !is EnhancementUiState.Processing) {
                    onEnhance()
                }
                .testTag("ai_enhance_bento_card"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    if (enhancementState is EnhancementUiState.Processing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else if (isError) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = when (enhancementState) {
                        is EnhancementUiState.Success -> "Re-Enhance"
                        is EnhancementUiState.Error -> "Retry AI"
                        else -> "Auto Enhance"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isError) Color(0xFFD32F2F) else BentoAiBlueText
                )

                Text(
                    text = if (isError) "Tap to retry" else "Auto LLM Detection",
                    fontSize = 11.sp,
                    color = if (isError) Color(0xFFD32F2F).copy(alpha = 0.8f) else BentoAiBlueText.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun BentoAiSceneIntelligenceCard(
    enhancementState: EnhancementUiState
) {
    val detectedCategory = when (enhancementState) {
        is EnhancementUiState.Success -> enhancementState.analysis.category
        is EnhancementUiState.Processing -> enhancementState.detectedScene
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_scene_intelligence_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(BentoPurpleContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = BentoPurpleDark
                        )
                    }
                    Text(
                        text = "Auto Scene Detection Engine",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFEDE7F6)
                ) {
                    Text(
                        text = "GEMINI LLM",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF673AB7),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp)
                    )
                }
            }

            Text(
                text = "Gemini AI analyzes the image to classify scenes (Portrait, Low Light, Food, Texture, Landscape, Architecture, etc.) and auto-applies tailored 4K restoration.",
                fontSize = 12.sp,
                color = BentoTextSecondary,
                lineHeight = 17.sp
            )

            // Intelligent Scene Capability Chips
            val scenes = listOf(
                "👤 Portrait",
                "🌙 Low Light",
                "🍽️ Food",
                "🔍 Texture",
                "🌿 Landscape",
                "🏙️ Architecture",
                "📄 Document"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(scenes) { sceneTag ->
                    val isMatched = detectedCategory != null && sceneTag.contains(detectedCategory.name.take(4), ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isMatched) BentoPurpleContainer else BentoCardMuted,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isMatched) BentoPurplePrimary else Color.Transparent
                        )
                    ) {
                        Text(
                            text = sceneTag,
                            fontSize = 11.sp,
                            fontWeight = if (isMatched) FontWeight.Bold else FontWeight.Medium,
                            color = if (isMatched) BentoPurpleDark else BentoTextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BentoErrorCard(
    errorMessage: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bento_error_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F1)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFCDD2))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD32F2F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Gemini AI Enhancement Error",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB71C1C)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss error",
                        tint = Color(0xFFB71C1C),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = Color(0xFFC62828),
                lineHeight = 17.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    modifier = Modifier.testTag("retry_ai_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Retry Gemini AI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BentoAiAnalysisCard(analysis: AiPhotoAnalysis) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_analysis_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(BentoPurpleContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = BentoPurpleDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "Detected Scene: ${analysis.category.emoji} ${analysis.sceneType}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = "${analysis.confidenceScore}% CONFIDENCE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF166534),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp)
                    )
                }
            }

            Text(
                text = analysis.aiInsight,
                fontSize = 12.sp,
                color = BentoTextSecondary,
                lineHeight = 17.sp
            )

            if (analysis.detectedElements.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    analysis.detectedElements.take(3).forEach { element ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BentoCardMuted
                        ) {
                            Text(
                                text = "• $element",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BentoTextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AiScorePill(
                    label = "Lighting",
                    value = "${analysis.lightingScore}%",
                    color = Color(0xFFE65100),
                    bg = Color(0xFFFFF3E0),
                    modifier = Modifier.weight(1f)
                )
                AiScorePill(
                    label = "Sharpness",
                    value = "${analysis.sharpnessScore}%",
                    color = BentoAiBlueText,
                    bg = BentoCardAiBlue,
                    modifier = Modifier.weight(1f)
                )
                AiScorePill(
                    label = "Noise Red.",
                    value = "${analysis.noiseReductionScore}%",
                    color = BentoPurpleText,
                    bg = BentoPurpleContainer,
                    modifier = Modifier.weight(1f)
                )
                AiScorePill(
                    label = "Output",
                    value = "4K HDR",
                    color = Color(0xFF2E7D32),
                    bg = Color(0xFFE8F5E9),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AiScorePill(
    label: String,
    value: String,
    color: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bg
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = color.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StudioActionControls(
    enhancementState: EnhancementUiState,
    isSaving: Boolean,
    onSave: () -> Unit,
    onReset: () -> Unit
) {
    if (enhancementState is EnhancementUiState.Success) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onSave,
                    enabled = !isSaving,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("save_enhanced_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Icon(
                        imageVector = Icons.Default.SaveAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSaving) "Saving…" else "Save to Gallery",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("reset_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoTextPrimary)
                ) {
                    Text("Reset", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun EmptyCameraState(
    onLoadSample: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("empty_camera_state"),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardMuted),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(BentoPurpleContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = BentoPurplePrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No Camera Photos Yet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tap the floating camera button below to snap a photo in-app. Camera AI will automatically process and enhance it with Gemini AI.",
                    fontSize = 13.sp,
                    color = BentoTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }

            FilledTonalButton(
                onClick = onLoadSample,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("load_sample_photo_button"),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = BentoCardAiBlue)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = BentoAiBluePrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Load Scenic Demo Photo", color = BentoAiBlueText, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private fun getRelativeTimeSpan(timeMillis: Long): String {
    val diff = System.currentTimeMillis() - timeMillis
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> "recently"
    }
}

private fun shareUri(context: android.content.Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Photo"))
}

private fun shareBitmap(context: android.content.Context, bitmap: Bitmap) {
    try {
        val cachePath = java.io.File(context.cacheDir, "shared_images")
        cachePath.mkdirs()
        val file = java.io.File(cachePath, "enhanced_share.jpg")
        val stream = java.io.FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
        stream.close()

        val contentUri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Enhanced Photo"))
    } catch (e: Exception) {
        Toast.makeText(context, "Sharing ready in local gallery", Toast.LENGTH_SHORT).show()
    }
}
