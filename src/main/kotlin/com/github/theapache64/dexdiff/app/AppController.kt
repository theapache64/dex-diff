package com.github.theapache64.dexdiff.app

import com.github.theapache64.dexdiff.data.repo.AppRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppController @Inject constructor(
    private val appRepo: AppRepo,
) {
    fun onArgs(args: Array<String>?) {
        appRepo.args = args?.toList()
    }
}