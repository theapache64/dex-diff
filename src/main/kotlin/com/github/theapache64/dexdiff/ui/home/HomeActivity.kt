package com.github.theapache64.dexdiff.ui.home

import com.theapache64.cyclone.core.Activity
import com.theapache64.cyclone.core.Intent

class HomeActivity : Activity() {
    companion object {

        private const val KEY_SPLASH_MSG = "splashMsg"

        fun getStartIntent(splashMsg: String): Intent {
            return Intent(HomeActivity::class).apply {
                putExtra(KEY_SPLASH_MSG, splashMsg)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val splashMsg = getStringExtra(KEY_SPLASH_MSG)
        println("Home says splash message is '$splashMsg'")
    }
}