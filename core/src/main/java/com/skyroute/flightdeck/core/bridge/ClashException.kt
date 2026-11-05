package com.skyroute.flightdeck.core.bridge

import androidx.annotation.Keep

@Keep
class ClashException(msg: String) : IllegalArgumentException(msg)