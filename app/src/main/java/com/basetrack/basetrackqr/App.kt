package com.basetrack.basetrackqr

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm

@HiltAndroidApp
class App : Application(){
    override fun onCreate() {
        super.onCreate()
        Realm.init(applicationContext)
    }
}