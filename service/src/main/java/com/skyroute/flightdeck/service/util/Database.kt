package com.skyroute.flightdeck.service.util

import com.skyroute.flightdeck.service.data.ImportedDao
import com.skyroute.flightdeck.service.data.PendingDao
import java.util.*

suspend fun generateProfileUUID(): UUID {
    var result = UUID.randomUUID()

    while (ImportedDao().exists(result) || PendingDao().exists(result)) {
        result = UUID.randomUUID()
    }

    return result
}
