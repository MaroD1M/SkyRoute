package com.skyroute.flightdeck.util

import android.content.Context
import android.content.Intent
import android.net.VpnService
import com.skyroute.flightdeck.common.compat.startForegroundServiceCompat
import com.skyroute.flightdeck.common.constants.Intents
import com.skyroute.flightdeck.common.util.intent
import com.skyroute.flightdeck.design.store.UiStore
import com.skyroute.flightdeck.service.ClashService
import com.skyroute.flightdeck.service.TunService
import com.skyroute.flightdeck.service.util.sendBroadcastSelf

fun Context.startClashService(): Intent? {
    val startTun = UiStore(this).enableVpn

    if (startTun) {
        val vpnRequest = VpnService.prepare(this)
        if (vpnRequest != null)
            return vpnRequest

        startForegroundServiceCompat(TunService::class.intent)
    } else {
        startForegroundServiceCompat(ClashService::class.intent)
    }

    return null
}

fun Context.stopClashService() {
    sendBroadcastSelf(Intent(Intents.ACTION_FLIGHT_REQUEST_LANDING))
}