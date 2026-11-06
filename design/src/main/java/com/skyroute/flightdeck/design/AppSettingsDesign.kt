package com.skyroute.flightdeck.design

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import com.skyroute.flightdeck.design.databinding.DesignSettingsCommonBinding
import com.skyroute.flightdeck.design.model.AppIconVariant
import com.skyroute.flightdeck.design.model.Behavior
import com.skyroute.flightdeck.design.model.CockpitStyle
import com.skyroute.flightdeck.design.model.DarkMode
import com.skyroute.flightdeck.design.preference.*
import com.skyroute.flightdeck.design.store.UiStore
import com.skyroute.flightdeck.design.util.applyFrom
import com.skyroute.flightdeck.design.util.bindAppBarElevation
import com.skyroute.flightdeck.design.util.layoutInflater
import com.skyroute.flightdeck.design.util.root
import com.skyroute.flightdeck.service.store.ServiceStore

class AppSettingsDesign(
    context: Context,
    uiStore: UiStore,
    srvStore: ServiceStore,
    behavior: Behavior,
    running: Boolean,
    onHideIconChange: (hide: Boolean) -> Unit,
    onIconChange: (icon: AppIconVariant) -> Unit,
    iconDrawable: (icon: AppIconVariant) -> Drawable,
) : Design<AppSettingsDesign.Request>(context) {
    enum class Request {
        ReCreateAllActivities
    }

    private val binding = DesignSettingsCommonBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    init {
        binding.surface = surface

        binding.activityBarLayout.applyFrom(context)

        binding.scrollRoot.bindAppBarElevation(binding.activityBarLayout)

        val screen = preferenceScreen(context) {
            category(R.string.behavior)

            switch(
                value = behavior::autoRestart,
                icon = R.drawable.ic_baseline_restore,
                title = R.string.auto_restart,
                summary = R.string.allow_clash_auto_restart,
            )

            category(R.string.interface_)

            selectableList(
                value = uiStore::darkMode,
                values = DarkMode.values(),
                valuesText = arrayOf(
                    R.string.follow_system_android_10,
                    R.string.always_light,
                    R.string.always_dark
                ),
                icon = R.drawable.ic_baseline_brightness_4,
                title = R.string.dark_mode
            ) {
                listener = OnChangedListener {
                    requests.trySend(Request.ReCreateAllActivities)
                }
            }

            selectableList(
                value = uiStore::cockpitStyle,
                values = CockpitStyle.values(),
                valuesText = arrayOf(
                    R.string.style_cyan,
                    R.string.style_amber,
                    R.string.style_violet
                ),
                icon = R.drawable.ic_radar,
                title = R.string.cockpit_style
            ) {
                listener = OnChangedListener {
                    requests.trySend(Request.ReCreateAllActivities)
                }
            }

            val preview = iconPreview(
                R.string.current_icon,
                iconDrawable(uiStore.appIcon),
                context.getText(iconText(uiStore.appIcon)),
            )

            selectableList(
                value = uiStore::appIcon,
                values = AppIconVariant.values(),
                valuesText = arrayOf(
                    R.string.icon_cyan,
                    R.string.icon_amber,
                    R.string.icon_violet,
                    R.string.icon_material
                ),
                icon = R.drawable.ic_aircraft_info,
                title = R.string.launcher_icon
            ) {
                listener = OnChangedListener {
                    onIconChange(uiStore.appIcon)
                    preview.setIcon(iconDrawable(uiStore.appIcon))
                    preview.setSummary(context.getText(iconText(uiStore.appIcon)))
                }
            }

            switch(
                value = uiStore::hideAppIcon,
                icon = R.drawable.ic_baseline_hide,
                title = R.string.hide_app_icon_title,
                summary = R.string.hide_app_icon_desc,
            ) {
                listener = OnChangedListener {
                    onHideIconChange(uiStore::hideAppIcon.get())
                }
            }

            switch(
                value = uiStore::hideFromRecents,
                icon = R.drawable.ic_baseline_stack,
                title = R.string.hide_from_recents_title,
                summary = R.string.hide_from_recents_desc,
            ) {
                listener = OnChangedListener {
                    requests.trySend(Request.ReCreateAllActivities)
                }
            }

            category(R.string.service)

            switch(
                value = srvStore::dynamicNotification,
                icon = R.drawable.ic_baseline_domain,
                title = R.string.show_traffic,
                summary = R.string.show_traffic_summary
            ) {
                enabled = !running
            }
        }

        binding.content.addView(screen.root)
    }

    private fun iconText(icon: AppIconVariant): Int {
        return when (icon) {
            AppIconVariant.Cyan -> R.string.icon_cyan
            AppIconVariant.Amber -> R.string.icon_amber
            AppIconVariant.Violet -> R.string.icon_violet
            AppIconVariant.MaterialYou -> R.string.icon_material
        }
    }
}