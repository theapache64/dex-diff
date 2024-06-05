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
    private val removedAppFiles : List<File>
) {


    fun make() {
        println("QuickTag: ReportMaker:make: Making report...")
        val reportFile = File("src/main/resources/report_template.html").copyTo(
            File("temp/report.html"),
            overwrite = true
        )

        val frameworkNote = "These are classes inside '${HomeViewModel.FRAMEWORK_PACKAGES.joinToString(", ")}' directory"
        val appNote = "These are classes NOT inside '${HomeViewModel.FRAMEWORK_PACKAGES.joinToString(", ")}' directory"

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

        reportFile.writeText(fullReport)
    }

    private fun String.addReportSummary(): String {
        return this
            .replace("{{beforeApkSize}}", "$beforeApkSizeInKb KB")
            .replace("{{afterApkSize}}", "$afterApkSizeInKb KB")
            .replace("{{diffApkSize}}", "${(afterApkSizeInKb - beforeApkSizeInKb).withSymbol()} KB")

            .replace("{{beforeTotalFiles}}", "$beforeTotalFiles")
            .replace("{{afterTotalFiles}}", "$afterTotalFiles")
            .replace("{{diffTotalFiles}}", "${(afterTotalFiles - beforeTotalFiles).withSymbol()} files")

            .replace("{{beforeTotalFrameworkFiles}}", "$beforeTotalFrameworkFiles (${(beforeTotalFiles )}%)")
            .replace("{{afterTotalFrameworkFiles}}", "$afterTotalFrameworkFiles")
            .replace("{{diffTotalFrameworkFiles}}", "${(afterTotalFrameworkFiles - beforeTotalFrameworkFiles).withSymbol()} files")

            .replace("{{beforeTotalAppFiles}}", "$beforeTotalAppFiles")
            .replace("{{afterTotalAppFiles}}", "$afterTotalAppFiles")
            .replace("{{diffTotalAppFiles}}", "${(afterTotalAppFiles - beforeTotalAppFiles).withSymbol()} files")

            .replace("{{beforeTotalClasses}}", "$beforeTotalClasses")
            .replace("{{afterTotalClasses}}", "$afterTotalClasses")
            .replace("{{diffTotalClasses}}", "${(afterTotalClasses - beforeTotalClasses).withSymbol()} classes")

            .replace("{{beforeTotalMethods}}", "$beforeTotalMethods")
            .replace("{{afterTotalMethods}}", "$afterTotalMethods")
            .replace("{{diffTotalMethods}}", "${(afterTotalMethods - beforeTotalMethods).withSymbol()} methods")
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

}




