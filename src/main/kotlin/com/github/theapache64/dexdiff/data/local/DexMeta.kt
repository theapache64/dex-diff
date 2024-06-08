package com.github.theapache64.dexdiff.data.local

data class DexMeta (
    var dexFileName : String?,
    var sizeInKb : Int?,
    var classesCount : Int=0,
)