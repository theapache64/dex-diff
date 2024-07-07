package com.github.theapache64.dexdiff.ui.home

import com.github.theapache64.dexdiff.data.local.DexMeta
import com.github.theapache64.dexdiff.data.repo.AppRepo
import com.github.theapache64.dexdiff.models.createFileResult
import com.github.theapache64.dexdiff.utils.ApkDecompiler
import com.github.theapache64.dexdiff.utils.ReportMaker
import com.github.theapache64.dexdiff.utils.roundToTwoDecimals
import com.theapache64.cyclone.core.livedata.LiveData
import com.theapache64.cyclone.core.livedata.MutableLiveData
import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import javax.inject.Inject


class HomeViewModel @Inject constructor(
    private val appRepo: AppRepo,
) {

    companion object {
        const val INIT_MSG = "🚀 Initialising..."
        const val DONE_MSG = "✅ Done"

        val FRAMEWORK_PACKAGES = listOf(
            "androidx/",
            "android/",
            "kotlinx/",
            "kotlin/",
            "java/",
        )
    }

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status


    fun init() {

        val analysisStarTime = System.currentTimeMillis()
        _status.value = INIT_MSG
        val appArgs = appRepo.args
        require(appArgs != null) {
            "Arguments not found"
        }

        val file = File("dex-diff-result")

        val beforeMd5 = appArgs.beforeApk.calculateMd5()
        val afterMd5 = appArgs.afterApk.calculateMd5()

        if(beforeMd5 == afterMd5){
            _status.value = "Before APK MD5: $beforeMd5"
            _status.value = "After APK MD5: $afterMd5"
            _status.value = "❌ Before and after APKs are same"
            return
        }

        val isDebug = false
        if (file.exists()) {
            if (!isDebug) {
                _status.value = "➡️ Deleting old results (${file.name})..."
                file.deleteRecursively()
                _status.value = "✅ Deleted old results"
            }
        }
        _status.value = "➡️ Decompiling before APK... (this may take some time)"
        var startTime = System.currentTimeMillis()
        val beforeReport = if (isDebug) {
            ApkDecompiler(appArgs.beforeApk).cachedBefore()
        } else {
            ApkDecompiler(appArgs.beforeApk).decompile()
        }
        _status.value = "✅ Decompiling before APK finished"
        _status.value = "➡️ Decompiling after APK... (this may take some time)"
        val afterReport = if (isDebug) {
            ApkDecompiler(appArgs.afterApk).cachedAfter()
        } else {
            ApkDecompiler(appArgs.afterApk).decompile()
        }
        _status.value = "✅ Decompiling after APK finished"
        _status.value = "✅ Decompile finished (${System.currentTimeMillis() - startTime}ms)"


        startTime = System.currentTimeMillis()
        _status.value = "➡️ Comparing before and after... (this may take some time)"
        val beforeFiles = beforeReport.sourceDir.walk().toList().filter { it.isFile }
        val afterFiles = afterReport.sourceDir.walk().toList().filter { it.isFile }
        val filesResult = createFileResult(
            appPackages = appArgs.appPackages,
            beforeReport = beforeReport,
            afterReport = afterReport
        )

        // app files
        val beforeAppFiles = filesResult.beforeAppFiles
        val afterAppFiles = filesResult.afterAppFiles
        val changedAppFiles = filesResult.changedAppFiles

        // library files
        val beforeLibraryFiles = filesResult.beforeLibraryFiles
        val afterLibraryFiles = filesResult.afterLibraryFiles
        val beforeTotalLibraryFiles = beforeLibraryFiles.size
        val afterTotalLibraryFiles = afterLibraryFiles.size

        // framework files
        val beforeFrameworkFiles = filesResult.beforeFrameworkFiles
        val afterFrameworkFiles = filesResult.afterFrameworkFiles
        val beforeTotalFrameworkFiles = beforeFrameworkFiles.size
        val afterTotalFrameworkFiles = afterFrameworkFiles.size

        _status.value = "✅ Comparing finished (${System.currentTimeMillis() - startTime}ms)"

        _status.value = "➡️ Making report..."
        val reportFile = ReportMaker(
            apkFileDetails = """
                Before: <code>${appArgs.beforeApk.name}</code> </br> 
                After: <code>${appArgs.afterApk.name}</code> </br> 
                App path: <code> ${appArgs.appPackages.joinToString(separator = ",")}</code>
            """.trimIndent(),
            appPackages = appArgs.appPackages,
            beforeApkSizeInKb = (appArgs.beforeApk.length() / 1024).toInt(),
            afterApkSizeInKb = (appArgs.afterApk.length() / 1024).toInt(),

            beforeFilesCount = beforeFiles.size,
            afterFilesCount = afterFiles.size,

            beforeAppFilesCount = beforeAppFiles.size,
            afterAppFilesCount = afterAppFiles.size,

            beforeFrameworkFilesCount = beforeTotalFrameworkFiles,
            afterFrameworkFilesCount = afterTotalFrameworkFiles,

            beforeLibraryFilesCount = beforeTotalLibraryFiles,
            afterLibraryFilesCount = afterTotalLibraryFiles,


            beforeTotalClasses = beforeReport.totalClasses,
            afterTotalClasses = afterReport.totalClasses,

            beforeTotalMethods = beforeReport.totalMethods,
            afterTotalMethods = afterReport.totalMethods,

            newAppFiles = filesResult.newAppFiles,
            removedAppFiles = filesResult.removedAppFiles,
            changedAppFiles = changedAppFiles,

            newFrameworkFiles = filesResult.newFrameworkFiles,
            removedFrameworkFiles = filesResult.removedFrameworkFiles,
            changedFrameworkFiles = filesResult.changedFrameworkFiles,

            newLibraryFiles = filesResult.newLibraryFiles,
            removedLibraryFiles = filesResult.removedLibraryFiles,
            changedLibraryFiles = filesResult.changedLibraryFiles,

            beforeDexMeta = filesResult.beforeDexMeta,
            afterDexMeta = filesResult.afterDexMeta,
        ).make()

        _status.value = "✅ Report ready (${((System.currentTimeMillis() - analysisStarTime) / 1000f).roundToTwoDecimals()}s) -> file://${reportFile.absolutePath} "
    }

}

private fun File.calculateMd5(): String {
    return DigestUtils.md5Hex(this.inputStream()).toString()
}

private fun <E> parseDexMeta(): DexMeta {
    TODO("Not yet implemented")
}


