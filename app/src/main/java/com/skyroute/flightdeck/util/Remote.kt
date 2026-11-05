package com.skyroute.flightdeck.util

import android.os.DeadObjectException
import com.skyroute.flightdeck.common.log.Log
import com.skyroute.flightdeck.remote.Remote
import com.skyroute.flightdeck.service.remote.IClashManager
import com.skyroute.flightdeck.service.remote.IProfileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

suspend fun <T> withClash(
    context: CoroutineContext = Dispatchers.IO,
    block: suspend IClashManager.() -> T
): T {
    while (true) {
        val remote = Remote.service.remote.get()
        val client = remote.clash()

        try {
            return withContext(context) { client.block() }
        } catch (e: DeadObjectException) {
            Log.w("Remote services panic")

            Remote.service.remote.reset(remote)
        }
    }
}

suspend fun <T> withProfile(
    context: CoroutineContext = Dispatchers.IO,
    block: suspend IProfileManager.() -> T
): T {
    while (true) {
        val remote = Remote.service.remote.get()
        val client = remote.profile()

        try {
            return withContext(context) { client.block() }
        } catch (e: DeadObjectException) {
            Log.w("Remote services panic")

            Remote.service.remote.reset(remote)
        }
    }
}
