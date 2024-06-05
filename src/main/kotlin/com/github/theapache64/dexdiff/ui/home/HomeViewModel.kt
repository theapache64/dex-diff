package com.github.theapache64.dexdiff.ui.home

import com.github.theapache64.dexdiff.data.repo.AppRepo
import com.github.theapache64.dexdiff.utils.ApkDecompiler
import com.theapache64.cyclone.core.livedata.LiveData
import com.theapache64.cyclone.core.livedata.MutableLiveData
import javax.inject.Inject


class HomeViewModel @Inject constructor(
    private val appRepo: AppRepo,
) {

    companion object {
        const val INIT_MSG = "➡️ initialising..."
        const val DONE_MSG = "✅ Done"
    }

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    init {
        _status.value = INIT_MSG
        val appArgs = appRepo.args
        require(appArgs != null) {
            "Arguments not found"
        }
        ApkDecompiler(appArgs.beforeApk).decompile()
    }


}