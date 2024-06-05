package com.github.theapache64.dexdiff.ui.home

import com.github.theapache64.dexdiff.data.repo.AppRepo
import com.github.theapache64.dexdiff.utils.ApkDecompiler
import com.github.theapache64.dexdiff.utils.ReportMaker
import com.theapache64.cyclone.core.livedata.LiveData
import com.theapache64.cyclone.core.livedata.MutableLiveData
import java.io.File
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


    init {
        _status.value = INIT_MSG
        val appArgs = appRepo.args
        require(appArgs != null) {
            "Arguments not found"
        }

        println("QuickTag: HomeViewModel:Deleting temp: ")

        File("temp").deleteRecursively()
        println("QuickTag: HomeViewModel:: Decompiling...")
        val beforeReport = ApkDecompiler(appArgs.beforeApk).decompile()
        val afterReport = ApkDecompiler(appArgs.afterApk).decompile()
        println("QuickTag: HomeViewModel:: Decompiled finished")

        // Find newly added files
        println("QuickTag: HomeViewModel:Finding newly added files: ")
        val beforeFiles = beforeReport.decompiledDir.walk().toList().filter { it.isFile }
        val afterFiles = afterReport.decompiledDir.walk().toList().filter { it.isFile }
        println("QuickTag: HomeViewModel:: files are ready to compare")
        println("QuickTag: HomeViewModel:: beforeFiles count : ${beforeFiles.size}")
        println("QuickTag: HomeViewModel:: afterFiles count : ${afterFiles.size}")
        println("QuickTag: HomeViewModel:: comparing")
        val newFiles = findNewOrRemovedFiles(beforeFiles, afterFiles)
        val removedFiles = findNewOrRemovedFiles(afterFiles, beforeFiles)
        println("QuickTag: HomeViewModel:: new files count: ${newFiles.size}")
        println("QuickTag: HomeViewModel:: deleted files count: ${removedFiles.size}")
        println("QuickTag: HomeViewModel:: done")

        val frameworkFiles = findFrameworkFiles(beforeFiles)
        val beforeTotalFrameworkFiles = frameworkFiles.size
        val afterTotalFrameworkFiles = findFrameworkFiles(afterFiles).size
        val appFiles = findAppFiles(beforeFiles)
        val beforeTotalAppFiles = appFiles.size
        val afterTotalAppFiles = findAppFiles(afterFiles).size

        val changedFrameworkFiles = findContentChangedFiles(frameworkFiles, afterReport.decompiledDir.generatedDirName())
        val changedAppFiles = findContentChangedFiles(appFiles, afterReport.decompiledDir.generatedDirName())

        ReportMaker(
            beforeApkSizeInKb = (appArgs.beforeApk.length() / 1024).toInt(),
            afterApkSizeInKb = (appArgs.afterApk.length() / 1024).toInt(),

            beforeTotalFiles = beforeFiles.size,
            afterTotalFiles = afterFiles.size,

            beforeTotalFrameworkFiles = beforeTotalFrameworkFiles,
            afterTotalFrameworkFiles = afterTotalFrameworkFiles,

            beforeTotalAppFiles = beforeTotalAppFiles,
            afterTotalAppFiles = afterTotalAppFiles,


            beforeTotalClasses = beforeReport.totalClasses,
            afterTotalClasses = afterReport.totalClasses,

            beforeTotalMethods = beforeReport.totalMethods,
            afterTotalMethods = afterReport.totalMethods,

            newFrameworkFiles = findFrameworkFiles(newFiles),
            removedFrameworkFiles = findFrameworkFiles(removedFiles),

            newAppFiles = findAppFiles(newFiles),
            removedAppFiles = findAppFiles(removedFiles),

            changedFrameworkFiles = changedFrameworkFiles,
            changedAppFiles = changedAppFiles
        ).make()
    }

    private fun findContentChangedFiles(beforeFiles: List<File>, afterSrcDirName : String): List<Pair<File, File>> {
        val changedFiles = mutableListOf<Pair<File, File>>()
        beforeFiles.forEach { beforeFile ->
            val afterFile =  File("temp/$afterSrcDirName/sources/${beforeFile.relativeAndroidPath()}")
            if(afterFile.exists()){
                if (beforeFile.readText() != afterFile.readText()) {
                    changedFiles.add(beforeFile to afterFile)
                }
            }
        }
        return changedFiles
    }


    private fun findNewOrRemovedFiles(beforeFiles: List<File>, afterFiles: List<File>): List<File> {
        val newFiles = mutableListOf<File>()
        afterFiles.forEach { afterFile ->
            if (!beforeFiles.any { beforeFile ->
                    val beforeRelPath = beforeFile.relativeAndroidPath()
                    val afterRelPath = afterFile.relativeAndroidPath()
                    beforeRelPath == afterRelPath
                }) {
                newFiles.add(afterFile)
            }
        }
        return newFiles
    }

    private fun findFrameworkFiles(files: List<File>): List<File> {
        return files.filter { file ->
            FRAMEWORK_PACKAGES.any { file.relativeAndroidPath().startsWith(it) }
        }
    }

    private fun findAppFiles(files: List<File>): List<File> {
        return files.filter { file ->
            FRAMEWORK_PACKAGES.none { file.relativeAndroidPath().startsWith(it) }
        }
    }

    private fun File.relativeAndroidPath(): String {
        return this.absolutePath.split("-decompiled/sources/").last()
    }

}

private fun File.generatedDirName(): String {
    return this.absolutePath.split("temp/")[1].split("/")[0]
}
