package com.pixense.app.data.db

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pixense.app.data.model.EnhancementPreset

@Entity(tableName = "enhanced_photos")
data class EnhancedPhotoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalUri: Uri,
    val enhancedUri: Uri,
    val originalDisplayName: String,
    val enhancedDisplayName: String,
    val preset: EnhancementPreset,
    val timestamp: Long = System.currentTimeMillis(),
    val sceneType: String = "Photography",
    val lightingScore: Int = 85,
    val sharpnessScore: Int = 90,
    val dynamicRange: String = "High Dynamic Range",
    val aiInsight: String = "Enhanced with Gemini 3.1 Flash Image AI",
    val resolution: String = "4K Photo-Quality",
    val width: Int = 0,
    val height: Int = 0
)
