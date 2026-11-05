package com.skyroute.flightdeck.common.constants

import android.content.ComponentName
import com.skyroute.flightdeck.common.util.packageName

object Components {
    private const val componentsPackageName = "com.skyroute.flightdeck"

    val MAIN_ACTIVITY = ComponentName(packageName, "$componentsPackageName.MainActivity")
    val PROPERTIES_ACTIVITY = ComponentName(packageName, "$componentsPackageName.PropertiesActivity")
}