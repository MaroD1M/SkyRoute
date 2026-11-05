package com.skyroute.flightdeck.util

import android.net.Uri

val Uri.fileName: String?
    get() = schemeSpecificPart.split("/").lastOrNull()