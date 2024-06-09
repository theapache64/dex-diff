package com.github.theapache64.dexdiff.ui.splash

import com.theapache64.cyclone.core.livedata.LiveData
import com.theapache64.cyclone.core.livedata.MutableLiveData
import javax.inject.Inject

class SplashViewModel @Inject constructor() {
    private val _goToHome = MutableLiveData<Boolean>()
    val goToHome: LiveData<Boolean> = _goToHome
    init {
        _goToHome.value = true
    }
}
