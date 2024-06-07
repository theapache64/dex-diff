package com.github.theapache64.dexdiff.models

import com.github.difflib.DiffUtils
import com.github.difflib.patch.DeltaType
import com.github.theapache64.dexdiff.ui.home.HomeViewModel
import com.github.theapache64.dexdiff.utils.DecompileReport
import com.github.theapache64.dexdiff.utils.readAsResource
import java.io.File
import java.nio.file.Files

data class FilesResult(
    val beforeFiles: List<File>,
    val afterFiles: List<File>,

    val newFiles: List<File>,
    val removedFiles: List<File>,

    val beforeAppFiles: List<File>,
    val afterAppFiles: List<File>,
    val newAppFiles: List<File>,
    val removedAppFiles: List<File>,
    val changedAppFiles: List<ChangedFile>,

    val beforeLibraryFiles: List<File>,
    val afterLibraryFiles: List<File>,
    val newLibraryFiles: List<File>,
    val removedLibraryFiles: List<File>,
    val changedLibraryFiles: List<ChangedFile>,

    val beforeFrameworkFiles: List<File>,
    val afterFrameworkFiles: List<File>,
    val newFrameworkFiles: List<File>,
    val removedFrameworkFiles: List<File>,
    val changedFrameworkFiles: List<ChangedFile>,
)


fun createFileResult(
    appPackages: List<String>,
    beforeReport: DecompileReport,
    afterReport: DecompileReport
): FilesResult {
    val afterSrcDirName = afterReport.decompiledDir.generatedDirName()

    val beforeFiles = beforeReport.decompiledDir.walk().toList()
    val afterFiles = afterReport.decompiledDir.walk().toList()

    val newFiles = mutableListOf<File>()
    val removedFiles = mutableListOf<File>()

    val newAppFiles = mutableListOf<File>()
    val removedAppFiles = mutableListOf<File>()
    val beforeAppFiles = mutableListOf<File>()
    val afterAppFiles = mutableListOf<File>()
    val changedAppFiles = mutableListOf<ChangedFile>()

    val newLibraryFiles = mutableListOf<File>()
    val removedLibraryFiles = mutableListOf<File>()
    val beforeLibraryFiles = mutableListOf<File>()
    val afterLibraryFiles = mutableListOf<File>()
    val changedLibraryFiles = mutableListOf<ChangedFile>()

    val newFrameworkFiles = mutableListOf<File>()
    val removedFrameworkFiles = mutableListOf<File>()
    val beforeFrameworkFiles = mutableListOf<File>()
    val afterFrameworkFiles = mutableListOf<File>()
    val changedFrameworkFiles = mutableListOf<ChangedFile>()


    // before files loop
    fileLooper(
        appPackages = appPackages,
        afterSrcDirName = afterSrcDirName,
        sourceList = beforeFiles,
        targetList = afterFiles,

        newOrRemovedFiles = removedFiles,

        newAppFiles = newAppFiles,
        beforeOrAfterAppFiles = beforeAppFiles,
        changedAppFiles = changedAppFiles,
        removedAppFiles = null,

        newLibraryFiles = newLibraryFiles,
        beforeOrAfterLibraryFiles = beforeLibraryFiles,
        changedLibraryFiles = changedLibraryFiles,
        removedLibraryFiles = null,

        newFrameworkFiles = newFrameworkFiles,
        beforeOrAfterFrameworkFiles = beforeFrameworkFiles,
        changedFrameworkFiles = changedFrameworkFiles,
        removedFrameworkFiles =  null,
    )

    // after files loop
    fileLooper(
        appPackages = appPackages,
        afterSrcDirName = afterSrcDirName,
        sourceList = afterFiles,
        targetList = beforeFiles,
        newOrRemovedFiles = newFiles,

        newAppFiles = null,
        removedAppFiles = removedAppFiles,
        beforeOrAfterAppFiles = afterAppFiles,
        changedAppFiles = null,

        newLibraryFiles = null,
        removedLibraryFiles = removedLibraryFiles,
        beforeOrAfterLibraryFiles = afterLibraryFiles,
        changedLibraryFiles = null,

        newFrameworkFiles = null,
        removedFrameworkFiles = removedFrameworkFiles,
        beforeOrAfterFrameworkFiles = afterFrameworkFiles,
        changedFrameworkFiles = null,
    )

    return FilesResult(
        beforeFiles = beforeFiles,
        afterFiles = afterFiles,
        newFiles = newFiles,
        removedFiles = removedFiles,
        beforeAppFiles = beforeAppFiles,
        afterAppFiles = afterAppFiles,
        changedAppFiles = changedAppFiles,
        newAppFiles = newAppFiles,
        removedAppFiles = removedAppFiles,
        beforeLibraryFiles = beforeLibraryFiles,
        afterLibraryFiles = afterLibraryFiles,
        newLibraryFiles = newLibraryFiles,
        removedLibraryFiles = removedLibraryFiles,
        changedLibraryFiles = changedLibraryFiles,
        beforeFrameworkFiles = beforeFrameworkFiles,
        afterFrameworkFiles = afterFrameworkFiles,
        newFrameworkFiles = newFrameworkFiles,
        removedFrameworkFiles = removedFrameworkFiles,
        changedFrameworkFiles = changedFrameworkFiles,

        )
}

private fun File.generatedDirName(): String {
    return this.absolutePath.split("dex-diff-result/")[1].split("/")[0]
}

private fun fileLooper(
    appPackages: List<String>,
    afterSrcDirName: String,
    sourceList: List<File>,
    targetList: List<File>,
    newOrRemovedFiles: MutableList<File>,

    newAppFiles: MutableList<File>?,
    removedAppFiles: MutableList<File>?,
    beforeOrAfterAppFiles: MutableList<File>,
    changedAppFiles: MutableList<ChangedFile>? = null,

    newLibraryFiles: MutableList<File>?,
    removedLibraryFiles: MutableList<File>?,
    beforeOrAfterLibraryFiles: MutableList<File>,
    changedLibraryFiles: MutableList<ChangedFile>? = null,

    newFrameworkFiles: MutableList<File>?,
    removedFrameworkFiles: MutableList<File>?,
    beforeOrAfterFrameworkFiles: MutableList<File>,
    changedFrameworkFiles: MutableList<ChangedFile>? = null

) {
    sourceList.filter { it.isFile }.forEach { sourceFile ->

        if (!targetList.any { afterFile ->
                val beforeRelPath = sourceFile.relativeAndroidPath()
                val afterRelPath = afterFile.relativeAndroidPath()
                beforeRelPath == afterRelPath
            }
        ) {
            newOrRemovedFiles.add(sourceFile)

            if (sourceFile.isAppFile(appPackages) && newAppFiles != null) {
                newAppFiles.add(sourceFile)
            }

            if (sourceFile.isLibraryFile(appPackages) && newLibraryFiles != null) {
                newLibraryFiles.add(sourceFile)
            }

            if (sourceFile.isFrameworkFile() && newFrameworkFiles != null) {
                newFrameworkFiles.add(sourceFile)
            }


            if (sourceFile.isAppFile(appPackages) && removedAppFiles != null) {
                removedAppFiles.add(sourceFile)
            }

            if (sourceFile.isLibraryFile(appPackages) && removedLibraryFiles != null) {
                removedLibraryFiles.add(sourceFile)
            }

            if (sourceFile.isFrameworkFile() && removedFrameworkFiles != null) {
                removedFrameworkFiles.add(sourceFile)
            }


        }


        if (sourceFile.isAppFile(appPackages)) {
            beforeOrAfterAppFiles.add(sourceFile)
        }

        if (sourceFile.isLibraryFile(appPackages)) {
            beforeOrAfterLibraryFiles.add(sourceFile)
        }

        if (sourceFile.isFrameworkFile()) {
            beforeOrAfterFrameworkFiles.add(sourceFile)
        }

        if (
            (sourceFile.isAppFile(appPackages) && changedAppFiles != null) ||
            (sourceFile.isLibraryFile(appPackages) && changedLibraryFiles != null) ||
            (sourceFile.isFrameworkFile() && changedFrameworkFiles != null)
        ) {
            val afterFile = File("dex-diff-result/$afterSrcDirName/sources/${sourceFile.relativeAndroidPath()}")
            if (afterFile.exists()) {
                if (sourceFile.readText() != afterFile.readText()) {
                    // file content changed
                    val packageNamePlusClassName = afterFile.absolutePath.split(afterSrcDirName)[1].replace("/", "_")
                    val diffHtml = File("dex-diff-result/$packageNamePlusClassName-diff.html").apply {
                        writeText("file_diff_template.html".readAsResource())
                    }

                    val diff = diffHtml.readText()
                        .replace("{{after}}", sourceFile.readText())
                        .replace("{{before}}", afterFile.readText())
                        .replace("{{fileName}}", afterFile.name)

                    diffHtml.writeText(diff)
                    val (linesAdded, linesRemoved) = countLinesAddedAndRemoved(sourceFile, afterFile)
                    val changedFiles = when {
                        sourceFile.isAppFile(appPackages) -> changedAppFiles
                        sourceFile.isLibraryFile(appPackages) -> changedLibraryFiles
                        sourceFile.isFrameworkFile() -> changedFrameworkFiles
                        else -> null
                    } ?: throw IllegalArgumentException("Unknown file type")

                    changedFiles.add(
                        ChangedFile(
                            beforeFile = sourceFile,
                            afterFile = afterFile,
                            diffHtml = diffHtml,
                            linesAdded = linesAdded,
                            linedRemoved = linesRemoved
                        )
                    )
                }
            }
        }
    }
}


private fun File.isLibraryFile(appPackages: List<String>): Boolean {
    return !isAppFile(appPackages) && HomeViewModel.FRAMEWORK_PACKAGES.none { relativeAndroidPath().startsWith(it) }
}

private fun File.isAppFile(appPackages: List<String>): Boolean {
    return appPackages.any { relativeAndroidPath().startsWith(it) }
}


private fun File.isFrameworkFile(): Boolean {
    return HomeViewModel.FRAMEWORK_PACKAGES.any { relativeAndroidPath().startsWith(it) }
}


private fun File.relativeAndroidPath(): String {
    return this.absolutePath.split("-decompiled/sources/").last()
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
