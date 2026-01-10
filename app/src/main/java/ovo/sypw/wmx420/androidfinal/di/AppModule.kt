package ovo.sypw.wmx420.androidfinal.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ovo.sypw.wmx420.androidfinal.data.remote.ApiService
import ovo.sypw.wmx420.androidfinal.data.remote.RetrofitClient
import ovo.sypw.wmx420.androidfinal.data.repository.NewsRepository
import ovo.sypw.wmx420.androidfinal.ui.screens.home.HomeViewModel

val networkModule = module {
    single { RetrofitClient.okHttpClient }
    single { RetrofitClient.retrofit }
    single { RetrofitClient.retrofit.create(ApiService::class.java) }
}
val repositoryModule = module {
    single { NewsRepository(get(), androidContext()) }
}


val viewModelModule = module {
    viewModelOf(::HomeViewModel)
//    viewModelOf(::VideoViewModel)
//    viewModelOf(::MeViewModel)
}