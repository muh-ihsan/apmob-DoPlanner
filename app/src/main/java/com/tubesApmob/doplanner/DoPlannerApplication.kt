package com.tubesApmob.doplanner

import android.app.Application
import timber.log.Timber

class DoPlannerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree()) // Untuk menggunakan Timber sebagai logging
    }
}