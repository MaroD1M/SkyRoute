package com.skyroute.flightdeck.design.util

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.skyroute.flightdeck.common.compat.foreground
import com.skyroute.flightdeck.design.model.AppInfo

fun PackageInfo.toAppInfo(pm: PackageManager): AppInfo {
    return AppInfo(
        packageName = packageName,
        icon = applicationInfo!!.loadIcon(pm).foreground(),
        label = applicationInfo!!.loadLabel(pm).toString(),
        installTime = firstInstallTime,
        updateDate = lastUpdateTime,
    )
}
