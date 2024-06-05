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
        val newFiles = findNewOrDeletedFiles(beforeFiles, afterFiles)
        val deletedFiles = findNewOrDeletedFiles(afterFiles, beforeFiles)
        println("QuickTag: HomeViewModel:: new files count: ${newFiles.size}")
        println("QuickTag: HomeViewModel:: deleted files count: ${deletedFiles.size}")
        println("QuickTag: HomeViewModel:: done")

        ReportMaker(
            beforeApkSizeInKb = (appArgs.beforeApk.length() / 1024).toInt(),
            afterApkSizeInKb = (appArgs.afterApk.length() / 1024).toInt(),

            beforeTotalFiles = beforeFiles.size,
            afterTotalFiles = afterFiles.size,

            beforeTotalClasses = beforeReport.totalClasses,
            afterTotalClasses = afterReport.totalClasses,

            beforeTotalMethods = beforeReport.totalMethods,
            afterTotalMethods = afterReport.totalMethods,

            newFiles = newFiles,
            deletedFiles = deletedFiles
        ).make()
    }


    private fun findNewOrDeletedFiles(beforeFiles: List<File>, afterFiles: List<File>): List<File> {
        val newFiles = mutableListOf<File>()
        afterFiles.forEach { afterFile ->
            if (!beforeFiles.any { beforeFile ->
                    val beforeAbsPath = beforeFile.absolutePath
                    val beforeRelPath = beforeAbsPath.split("-decompiled/sources/").last()

                    val afterAbsPath = afterFile.absolutePath
                    val afterRelPath = afterAbsPath.split("-decompiled/sources/").last()
                    beforeRelPath == afterRelPath
                }) {
                newFiles.add(afterFile)
            }
        }
        return newFiles
    }


}