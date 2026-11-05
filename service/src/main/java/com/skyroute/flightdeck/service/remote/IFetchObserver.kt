package com.skyroute.flightdeck.service.remote

import com.skyroute.flightdeck.core.model.FetchStatus
import com.github.kr328.kaidl.BinderInterface

@BinderInterface
fun interface IFetchObserver {
    fun updateStatus(status: FetchStatus)
}