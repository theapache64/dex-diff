package com.github.theapache64.dexdiff.utils

import com.github.theapache64.dexdiff.ui.home.HomeViewModel
import java.io.File

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

    private val newAppFiles : List<File>,
    private val removedAppFiles : List<File>,

    private val changedFrameworkFiles : List<Pair<File, File>>,
    private val changedAppFiles : List<Pair<File, File>>
) {


    fun make() {
        println("QuickTag: ReportMaker:make: Making report...")
        val reportFile = File("src/main/resources/report_template.html").copyTo(
            File("temp/report.html"),
            overwrite = true
        )

        val frameworkNote = "These are classes inside '${HomeViewModel.FRAMEWORK_PACKAGES.joinToString(", ")}' directory"
        val frameworkChangedNote = "These are classes inside '${HomeViewModel.FRAMEWORK_PACKAGES.joinToString(", ")}' directory with content changes"
        val appNote = "These are classes NOT inside '${HomeViewModel.FRAMEWORK_PACKAGES.joinToString(", ")}' directory"
        val appChangedNote = "These are classes NOT inside '${HomeViewModel.FRAMEWORK_PACKAGES.joinToString(", ")}' directory with content changes"

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
            .add(
                files = changedFrameworkFiles.map { it.first },
                note = frameworkChangedNote,
                key = "changedFrameworkFilesTable"
            )
            .add(
                files = changedAppFiles.map { it.first },
                note = appChangedNote,
                key = "changedAppFilesTable"
            )

        reportFile.writeText(fullReport)
    }

    private fun String.addReportSummary(): String {
        return this
            .replace("{{beforeApkSize}}", "$beforeApkSizeInKb KB")
            .replace("{{afterApkSize}}", "$afterApkSizeInKb KB")
            .replace("{{diffApkSize}}", "${(afterApkSizeInKb - beforeApkSizeInKb).withSymbol()} KB")

            .replace("{{beforeTotalFiles}}", "$beforeTotalFiles (100%)")
            .replace("{{afterTotalFiles}}", "$afterTotalFiles (100%)")
            .replace("{{diffTotalFiles}}", "${(afterTotalFiles - beforeTotalFiles).withSymbol()} files")

            .replace("{{beforeTotalFrameworkFiles}}", "$beforeTotalFrameworkFiles (${((beforeTotalFrameworkFiles / beforeTotalFiles.toFloat()) * 100).roundToTwoDecimals()}%)")
            .replace("{{afterTotalFrameworkFiles}}", "$afterTotalFrameworkFiles (${((afterTotalFrameworkFiles / afterTotalFiles.toFloat()) * 100).roundToTwoDecimals()}%)")
            .replace("{{diffTotalFrameworkFiles}}", "${(afterTotalFrameworkFiles - beforeTotalFrameworkFiles).withSymbol()} files")

            .replace("{{beforeTotalAppFiles}}", "$beforeTotalAppFiles (${((beforeTotalAppFiles / beforeTotalFiles.toFloat()) * 100).roundToTwoDecimals()}%)")
            .replace("{{afterTotalAppFiles}}", "$afterTotalAppFiles (${((afterTotalAppFiles / beforeTotalFiles.toFloat()) * 100).roundToTwoDecimals()}%)")
            .replace("{{diffTotalAppFiles}}", "${(afterTotalAppFiles - beforeTotalAppFiles).withSymbol()} files")

            .replace("{{beforeTotalClasses}}", "$beforeTotalClasses")
            .replace("{{afterTotalClasses}}", "$afterTotalClasses")
            .replace("{{diffTotalClasses}}", "${(afterTotalClasses - beforeTotalClasses).withSymbol()} classes")

            .replace("{{beforeTotalMethods}}", "$beforeTotalMethods")
            .replace("{{afterTotalMethods}}", "$afterTotalMethods")
            .replace("{{diffTotalMethods}}", "${(afterTotalMethods - beforeTotalMethods).withSymbol()} methods")

            .replace("{{changedFilesCount}}", "${changedFrameworkFiles.size} files")
    }


    private fun String.add(
        files : List<File>,
        note : String,
        key : String,
    ): String {
        val table = StringBuilder()

        val tableBody = files.joinToString("\n") { file ->
            """
                <tr>
                    <td><a target="_blank" href="file://${file.absolutePath}">${file.name}</a></td>
                    <td>${file.readLines().size}</td>
                </tr>
            """.trimIndent()
        }

        table.append(
            """
            <div class="alert alert-info">
              <strong>NOTE: $note</strong> 
            </div>
            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>File</th>
                        <th>Lines</th>
                    </tr>
                </thead>
                <tbody>
                    $tableBody
                </tbody>
            </table>
        """.trimIndent()
        )

        return this.replace("{{$key}}", table.toString())
    }

    private fun Int.withSymbol(): String {
        return if (this > 0) {
            "+${this}"
        } else {
            "$this"
        }
    }


    private fun Float.roundToTwoDecimals(): String {
        return String.format("%.2f", this)
    }

}





