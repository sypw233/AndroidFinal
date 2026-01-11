package ovo.sypw.wmx420.androidfinal.data.remote

import okhttp3.ResponseBody
import ovo.sypw.wmx420.androidfinal.data.model.BilibiliRankingResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun getRss(@Url url: String): ResponseBody

    @GET("https://api.bilibili.com/x/web-interface/ranking/v2")
    suspend fun getBilibiliRankingData(
        @Query("rid") rid: Int = 0,      // 分区ID，0为全站
        @Query("type") type: String = "all", // all为全站
        @Header("Cookie") cookie: String,
        @Header("User-Agent") userAgent: String = "Mozilla/5.0 (Linux; Android 16; Pixel 9 Pro Build/BP1A.250305.008) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.6998.35 Mobile Safari/537.36",
        @Header("Referer") referer: String = "https://www.bilibili.com/"
    ): BilibiliRankingResponse
}