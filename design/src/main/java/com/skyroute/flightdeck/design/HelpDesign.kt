package com.skyroute.flightdeck.design

import android.content.Context
import android.net.Uri
import android.view.View
import com.skyroute.flightdeck.design.databinding.DesignSettingsCommonBinding
import com.skyroute.flightdeck.design.preference.category
import com.skyroute.flightdeck.design.preference.clickable
import com.skyroute.flightdeck.design.preference.preferenceScreen
import com.skyroute.flightdeck.design.preference.tips
import com.skyroute.flightdeck.design.util.applyFrom
import com.skyroute.flightdeck.design.util.bindAppBarElevation
import com.skyroute.flightdeck.design.util.layoutInflater
import com.skyroute.flightdeck.design.util.root

class HelpDesign(
    context: Context,
    openLink: (Uri) -> Unit,
) : Design<Unit>(context) {
    private val binding = DesignSettingsCommonBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    init {
        binding.surface = surface

        binding.activityBarLayout.applyFrom(context)

        binding.scrollRoot.bindAppBarElevation(binding.activityBarLayout)

        val screen = preferenceScreen(context) {
            tips(R.string.tips_help)

            category(R.string.document)

            clickable(
                title = R.string.clash_wiki,
                summary = R.string.clash_wiki_url
            ) {
                clicked {
                    openLink(Uri.parse(context.getString(R.string.clash_wiki_url)))
                }
            }

            clickable(
                title = R.string.clash_meta_wiki,
                summary = R.string.clash_meta_wiki_url
            ) {
                clicked {
                    openLink(Uri.parse(context.getString(R.string.clash_meta_wiki_url)))
                }
            }

            category(R.string.sources)

            clickable(
                title = R.string.clash_meta_core,
                summary = R.string.clash_meta_core_url
            ) {
                clicked {
                    openLink(Uri.parse(context.getString(R.string.clash_meta_core_url)))
                }
            }

            clickable(
                title = R.string.clash_meta_for_android,
                summary = R.string.meta_github_url
            ) {
                clicked {
                    openLink(Uri.parse(context.getString(R.string.meta_github_url)))
                }
            }

            category(R.string.legal)

            clickable(
                title = R.string.upstream_project,
                summary = R.string.upstream_url
            ) {
                clicked {
                    openLink(Uri.parse(context.getString(R.string.upstream_url)))
                }
            }

            clickable(
                title = R.string.license_title,
                summary = R.string.license_url
            ) {
                clicked {
                    openLink(Uri.parse(context.getString(R.string.license_url)))
                }
            }
        }

        binding.content.addView(screen.root)
    }
}