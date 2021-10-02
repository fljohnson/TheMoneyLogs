package com.fouracessoftware.themoneylogs

import android.app.Application
import android.content.Context
import com.fouracessoftware.themoneylogs.data.roomy.CentralContent

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        context = getApplicationContext()

        CentralContent.engage()

    }

    companion object {
        private lateinit var context: Context
        fun getContext(): Context {
            return context
        }
    }
}