package com.skyroute.flightdeck.service.remote

import com.skyroute.flightdeck.core.model.LogMessage
import com.github.kr328.kaidl.BinderInterface

@BinderInterface
interface ILogObserver {
    fun newItem(log: LogMessage)
}