package com.github.theapache64.dexdiff.ui.home

import com.github.difflib.DiffUtils
import com.github.difflib.patch.DeltaType
import com.github.theapache64.dexdiff.data.repo.AppRepo
import com.github.theapache64.dexdiff.models.ChangedFile
import com.github.theapache64.dexdiff.utils.ApkDecompiler
import com.github.theapache64.dexdiff.utils.ReportMaker
import com.github.theapache64.dexdiff.utils.roundToTwoDecimals
import com.theapache64.cyclone.core.livedata.LiveData
import com.theapache64.cyclone.core.livedata.MutableLiveData
import java.io.File
import java.nio.file.Files
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


    fun init(){
        val analysisStarTime = System.currentTimeMillis()
        _status.value = INIT_MSG
        val appArgs = appRepo.args
        require(appArgs != null) {
            "Arguments not found"
        }

        _status.value = "➡️ Deleting temp directory..."

        File("temp").deleteRecursively()
        _status.value = "➡️ Decompiling before APK... (this may take some time)"
        var startTime = System.currentTimeMillis()
        val beforeReport = ApkDecompiler(appArgs.beforeApk).decompile()
        _status.value = "➡️ Decompiling after APK... (this may take some time)"
        val afterReport = ApkDecompiler(appArgs.afterApk).decompile()
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
        val newFiles = findNewOrRemovedFiles(beforeFiles, afterFiles)
        val removedFiles = findNewOrRemovedFiles(afterFiles, beforeFiles)
        _status.value = "➡️ new files count: ${newFiles.size}"
        _status.value = "➡️ deleted files count: ${removedFiles.size}"
        _status.value = "➡️ done"

        val frameworkFiles = findFrameworkFiles(beforeFiles)
        val beforeTotalFrameworkFiles = frameworkFiles.size
        val afterTotalFrameworkFiles = findFrameworkFiles(afterFiles).size
        val appFiles = findAppFiles(beforeFiles)
        val beforeTotalAppFiles = appFiles.size
        val afterTotalAppFiles = findAppFiles(afterFiles).size

        val changedFrameworkFiles =
            findContentChangedFiles(frameworkFiles, afterReport.decompiledDir.generatedDirName())
        val changedAppFiles = findContentChangedFiles(appFiles, afterReport.decompiledDir.generatedDirName())
        _status.value = "⏱\uFE0F ➡️ Analysis took ${System.currentTimeMillis() - startTime}ms "

        val reportFile = ReportMaker(
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

        _status.value = "✅ Report ready (${((System.currentTimeMillis() - analysisStarTime) / 1000f).roundToTwoDecimals()}s) -> file://${reportFile.absolutePath} "
    }

    private fun countLinesAddedAndRemoved(beforeFile: File, afterFile: File): Pair<Int, Int> {
        val diff = DiffUtils.diff(
            Files.readAllLines(beforeFile.toPath()),
            Files.readAllLines(afterFile.toPath())
        )

        var linesAdded = 0
        var linesRemoved = 0

        for (delta in diff.deltas) {
            when (delta.type) {
                DeltaType.CHANGE -> {
                    linesAdded += delta.source.lines.size
                    linesRemoved += delta.target.lines.size
                }
                DeltaType.INSERT -> linesAdded += delta.target.lines.size
                DeltaType.DELETE -> linesRemoved += delta.source.lines.size
                null, DeltaType.EQUAL -> {
                    // do nothing
                }
            }
        }

        return Pair(linesAdded, linesRemoved)
    }

    private fun findContentChangedFiles(beforeFiles: List<File>, afterSrcDirName: String): List<ChangedFile> {
        val changedFiles = mutableListOf<ChangedFile>()
        beforeFiles.forEach { beforeFile ->
            val afterFile = File("temp/$afterSrcDirName/sources/${beforeFile.relativeAndroidPath()}")
            if (afterFile.exists()) {
                if (beforeFile.readText() != afterFile.readText()) {
                    // file content changed
                    val packageNamePlusClassName = afterFile.absolutePath.split(afterSrcDirName)[1].replace("/", "_")
                    val diffHtml = File("temp/$packageNamePlusClassName-diff.html")
                    File("src/main/resources/file_diff_template.html").copyTo(
                        diffHtml,
                        overwrite = true
                    )

                    val diff = diffHtml.readText()
                        .replace("{{after}}", beforeFile.readText())
                        .replace("{{before}}", afterFile.readText())
                        .replace("{{fileName}}", afterFile.name)

                    diffHtml.writeText(diff)
                    val (linesAdded, linesRemoved) = countLinesAddedAndRemoved(beforeFile, afterFile)
                    changedFiles.add(
                        ChangedFile(
                            beforeFile = beforeFile,
                            afterFile = afterFile,
                            diffHtml = diffHtml,
                            linesAdded = linesAdded,
                            linedRemoved = linesRemoved
                        )
                    )
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
