package com.github.theapache64.dexdiff.ui.home

import com.github.theapache64.dexdiff.data.repo.AppRepo
import com.github.theapache64.dexdiff.models.createFileResult
import com.github.theapache64.dexdiff.utils.ApkDecompiler
import com.github.theapache64.dexdiff.utils.ReportMaker
import com.github.theapache64.dexdiff.utils.roundToTwoDecimals
import com.theapache64.cyclone.core.livedata.LiveData
import com.theapache64.cyclone.core.livedata.MutableLiveData
import javax.inject.Inject


class HomeViewModel @Inject constructor(
    private val appRepo: AppRepo,
) {

    companion object {
        const val INIT_MSG = "➡️ initialising..."
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

        _status.value = "➡️ Deleting old results..."

        // File("dex-diff-result").deleteRecursively()
        _status.value = "➡️ Decompiling before APK... (this may take some time)"
        var startTime = System.currentTimeMillis()
        val beforeReport = ApkDecompiler(appArgs.beforeApk).cachedBefore()
        _status.value = "➡️ Decompiling after APK... (this may take some time)"
        val afterReport = ApkDecompiler(appArgs.afterApk).cachedAfter()
        _status.value = "⏱\uFE0F ➡️ Decompiled finished. Took ${System.currentTimeMillis() - startTime}ms "

        // Find newly added files
        _status.value = "➡️ Finding newly added files: "
        startTime = System.currentTimeMillis()
        val beforeFiles = beforeReport.decompiledDir.walk().toList().filter { it.isFile }
        val afterFiles = afterReport.decompiledDir.walk().toList().filter { it.isFile }
        _status.value = "➡️ files are ready to compare"
        _status.value = "➡️ beforeFiles count : ${beforeFiles.size}"
        _status.value = "➡️ afterFiles count : ${afterFiles.size}"
        _status.value = "➡️ comparing..."

        val filesResult = createFileResult(
            appPackages = appArgs.appPackages,
            beforeReport = beforeReport,
            afterReport = afterReport
        )

        _status.value = "➡️ new files count: ${filesResult.newFiles.size}"
        _status.value = "➡️ removed files count: ${filesResult.removedFiles.size}"

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

        _status.value = "⏱\uFE0F ➡️ Analysis took ${System.currentTimeMillis() - startTime}ms "

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
            changedLibraryFiles = filesResult.changedLibraryFiles

        ).make()

        println("QuickTag: HomeViewModel:init: Verifying data...")

        _status.value =
            "✅ Report ready (${((System.currentTimeMillis() - analysisStarTime) / 1000f).roundToTwoDecimals()}s) -> file://${reportFile.absolutePath} "
    }

}


