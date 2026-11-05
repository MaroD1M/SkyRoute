package com.skyroute.flightdeck.design.util

import com.skyroute.flightdeck.design.view.ObservableScrollView

val ObservableScrollView.isTop: Boolean
    get() = scrollX == 0 && scrollY == 0
