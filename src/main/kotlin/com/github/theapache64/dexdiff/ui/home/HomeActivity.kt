package com.github.theapache64.dexdiff.ui.home

import com.github.theapache64.dexdiff.data.local.AppArgs
import com.theapache64.cyclone.core.Activity
import com.theapache64.cyclone.core.Intent

class HomeActivity : Activity() {
    companion object {

        private const val KEY_APP_ARGS = "app_args"

        fun getStartIntent(appArgs: AppArgs): Intent {
            return Intent(HomeActivity::class).apply {
                putExtra(KEY_APP_ARGS, appArgs)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val appArgs = intent.data[KEY_APP_ARGS] as AppArgs
        println("App Args is $appArgs")
    }
}