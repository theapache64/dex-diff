package com.github.theapache64.dexdiff.utils

import com.github.theapache64.dexdiff.data.local.DexMeta
import com.github.theapache64.dexdiff.models.ChangedFile
import com.github.theapache64.dexdiff.ui.home.HomeViewModel
import java.io.File


fun File.parsePackageName(): String {
    return this.absolutePath.split("-decompiled/sources/")[1].let { s1 -> s1.substring(0, s1.lastIndexOf('/')) }
        .replace("/", ".")
}

class ReportMaker(
    val apkFileDetails: String,
    val appPackages: List<String>,
    private val beforeApkSizeInKb: Int,
    private val afterApkSizeInKb: Int,

    private val beforeFilesCount: Int,
    private val afterFilesCount: Int,

    private val beforeAppFilesCount: Int,
    private val afterAppFilesCount: Int,

    private val beforeFrameworkFilesCount: Int,
    private val afterFrameworkFilesCount: Int,

    private val beforeLibraryFilesCount: Int,
    private val afterLibraryFilesCount: Int,


    private val beforeTotalClasses: Int,
    private val afterTotalClasses: Int,

    private val beforeTotalMethods: Int,
    private val afterTotalMethods: Int,

    private val newFrameworkFiles: List<File>,
    private val removedFrameworkFiles: List<File>,
    private val changedFrameworkFiles: List<ChangedFile>,

    private val newAppFiles: List<File>,
    private val removedAppFiles: List<File>,
    private val changedAppFiles: List<ChangedFile>,

    private val newLibraryFiles: List<File>,
    private val removedLibraryFiles: List<File>,
    private val changedLibraryFiles: List<ChangedFile>,
    private val beforeDexMeta: Map<String, DexMeta>,
    private val afterDexMeta: Map<String, DexMeta>
) {


    fun make(): File {
        val reportFile = File("dex-diff-result/report.html").apply {
            writeText("report_template.html".readAsResource())
        }

        val frameworkNote =
            "These are files inside '${HomeViewModel.FRAMEWORK_PACKAGES.joinToString(", ")}' directory"
        val frameworkChangedNote =
            "These are files inside '${HomeViewModel.FRAMEWORK_PACKAGES.joinToString(", ")}' directory with content changes"

        val appNote = "These are files outside '${HomeViewModel.FRAMEWORK_PACKAGES.joinToString(", ")}' and '${
            appPackages.joinToString(",")
        }' directory"
        val appChangedNote =
            "These are files outside '${HomeViewModel.FRAMEWORK_PACKAGES.joinToString(", ")}' and '${
                appPackages.joinToString(",")
            }' directory with content changes"

        val fullReport = reportFile.readText()
            .addReportSummary()
            .addDexFilesTable()
            .add(
                files = newLibraryFiles,
                note = appNote,
                key = "newLibraryFilesTable"
            )
            .add(
                files = removedLibraryFiles,
                note = appNote,
                key = "removedLibraryFilesTable"
            )
            .addChangedFiles(
                files = changedLibraryFiles,
                note = appChangedNote,
                replaceKey = "changedLibraryFilesTable"
            )
            .add(
                files = newFrameworkFiles,
                note = frameworkNote,
                key = "newFrameworkFilesTable"
            )
            .add(
                files = removedFrameworkFiles,
                note = frameworkNote,
                key = "removedFrameworkFilesTable"
            )
            .addChangedFiles(
                files = changedFrameworkFiles,
                note = frameworkChangedNote,
                replaceKey = "changedFrameworkFilesTable"
            )
            .add(
                files = newAppFiles,
                note = "These are files inside '${appPackages.joinToString(",")}'",
                key = "newAppFilesTable"
            )
            .add(
                files = removedAppFiles,
                note = "These are files inside '${appPackages.joinToString(",")}'",
                key = "removedAppFilesTable"
            )
            .addChangedFiles(
                files = changedAppFiles,
                note = "These are files inside '${appPackages.joinToString(",")}' with content changes",
                replaceKey = "changedAppFilesTable"
            )


        reportFile.writeText(fullReport)

        return reportFile
    }

    private fun String.addReportSummary(): String {
        return this
            .replace("{{apkFileDetails}}", apkFileDetails)
            .replace("{{beforeApkSize}}", "$beforeApkSizeInKb KB")
            .replace("{{afterApkSize}}", "$afterApkSizeInKb KB")
            .replace("{{diffApkSize}}", "${(afterApkSizeInKb - beforeApkSizeInKb).withSymbol()} KB")

            .replace("{{beforeTotalFiles}}", "$beforeFilesCount (100%)")
            .replace("{{afterTotalFiles}}", "$afterFilesCount (100%)")
            .replace("{{diffTotalFiles}}", "${(afterFilesCount - beforeFilesCount).withSymbol()} files (100%)")

            .replace(
                "{{beforeAppFiles}}",
                "$beforeAppFilesCount (${((beforeAppFilesCount / beforeFilesCount.toFloat()) * 100).roundToTwoDecimals()}%)"
            )
            .replace(
                "{{afterAppFiles}}",
                "$afterAppFilesCount (${((afterAppFilesCount / afterFilesCount.toFloat()) * 100).roundToTwoDecimals()}%)"
            )
            .replace(
                "{{diffAppFiles}}",
                "${(afterAppFilesCount - beforeAppFilesCount).withSymbol()} files (${(((afterAppFilesCount - beforeAppFilesCount) / (afterFilesCount - beforeFilesCount).toFloat()) * 100).roundToTwoDecimals()}%)"
            )

            .replace(
                "{{beforeTotalLibraryFiles}}",
                "$beforeLibraryFilesCount (${((beforeLibraryFilesCount / beforeFilesCount.toFloat()) * 100).roundToTwoDecimals()}%)"
            )
            .replace(
                "{{afterTotalLibraryFiles}}",
                "$afterLibraryFilesCount (${((afterLibraryFilesCount / afterFilesCount.toFloat()) * 100).roundToTwoDecimals()}%)"
            )
            .replace(
                "{{diffTotalLibraryFiles}}",
                "${(afterLibraryFilesCount - beforeLibraryFilesCount).withSymbol()} files (${(((afterLibraryFilesCount - beforeLibraryFilesCount) / (afterFilesCount - beforeFilesCount).toFloat()) * 100).roundToTwoDecimals()}%)"
            )


            .replace(
                "{{beforeTotalFrameworkFiles}}",
                "$beforeFrameworkFilesCount (${((beforeFrameworkFilesCount / beforeFilesCount.toFloat()) * 100).roundToTwoDecimals()}%)"
            )
            .replace(
                "{{afterTotalFrameworkFiles}}",
                "$afterFrameworkFilesCount (${((afterFrameworkFilesCount / afterFilesCount.toFloat()) * 100).roundToTwoDecimals()}%)"
            )
            .replace(
                "{{diffTotalFrameworkFiles}}",
                "${(afterFrameworkFilesCount - beforeFrameworkFilesCount).withSymbol()} files (${(((afterFrameworkFilesCount - beforeFrameworkFilesCount) / (afterFilesCount - beforeFilesCount).toFloat()) * 100).roundToTwoDecimals()}%)"
            )


            .replace("{{beforeTotalClasses}}", "$beforeTotalClasses")
            .replace("{{afterTotalClasses}}", "$afterTotalClasses")
            .replace("{{diffTotalClasses}}", "${(afterTotalClasses - beforeTotalClasses).withSymbol()} classes")

            .replace("{{beforeTotalMethods}}", "$beforeTotalMethods")
            .replace("{{afterTotalMethods}}", "$afterTotalMethods")
            .replace("{{diffTotalMethods}}", "${(afterTotalMethods - beforeTotalMethods).withSymbol()} methods")

            .replace(
                "{{changedFilesCount}}",
                "${changedFrameworkFiles.size + changedLibraryFiles.size + changedAppFiles.size} files"
            )
            .replace("{{changedLibraryFilesCount}}", "${changedLibraryFiles.size} files")
            .replace("{{changedFrameworkFilesCount}}", "${changedFrameworkFiles.size} files")
            .replace("{{changedAppFilesCount}}", "${changedAppFiles.size} files")

            .replace("{{newAppFilesListCount}}", newAppFiles.size.takeIf { it > 0 }?.let { "($it files)" } ?: "")
            .replace("{{removedAppFilesListCount}}", removedAppFiles.size.takeIf { it > 0 }?.let { "($it files)" } ?: "")
            .replace("{{changedAppFilesListCount}}", changedAppFiles.size.takeIf { it > 0 }?.let { "($it files)" } ?: "")
        
            .replace("{{newLibraryFilesListCount}}", newLibraryFiles.size.takeIf { it > 0 }?.let { "($it files)" } ?: "")
            .replace("{{removedLibraryFilesListCount}}", removedLibraryFiles.size.takeIf { it > 0 }?.let { "($it files)" } ?: "")
            .replace("{{changedLibraryFilesListCount}}", changedLibraryFiles.size.takeIf { it > 0 }?.let { "($it files)" } ?: "")
        
            .replace("{{newFrameworkFilesListCount}}", newFrameworkFiles.size.takeIf { it > 0 }?.let { "($it files)" } ?: "")
            .replace("{{removedFrameworkFilesListCount}}", removedFrameworkFiles.size.takeIf { it > 0 }?.let { "($it files)" } ?: "")
            .replace("{{changedFrameworkFilesListCount}}", changedFrameworkFiles.size.takeIf { it > 0 }?.let { "($it files)" } ?: "")
    }


    private fun String.add(
        files: List<File>,
        note: String,
        key: String,
    ): String {
        val table = StringBuilder()

        val tableBody = files
            .map {
                Pair(it, it.readLines().size)
            }
            .sortedByDescending { it.second } // by line size
            .joinToString("\n") { (file, lineCount) ->
                """
                <tr>
                    <td><a target="_blank" href="file://${file.absolutePath}">${file.name}</a></td>
                     <td>${file.parsePackageName()}</td>
                    <td>${lineCount}</td>
                </tr>
            """.trimIndent()
            }

        buildTable(table = table, note = note, tableBody = tableBody, isTableEmpty = files.isEmpty())
        return this.replace("{{$key}}", table.toString())
    }


    private fun String.addDexFilesTable(): String {
        val tableBuilder = StringBuilder()
        val dexFiles = beforeDexMeta.keys + afterDexMeta.keys
        for(dexFile in dexFiles){
            val beforeMeta = beforeDexMeta[dexFile]
            val afterMeta = afterDexMeta[dexFile]
            tableBuilder.append(
                """
                <tr>
                    <td>${dexFile}</td>
                    <td>
                        
                        ${beforeMeta?.sizeInKb ?: 0} KB
                    </td>
                    <td>
                        
                        ${afterMeta?.sizeInKb ?: 0} KB
                    </td>
                    <td>
                        ${afterMeta?.sizeInKb?.minus(beforeMeta?.sizeInKb ?: 0)?.withSymbol()} KB
                    </td>
                </tr>
            """.trimIndent()
            )
        }
        val table  = """
            <div class="tableContainer">
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th style="width: 40%;">File</th>
                            <th style="width: 20%;">Before</th>
                            <th style="width: 20%;">After</th>
                            <th style="width: 20%;">Diff</th>
                        </tr>
                    </thead>
                    <tbody>
                        $tableBuilder
                    </tbody>
                </table>
            </div>
        """.trimIndent()
        return this.replace("{{dexFilesTable}}", table)
    }

    private fun String.addChangedFiles(
        files: List<ChangedFile>,
        note: String,
        replaceKey: String,
    ): String {
        val tableBuilder = StringBuilder()

        val tableBody = files
            .sortedByDescending { changedFile -> changedFile.linesAdded + changedFile.linedRemoved }
            .joinToString("\n") { changedFile ->
                """
                <tr>
                    <td><a target="_blank" href="file://${changedFile.diffHtml.absolutePath}">${changedFile.beforeFile.name}</a></td>
                    <td>${changedFile.beforeFile.parsePackageName()}</td>
                    <td><span class="label label-danger">--${changedFile.linedRemoved}</span> <span class="label label-success">++${changedFile.linesAdded}</span></td>
                </tr>
            """.trimIndent()
            }

        buildTable(table = tableBuilder, note = note, tableBody = tableBody, isTableEmpty = files.isEmpty())
        return this.replace("{{$replaceKey}}", tableBuilder.toString())
    }

    private fun buildTable(table: StringBuilder, note: String, tableBody: String, isTableEmpty: Boolean) {
        if (isTableEmpty) {

            table.append(
                """
                <div class="alert alert-info">
                  <strong>NOTE: $note</strong> 
                </div>
                <div class="alert alert-success">
                  <strong>No files found</strong> 
                </div>
            """.trimIndent()
            )
        } else {
            table.append(
                """
                <div class="alert alert-info">
                  <strong>NOTE: $note</strong> 
                </div>
                <div class="tableContainer">
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th style="width: 60%;">File</th>
                                <th style="width: 28%;">Package</th>
                                <th style="width: 12%;">Lines</th>
                            </tr>
                        </thead>
                        <tbody>
                            $tableBody
                        </tbody>
                    </table>
                </div>
            """.trimIndent()
            )

        }
    }

    private fun Int.withSymbol(): String {
        return if (this > 0) {
            "+${this}"
        } else {
            "$this"
        }
    }


}


fun String.readAsResource(): String {
    val classloader = Thread.currentThread().contextClassLoader
    return classloader.getResourceAsStream(this)?.reader()?.readText() ?: error("Resource not found : $this")
}

fun Float.roundToTwoDecimals(): String {
    return String.format("%.2f", this)
}





