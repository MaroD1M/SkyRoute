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
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.sin

class FlightRadarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var inFlight: Boolean = false
        set(value) {
            if (field == value) {
                return
            }

            field = value

            if (value) {
                startFlight()
            } else {
                stopFlight()
            }

            invalidate()
        }

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
    private val white = Color.parseColor("#E6F7FF")
    private val dim = Color.parseColor("#8FB8C9")

    private fun themeColor(attr: Int, fallback: Int): Int {
        val value = TypedValue()

        return if (context.theme.resolveAttribute(attr, value, true)) {
            value.data
        } else {
            fallback
        }
    }

    private fun withAlpha(color: Int, alpha: Int): Int {
        return (color and 0x00FFFFFF) or (alpha shl 24)
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
        color = withAlpha(accent, 0x1F)
    }

    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = withAlpha(accent, 0x55)
    }

    private val routePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f * density
        color = accent
        pathEffect = DashPathEffect(floatArrayOf(14f * density, 12f * density), 0f)
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = withAlpha(accent, 0x22)
    }

    private val nodePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val planePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = white
    }

    private val planeStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.6f * density
        color = accent
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = dim
        textSize = 10f * scaledDensity
        typeface = Typeface.MONOSPACE
    }

    private val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = secondary
        textSize = 10f * scaledDensity
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    }

    private val route = Path()
    private val plane = Path()
    private val measure = PathMeasure()
    private val position = FloatArray(2)
    private val tangent = FloatArray(2)

    private var animator: ValueAnimator? = null
    private var landAnimator: ValueAnimator? = null
    private var progress = 0f
    private var dashPhase = 0f

    init {
        setWillNotDraw(false)
    }

    private fun startFlight() {
        landAnimator?.cancel()
        landAnimator = null

        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 6200L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                dashPhase = (dashPhase + 5f * density) % (26f * density)
                routePaint.pathEffect = DashPathEffect(
                    floatArrayOf(14f * density, 12f * density),
                    dashPhase
                )
                invalidate()
            }
            start()
        }
    }

    private fun stopFlight() {
        animator?.cancel()
        animator = null

        landAnimator?.cancel()

        if (progress <= 0f) {
            progress = 0f
            invalidate()
            return
        }

        landAnimator = ValueAnimator.ofFloat(progress, 0f).apply {
            duration = 1400L
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                dashPhase = (dashPhase - 4f * density + 26f * density) % (26f * density)
                routePaint.pathEffect = DashPathEffect(
                    floatArrayOf(14f * density, 12f * density),
                    dashPhase
                )
                invalidate()
            }
            start()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (inFlight) {
            startFlight()
        }
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        animator = null
        landAnimator?.cancel()
        landAnimator = null

        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        if (width <= 0f || height <= 0f) {
            return
        }

        val left = paddingLeft.toFloat() + 8f * density
        val top = paddingTop.toFloat() + 8f * density
        val right = width - paddingRight.toFloat() - 8f * density
        val bottom = height - paddingBottom.toFloat() - 8f * density

        if (right <= left || bottom <= top) {
            return
        }

        val gridStep = (right - left) / 10f

        var x = left
        while (x <= right) {
            canvas.drawLine(x, top, x, bottom, gridPaint)
            x += gridStep
        }

        var y = top
        while (y <= bottom) {
            canvas.drawLine(left, y, right, y, gridPaint)
            y += gridStep
        }

        val radarX = left + (right - left) * 0.70f
        val radarY = top + (bottom - top) * 0.40f
        val radarRadius = min(right - left, bottom - top) * 0.34f

        for (index in 1..3) {
            canvas.drawCircle(radarX, radarY, radarRadius * index / 3f, ringPaint)
        }

        val originX = left + (right - left) * 0.12f
        val originY = top + (bottom - top) * 0.80f
        val controlX = left + (right - left) * 0.44f
        val controlY = top + (bottom - top) * 0.34f
        val destinationX = left + (right - left) * 0.88f
        val destinationY = top + (bottom - top) * 0.22f

        route.reset()
        route.moveTo(originX, originY)
        route.quadTo(controlX, controlY, destinationX, destinationY)

        canvas.drawPath(route, routePaint)

        val nodeRadius = 4.5f * density
        val pulseRadius = 12f * density

        glowPaint.color = withAlpha(secondary, 0x33)
        canvas.drawCircle(originX, originY, pulseRadius, glowPaint)
        nodePaint.color = secondary
        canvas.drawCircle(originX, originY, nodeRadius, nodePaint)

        val pulse = if (inFlight) {
            0.85f + 0.15f * sin(progress * 2f * PI).toFloat()
        } else {
            1f
        }

        glowPaint.color = withAlpha(accent, 0x33)
        canvas.drawCircle(destinationX, destinationY, pulseRadius * pulse, glowPaint)
        nodePaint.color = accent
        canvas.drawCircle(destinationX, destinationY, nodeRadius, nodePaint)

        canvas.drawText("ORIGIN", originX - 18f * density, originY + 22f * density, labelPaint)
        canvas.drawText("DEST", destinationX - 16f * density, destinationY - 14f * density, accentPaint)

        measure.setPath(route, false)

        if (measure.length > 0f) {
            val t = if (inFlight) progress else 0f

            measure.getPosTan(measure.length * t, position, tangent)

            val angle = Math.toDegrees(
                atan2(tangent[1].toDouble(), tangent[0].toDouble())
            ).toFloat() + 90f

            drawPlane(canvas, position[0], position[1], angle)
        }
    }

    private fun drawPlane(canvas: Canvas, x: Float, y: Float, angle: Float) {
        val size = 11f * density

        plane.reset()
        plane.moveTo(0f, -size)
        plane.lineTo(0.08f * size, -0.25f * size)
        plane.lineTo(0.55f * size, 0.05f * size)
        plane.lineTo(0.65f * size, 0.18f * size)
        plane.lineTo(0.13f * size, 0.08f * size)
        plane.lineTo(0.16f * size, 0.55f * size)
        plane.lineTo(0.30f * size, 0.68f * size)
        plane.lineTo(0.30f * size, 0.78f * size)
        plane.lineTo(0.02f * size, 0.68f * size)
        plane.lineTo(-0.02f * size, 0.68f * size)
        plane.lineTo(-0.30f * size, 0.78f * size)
        plane.lineTo(-0.30f * size, 0.68f * size)
        plane.lineTo(-0.16f * size, 0.55f * size)
        plane.lineTo(-0.13f * size, 0.08f * size)
        plane.lineTo(-0.65f * size, 0.18f * size)
        plane.lineTo(-0.55f * size, 0.05f * size)
        plane.lineTo(-0.08f * size, -0.25f * size)
        plane.close()

        canvas.save()
        canvas.translate(x, y)
        canvas.rotate(angle)

        glowPaint.color = withAlpha(accent, 0x44)
        canvas.drawCircle(0f, 0f, size * 1.15f, glowPaint)
        canvas.drawPath(plane, planePaint)
        canvas.drawPath(plane, planeStroke)

        canvas.restore()
    }
}
