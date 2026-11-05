package com.skyroute.flightdeck.util

import android.content.Context
import java.io.File

val Context.logsDir: File
    get() = cacheDir.resolve("logs")

val Context.clashDir: File
    get() = filesDir.resolve("skyroute")