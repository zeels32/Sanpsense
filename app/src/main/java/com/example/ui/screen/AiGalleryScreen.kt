package com.example.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.data.db.EnhancedPhotoEntity
import com.example.data.model.EnhancementPreset
import com.example.ui.theme.BentoTheme
import com.example.ui.viewmodel.CameraAiViewModel
import com.example.ui.viewmodel.StudioTab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AiGalleryScreen(
    viewModel: CameraAiViewModel,
    modifier: Modifier = Modifier
) {
    val enhancedPhotos by viewModel.enhancedPhotos.collectAsState()
    val selectedPhoto by viewModel.selectedGalleryPhoto.collectAsState()
    val context = LocalContext.current

    var selectedSceneFilter by remember { mutableStateOf<String?>(null) }

    // Distinct scene types found in existing enhanced photos
    val availableSceneCategories = remember(enhancedPhotos) {
        enhancedPhotos.map { it.sceneType }.distinct().filter { it.isNotBlank() }
    }

    val filteredPhotos = remember(enhancedPhotos, selectedSceneFilter) {
        if (selectedSceneFilter == null) {
            enhancedPhotos
        } else {
            enhancedPhotos.filter { it.sceneType.contains(selectedSceneFilter!!, ignoreCase = true) }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BentoTheme.colors.bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Gallery Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gallery_header_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoTheme.colors.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoTheme.colors.border),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(BentoTheme.colors.purpleContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = BentoTheme.colors.purplePrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = stringResource(R.string.gallery_title),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTheme.colors.textPrimary
                                )
                                Text(
                                    text = "${enhancedPhotos.size} photos remastered • Saved to Pictures/Camera_AI",
                                    fontSize = 12.sp,
                                    color = BentoTheme.colors.textSecondary
                                )
                            }
                        }

                        /*
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BentoTheme.colors.purpleContainer
                            ) {
                                Text(
                                    text = "${enhancedPhotos.size} SAVED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BentoTheme.colors.purplePrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        */
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Detected Scene Filters Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        item {
                            GalleryFilterChip(
                                label = "All (${enhancedPhotos.size})",
                                isSelected = selectedSceneFilter == null,
                                onClick = { selectedSceneFilter = null }
                            )
                        }

                        items(availableSceneCategories) { scene ->
                            val count = enhancedPhotos.count { it.sceneType.equals(scene, ignoreCase = true) }
                            GalleryFilterChip(
                                label = "$scene ($count)",
                                isSelected = selectedSceneFilter.equals(scene, ignoreCase = true),
                                onClick = {
                                    selectedSceneFilter = if (selectedSceneFilter.equals(scene, ignoreCase = true)) null else scene
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredPhotos.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(BentoTheme.colors.cardMuted),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = BentoTheme.colors.textSecondary,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                        Text(
                            text = if (selectedSceneFilter != null) "No photos in $selectedSceneFilter" else stringResource(R.string.gallery_empty_title),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTheme.colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(R.string.gallery_empty_desc),
                            fontSize = 13.sp,
                            color = BentoTheme.colors.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.selectTab(StudioTab.STUDIO) },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BentoTheme.colors.purplePrimary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("gallery_go_studio_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open Studio Workspace", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Photo Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredPhotos, key = { it.id }) { photo ->
                        GalleryPhotoCard(
                            photo = photo,
                            onClick = { viewModel.selectGalleryPhoto(photo) }
                        )
                    }
                }
            }
        }

        // Fullscreen Detail & Comparison Dialog
        selectedPhoto?.let { photo ->
            GalleryPhotoDetailDialog(
                photo = photo,
                onDismiss = { viewModel.selectGalleryPhoto(null) },
                onReEnhance = {
                    viewModel.reEnhanceGalleryPhoto(photo, EnhancementPreset.AUTO)
                },
                onOpenInStudio = {
                    viewModel.setLatestPhotoFromGallery(photo)
                },
                onDelete = {
                    viewModel.deleteEnhancedPhoto(photo)
                },
                onShare = {
                    sharePhoto(context, photo.enhancedUri, photo.enhancedDisplayName)
                }
            )
        }
    }
}

@Composable
fun GalleryFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) BentoTheme.colors.purplePrimary else BentoTheme.colors.cardMuted,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) BentoTheme.colors.purplePrimary else BentoTheme.colors.border
        )
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else BentoTheme.colors.textSecondary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GalleryPhotoCard(
    photo: EnhancedPhotoEntity,
    onClick: () -> Unit
) {
    val dateStr = remember(photo.timestamp) {
        val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        sdf.format(Date(photo.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.78f)
            .clickable { onClick() }
            .testTag("gallery_photo_card_${photo.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BentoTheme.colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoTheme.colors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(BentoTheme.colors.cardMuted)
            ) {
                AsyncImage(
                    model = photo.enhancedUri,
                    contentDescription = photo.enhancedDisplayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay at top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
                            )
                        )
                )

                // Scene Type Badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = BentoTheme.colors.purplePrimary.copy(alpha = 0.88f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = photo.sceneType,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Sharpness / Lighting Score
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = "4K • ${photo.sharpnessScore}% CLARITY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Card Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = photo.enhancedDisplayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$dateStr • Auto AI Remastered",
                    fontSize = 10.sp,
                    color = BentoTheme.colors.textSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun GalleryPhotoDetailDialog(
    photo: EnhancedPhotoEntity,
    onDismiss: () -> Unit,
    onReEnhance: (EnhancementPreset) -> Unit,
    onOpenInStudio: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    var isHoldingOriginal by remember { mutableStateOf(false) }
    var isOverlayVisible by remember { mutableStateOf(true) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text(
                    text = "Delete AI Remaster?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BentoTheme.colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete \"${photo.enhancedDisplayName}\" from your AI Gallery and device storage?",
                    fontSize = 14.sp,
                    color = BentoTheme.colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("confirm_delete_ai_photo_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteConfirmation = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = BentoTheme.colors.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Auto-hide floating information overlay after 3.5 seconds of inactivity
    LaunchedEffect(isOverlayVisible, isHoldingOriginal) {
        if (isOverlayVisible && !isHoldingOriginal) {
            delay(3500)
            isOverlayVisible = false
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            isOverlayVisible = !isOverlayVisible
                        },
                        onPress = {
                            isHoldingOriginal = true
                            tryAwaitRelease()
                            isHoldingOriginal = false
                        }
                    )
                }
        ) {
            val activeUri = if (isHoldingOriginal) photo.originalUri else photo.enhancedUri

            // Full Screen Image Preview
            AsyncImage(
                model = activeUri,
                contentDescription = "Photo Preview",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            // Hold-to-Compare Indicator Badge (Always visible or on compare)
            if (isHoldingOriginal) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Photo,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "ORIGINAL RAW CAPTURE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Top Floating Action Bar Overlay
            AnimatedVisibility(
                visible = isOverlayVisible,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = photo.enhancedDisplayName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Gemini AI Remastered • ${photo.sceneType.uppercase()}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = onShare,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            IconButton(
                                onClick = { showDeleteConfirmation = true },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE53935).copy(alpha = 0.3f))
                                    .testTag("gallery_delete_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete from AI Gallery",
                                    tint = Color(0xFFFF8A80),
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Subtle Hint Chip when Overlay is Hidden
            AnimatedVisibility(
                visible = !isOverlayVisible && !isHoldingOriginal,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Tap for details • Hold to compare original",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Bottom Floating Information & Controls Card Overlay
            AnimatedVisibility(
                visible = isOverlayVisible,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E222B).copy(alpha = 0.92f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // AI Insights Banner
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF673AB7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Gemini AI Scene Intelligence",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = photo.aiInsight,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    lineHeight = 15.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Metrics Pill Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            MetricPill(
                                label = "Scene",
                                value = photo.sceneType,
                                modifier = Modifier.weight(1f)
                            )
                            MetricPill(
                                label = "Sharpness",
                                value = "${photo.sharpnessScore}%",
                                modifier = Modifier.weight(1f)
                            )
                            MetricPill(
                                label = "Lighting",
                                value = "${photo.lightingScore}%",
                                modifier = Modifier.weight(1f)
                            )
                            MetricPill(
                                label = "Quality",
                                value = "4K Restored",
                                modifier = Modifier.weight(1.1f)
                            )
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                        // Actions Row: Open in Studio & Re-Enhance
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onOpenInStudio,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Open Studio",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Button(
                                onClick = { onReEnhance(EnhancementPreset.AUTO) },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(44.dp)
                                    .testTag("re_enhance_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Re-Remaster",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.08f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.5f)
            )
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun sharePhoto(context: Context, uri: Uri, name: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/jpeg"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, "Remastered with Gemini AI ($name)")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share AI Enhanced Photo"))
}
