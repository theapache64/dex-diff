package com.github.theapache64.dexdiff.app

import com.github.theapache64.dexdiff.ui.splash.SplashActivity
import com.theapache64.cyclone.core.Application


/**
 * Application class
 */
class App : Application() {

    companion object {
        var args: Array<String>? = null
        lateinit var di: AppComponent
    }


    override fun onCreate() {
        super.onCreate()
        di = DaggerAppComponent.create()
        di.inject(this)

        val splashIntent = SplashActivity.getStartIntent()
        startActivity(splashIntent)
    }
}

/**
 * Entry point
 */
fun main(args: Array<String>) {
    println("⚔️ dex-diff v0.0.7")
    App.args = args
    App().onCreate()
}