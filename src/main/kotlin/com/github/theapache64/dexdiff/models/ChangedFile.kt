package com.github.theapache64.dexdiff.models

import java.io.File

data class ChangedFile(
    val beforeFile : File,
    val afterFile : File,
    val diffHtml : File,
    val linesAdded : Int,
    val linedRemoved : Int
)
