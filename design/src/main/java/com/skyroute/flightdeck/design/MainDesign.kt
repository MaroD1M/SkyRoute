package com.skyroute.flightdeck.design

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.skyroute.flightdeck.core.model.TunnelState
import com.skyroute.flightdeck.core.util.trafficTotal
import com.skyroute.flightdeck.design.databinding.DesignAboutBinding
import com.skyroute.flightdeck.design.databinding.DesignMainBinding
import com.skyroute.flightdeck.design.util.layoutInflater
import com.skyroute.flightdeck.design.util.resolveThemedColor
import com.skyroute.flightdeck.design.util.root
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainDesign(context: Context) : Design<MainDesign.Request>(context) {
    enum class Request {
        ToggleStatus,
        OpenProxy,
        OpenProfiles,
        OpenProviders,
        OpenLogs,
        OpenSettings,
        OpenHelp,
        OpenAbout,
    }

    private val binding = DesignMainBinding
        .inflate(context.layoutInflater, context.root, false)

    private var lastRunning: Boolean? = null

    override val root: View
        get() = binding.root

    suspend fun setProfileName(name: String?) {
        withContext(Dispatchers.Main) {
            binding.profileName = name
        }
    }

    suspend fun setClashRunning(running: Boolean) {
        withContext(Dispatchers.Main) {
            val changed = lastRunning != null && lastRunning != running
            lastRunning = running

            binding.clashRunning = running
            binding.flightRadar.inFlight = running
            binding.mainActionCard.strokeColor = context.resolveThemedColor(
                if (running) {
                    com.google.android.material.R.attr.colorPrimary
                } else {
                    R.attr.colorClashStopped
                }
            )

            if (!changed) {
                return@withContext
            }

            binding.statusChip.animate().cancel()
            binding.statusChip.animate()
                .scaleX(1.06f)
                .scaleY(1.06f)
                .setDuration(120L)
                .withEndAction {
                    binding.statusChip.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(180L)
                        .start()
                }
                .start()

            binding.mainActionCard.animate().cancel()
            binding.mainActionCard.animate()
                .scaleX(0.985f)
                .scaleY(0.985f)
                .setDuration(110L)
                .withEndAction {
                    binding.mainActionCard.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(180L)
                        .start()
                }
                .start()

            binding.flightTransition.play(running)
        }
    }

    suspend fun setForwarded(value: Long) {
        withContext(Dispatchers.Main) {
            binding.forwarded = value.trafficTotal()
        }
    }

    suspend fun setMode(mode: TunnelState.Mode) {
        withContext(Dispatchers.Main) {
            binding.mode = when (mode) {
                TunnelState.Mode.Direct -> context.getString(R.string.direct_mode)
                TunnelState.Mode.Global -> context.getString(R.string.global_mode)
                TunnelState.Mode.Rule -> context.getString(R.string.rule_mode)
                else -> context.getString(R.string.rule_mode)
            }
        }
    }

    suspend fun setHasProviders(has: Boolean) {
        withContext(Dispatchers.Main) {
            binding.hasProviders = has
        }
    }

    suspend fun showAbout(versionName: String) {
        withContext(Dispatchers.Main) {
            val binding = DesignAboutBinding.inflate(context.layoutInflater).apply {
                this.versionName = versionName
            }

            AlertDialog.Builder(context)
                .setView(binding.root)
                .show()
        }
    }

    init {
        binding.self = this

        val started = context.resolveThemedColor(com.google.android.material.R.attr.colorPrimary)
        val stopped = context.resolveThemedColor(R.attr.colorClashStopped)

        binding.colorClashStarted = started
        binding.colorClashStopped = stopped
        binding.mainActionCard.strokeColor = stopped

        launch {
            while (isActive) {
                withContext(Dispatchers.Main) {
                    binding.liveIndicator.animate().alpha(0.3f).setDuration(700L).start()
                }
                delay(700L)
                withContext(Dispatchers.Main) {
                    binding.liveIndicator.animate().alpha(1f).setDuration(700L).start()
                }
                delay(700L)
            }
        }
    }

    fun request(request: Request) {
        requests.trySend(request)
    }
}