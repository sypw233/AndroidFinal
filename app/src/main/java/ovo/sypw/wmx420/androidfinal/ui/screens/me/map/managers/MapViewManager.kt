package ovo.sypw.wmx420.androidfinal.ui.screens.me.map.managers

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.MapView
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.services.core.PoiItemV2

/**
 * 地图视图管理器
 * 封装 MapView 和 AMap 的创建、配置和交互操作
 */
class MapViewManager(
    context: Context
) {
    /**
     * 地图视图
     */
    val mapView: MapView = MapView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        onCreate(Bundle())
    }

    /**
     * 地图对象
     */
    val aMap = mapView.map

    /**
     * 配置地图定位样式
     *
     * @param locationSource 定位源
     */
    fun setupLocationStyle(locationSource: LocationSource) {
        aMap.setLocationSource(locationSource)
        aMap.isMyLocationEnabled = true
        aMap.myLocationStyle = MyLocationStyle().apply {
            myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW)
            interval(2000)
        }
        aMap.uiSettings.isMyLocationButtonEnabled = false
    }

    /**
     * 移动地图视角到指定位置
     *
     * @param latLng 目标位置
     * @param zoom 缩放级别
     * @param animate 是否使用动画
     */
    fun moveTo(latLng: LatLng, zoom: Float = 15f, animate: Boolean = true) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom)
        if (animate) {
            aMap.animateCamera(cameraUpdate)
        } else {
            aMap.moveCamera(cameraUpdate)
        }
    }

    /**
     * 清除地图上的所有标记
     */
    fun clearMarkers() {
        aMap.clear()
    }

    /**
     * 添加 POI 标记到地图
     *
     * @param poiList POI 列表
     * @return 添加的 Marker 列表
     */
    fun addPoiMarkers(poiList: List<PoiItemV2>): List<Marker> {
        val markers = mutableListOf<Marker>()

        poiList.forEachIndexed { index, poi ->
            poi.latLonPoint?.let { point ->
                val markerOptions = MarkerOptions()
                    .position(LatLng(point.latitude, point.longitude))
                    .title(poi.title)
                    .snippet(poi.snippet)

                val marker = aMap.addMarker(markerOptions)
                marker.`object` = index
                markers.add(marker)
            }
        }

        return markers
    }

    /**
     * 设置标记点击监听器
     *
     * @param onMarkerClick 点击回调，参数为标记关联的索引
     */
    fun setOnMarkerClickListener(onMarkerClick: (Int) -> Unit) {
        aMap.setOnMarkerClickListener { marker ->
            val index = marker.`object` as? Int ?: return@setOnMarkerClickListener false
            onMarkerClick(index)
            true
        }
    }

    /**
     * 恢复地图
     */
    fun onResume() {
        mapView.onResume()
    }

    /**
     * 暂停地图
     */
    fun onPause() {
        mapView.onPause()
    }

    /**
     * 销毁地图资源
     */
    fun destroy() {
        try {
            aMap.isMyLocationEnabled = false
            aMap.clear()
            mapView.onDestroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
