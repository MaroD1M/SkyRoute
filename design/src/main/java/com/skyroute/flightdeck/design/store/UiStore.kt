package com.skyroute.flightdeck.design.store

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.skyroute.flightdeck.common.store.Store
import com.skyroute.flightdeck.common.store.asStoreProvider
import com.skyroute.flightdeck.core.model.ProxySort
import com.skyroute.flightdeck.design.model.AppIconVariant
import com.skyroute.flightdeck.design.model.AppInfoSort
import com.skyroute.flightdeck.design.model.CockpitStyle
import com.skyroute.flightdeck.design.model.DarkMode

class UiStore(context: Context) {
    private val store = Store(
        context
            .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            .asStoreProvider()
    )

    var enableVpn: Boolean by store.boolean(
        key = "enable_vpn",
        defaultValue = true
    )

    var darkMode: DarkMode by store.enum(
        key = "dark_mode",
        defaultValue = DarkMode.Auto,
        values = DarkMode.values()
    )

    var hideAppIcon: Boolean by store.boolean(
        key = "hide_app_icon",
        defaultValue = false,
    )

    var cockpitStyle: CockpitStyle by store.enum(
        key = "cockpit_style",
        defaultValue = CockpitStyle.Cyan,
        values = CockpitStyle.values(),
    )

    var appIcon: AppIconVariant by store.enum(
        key = "app_icon",
        defaultValue = AppIconVariant.Cyan,
        values = AppIconVariant.values(),
    )

    var hideFromRecents: Boolean by store.boolean(
        key = "hide_from_recents",
        defaultValue = false,
    )

    var proxyExcludeNotSelectable by store.boolean(
        key = "proxy_exclude_not_selectable",
        defaultValue = false,
    )

    var proxyLine: Int by store.int(
        key = "proxy_line",
        defaultValue = 2
    )

    var proxySort: ProxySort by store.enum(
        key = "proxy_sort",
        defaultValue = ProxySort.Default,
        values = ProxySort.values()
    )

    var proxyLastGroup: String by store.string(
        key = "proxy_last_group",
        defaultValue = ""
    )

    var accessControlSort: AppInfoSort by store.enum(
        key = "access_control_sort",
        defaultValue = AppInfoSort.Label,
        values = AppInfoSort.values(),
    )

    var accessControlReverse: Boolean by store.boolean(
        key = "access_control_reverse",
        defaultValue = false
    )

    var accessControlSystemApp: Boolean by store.boolean(
        key = "access_control_system_app",
        defaultValue = false,
    )

    companion object {
        private const val PREFERENCE_NAME = "ui"

        val Context.mainActivityAlias: ComponentName
            get() = ComponentName(this, "com.skyroute.flightdeck.MainActivityAlias")

        val Context.mainActivityAliasAmber: ComponentName
            get() = ComponentName(this, "com.skyroute.flightdeck.MainActivityAliasAmber")

        val Context.mainActivityAliasViolet: ComponentName
            get() = ComponentName(this, "com.skyroute.flightdeck.MainActivityAliasViolet")

        val Context.mainActivityAliasMaterial: ComponentName
            get() = ComponentName(this, "com.skyroute.flightdeck.MainActivityAliasMaterial")

        fun launcherAlias(context: Context, variant: AppIconVariant): ComponentName {
            return when (variant) {
                AppIconVariant.Cyan -> context.mainActivityAlias
                AppIconVariant.Amber -> context.mainActivityAliasAmber
                AppIconVariant.Violet -> context.mainActivityAliasViolet
                AppIconVariant.MaterialYou -> context.mainActivityAliasMaterial
            }
        }

        fun applyLauncherIcon(
            context: Context,
            variant: AppIconVariant,
            hidden: Boolean,
        ) {
            val aliases = listOf(
                context.mainActivityAlias,
                context.mainActivityAliasAmber,
                context.mainActivityAliasViolet,
                context.mainActivityAliasMaterial,
            )
            val selected = launcherAlias(context, variant)

            aliases.forEach { alias ->
                val state = if (!hidden && alias == selected) {
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                } else {
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                }

                context.packageManager.setComponentEnabledSetting(
                    alias,
                    state,
                    PackageManager.DONT_KILL_APP,
                )
            }
        }
    }
}