package com.skyroute.flightdeck.store

import android.content.Context
import com.skyroute.flightdeck.common.store.Store
import com.skyroute.flightdeck.common.store.asStoreProvider

class AppStore(context: Context) {
    private val store = Store(
        context
            .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            .asStoreProvider()
    )

    var updatedAt: Long by store.long(
        key = "updated_at",
        defaultValue = -1,
    )

    companion object {
        private const val FILE_NAME = "app"
    }
}