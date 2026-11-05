package com.skyroute.flightdeck

import android.content.pm.PackageManager
import com.skyroute.flightdeck.common.util.componentName
import com.skyroute.flightdeck.design.AppSettingsDesign
import com.skyroute.flightdeck.design.model.AppIconVariant
import com.skyroute.flightdeck.design.model.Behavior
import com.skyroute.flightdeck.design.store.UiStore
import com.skyroute.flightdeck.service.store.ServiceStore
import com.skyroute.flightdeck.util.ApplicationObserver
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select

class AppSettingsActivity : BaseActivity<AppSettingsDesign>(), Behavior {
    override suspend fun main() {
        val design = AppSettingsDesign(
            this,
            uiStore,
            ServiceStore(this),
            this,
            clashRunning,
            ::onHideIconChange,
            ::onIconChange,
        )

        setContentDesign(design)

        while (isActive) {
            select<Unit> {
                events.onReceive {
                    when (it) {
                        Event.ClashStart, Event.ClashStop, Event.ServiceRecreated ->
                            recreate()
                        else -> Unit
                    }
                }
                design.requests.onReceive {
                    ApplicationObserver.createdActivities.forEach {
                        it.recreate()
                    }
                }
            }
        }
    }

    override var autoRestart: Boolean
        get() {
            val status = packageManager.getComponentEnabledSetting(
                RestartReceiver::class.componentName
            )

            return status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        }
        set(value) {
            val status = if (value)
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            else
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED

            packageManager.setComponentEnabledSetting(
                RestartReceiver::class.componentName,
                status,
                PackageManager.DONT_KILL_APP,
            )
        }

    private fun onHideIconChange(hide: Boolean) {
        UiStore.applyLauncherIcon(this, uiStore.appIcon, hide)
    }

    private fun onIconChange(icon: AppIconVariant) {
        UiStore.applyLauncherIcon(this, icon, uiStore.hideAppIcon)
    }
}
