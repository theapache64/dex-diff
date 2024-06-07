package com.github.theapache64.dexdiff.data.repo

import com.github.theapache64.dexdiff.app.App
import com.github.theapache64.dexdiff.data.local.AppArgs
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface AppRepo {
    var args: AppArgs?
}

@Singleton
class AppRepoImpl @Inject constructor() : AppRepo {
    override var args: AppArgs? = AppArgs(
        beforeApk = File(App.args?.getOrNull(0) ?: error("Before APK is missing")),
        afterApk = File(App.args?.getOrNull(1) ?: error("After APK is missing")),
        appPackages = App.args?.getOrNull(2)
            ?.split(",")
            ?.map {
                it.replace(".", "/")
            } ?: emptyList()
    )
}