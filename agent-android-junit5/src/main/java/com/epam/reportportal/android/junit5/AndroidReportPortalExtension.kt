package com.epam.reportportal.android.junit5

import com.epam.reportportal.junit5.ReportPortalExtension

class AndroidReportPortalExtension : ReportPortalExtension() {
    init {
        AndroidReportPortalRunListener.addLaunchFinisher(this)
    }
}
