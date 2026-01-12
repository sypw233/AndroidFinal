plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "ovo.sypw.wmx420.androidfinal"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "ovo.sypw.wmx420.androidfinal"
        minSdk = 35
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
        buildFeatures {
            compose = true
        }
    }
    packaging {
        resources {
            // 使用 excludes 直接将其从打包过程中剔除
            excludes.add("META-INF/INDEX.LIST")
            excludes.add("META-INF/DEPENDENCIES")
             excludes.add("META-INF/io.netty.versions.properties")
            // excludes.add("META-INF/okio.kotlin_module")
        }
    }
    dependencies {
        implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.compose.ui)
        implementation(libs.androidx.compose.ui.graphics)
        implementation(libs.androidx.compose.ui.tooling.preview)
        implementation(libs.androidx.compose.material3)
        implementation(libs.androidx.compose.material.icons.core)
        implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
        debugImplementation(libs.androidx.compose.ui.tooling)

        implementation(libs.androidx.navigation.compose)
        implementation(libs.androidx.compose.material.icons.extended)

        implementation(libs.androidx.compose.foundation)
        implementation(libs.androidx.compose.animation)

        // 生命周期监听 (Lifecycle & ViewModel)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.lifecycle.viewmodel.compose)
        implementation(libs.androidx.lifecycle.process)

        // Google Ads
        implementation(libs.play.services.ads)

        // Kotlin Coroutines
        implementation(libs.kotlinx.coroutines.android)
        implementation(libs.kotlinx.coroutines.play.services)

        // 依赖注入 (Koin)
        implementation(libs.koin.android)
        implementation(libs.koin.androidx.compose)

        // 网络请求 (Retrofit & Gson)
        implementation(libs.retrofit)
        implementation(libs.converter.gson)
        implementation(libs.retrofit2.kotlinx.serialization.converter)
        // Kotlin Serialization JSON 库
        implementation(libs.kotlinx.serialization.json)

        // 图片加载
        implementation(libs.coil.compose)
        implementation(libs.coil.network.okhttp)

        // Firebase
        implementation(platform(libs.firebase.bom))
        implementation(libs.firebase.analytics)
        implementation(libs.firebase.auth)
        implementation(libs.firebase.firestore)
        implementation(libs.play.services.auth)
        // Media3 ExoPlayer
        implementation(libs.androidx.media3.exoplayer)
        implementation(libs.androidx.media3.ui)

        // Charts
        implementation(libs.compose.charts)

        // Compose WebView
        implementation(libs.compose.webview)

        // AI agent
        implementation(libs.koog.agents)
    }
}
dependencies {
    implementation(libs.androidx.compose.animation)
}
