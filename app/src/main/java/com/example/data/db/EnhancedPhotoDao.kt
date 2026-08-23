package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EnhancedPhotoDao {
    @Query("SELECT * FROM enhanced_photos ORDER BY timestamp DESC")
    fun getAllEnhancedPhotos(): Flow<List<EnhancedPhotoEntity>>

    @Query("SELECT * FROM enhanced_photos WHERE id = :id LIMIT 1")
    suspend fun getPhotoById(id: Long): EnhancedPhotoEntity?

    @Query("SELECT * FROM enhanced_photos WHERE originalUri = :originalUri ORDER BY timestamp DESC")
    fun getPhotosForOriginal(originalUri: String): Flow<List<EnhancedPhotoEntity>>

    @Query("SELECT COUNT(*) FROM enhanced_photos WHERE originalUri = :originalUriString OR enhancedUri = :uriString")
    suspend fun isUriAlreadyEnhanced(originalUriString: String, uriString: String): Int

    @Query("SELECT COUNT(*) FROM enhanced_photos WHERE originalDisplayName = :name OR enhancedDisplayName = :name")
    suspend fun isNameAlreadyEnhanced(name: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: EnhancedPhotoEntity): Long

    @Update
    suspend fun update(photo: EnhancedPhotoEntity)

    @Delete
    suspend fun delete(photo: EnhancedPhotoEntity)

    @Query("DELETE FROM enhanced_photos WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM enhanced_photos")
    suspend fun deleteAll()
}
