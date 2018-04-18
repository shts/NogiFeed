package shts.jp.android.nogifeed

import android.app.Application
import android.content.Intent
import com.facebook.stetho.Stetho

import shts.jp.android.nogifeed.receivers.TokenRegistrationService

class NogiFeedApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        startService(Intent(this, TokenRegistrationService::class.java))
    }
}
