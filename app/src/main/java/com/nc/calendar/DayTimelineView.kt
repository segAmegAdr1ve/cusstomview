package com.nc.calendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import com.nc.calendar.Constants.TIME_FORMAT_PATTERN
import com.nc.calendar.Constants.locale
import java.time.LocalDateTime

class DayTimelineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val spaceBetweenHorizontalSeparators = 100f
    private val verticalLineOffset = 130f
    private val lineCount = 24
    private val horizontalLineBeyondVerticalExtension = 15
    private val verticalLineStartX = verticalLineOffset - horizontalLineBeyondVerticalExtension
    private val textStartX = verticalLineOffset / 2
    private val textVerticalOffset = 10
    private val totalHeight
        get() = spaceBetweenHorizontalSeparators * lineCount

    private val today: LocalDateTime = LocalDateTime.now()
    var selectedDateTime: LocalDateTime = today
        set(value) {
            field = value
            currentDayOfMonth = selectedDateTime.dayOfMonth
            invalidate()
        }
    private val isCurrentDay
        get() = today.toLocalDate() == selectedDateTime.toLocalDate()
    private val currentTimeLineOffset = calculateCurrentTimeLineOffset()
    private val currentTimeLineCircleRadius = 10f
    private var currentDayOfMonth: Int = selectedDateTime.dayOfMonth

    private val separatorPaint = Paint().apply {
        color = resources.getColor(R.color.md_theme_secondary, context.theme)
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    private val currentTimePaint = Paint().apply {
        color = resources.getColor(R.color.md_theme_primary, context.theme)
        style = Paint.Style.FILL
        strokeWidth = 2f
    }

    private val timePeriodPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.md_theme_secondary, context.theme)
        typeface = Typeface.MONOSPACE
        textSize = resources.getDimension(R.dimen.medium_text_size)
        textAlign = Paint.Align.CENTER
    }

    private val linePath = Path().apply {
        repeat(lineCount) {
            this.moveTo(ZERO_POSITION_F, spaceBetweenHorizontalSeparators * it)
            this.lineTo(width * 1f, spaceBetweenHorizontalSeparators * it)
        }
    }

    private val timeList = (TIMELINE_START..TIMELINE_END).map {
        String.format(locale, TIME_FORMAT_PATTERN, it)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (index in 0..<lineCount) {
            linePath.moveTo(
                verticalLineStartX,
                (spaceBetweenHorizontalSeparators * index)
            )
            linePath.lineTo(
                width * 1f,
                (spaceBetweenHorizontalSeparators * index)
            )
            canvas.drawPath(linePath, separatorPaint)
        }

        for (index in 1..<lineCount) {
            canvas.drawText(
                timeList[index],
                textStartX,
                (spaceBetweenHorizontalSeparators * index) + textVerticalOffset,
                timePeriodPaint
            )
        }

        canvas.drawLine(
            verticalLineOffset,
            ZERO_POSITION_F,
            verticalLineOffset,
            totalHeight,
            separatorPaint
        )

        if (isCurrentDay) {
            canvas.drawLine(
                verticalLineOffset,
                currentTimeLineOffset,
                width * 1f,
                currentTimeLineOffset,
                currentTimePaint
            )
            canvas.drawCircle(
                verticalLineOffset,
                currentTimeLineOffset,
                currentTimeLineCircleRadius,
                currentTimePaint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = (lineCount * spaceBetweenHorizontalSeparators)
        setMeasuredDimension(width, height.toInt())
    }

    private fun calculateCurrentTimeLineOffset(): Float {
        var result = ZERO_POSITION_F
        result += selectedDateTime.hour * spaceBetweenHorizontalSeparators
        result += spaceBetweenHorizontalSeparators * selectedDateTime.minute / MINUTE_IN_HOUR
        return result
    }

    companion object {
        const val TIMELINE_START = 0
        const val TIMELINE_END = 23
        const val MINUTE_IN_HOUR = 60
        const val ZERO_POSITION_F = 0f
    }
}