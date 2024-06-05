package com.github.theapache64.dexdiff.utils

import java.io.File

class ReportMaker(
    val beforeApkSizeInKb: Int,
    val afterApkSizeInKb: Int,

    val beforeTotalFiles: Int,
    val afterTotalFiles: Int,

    val beforeTotalClasses: Int,
    val afterTotalClasses: Int,

    val beforeTotalMethods: Int,
    val afterTotalMethods: Int,

    val newFiles: List<File>,
    val deletedFiles: List<File>
) {
    fun make() {
        println("QuickTag: ReportMaker:make: Making report...")
        val reportFile = File("src/main/resources/report_template.html").copyTo(
            File("temp/report.html"),
            overwrite = true
        )

        val fullReport = reportFile.readText()
            .addReportSummary()

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

            .replace("{{beforeTotalClasses}}", "$beforeTotalClasses")
            .replace("{{afterTotalClasses}}", "$afterTotalClasses")
            .replace("{{diffTotalClasses}}", "${(afterTotalClasses - beforeTotalClasses).withSymbol()} classes")

            .replace("{{beforeTotalMethods}}", "$beforeTotalMethods")
            .replace("{{afterTotalMethods}}", "$afterTotalMethods")
            .replace("{{diffTotalMethods}}", "${(afterTotalMethods - beforeTotalMethods).withSymbol()} methods")
    }

    private fun Int.withSymbol(): String {
        return if (this > 0) {
            "+${this}"
        } else {
            "$this"
        }
    }

}



