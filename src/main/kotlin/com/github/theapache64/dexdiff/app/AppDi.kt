package com.github.theapache64.dexdiff.app

import com.github.theapache64.dexdiff.di.module.RepoModule
import com.github.theapache64.dexdiff.ui.home.HomeActivity
import com.github.theapache64.dexdiff.ui.splash.SplashActivity
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        RepoModule::class
    ]
)
interface AppComponent {
    fun inject(app: App)
    fun inject(splashActivity: SplashActivity)
    fun inject(homeActivity: HomeActivity)
}


