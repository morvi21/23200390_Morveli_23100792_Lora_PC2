package com.example.prcticacalificada2

import android.app.Application

class DiarioEspacialApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
