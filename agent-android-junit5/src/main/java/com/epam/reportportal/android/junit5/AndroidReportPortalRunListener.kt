/*
 * Copyright 2021 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
