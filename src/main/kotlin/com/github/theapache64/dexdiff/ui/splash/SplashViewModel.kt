package com.github.theapache64.dexdiff.ui.splash

import com.github.theapache64.dexdiff.data.repo.AppRepo
import com.theapache64.cyclone.core.livedata.LiveData
import com.theapache64.cyclone.core.livedata.MutableLiveData
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    appRepo: AppRepo
) {
    companion object{
        const val VERSION = "0.0.3"
    }

    private val _welcomeMsg = MutableLiveData<String>()
    val welcomeMsg: LiveData<String> = _welcomeMsg

    private val _goToHome = MutableLiveData<Boolean>()
    val goToHome: LiveData<Boolean> = _goToHome

    init {
        val splashMsg = "⚔️ dex-diff v$VERSION"
        _welcomeMsg.value = splashMsg
        _goToHome.value = true
    }


}
