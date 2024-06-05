package com.github.theapache64.dexdiff.utils

import jadx.api.JadxArgs
import jadx.api.JadxDecompiler
import java.io.File


data class DexFile(
    val file: List<File>
)

class ApkDecompiler(
    private val apkFile: File
) {
    fun decompile() {
        val jadxArgs = JadxArgs()
        jadxArgs.setInputFile(apkFile)
        jadxArgs.outDir = File("temp/output")
        try {
            JadxDecompiler(jadxArgs).use { jadx ->
                jadx.load()
                jadx.save()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}