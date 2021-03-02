@file:Suppress("ControlFlowWithEmptyBody")

package com.epam.reportportal.android.junit5

import org.junit.runner.Result
import org.junit.runner.notification.RunListener
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class AndroidReportPortalRunListener : RunListener() {
    companion object {
        @JvmStatic
        private val EXTENSIONS: Queue<AndroidReportPortalExtension> = ConcurrentLinkedQueue()

        fun addLaunchFinisher(extension: AndroidReportPortalExtension) {
            EXTENSIONS.add(extension)
        }
    }

    override fun testRunFinished(result: Result) {
        while (EXTENSIONS.poll().also { it?.finish() } != null) {
        }
    }
}
