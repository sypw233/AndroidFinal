package ovo.sypw.wmx420.androidfinal.ui.screens.me.map.state

import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.PoiItemV2

/**
 * 地图 UI 状态
 */
data class MapUiState(
    val showSearchPanel: Boolean = false,
    val searchKeyWord: String = "",
    val isSearching: Boolean = false
)

/**
 * 定位状态
 */
data class LocationState(
    val currentLocation: LatLng = LatLng(39.9054895, 116.3976317),
    val isLocationReady: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val isFirstLocate: Boolean = true
)

/**
 * POI 状态
 */
data class PoiState(
    val poiList: List<PoiItemV2> = emptyList(),
    val selectedPoi: PoiItemV2? = null
)

/**
 * 地图屏幕整体状态
 */
data class MapScreenState(
    val uiState: MapUiState = MapUiState(),
    val locationState: LocationState = LocationState(),
    val poiState: PoiState = PoiState()
)

/**
 * 快捷搜索分类常量
 */
object MapConstants {
    val QUICK_SEARCH_CATEGORIES = listOf(
        "厕所", "卫生间", "洗手间", "Toilet", "餐厅", "加油站", "停车场"
    )
}
