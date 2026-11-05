package com.skyroute.flightdeck.service.clash.module

import android.app.Service
import com.skyroute.flightdeck.common.constants.Intents
import com.skyroute.flightdeck.common.log.Log

class CloseModule(service: Service) : Module<CloseModule.RequestClose>(service) {
    object RequestClose

    override suspend fun run() {
        val broadcasts = receiveBroadcast {
            addAction(Intents.ACTION_FLIGHT_REQUEST_LANDING)
        }

        broadcasts.receive()

        Log.d("User request close")

        return enqueueEvent(RequestClose)
    }
}