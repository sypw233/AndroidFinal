# AndroidFinal

一个基于 Jetpack Compose 的现代化 Android 应用程序，采用 Material Design 3 设计规范，集成了多种功能模块。

---

## 项目概述

本项目是一个综合性 Android 应用，包含新闻浏览、视频播放、Bilibili 排行榜展示、用户认证、地图服务等功能。项目采用 MVVM 架构模式，使用 Kotlin Coroutines 进行异步处理，并通过 Koin 实现依赖注入。

---

## 技术栈

| 类别 | 技术 |
|------|------|
| **开发语言** | Kotlin 2.2.21 |
| **UI 框架** | Jetpack Compose (BOM 2025.12.01) |
| **设计规范** | Material Design 3 |
| **架构模式** | MVVM |
| **依赖注入** | Koin 4.1.1 |
| **网络请求** | Retrofit 3.0.0 + Gson |
| **图片加载** | Coil 3.3.0 |
| **视频播放** | Media3 ExoPlayer 1.9.0 |
| **认证服务** | Firebase Auth |
| **数据存储** | Firebase Firestore |
| **导航** | Navigation Compose 2.9.6 |
| **图表展示** | Compose Charts 0.2.1 |
| **WebView** | Compose WebView 0.33.6 |
| **地图服务** | 高德地图 SDK |
| **广告服务** | Google AdMob 24.9.0 |

---

## 项目结构

```
app/src/main/java/ovo/sypw/wmx420/androidfinal/
|-- MainActivity.kt           # 应用主入口
|-- MyApplication.kt          # Application 类
|-- ads/                      # 广告模块
|-- data/
|   |-- model/               # 数据模型
|   |   |-- Banner.kt        # 轮播图数据
|   |   |-- BilibiliRanking.kt  # B站排行榜数据
|   |   |-- ChartData.kt     # 图表数据
|   |   |-- News.kt          # 新闻数据
|   |   |-- User.kt          # 用户数据
|   |   |-- Video.kt         # 视频数据
|   |-- remote/              # 网络 API 服务
|   |-- repository/          # 数据仓库
|       |-- BilibiliRepository.kt
|       |-- NewsRepository.kt
|       |-- UserRepository.kt
|       |-- VideoRepository.kt
|-- di/                      # 依赖注入模块
|-- ui/
|   |-- components/          # 通用 UI 组件
|   |-- navigation/          # 导航配置
|   |-- screens/
|       |-- bilibilirank/    # B站排行榜页面
|       |-- home/            # 首页
|       |-- intro/           # 引导页
|       |-- login/           # 登录页
|       |-- main/            # 主页面框架
|       |-- me/              # 个人中心
|       |-- settings/        # 设置页
|       |-- splash/          # 启动页
|       |-- video/           # 视频页面
|       |-- webview/         # WebView 页面
|-- utils/                   # 工具类
```

---

## 功能模块

### 首页 (Home)
- 轮播图展示
- 分类按钮导航
- 新闻列表 (支持分页加载和下拉刷新)
- 返回顶部浮动按钮

### Bilibili 排行榜
- 热门视频排行展示
- 多种图表可视化 (折线图、柱状图、饼图)
- 点击跳转视频详情

### 视频模块
- 视频列表展示
- 视频详情播放 (基于 ExoPlayer)

### 个人中心 (Me)
- 用户登录/登出
- 个人信息展示
- 地图服务入口
- 设置入口

### 用户认证
- Firebase 邮箱登录
- Google 账号登录
- 手机号验证码登录

### 地图服务
- 高德地图集成
- 位置服务

---

## 环境配置

### 前置要求
- Android Studio Ladybug 或更高版本
- JDK 17
- Android SDK 35+ (minSdk: 35, targetSdk: 36)

### 配置步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd AndroidFinal
   ```

2. **配置 Firebase**
   - 在 Firebase Console 创建项目
   - 下载 `google-services.json` 文件
   - 将文件放置到 `app/` 目录下

3. **配置高德地图 (可选)**
   - 在高德开放平台申请 API Key
   - 在 `AndroidManifest.xml` 中更新 `com.amap.api.v2.apikey` 的值

4. **构建运行**
   ```bash
   ./gradlew assembleDebug
   ```

---

## 权限说明

| 权限 | 用途 |
|------|------|
| INTERNET | 网络访问 |
| ACCESS_NETWORK_STATE | 网络状态检测 |
| ACCESS_FINE_LOCATION | 精确位置 (地图功能) |
| ACCESS_COARSE_LOCATION | 粗略位置 (地图功能) |

---

## 架构说明

项目采用 MVVM (Model-View-ViewModel) 架构:

- **Model**: `data/model/` 目录下的数据类和 `data/repository/` 目录下的数据仓库
- **View**: `ui/screens/` 目录下的 Composable 函数
- **ViewModel**: 各功能模块对应的 ViewModel 类

数据流向:
```
Repository -> ViewModel (StateFlow) -> Composable UI
```

---

## 依赖管理

项目使用 Version Catalog 管理依赖版本，配置文件位于 `gradle/libs.versions.toml`。
