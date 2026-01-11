package ovo.sypw.wmx420.androidfinal.ui.screens.me.map.managers

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItemV2
import com.amap.api.services.poisearch.PoiResultV2
import com.amap.api.services.poisearch.PoiSearchV2

private const val TAG = "PoiSearchManager"

/**
 * POI 搜索结果回调
 */
interface PoiSearchCallback {
    fun onSearchSuccess(poiList: List<PoiItemV2>)
    fun onSearchFailed(message: String)
    fun onSearchStart()
    fun onSearchEnd()
}

/**
 * POI 搜索管理器
 * 封装高德地图 POI 搜索功能
 */
class PoiSearchManager(
    private val context: Context
) {
    /**
     * 执行 POI 搜索
     *
     * @param keyword 搜索关键词
     * @param centerLat 搜索中心点纬度
     * @param centerLng 搜索中心点经度
     * @param radius 搜索半径（米）
     * @param pageSize 每页结果数量
     * @param callback 搜索结果回调
     */
    fun searchPoi(
        keyword: String,
        centerLat: Double,
        centerLng: Double,
        radius: Int = 10000,
        pageSize: Int = 20,
        callback: PoiSearchCallback
    ) {
        if (keyword.isBlank()) {
            Toast.makeText(context, "请输入搜索关键词", Toast.LENGTH_SHORT).show()
            return
        }

        callback.onSearchStart()

        try {
            val query = PoiSearchV2.Query(keyword, "", "").apply {
                this.pageSize = pageSize
                this.pageNum = 0
            }

            val poiSearch = PoiSearchV2(context, query).apply {
                bound = PoiSearchV2.SearchBound(
                    LatLonPoint(centerLat, centerLng),
                    radius
                )
            }

            poiSearch.setOnPoiSearchListener(object : PoiSearchV2.OnPoiSearchListener {
                override fun onPoiSearched(result: PoiResultV2?, rCode: Int) {
                    callback.onSearchEnd()

                    if (rCode == 1000 && result != null) {
                        val pois = result.pois ?: emptyList()
                        val filteredPois = pois.filter { it.latLonPoint != null }
                        callback.onSearchSuccess(filteredPois)
                    } else {
                        callback.onSearchFailed("未找到相关地点")
                    }
                }

                override fun onPoiItemSearched(poiItem: PoiItemV2?, rCode: Int) {
                    // 不处理单个 POI 搜索
                }
            })

            poiSearch.searchPOIAsyn()

        } catch (e: Exception) {
            callback.onSearchEnd()
            Log.e(TAG, "POI搜索失败", e)
            callback.onSearchFailed("搜索失败: ${e.message}")
        }
    }
}
