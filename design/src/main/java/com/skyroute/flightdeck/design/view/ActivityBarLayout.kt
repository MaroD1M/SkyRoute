package com.skyroute.flightdeck.design.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.skyroute.flightdeck.common.compat.getColorCompat
import com.skyroute.flightdeck.design.R

class ActivityBarLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr, defStyleRes) {
    init {
        alpha = 0.96f

        setBackgroundColor(context.getColorCompat(R.color.color_hud_bar))
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        super.dispatchTouchEvent(ev)

        return true
    }
}