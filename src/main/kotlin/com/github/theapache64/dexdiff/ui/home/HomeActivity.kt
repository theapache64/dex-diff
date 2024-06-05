package com.github.theapache64.dexdiff.ui.home

import com.github.theapache64.dexdiff.app.App
import com.github.theapache64.dexdiff.data.local.AppArgs
import com.github.theapache64.dexdiff.ui.splash.SplashViewModel
import com.theapache64.cyclone.core.Activity
import com.theapache64.cyclone.core.Intent
import javax.inject.Inject

class HomeActivity : Activity() {

    @Inject
    lateinit var viewModel: HomeViewModel

    companion object {
        fun getStartIntent(): Intent {
            return Intent(HomeActivity::class)
        }
    }

    override fun onCreate() {
        super.onCreate()
        App.di.inject(this)

        viewModel.status.observe { msg ->
            println(msg)
        }
    }
}