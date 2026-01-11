package ovo.sypw.wmx420.androidfinal.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ovo.sypw.androidendproject.ui.screens.login.LoginViewModel
import ovo.sypw.wmx420.androidfinal.data.remote.ApiService
import ovo.sypw.wmx420.androidfinal.data.remote.RetrofitClient
import ovo.sypw.wmx420.androidfinal.data.repository.BilibiliRepository
import ovo.sypw.wmx420.androidfinal.data.repository.NewsRepository
import ovo.sypw.wmx420.androidfinal.data.repository.UserRepository
import ovo.sypw.wmx420.androidfinal.data.repository.VideoRepository
import ovo.sypw.wmx420.androidfinal.ui.screens.bilibilirank.BilibiliRankViewModel
import ovo.sypw.wmx420.androidfinal.ui.screens.home.HomeViewModel
import ovo.sypw.wmx420.androidfinal.ui.screens.me.MeViewModel
import ovo.sypw.wmx420.androidfinal.ui.screens.video.VideoViewModel

val networkModule = module {
    single { RetrofitClient.okHttpClient }
    single { RetrofitClient.retrofit }
    single { RetrofitClient.retrofit.create(ApiService::class.java) }
}
val repositoryModule = module {
    single { NewsRepository(get(), androidContext()) }
    single { UserRepository() }
    single { VideoRepository() }
    single { BilibiliRepository(get(), androidContext()) }
}


val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::BilibiliRankViewModel)
    viewModelOf(::MeViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::VideoViewModel)
}