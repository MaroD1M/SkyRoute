package com.skyroute.flightdeck.core.bridge

import androidx.annotation.Keep

@Keep
interface LogcatInterface {
    fun received(jsonPayload: String)
}