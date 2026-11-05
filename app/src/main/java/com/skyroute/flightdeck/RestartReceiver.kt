package com.skyroute.flightdeck

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.skyroute.flightdeck.service.StatusProvider
import com.skyroute.flightdeck.util.startClashService

class RestartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED -> {
                if (StatusProvider.shouldStartClashOnBoot)
                    context.startClashService()
            }
        }
    }
}