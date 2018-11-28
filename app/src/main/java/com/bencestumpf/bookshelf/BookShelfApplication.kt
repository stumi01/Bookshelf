package com.bencestumpf.bookshelf

import android.app.Application
import com.bencestumpf.bookshelf.di.appModule
import com.bencestumpf.bookshelf.di.dataModule
import com.bencestumpf.bookshelf.di.domainModule
import org.koin.android.ext.android.startKoin

class BookShelfApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(
            this,
            listOf(appModule, domainModule, dataModule)
        ) //, retrofitModule)) TODO enable this if the backend ready
    }

}