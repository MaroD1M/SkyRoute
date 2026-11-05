package com.skyroute.flightdeck

import com.skyroute.flightdeck.common.compat.versionCodeCompat
import com.skyroute.flightdeck.common.log.Log
import com.skyroute.flightdeck.design.AppCrashedDesign
import com.skyroute.flightdeck.log.SystemLogcat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

class AppCrashedActivity : BaseActivity<AppCrashedDesign>() {
    override suspend fun main() {
        val design = AppCrashedDesign(this)

        setContentDesign(design)

        val packageInfo = withContext(Dispatchers.IO) {
            packageManager.getPackageInfo(packageName, 0)
        }

        Log.i("App version: versionName = ${packageInfo.versionName} versionCode = ${packageInfo.versionCodeCompat}")

        val logs = withContext(Dispatchers.IO) {
            SystemLogcat.dumpCrash()
        }

        design.setAppLogs(logs)

        while (isActive) {
            events.receive()
        }
    }
}