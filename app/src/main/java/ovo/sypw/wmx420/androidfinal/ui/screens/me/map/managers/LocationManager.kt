package ovo.sypw.wmx420.androidfinal.ui.screens.me.map.managers

import android.content.Context
import android.util.Log
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.LocationSource
import com.amap.api.maps.model.LatLng

private const val TAG = "LocationManager"

/**
 * 定位管理器
 * 封装高德地图定位客户端的创建、配置和生命周期管理
 */
/**
 * 自定义定位源，用于地图 SDK 显示定位蓝点
 */
class MapLocationSource : LocationSource {
    var listener: LocationSource.OnLocationChangedListener? = null
        private set

    override fun activate(p0: LocationSource.OnLocationChangedListener?) {
        listener = p0
    }

    override fun deactivate() {
        listener = null
    }
}

class LocationManager(
    private val context: Context,
    private val onLocationUpdate: (LatLng, Boolean) -> Unit,
    private val onLocationError: (Int, String) -> Unit
) {
    private var locationClient: AMapLocationClient? = null
    private var isFirstLocate = true

    /**
     * 定位源，用于地图 SDK 显示定位蓝点
     */
    val locationSource = MapLocationSource()

    /**
     * 定位监听器
     */
    private val locationListener = AMapLocationListener { location ->
        if (location != null) {
            if (location.errorCode == 0) {
                // 将定位数据传递给地图 SDK
                locationSource.listener?.onLocationChanged(location)

                val latLng = LatLng(location.latitude, location.longitude)
                onLocationUpdate(latLng, isFirstLocate)

                if (isFirstLocate) {
                    isFirstLocate = false
                }
            } else {
                Log.w(TAG, "定位失败: errorCode=${location.errorCode}, errorInfo=${location.errorInfo}")
                onLocationError(location.errorCode, location.errorInfo ?: "未知错误")
            }
        }
    }

    /**
     * 初始化定位客户端
     */
    fun initialize(): Boolean {
        return try {
            AMapLocationClient.updatePrivacyShow(context, true, true)
            AMapLocationClient.updatePrivacyAgree(context, true)

            locationClient = AMapLocationClient(context).apply {
                val option = AMapLocationClientOption().apply {
                    locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
                    locationPurpose = AMapLocationClientOption.AMapLocationPurpose.SignIn
                    isOnceLocation = false
                    isNeedAddress = true
                    interval = 1000
                }
                setLocationOption(option)
                setLocationListener(locationListener)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "初始化定位客户端失败: $e")
            false
        }
    }

    /**
     * 启动定位
     */
    fun startLocation() {
        try {
            locationClient?.startLocation()
            Log.d(TAG, "定位服务已启动")
        } catch (e: Exception) {
            Log.e(TAG, "启动定位服务失败", e)
        }
    }

    /**
     * 停止定位
     */
    fun stopLocation() {
        try {
            locationClient?.stopLocation()
        } catch (e: Exception) {
            Log.e(TAG, "停止定位服务失败", e)
        }
    }

    /**
     * 检查定位服务是否已启动
     */
    fun isStarted(): Boolean = locationClient?.isStarted == true

    /**
     * 重置首次定位标志
     */
    fun resetFirstLocate() {
        isFirstLocate = true
    }

    /**
     * 销毁资源
     */
    fun destroy() {
        try {
            locationClient?.stopLocation()
            locationClient?.onDestroy()
            locationClient = null
        } catch (e: Exception) {
            Log.e(TAG, "销毁定位客户端失败", e)
        }
    }
}
