plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
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

        // 生命周期监听 (Lifecycle & ViewModel)
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
        implementation("androidx.lifecycle:lifecycle-process:2.10.0")

        // Google Ads
        implementation("com.google.android.gms:play-services-ads:24.9.0")

        // Kotlin Coroutines
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

        // 依赖注入 (Koin)
        implementation("io.insert-koin:koin-android:4.1.1")
        implementation("io.insert-koin:koin-androidx-compose:4.1.1")

        // 网络请求 (Retrofit & Gson)
        implementation("com.squareup.retrofit2:retrofit:3.0.0")
        implementation("com.squareup.retrofit2:converter-gson:3.0.0")
        implementation("com.google.code.gson:gson:2.13.2")

        // 图片加载
        implementation("io.coil-kt.coil3:coil-compose:3.3.0")
        implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

        // Firebase
        implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
        implementation("com.google.firebase:firebase-analytics")
        implementation("com.google.firebase:firebase-auth")
        implementation("com.google.firebase:firebase-firestore")

        // Media3 ExoPlayer
        implementation("androidx.media3:media3-exoplayer:1.9.0")
        implementation("androidx.media3:media3-ui:1.9.0")

        // Compose Chart
        implementation("com.patrykandpatrick.vico:compose-m3:2.4.1")

        // Compose WebView
        implementation("io.github.kevinnzou:compose-webview:0.33.6")

        // AI agent
        implementation("ai.koog:koog-agents:0.6.0")
    }
}