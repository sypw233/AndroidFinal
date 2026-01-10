package ovo.sypw.wmx420.androidfinal.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://localhost"
    private const val TIME_OUT = 30L // 30秒超时

    private val jsonConfig = Json {
        ignoreUnknownKeys = true // 忽略未知字段
        isLenient = true         // 宽容模式（允许不规范的 JSON）
        coerceInputValues = true // 强转输入（比如 null 转默认值）
        encodeDefaults = true    // 序列化时包含默认值
    }

    val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)

        builder.build()
    }

    // 4. 配置 Retrofit 实例
    val retrofit: Retrofit by lazy {
        val contentType = "application/json".toMediaType()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            // 挂载 Kotlin Serialization 转换器
            .addConverterFactory(jsonConfig.asConverterFactory(contentType))
            .build()
    }


}