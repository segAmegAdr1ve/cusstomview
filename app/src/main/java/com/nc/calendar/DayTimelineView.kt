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
    private val separatorWidth = 1f
    private val currentTimelineWidth = 2f
    private val currentTimelineCircleRadius = 10f
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
    private var currentDayOfMonth: Int = selectedDateTime.dayOfMonth

    private val separatorPaint = Paint().apply {
        color = resources.getColor(R.color.md_theme_secondary, context.theme)
        style = Paint.Style.STROKE
        strokeWidth = separatorWidth
    }

    private val currentTimePaint = Paint().apply {
        color = resources.getColor(R.color.md_theme_primary, context.theme)
        style = Paint.Style.FILL
        strokeWidth = currentTimelineWidth
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
            this.lineTo(width.toFloat(), spaceBetweenHorizontalSeparators * it)
        }
    }

    private val timeList = (TIME_LIST_START..TIME_LIST_END).map {
        String.format(locale, TIME_FORMAT_PATTERN, it)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (index in TIMELINE_START..<lineCount) {
            linePath.moveTo(
                verticalLineStartX,
                (spaceBetweenHorizontalSeparators * index)
            )
            linePath.lineTo(
                width.toFloat(),
                (spaceBetweenHorizontalSeparators * index)
            )
            canvas.drawPath(linePath, separatorPaint)
        }

        for (index in TIMELINE_START..<lineCount) {
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
                width.toFloat(),
                currentTimeLineOffset,
                currentTimePaint
            )
            canvas.drawCircle(
                verticalLineOffset,
                currentTimeLineOffset,
                currentTimelineCircleRadius,
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
        private const val TIMELINE_START = 1
        private const val TIME_LIST_START = 0
        private const val TIME_LIST_END = 23
        private const val MINUTE_IN_HOUR = 60
        private const val ZERO_POSITION_F = 0f
    }
}