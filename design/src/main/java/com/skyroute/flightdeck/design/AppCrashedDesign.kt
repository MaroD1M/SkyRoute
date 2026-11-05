package com.skyroute.flightdeck.design

import android.content.Context
import android.view.View
import com.skyroute.flightdeck.design.databinding.DesignAppCrashedBinding
import com.skyroute.flightdeck.design.util.applyFrom
import com.skyroute.flightdeck.design.util.bindAppBarElevation
import com.skyroute.flightdeck.design.util.layoutInflater
import com.skyroute.flightdeck.design.util.root

class AppCrashedDesign(context: Context) : Design<Unit>(context) {
    private val binding = DesignAppCrashedBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    fun setAppLogs(logs: String) {
        binding.logsView.text = logs
    }

    init {
        binding.self = this

        binding.activityBarLayout.applyFrom(context)

        binding.scrollRoot.bindAppBarElevation(binding.activityBarLayout)
    }
}