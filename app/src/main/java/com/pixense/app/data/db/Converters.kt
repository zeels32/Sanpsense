package com.pixense.app.data.db

import android.net.Uri
import androidx.room.TypeConverter
import com.pixense.app.data.model.EnhancementPreset

class Converters {
    @TypeConverter
    fun fromUri(uri: Uri?): String? = uri?.toString()

    @TypeConverter
    fun toUri(uriString: String?): Uri? = uriString?.let { Uri.parse(it) }

    @TypeConverter
    fun fromPreset(preset: EnhancementPreset?): String? = preset?.name

    @TypeConverter
    fun toPreset(name: String?): EnhancementPreset? = name?.let {
        try {
            EnhancementPreset.valueOf(it)
        } catch (e: Exception) {
            EnhancementPreset.AUTO_PRO
        }
    }
}
