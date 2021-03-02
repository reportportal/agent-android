package com.epam.reportportal.android.junit5

import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner

class AndroidJUnit5ReportPortalRunner : AndroidJUnitRunner() {
    override fun onCreate(arguments: Bundle) {
        arguments.putString("runnerBuilder", "de.mannodermaus.junit5.AndroidJUnit5Builder")
        arguments.putString("listener", "com.epam.test.android.espresso.junit5.AndroidReportPortalRunListener")
        super.onCreate(arguments)
    }
}
