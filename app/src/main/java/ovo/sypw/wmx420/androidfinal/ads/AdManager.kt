package ovo.sypw.wmx420.androidfinal.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import ovo.sypw.wmx420.androidfinal.utils.PreferenceUtils

/**
 * App Open Ad 管理器
 * 在应用从后台切换到前台时显示开屏广告
 * 仅当用户设置启用 Google 广告时生效
 */
class AppOpenAdManager(private val application: Application) : DefaultLifecycleObserver,
    Application.ActivityLifecycleCallbacks {

    companion object {
        private const val TAG = "AppOpenAdManager"

        // Google AdMob 开屏广告测试 ID
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"
    }

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var currentActivity: Activity? = null

    private var isFirstLaunch = true

    private var retryAttempt = 0

    init {
        // 注册应用生命周期观察者
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        // 注册 Activity 生命周期回调
        application.registerActivityLifecycleCallbacks(this)
    }

    /**
     * 检查是否启用 Google 广告
     */
    private fun isGoogleAdEnabled(): Boolean {
        return PreferenceUtils.useGoogleAd(application)
    }

    /**
     * 检查广告是否可用
     */
    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && !isShowingAd
    }

    /**
     * 加载开屏广告（仅在启用 Google 广告时）
     */
    /**
     * 加载开屏广告（仅在启用 Google 广告时）
     */
    fun loadAd() {
        val googleAdEnabled = isGoogleAdEnabled()
        val adAvailable = isAdAvailable()
        Log.d(
            TAG,
            "loadAd() called. GoogleAdEnabled: $googleAdEnabled, IsLoading: $isLoadingAd, IsAdAvailable: $adAvailable"
        )

        // 如果未启用 Google 广告，不加载
        if (!googleAdEnabled) {
            Log.d(TAG, "Google 广告未启用，跳过加载")
            isFirstLaunch = false
            return
        }

        if (isLoadingAd || adAvailable) {
            Log.d(TAG, "广告正在加载中或已可用，不重复加载")
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        Log.d(TAG, "开始请求 Google 开屏广告: $AD_UNIT_ID")

        AppOpenAd.load(
            application,
            AD_UNIT_ID,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    Log.d(TAG, "开屏广告加载成功")
                    appOpenAd = ad
                    isLoadingAd = false
                    retryAttempt = 0 // 重置重试次数

                    // 首次启动时立即显示广告
                    if (isFirstLaunch) {
                        if (currentActivity != null) {
                            Log.d(TAG, "首次启动，Activity已就绪，立即展示广告")
                            isFirstLaunch = false
                            showAdIfAvailable()
                        } else {
                            Log.d(TAG, "首次启动，Activity未就绪，等待 onActivityStarted")
                            // 保持 isFirstLaunch = true，让 onActivityStarted 处理
                        }
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(
                        TAG,
                        "开屏广告加载失败: Code=${loadAdError.code}, Message=${loadAdError.message}, Response=${loadAdError.responseInfo}"
                    )
                    isLoadingAd = false

                    // 失败后尝试重试（最多 3 次）
                    if (retryAttempt < 3) {
                        retryAttempt++
                        val delayMillis = (retryAttempt * 1000L).coerceAtMost(5000L)
                        Log.d(TAG, "将在 ${delayMillis}ms 后重试加载广告 (第 $retryAttempt 次)")

                        Handler(Looper.getMainLooper()).postDelayed({
                            loadAd()
                        }, delayMillis)
                    } else {
                        Log.w(TAG, "广告加载重试次数耗尽，不再重试")
                        isFirstLaunch = false // 超过重试次数，不再尝试
                    }
                }
            }
        )
    }

    /**
     * 显示开屏广告（如果可用且已启用 Google 广告）
     */
    fun showAdIfAvailable() {
        Log.d(
            TAG,
            "showAdIfAvailable() called. IsShowing: $isShowingAd, IsAdAvailable: ${isAdAvailable()}"
        )
        // 如果未启用 Google 广告，不显示
        if (!isGoogleAdEnabled()) {
            Log.d(TAG, "Google 广告未启用，不展示")
            return
        }

        if (isShowingAd) {
            Log.d(TAG, "广告正在显示中")
            return
        }

        if (!isAdAvailable()) {
            Log.d(TAG, "广告不可用，尝试加载")
            loadAd()
            return
        }

        val activity = currentActivity ?: run {
            Log.e(TAG, "当前 Activity 为 null，无法展示广告")
            return
        }

        Log.d(TAG, "准备在 Activity: $activity 展示广告")

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "广告已关闭")
                appOpenAd = null
                isShowingAd = false
                // 广告关闭后预加载下一个广告
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "广告显示失败: Code=${adError.code}, Message=${adError.message}")
                appOpenAd = null
                isShowingAd = false
                loadAd()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "广告正在显示")
                isShowingAd = true
            }
        }

        isShowingAd = true
        appOpenAd?.show(activity)
    }

    // ========== DefaultLifecycleObserver 实现 ==========

    override fun onStart(owner: LifecycleOwner) {
        // 应用从后台切换到前台时显示广告（仅当启用 Google 广告时）
        if (!isFirstLaunch && isGoogleAdEnabled()) {
            Log.d(TAG, "从后台返回，延迟 1s 展示广告")
            Handler(Looper.getMainLooper()).postDelayed({
                // 二次检查，避免 1s 后应用又切后台了（虽然 showAdIfAvailable 内部也会检查但多一层保险）
                if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    showAdIfAvailable()
                } else {
                    Log.d(TAG, "延迟后应用不再前台，取消展示")
                }
            }, 1000)
        }
    }

    // ========== Application.ActivityLifecycleCallbacks 实现 ==========

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        // 如果当前没有显示广告，更新当前 Activity
        if (!isShowingAd) {
            currentActivity = activity
        }

        // 检查是否有延迟的冷启动广告需要展示
        if (isFirstLaunch && isGoogleAdEnabled() && isAdAvailable()) {
            Log.d(TAG, "onActivityStarted: 首次启动，展示等待中的广告")
            isFirstLaunch = false
            showAdIfAvailable()
        }
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }
}
