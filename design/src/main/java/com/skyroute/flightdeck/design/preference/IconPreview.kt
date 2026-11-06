package com.skyroute.flightdeck.design.preference

import android.graphics.drawable.Drawable
import androidx.annotation.StringRes
import com.skyroute.flightdeck.design.databinding.PreferenceIconPreviewBinding
import com.skyroute.flightdeck.design.util.layoutInflater

interface IconPreviewPreference : Preference {
    fun setIcon(drawable: Drawable?)
    fun setSummary(text: CharSequence?)
}

fun PreferenceScreen.iconPreview(
    @StringRes title: Int,
    icon: Drawable?,
    summary: CharSequence?,
): IconPreviewPreference {
    val binding = PreferenceIconPreviewBinding
        .inflate(context.layoutInflater, root, false)

    val impl = object : IconPreviewPreference {
        override val view = binding.root

        override fun setIcon(drawable: Drawable?) {
            binding.iconView.setImageDrawable(drawable)
        }

        override fun setSummary(text: CharSequence?) {
            binding.summaryView.text = text
        }
    }

    binding.titleView.setText(title)
    impl.setIcon(icon)
    impl.setSummary(summary)

    addElement(impl)

    return impl
}
