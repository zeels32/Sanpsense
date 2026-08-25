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
        val pageSize = params.loadSize.coerceAtLeast(PAGE_SIZE)
        val offset = page * PAGE_SIZE

        return try {
            // Request pageSize + 1 to determine if more items exist
            val photosWithExtra = repository.queryDcimPhotosPaged(offset = offset, limit = pageSize + 1)
            val hasMore = photosWithExtra.size > pageSize
            val photos = if (hasMore) photosWithExtra.take(pageSize) else photosWithExtra

            LoadResult.Page(
                data = photos,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (hasMore) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}
