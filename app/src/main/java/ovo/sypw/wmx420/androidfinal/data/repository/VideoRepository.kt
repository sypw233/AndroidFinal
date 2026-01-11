package ovo.sypw.wmx420.androidfinal.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ovo.sypw.wmx420.androidfinal.data.model.Video

class VideoRepository {

    fun getVideoList(): Flow<Result<List<Video>>> = flow {
        try {
            val videos = Video.mock()
            emit(Result.success(videos))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

