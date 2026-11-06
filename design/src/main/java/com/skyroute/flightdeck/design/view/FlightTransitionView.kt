package com.skyroute.flightdeck.design.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import com.skyroute.flightdeck.design.R
import kotlin.math.atan2

class FlightTransitionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val density = resources.displayMetrics.density
    private val scaledDensity = resources.displayMetrics.scaledDensity
    private val accent = themeColor(
        com.google.android.material.R.attr.colorPrimary,
        Color.parseColor("#00E5FF")
    )
    private val secondary = themeColor(
        com.google.android.material.R.attr.colorSecondary,
        Color.parseColor("#FFC400")
    )
    private val white = Color.parseColor("#F2FCFF")
    private val scrimColor = Color.parseColor("#050B18")

    private val scrimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val routePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.8f * density
        color = accent
        pathEffect = DashPathEffect(floatArrayOf(10f * density, 10f * density), 0f)
    }
    private val trailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = accent
    }
    private val planePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = white
    }
    private val planeStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.4f * density
        color = accent
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = white
        textSize = 28f * scaledDensity
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
    }
    private val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = accent
        textSize = 11f * scaledDensity
        textAlign = Paint.Align.CENTER
        typeface = Typeface.MONOSPACE
    }

    private val route = Path()
    private val plane = Path()
    private val measure = PathMeasure()
    private val position = FloatArray(2)
    private val tangent = FloatArray(2)

    private var animator: ValueAnimator? = null
    private var progress = 0f
    private var takeoff = true
    private var generation = 0

    init {
        setWillNotDraw(false)
        visibility = GONE
    }

    fun play(takeoff: Boolean, onEnd: () -> Unit = {}) {
        this.takeoff = takeoff

        generation += 1
        val token = generation

        val previous = animator
        animator = null
        previous?.cancel()

        progress = 0f
        visibility = VISIBLE
        isClickable = true
        isFocusable = true
        bringToFront()

        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1050L
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            doOnEnd {
                if (token == generation) {
                    visibility = GONE
                    isClickable = false
                    isFocusable = false
                    onEnd()
                }
            }
            start()
        }
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        animator = null
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        if (width <= 0f || height <= 0f) {
            return
        }

        val fade = when {
            progress < 0.12f -> progress / 0.12f
            progress > 0.82f -> (1f - progress) / 0.18f
            else -> 1f
        }.coerceIn(0f, 1f)
        val alpha = (fade * 255f).toInt()

        scrimPaint.color = withAlpha(scrimColor, (190f * fade).toInt())
        canvas.drawRect(0f, 0f, width, height, scrimPaint)

        val startX = if (takeoff) width * 0.14f else width * 0.86f
        val startY = if (takeoff) height * 0.78f else height * 0.22f
        val endX = if (takeoff) width * 0.86f else width * 0.14f
        val endY = if (takeoff) height * 0.22f else height * 0.78f
        val controlX = width * 0.50f
        val controlY = if (takeoff) height * 0.34f else height * 0.66f

        route.reset()
        route.moveTo(startX, startY)
        route.quadTo(controlX, controlY, endX, endY)

        routePaint.color = withAlpha(accent, (95f * fade).toInt())
        canvas.drawPath(route, routePaint)

        measure.setPath(route, false)

        if (measure.length <= 0f) {
            return
        }

        val travel = if (takeoff) progress else 1f - progress
        val direction = if (takeoff) 1f else -1f
        val trailCount = 28

        for (index in 0 until trailCount) {
            val trail = travel - index * 0.008f * direction

            if (trail < 0f || trail > 1f) {
                continue
            }

            measure.getPosTan(measure.length * trail, position, tangent)

            val trailAlpha = (145f * (1f - index / trailCount.toFloat()) * fade)
                .toInt()
                .coerceIn(0, 255)
            val radius = (2.4f - index * 0.055f).coerceAtLeast(0.7f) * density

            trailPaint.color = withAlpha(accent, trailAlpha)
            canvas.drawCircle(position[0], position[1], radius, trailPaint)
        }

        measure.getPosTan(measure.length * travel, position, tangent)

        val angle = Math.toDegrees(
            atan2(tangent[1].toDouble(), tangent[0].toDouble())
        ).toFloat() + 90f + (if (takeoff) 0f else 180f)

        trailPaint.color = withAlpha(accent, (55f * fade).toInt())
        canvas.drawCircle(position[0], position[1], 22f * density * fade, trailPaint)

        drawPlane(canvas, position[0], position[1], angle, fade)

        textPaint.alpha = alpha
        subtitlePaint.alpha = (alpha * 0.85f).toInt()

        val textY = if (takeoff) height * 0.54f else height * 0.46f

        canvas.drawText(
            context.getString(if (takeoff) R.string.transition_takeoff else R.string.transition_landing),
            width / 2f,
            textY,
            textPaint
        )
        canvas.drawText(
            context.getString(R.string.transition_subtitle),
            width / 2f,
            textY + 24f * scaledDensity,
            subtitlePaint
        )
    }

    private fun drawPlane(canvas: Canvas, x: Float, y: Float, angle: Float, fade: Float) {
        val size = 15f * density

        plane.reset()
        plane.moveTo(0f, -size)
        plane.lineTo(0.052f * size, -0.74f * size)
        plane.lineTo(0.066f * size, -0.23f * size)
        plane.lineTo(0.52f * size, 0.05f * size)
        plane.lineTo(0.70f * size, 0.16f * size)
        plane.lineTo(0.735f * size, 0.26f * size)
        plane.lineTo(0.095f * size, 0.155f * size)
        plane.lineTo(0.115f * size, 0.48f * size)
        plane.lineTo(0.295f * size, 0.635f * size)
        plane.lineTo(0.325f * size, 0.735f * size)
        plane.lineTo(0.055f * size, 0.665f * size)
        plane.lineTo(0f, 0.695f * size)
        plane.lineTo(-0.055f * size, 0.665f * size)
        plane.lineTo(-0.325f * size, 0.735f * size)
        plane.lineTo(-0.295f * size, 0.635f * size)
        plane.lineTo(-0.115f * size, 0.48f * size)
        plane.lineTo(-0.095f * size, 0.155f * size)
        plane.lineTo(-0.735f * size, 0.26f * size)
        plane.lineTo(-0.70f * size, 0.16f * size)
        plane.lineTo(-0.52f * size, 0.05f * size)
        plane.lineTo(-0.066f * size, -0.23f * size)
        plane.lineTo(-0.052f * size, -0.74f * size)
        plane.close()

        canvas.save()
        canvas.translate(x, y)
        canvas.rotate(angle)

        planePaint.alpha = (255f * fade).toInt()
        planeStroke.alpha = (255f * fade).toInt()
        trailPaint.color = withAlpha(secondary, (170f * fade).toInt())

        canvas.drawPath(plane, planePaint)
        canvas.drawPath(plane, planeStroke)
        canvas.drawCircle(0f, -size * 0.70f, size * 0.052f, trailPaint)

        canvas.restore()
    }

    private fun themeColor(attr: Int, fallback: Int): Int {
        val value = TypedValue()

        return if (context.theme.resolveAttribute(attr, value, true)) {
            value.data
        } else {
            fallback
        }
    }

    private fun withAlpha(color: Int, alpha: Int): Int {
        return (color and 0x00FFFFFF) or ((alpha.coerceIn(0, 255)) shl 24)
    }
}
