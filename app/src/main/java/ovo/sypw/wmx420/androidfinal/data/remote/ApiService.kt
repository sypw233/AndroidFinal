package ovo.sypw.wmx420.androidfinal.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun getRss(@Url url: String): ResponseBody
}