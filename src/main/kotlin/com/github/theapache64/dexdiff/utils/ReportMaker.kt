package com.github.theapache64.dexdiff.utils

import com.github.theapache64.dexdiff.models.ChangedFile
import com.github.theapache64.dexdiff.ui.home.HomeViewModel
import java.io.File


fun File.parsePackageName(): String {
    return this.absolutePath.split("-decompiled/sources/")[1].let { s1 -> s1.substring(0, s1.lastIndexOf('/')) }
        .replace("/", ".")
}

class ReportMaker(
    private val beforeApkSizeInKb: Int,
    private val afterApkSizeInKb: Int,

    private val beforeTotalFiles: Int,
    private val afterTotalFiles: Int,

    private val beforeTotalFrameworkFiles: Int,
    private val afterTotalFrameworkFiles: Int,

    private val beforeTotalAppFiles: Int,
    private val afterTotalAppFiles: Int,


    private val beforeTotalClasses: Int,
    private val afterTotalClasses: Int,

    private val beforeTotalMethods: Int,
    private val afterTotalMethods: Int,

    private val newFrameworkFiles: List<File>,
    private val removedFrameworkFiles: List<File>,

    private val newAppFiles: List<File>,
    private val removedAppFiles: List<File>,

    private val changedFrameworkFiles: List<ChangedFile>,
    private val changedAppFiles: List<ChangedFile>
) {


    fun make(): File {
        println("QuickTag: ReportMaker:make: Making report...")
        val reportFile = File("dex-diff-result/report.html").apply {
            writeText("report_template.html".readAsResource())
        }

        val frameworkNote =
            "These are classes inside '${HomeViewModel.FRAMEWORK_PACKAGES.joinToString(", ")}' directory"
        val frameworkChangedNote =
            "These are classes inside '${HomeViewModel.FRAMEWORK_PACKAGES.joinToString(", ")}' directory with content changes"
        val appNote = "These are classes outside '${HomeViewModel.FRAMEWORK_PACKAGES.joinToString(", ")}' directory"
        val appChangedNote =
            "These are classes outside '${HomeViewModel.FRAMEWORK_PACKAGES.joinToString(", ")}' directory with content changes"

        val fullReport = reportFile.readText()
            .addReportSummary()
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
            .add(
                files = newAppFiles,
                note = appNote,
                key = "newAppFilesTable"
            )
            .add(
                files = removedAppFiles,
                note = appNote,
                key = "removedAppFilesTable"
            )
            .addChangedFiles(
                files = changedFrameworkFiles,
                note = frameworkChangedNote,
                replaceKey = "changedFrameworkFilesTable"
            )
            .addChangedFiles(
                files = changedAppFiles,
                note = appChangedNote,
                replaceKey = "changedAppFilesTable"
            )

        reportFile.writeText(fullReport)

        return reportFile
    }

    private fun String.addReportSummary(): String {
        return this
            .replace("{{beforeApkSize}}", "$beforeApkSizeInKb KB")
            .replace("{{afterApkSize}}", "$afterApkSizeInKb KB")
            .replace("{{diffApkSize}}", "${(afterApkSizeInKb - beforeApkSizeInKb).withSymbol()} KB")

            .replace("{{beforeTotalFiles}}", "$beforeTotalFiles (100%)")
            .replace("{{afterTotalFiles}}", "$afterTotalFiles (100%)")
            .replace("{{diffTotalFiles}}", "${(afterTotalFiles - beforeTotalFiles).withSymbol()} files")

            .replace(
                "{{beforeTotalFrameworkFiles}}",
                "$beforeTotalFrameworkFiles (${((beforeTotalFrameworkFiles / beforeTotalFiles.toFloat()) * 100).roundToTwoDecimals()}%)"
            )
            .replace(
                "{{afterTotalFrameworkFiles}}",
                "$afterTotalFrameworkFiles (${((afterTotalFrameworkFiles / afterTotalFiles.toFloat()) * 100).roundToTwoDecimals()}%)"
            )
            .replace(
                "{{diffTotalFrameworkFiles}}",
                "${(afterTotalFrameworkFiles - beforeTotalFrameworkFiles).withSymbol()} files"
            )

            .replace(
                "{{beforeTotalAppFiles}}",
                "$beforeTotalAppFiles (${((beforeTotalAppFiles / beforeTotalFiles.toFloat()) * 100).roundToTwoDecimals()}%)"
            )
            .replace(
                "{{afterTotalAppFiles}}",
                "$afterTotalAppFiles (${((afterTotalAppFiles / beforeTotalFiles.toFloat()) * 100).roundToTwoDecimals()}%)"
            )
            .replace("{{diffTotalAppFiles}}", "${(afterTotalAppFiles - beforeTotalAppFiles).withSymbol()} files")

            .replace("{{beforeTotalClasses}}", "$beforeTotalClasses")
            .replace("{{afterTotalClasses}}", "$afterTotalClasses")
            .replace("{{diffTotalClasses}}", "${(afterTotalClasses - beforeTotalClasses).withSymbol()} classes")

            .replace("{{beforeTotalMethods}}", "$beforeTotalMethods")
            .replace("{{afterTotalMethods}}", "$afterTotalMethods")
            .replace("{{diffTotalMethods}}", "${(afterTotalMethods - beforeTotalMethods).withSymbol()} methods")

            .replace("{{changedFilesCount}}", "${changedFrameworkFiles.size + changedAppFiles.size} files")
            .replace("{{changedAppFilesCount}}", "${changedAppFiles.size} files")
            .replace("{{changedFrameworkFilesCount}}", "${changedFrameworkFiles.size} files")
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

    private fun String.addChangedFiles(
        files: List<ChangedFile>,
        note: String,
        replaceKey: String,
    ): String {
        val table = StringBuilder()

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

        buildTable(table = table, note = note, tableBody = tableBody, isTableEmpty = files.isEmpty())
        return this.replace("{{$replaceKey}}", table.toString())
    }

    private fun buildTable(table: StringBuilder, note: String, tableBody: String, isTableEmpty : Boolean) {
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
                                <th>File</th>
                                <th>Package</th>
                                <th>Lines</th>
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





