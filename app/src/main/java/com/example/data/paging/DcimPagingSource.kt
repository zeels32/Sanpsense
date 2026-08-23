package com.example.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.data.model.CameraPhoto
import com.example.data.repository.CameraCaptureRepository

class DcimPagingSource(
    private val repository: CameraCaptureRepository
) : PagingSource<Int, CameraPhoto>() {

    override fun getRefreshKey(state: PagingState<Int, CameraPhoto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CameraPhoto> {
        val page = params.key ?: 0
        val pageSize = params.loadSize
        val offset = page * pageSize

        return try {
            val photos = repository.queryDcimPhotosPaged(offset = offset, limit = pageSize)

            // Fallback for demo if device gallery is totally empty on page 0
            val resultData = if (page == 0 && photos.isEmpty()) {
                val latest = repository.latestPhoto.value
                if (latest != null) listOf(latest) else emptyList()
            } else {
                photos
            }

            LoadResult.Page(
                data = resultData,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (photos.isEmpty() || photos.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
