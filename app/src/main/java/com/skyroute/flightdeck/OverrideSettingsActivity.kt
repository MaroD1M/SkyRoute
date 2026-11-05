package com.skyroute.flightdeck

import android.content.pm.PackageManager
import com.skyroute.flightdeck.common.compat.getDrawableCompat
import com.skyroute.flightdeck.common.constants.Metadata
import com.skyroute.flightdeck.core.Clash
import com.skyroute.flightdeck.design.OverrideSettingsDesign
import com.skyroute.flightdeck.design.model.AppInfo
import com.skyroute.flightdeck.design.util.toAppInfo
import com.skyroute.flightdeck.service.store.ServiceStore
import com.skyroute.flightdeck.util.withClash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext

class OverrideSettingsActivity : BaseActivity<OverrideSettingsDesign>() {
    override suspend fun main() {
        val configuration = withClash { queryOverride(Clash.OverrideSlot.Persist) }
        val service = ServiceStore(this)

        defer {
            withClash {
                patchOverride(Clash.OverrideSlot.Persist, configuration)
            }
        }

        val design = OverrideSettingsDesign(
            this,
            configuration
        )

        setContentDesign(design)

        while (isActive) {
            select<Unit> {
                events.onReceive {

                }
                design.requests.onReceive {
                    when (it) {
                        OverrideSettingsDesign.Request.ResetOverride -> {
                            if (design.requestResetConfirm()) {
                                defer {
                                    withClash {
                                        clearOverride(Clash.OverrideSlot.Persist)
                                    }
                                }

                                finish()
                            }
                        }
                    }
                }
            }
        }
    }
}