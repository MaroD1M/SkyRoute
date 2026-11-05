package com.skyroute.flightdeck.core.bridge

import androidx.annotation.Keep

@Keep
interface FetchCallback {
    fun report(statusJson: String)
    fun complete(error: String?)
}