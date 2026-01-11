package ovo.sypw.wmx420.androidfinal.ui.screens.me.map

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.PoiItemV2
import ovo.sypw.wmx420.androidfinal.ui.screens.me.map.components.PoiDetailCard
import ovo.sypw.wmx420.androidfinal.ui.screens.me.map.components.PoiSearchResultItem
import ovo.sypw.wmx420.androidfinal.ui.screens.me.map.components.QuickSearchChip
import ovo.sypw.wmx420.androidfinal.ui.screens.me.map.managers.LocationManager
import ovo.sypw.wmx420.androidfinal.ui.screens.me.map.managers.MapViewManager
import ovo.sypw.wmx420.androidfinal.ui.screens.me.map.managers.PoiSearchCallback
import ovo.sypw.wmx420.androidfinal.ui.screens.me.map.managers.PoiSearchManager
import ovo.sypw.wmx420.androidfinal.ui.screens.me.map.state.MapConstants

private const val TAG = "MapScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBack: () -> Unit
) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    // UI 状态
    var showSearchPanel by remember { mutableStateOf(false) }
    var searchKeyWord by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    // POI 状态
    var poiList by remember { mutableStateOf<List<PoiItemV2>>(emptyList()) }
    var selectedPoi by remember { mutableStateOf<PoiItemV2?>(null) }

    // 定位状态
    var currentLocation by remember { mutableStateOf(LatLng(39.9054895, 116.3976317)) }
    var isLocationReady by remember { mutableStateOf(false) }

    // 权限状态
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (granted) {
            Toast.makeText(context, "已获取定位权限", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "未获取定位权限", Toast.LENGTH_SHORT).show()
        }
    }

    // 地图管理器
    val mapViewManager = remember { MapViewManager(context) }

    // 定位管理器
    val locationManager = remember {
        LocationManager(
            context = context,
            onLocationUpdate = { latLng, isFirstLocate ->
                currentLocation = latLng
                isLocationReady = true

                // 仅首次定位时自动移动视角
                if (isFirstLocate) {
                    mapViewManager.moveTo(latLng, 15f, animate = false)
                }
            },
            onLocationError = { errorCode, errorInfo ->
                Toast.makeText(context, "定位失败：$errorInfo", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // POI 搜索管理器
    val poiSearchManager = remember { PoiSearchManager(context) }

    // 设置地图定位样式
    LaunchedEffect(mapViewManager) {
        mapViewManager.setupLocationStyle(locationManager.locationSource)
    }

    // 设置标记点击监听
    LaunchedEffect(mapViewManager) {
        mapViewManager.setOnMarkerClickListener { index ->
            if (index in poiList.indices) {
                selectedPoi = poiList[index]
                showSearchPanel = false
            }
        }
    }

    // 权限获取后初始化并启动定位
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            if (locationManager.initialize()) {
                locationManager.startLocation()
            }
        }
    }

    // 请求权限
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // 打开搜索面板时自动聚焦
    LaunchedEffect(showSearchPanel) {
        if (showSearchPanel) {
            try {
                focusRequester.requestFocus()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    // 清理资源
    DisposableEffect(Unit) {
        onDispose {
            locationManager.destroy()
            mapViewManager.destroy()
        }
    }

    // 执行 POI 搜索
    fun searchPoi(keyword: String) {
        focusManager.clearFocus()
        selectedPoi = null

        poiSearchManager.searchPoi(
            keyword = keyword,
            centerLat = currentLocation.latitude,
            centerLng = currentLocation.longitude,
            callback = object : PoiSearchCallback {
                override fun onSearchStart() {
                    isSearching = true
                }

                override fun onSearchEnd() {
                    isSearching = false
                }

                override fun onSearchSuccess(results: List<PoiItemV2>) {
                    poiList = results

                    // 清除旧标记，添加新标记
                    mapViewManager.clearMarkers()
                    mapViewManager.addPoiMarkers(results)

                    // 移动到第一个结果
                    results.firstOrNull()?.latLonPoint?.let { firstPoint ->
                        mapViewManager.moveTo(
                            LatLng(firstPoint.latitude, firstPoint.longitude),
                            15f
                        )
                    }
                }

                override fun onSearchFailed(message: String) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    poiList = emptyList()
                }
            }
        )
    }

    // 快捷搜索分类
    val quickSearchCategories = MapConstants.QUICK_SEARCH_CATEGORIES

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("附近地图") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 地图
            AndroidView(
                factory = { mapViewManager.mapView },
                modifier = Modifier.fillMaxSize(),
                update = { _ ->
                    mapViewManager.onResume()
                }
            )

            // FAB 按钮组
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 搜索按钮
                FloatingActionButton(
                    onClick = { showSearchPanel = !showSearchPanel },
                    containerColor = if (showSearchPanel)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        if (showSearchPanel) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = if (showSearchPanel) "关闭搜索" else "搜索"
                    )
                }

                // 定位按钮
                FloatingActionButton(
                    onClick = {
                        if (hasLocationPermission) {
                            if (!locationManager.isStarted()) {
                                locationManager.startLocation()
                            }
                            // 手动点击定位，强制移动视角
                            if (isLocationReady) {
                                mapViewManager.moveTo(currentLocation, 15f)
                                Toast.makeText(context, "已回到当前位置", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "正在获取位置...", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "定位到当前位置")
                }
            }

            // 搜索面板（从顶部滑入）
            AnimatedVisibility(
                visible = showSearchPanel,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // 搜索输入框
                        OutlinedTextField(
                            value = searchKeyWord,
                            onValueChange = { searchKeyWord = it },
                            placeholder = { Text("搜索附近地点...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            trailingIcon = {
                                if (isSearching) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else if (searchKeyWord.isNotBlank()) {
                                    IconButton(onClick = { searchPoi(searchKeyWord) }) {
                                        Icon(Icons.Default.Search, contentDescription = "搜索")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 快捷搜索分类
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(quickSearchCategories) { category ->
                                QuickSearchChip(
                                    text = category,
                                    onClick = {
                                        searchKeyWord = category
                                        searchPoi(category)
                                    }
                                )
                            }
                        }

                        // 搜索结果列表
                        if (poiList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "搜索结果 (${poiList.size})",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 300.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(poiList.take(15)) { poi ->
                                    PoiSearchResultItem(
                                        poi = poi,
                                        onClick = {
                                            selectedPoi = poi
                                            showSearchPanel = false
                                            poi.latLonPoint?.let { point ->
                                                mapViewManager.moveTo(
                                                    LatLng(point.latitude, point.longitude),
                                                    17f
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // POI 详情卡片
            selectedPoi?.let { poi ->
                PoiDetailCard(
                    poi = poi,
                    onDismiss = { selectedPoi = null },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .padding(bottom = 80.dp)
                )
            }
        }
    }
}
