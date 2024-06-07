package com.github.theapache64.dexdiff.data.local

import java.io.File

data class AppArgs(
    val beforeApk: File,
    val afterApk: File,
    val appPackages : List<String>
)
