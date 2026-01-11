package ovo.sypw.wmx420.androidfinal

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import ovo.sypw.wmx420.androidfinal.ads.AppOpenAdManager
import ovo.sypw.wmx420.androidfinal.di.networkModule
import ovo.sypw.wmx420.androidfinal.di.repositoryModule
import ovo.sypw.wmx420.androidfinal.di.viewModelModule

class MyApplication : Application() {
    var appOpenAdManager: AppOpenAdManager? = null

    override fun onCreate() {
        super.onCreate()

        // 初始化 Firebase
        FirebaseApp.initializeApp(this)
        // 初始化koin
        initKoin()
        appOpenAdManager = AppOpenAdManager(this)
        // 初始化 Google Ads
        MobileAds.initialize(this) { status ->
            Log.d("Ads", "Initialization complete: $status")
        }
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(
                listOf(
                    networkModule,
                    repositoryModule,
                    viewModelModule,
                    )
            )
        }
    }
}
