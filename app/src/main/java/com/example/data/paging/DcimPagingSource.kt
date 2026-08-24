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
            anchorPage?.prevKey?.plus(PAGE_SIZE) ?: anchorPage?.nextKey?.minus(PAGE_SIZE)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CameraPhoto> {
        val offset = params.key ?: 0
        val limit = params.loadSize.coerceAtLeast(PAGE_SIZE)

        return try {
            val photos = repository.queryDcimPhotosPaged(offset = offset, limit = limit)

            LoadResult.Page(
                data = photos,
                prevKey = if (offset == 0) null else maxOf(0, offset - limit),
                nextKey = if (photos.isEmpty() || photos.size < limit) null else offset + photos.size
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}
