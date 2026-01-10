package ovo.sypw.wmx420.androidfinal.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import ovo.sypw.wmx420.androidfinal.data.model.Banner
import ovo.sypw.wmx420.androidfinal.data.model.News
import ovo.sypw.wmx420.androidfinal.data.model.NewsListData
import ovo.sypw.wmx420.androidfinal.data.remote.ApiService
import ovo.sypw.wmx420.androidfinal.utils.extractFirstImageUrl
import ovo.sypw.wmx420.androidfinal.utils.formatDate
import java.io.StringReader

/**
 * 刷新结果数据类
 */
data class RefreshResultData(
    val newsListData: NewsListData,
    val newItemsCount: Int
)

class NewsRepository(
    private val apiService: ApiService,
    private val context: Context
) {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // 内存缓存
    private var cachedNews = mutableListOf<News>()

    init {
        // 启动时从本地加载缓存
        loadCacheFromDisk()
    }

    /**
     * 获取新闻列表（分页）
     */
    fun getNewsList(page: Int = 1, pageSize: Int = 10): Flow<Result<NewsListData>> = flow {
        try {
            // 首次加载或缓存过期时获取新数据
            if (cachedNews.isEmpty() || isCacheExpired()) {
                fetchAndParseRss()
            }
            emit(Result.success(getPageData(page, pageSize)))
        } catch (e: Exception) {
            e.printStackTrace()
            // 失败时尝试使用缓存数据
            if (cachedNews.isNotEmpty()) {
                val pageData = getPageData(page, pageSize)
                if (pageData.news.isNotEmpty()) {
                    emit(Result.success(pageData))
                    return@flow
                }
            }
            emit(Result.success(getMockData(page, pageSize)))
        }
    }

    /**
     * 刷新数据并返回新条目数量
     */
    fun refreshNews(pageSize: Int = 10): Flow<Result<RefreshResultData>> = flow {
        try {
            // 记录旧数据的ID集合
            val oldIds = cachedNews.map { it.id }.toSet()

            // 获取新数据
            val newlyParsedNews = fetchRssData()

            if (newlyParsedNews.isEmpty()) {
                emit(
                    Result.success(
                        RefreshResultData(
                            newsListData = getFirstPageData(pageSize),
                            newItemsCount = 0
                        )
                    )
                )
                return@flow
            }

            // 计算新增条目数
            val newItemsCount = newlyParsedNews.count { it.id !in oldIds }

            // 合并并保存数据
            mergeAndSaveNews(newlyParsedNews)

            emit(
                Result.success(
                    RefreshResultData(
                        newsListData = getFirstPageData(pageSize),
                        newItemsCount = newItemsCount
                    )
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    private fun getPageData(page: Int, pageSize: Int): NewsListData {
        val total = cachedNews.size
        val fromIndex = (page - 1) * pageSize

        if (fromIndex >= total) {
            return NewsListData(emptyList(), emptyList(), false)
        }

        val toIndex = minOf(fromIndex + pageSize, total)
        val pageData = cachedNews.subList(fromIndex, toIndex).toList()

        return NewsListData(
            banners = if (page == 1) getBannersFromNews(cachedNews) else emptyList(),
            news = pageData,
            hasMore = toIndex < total
        )
    }

    private fun getFirstPageData(pageSize: Int) = getPageData(1, pageSize)

    private fun isCacheExpired(): Boolean {
        val cacheTime = prefs.getLong(CACHE_TIME_KEY, 0L)
        return System.currentTimeMillis() - cacheTime > CACHE_DURATION
    }

    private fun loadCacheFromDisk() {
        try {
            val json = prefs.getString(CACHE_KEY, null)
            if (!json.isNullOrEmpty()) {
                val type = object : TypeToken<List<News>>() {}.type
                val loaded: List<News> = gson.fromJson(json, type)
                cachedNews.clear()
                cachedNews.addAll(loaded)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveCacheToDisk() {
        try {
            val json = gson.toJson(cachedNews)
            prefs.edit()
                .putString(CACHE_KEY, json)
                .putLong(CACHE_TIME_KEY, System.currentTimeMillis())
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getBannersFromNews(newsList: List<News>): List<Banner> {
        return newsList.filter { !it.imageUrl.isNullOrEmpty() }
            .take(3)
            .map { news ->
                Banner(
                    id = news.id,
                    imageUrl = news.imageUrl!!,
                    title = news.title,
                    linkUrl = news.url
                )
            }
    }

    private suspend fun fetchAndParseRss() {
        val parsedNews = fetchRssData()
        if (parsedNews.isNotEmpty()) {
            mergeAndSaveNews(parsedNews)
        }
    }

    private suspend fun fetchRssData(): List<News> {
        val responseBody = apiService.getRss(RSS_URL)
        val xmlString = responseBody.string()
        return parseRss(xmlString)
    }

    private fun mergeAndSaveNews(newNews: List<News>) {
        val newIds = newNews.map { it.id }.toSet()
        val oldUniqueNews = cachedNews.filter { it.id !in newIds }

        cachedNews.clear()
        cachedNews.addAll(newNews)
        cachedNews.addAll(oldUniqueNews)
        saveCacheToDisk()
    }

    private suspend fun parseRss(xml: String): List<News> = withContext(Dispatchers.IO) {
        val newsList = mutableListOf<News>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            var eventType = parser.eventType
            var currentTag = ""

            var title = ""
            var link = ""
            var description = ""
            var pubDate = ""
            var insideItem = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name
                        if (currentTag == "item") {
                            insideItem = true
                            title = ""
                            link = ""
                            description = ""
                            pubDate = ""
                        }
                    }

                    XmlPullParser.TEXT -> {
                        if (insideItem) {
                            val text = parser.text
                            when (currentTag) {
                                "title" -> title = text
                                "link" -> link = text
                                "description" -> description = text
                                "pubDate" -> pubDate = text
                            }
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        if (parser.name == "item") {
                            insideItem = false
                            val plainContent = description.replace(Regex("<.*?>"), "").trim()

                            newsList.add(
                                News(
                                    id = link,
                                    title = title,
                                    content = plainContent.take(100) + "...",
                                    author = "ITHOME",
                                    publishTime = pubDate.formatDate(),
                                    imageUrl = description.extractFirstImageUrl(),
                                    url = link,
                                )
                            )
                        }
                        currentTag = ""
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        newsList
    }



    private fun getMockData(page: Int, pageSize: Int): NewsListData {
        val mockNews = News.mock(page, pageSize)
        return NewsListData(
            banners = if (page == 1) Banner.mock() else emptyList(),
            news = mockNews,
            hasMore = page < 5
        )
    }

    companion object {
        private const val PREFS_NAME = "news_cache"
        private const val CACHE_KEY = "cached_news_list"
        private const val CACHE_TIME_KEY = "cache_time"
        private const val CACHE_DURATION = 2 * 60 * 1000L // 2 minutes
        private const val RSS_URL = "https://www.ithome.com/rss/"
    }
}
