package com.github.theapache64.dexdiff.utils

import jadx.api.JadxArgs
import jadx.api.JadxDecompiler
import jadx.api.JavaClass
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


data class DecompileReport(
    val decompiledDir: File,
    val totalFiles: Int,
    val totalClasses: Int,
    val totalMethods: Int
)

class ApkDecompiler(
    private val apkFile: File
) {

    fun cachedBefore(): DecompileReport {
        return DecompileReport(
            decompiledDir = File("dex-diff-result/without-fullmode-2024_06_07__12_08_15-decompiled/sources"),
            totalFiles = 1765,
            totalClasses = 2086,
            totalMethods = 6774
        )
    }

    fun cachedAfter(): DecompileReport {
        return DecompileReport(
            decompiledDir = File("dex-diff-result/with-fullmode-2024_06_07__12_08_19-decompiled/sources"),
            totalFiles = 1088,
            totalClasses = 1240,
            totalMethods = 5359
        )
    }

    fun decompile(): DecompileReport {
        val decompiledDir = File("dex-diff-result/${apkFile.nameWithoutExtension}-${currentDateTime()}-decompiled")
        val jadxArgs = JadxArgs()
        jadxArgs.setInputFile(apkFile)
        jadxArgs.outDir = decompiledDir
        val totalClasses: Int
        val totalMethods: Int

        JadxDecompiler(jadxArgs).use { jadx ->
            jadx.load()
            val (classesCount, methodsCount) = countClassesAndMethodsRecursively(jadx.classes)
            totalClasses = classesCount
            totalMethods = methodsCount
            jadx.save()
        }
        val sourceDir = decompiledDir.resolve("sources")
        return DecompileReport(
            decompiledDir = sourceDir,
            totalFiles = sourceDir.walk().toList().filter { it.isFile }.size,
            totalClasses = totalClasses,
            totalMethods = totalMethods
        )
    }

    private fun countClassesAndMethodsRecursively(classes: List<JavaClass>): Pair<Int, Int> {
        var classCount = 0
        var methodCount = 0
        for (javaClass in classes) {
            classCount++
            methodCount += javaClass.methods.size
            val (nestedClassCount, nestedMethodCount) = countClassesAndMethodsRecursively(javaClass.innerClasses)
            classCount += nestedClassCount
            methodCount += nestedMethodCount
        }
        return Pair(classCount, methodCount)
    }

    private fun currentDateTime(): String {
        val timestamp = System.currentTimeMillis()
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("yyyy_MM_dd__HH_mm_ss")
        return sdf.format(date)
    }
}