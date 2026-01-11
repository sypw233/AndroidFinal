package ovo.sypw.wmx420.androidfinal.data.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ovo.sypw.wmx420.androidfinal.data.model.BilibiliRankingItem
import ovo.sypw.wmx420.androidfinal.data.remote.ApiService
import ovo.sypw.wmx420.androidfinal.utils.PreferenceUtils

class BilibiliRepository(
    private val apiService: ApiService,
    private val context: Context
) {
    private fun getCookies(): String = PreferenceUtils.getBilibiliCookies(context)

    fun getBilibiliRankingData(): Flow<Result<List<BilibiliRankingItem>>> = flow {
        try {
            val cookies = getCookies()
            val response = apiService.getBilibiliRankingData(cookie = cookies)
            if (response.code == 0 && response.data?.list != null) {
                emit(Result.success(response.data.list))
            } else {
                emit(Result.failure(Exception(response.message)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}