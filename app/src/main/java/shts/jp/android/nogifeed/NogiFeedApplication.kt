package shts.jp.android.nogifeed

import android.app.Application
import android.content.Intent

import shts.jp.android.nogifeed.receivers.TokenRegistrationService

class NogiFeedApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, TokenRegistrationService::class.java))
    }
}
