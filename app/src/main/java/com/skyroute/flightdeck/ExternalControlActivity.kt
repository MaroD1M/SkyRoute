package com.skyroute.flightdeck

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.skyroute.flightdeck.common.constants.Intents
import com.skyroute.flightdeck.common.util.intent
import com.skyroute.flightdeck.common.util.setUUID
import com.skyroute.flightdeck.design.MainDesign
import com.skyroute.flightdeck.design.ui.ToastDuration
import com.skyroute.flightdeck.remote.StatusClient
import com.skyroute.flightdeck.service.model.Profile
import com.skyroute.flightdeck.util.startClashService
import com.skyroute.flightdeck.util.stopClashService
import com.skyroute.flightdeck.util.withProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*
import com.skyroute.flightdeck.design.R

class ExternalControlActivity : Activity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)

        when(intent.action) {
            Intent.ACTION_VIEW -> {
                val uri = intent.data ?: return finish()
                val url = uri.getQueryParameter("url") ?: return finish()

                launch {
                    val uuid = withProfile {
                        val type = when (uri.getQueryParameter("type")?.lowercase(Locale.getDefault())) {
                            "url" -> Profile.Type.Url
                            "file" -> Profile.Type.File
                            else -> Profile.Type.Url
                        }
                        val name = uri.getQueryParameter("name") ?: getString(R.string.new_profile)

                        val parsedInterval = uri.getQueryParameter("update-interval")?.toLongOrNull() ?: 0L
                        val updateInterval = if (parsedInterval > 0) parsedInterval.coerceAtLeast(15L) else 0L
                        val intervalMs = java.util.concurrent.TimeUnit.MINUTES.toMillis(updateInterval)

                        create(type, name).also {
                            patch(it, name, url, intervalMs, null)
                        }
                    }
                    startActivity(PropertiesActivity::class.intent.setUUID(uuid))
                    finish()
                }
                return
            }

            Intents.ACTION_TOGGLE_FLIGHT -> {
                if (isClashRunning()) {
                    stopClash()
                } else {
                    startClash()
                }
            }
            
            Intents.ACTION_TAKEOFF -> {
                if (isClashRunning()) {
                    Toast.makeText(this, R.string.external_control_started, Toast.LENGTH_LONG).show()
                } else {
                    startClash()
                }
            }
            
            Intents.ACTION_LAND -> {
                stopClash()
            }
        }
        return finish()
    }

    private fun isClashRunning(): Boolean {
        return StatusClient(this).currentProfile() != null
    }

    private fun startClash() {
//        if (currentProfile == null) {
//            Toast.makeText(this, R.string.no_profile_selected, Toast.LENGTH_LONG).show()
//            return
//        }
        val vpnRequest = startClashService()
        if (vpnRequest != null) {
            Toast.makeText(this, R.string.unable_to_start_vpn, Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(this, R.string.external_control_started, Toast.LENGTH_LONG).show()
    }

    private fun stopClash() {
        stopClashService()
        Toast.makeText(this, R.string.external_control_stopped, Toast.LENGTH_LONG).show()
    }

    override fun finish() {
        super.finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }
}
