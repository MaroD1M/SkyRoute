package com.skyroute.flightdeck.common.constants

import com.skyroute.flightdeck.common.util.packageName

object Intents {
    // Public
    val ACTION_PROVIDE_URL = "$packageName.action.PROVIDE_URL"
    val ACTION_TAKEOFF = "$packageName.action.TAKEOFF"
    val ACTION_LAND = "$packageName.action.LAND"
    val ACTION_TOGGLE_FLIGHT = "$packageName.action.TOGGLE_FLIGHT"

    const val EXTRA_NAME = "name"

    // Self
    val ACTION_SERVICE_RECREATED = "$packageName.intent.action.FLIGHT_RECREATED"
    val ACTION_FLIGHT_DEPARTED = "$packageName.intent.action.FLIGHT_DEPARTED"
    val ACTION_FLIGHT_LANDED = "$packageName.intent.action.FLIGHT_LANDED"
    val ACTION_FLIGHT_REQUEST_LANDING = "$packageName.intent.action.FLIGHT_REQUEST_LANDING"
    val ACTION_PROFILE_CHANGED = "$packageName.intent.action.PROFILE_CHANGED"
    val ACTION_PROFILE_UPDATE_COMPLETED = "$packageName.intent.action.PROFILE_UPDATE_COMPLETED"
    val ACTION_PROFILE_UPDATE_FAILED = "$packageName.intent.action.PROFILE_UPDATE_FAILED"
    val ACTION_PROFILE_REQUEST_UPDATE = "$packageName.intent.action.REQUEST_UPDATE"
    val ACTION_PROFILE_SCHEDULE_UPDATES = "$packageName.intent.action.SCHEDULE_UPDATES"
    val ACTION_PROFILE_LOADED = "$packageName.intent.action.PROFILE_LOADED"
    val ACTION_OVERRIDE_CHANGED = "$packageName.intent.action.OVERRIDE_CHANGED"

    const val EXTRA_STOP_REASON = "stop_reason"
    const val EXTRA_UUID = "uuid"
    const val EXTRA_FAIL_REASON = "fail_reason"
}