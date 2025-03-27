package com.example.cusstomview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import java.time.LocalDateTime
import java.util.Locale

const val TIMELINE_START = 0
const val TIMELINE_END = 23

class DayTimelineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val spaceBetweenHorizontalSeparators = 100f
    private val paddingVertical = 0
    private val verticalLineOffset = 130f
    private val lineCount = 24
    private val totalHeight
        get() = spaceBetweenHorizontalSeparators * lineCount
    private val timeFormatPattern = "%02d:00"
    private val locale = Locale("ru")
    
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
        color = resources.getColor(
            com.google.android.material.R.color.material_grey_300,
            context.theme
        )
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    private val currentTimePaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        strokeWidth = 2f
    }

    private val timePeriodPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        typeface = Typeface.MONOSPACE
        textSize = resources.getDimension(R.dimen.timeline_text_size)
        textAlign = Paint.Align.CENTER
    }

    private val linePath = Path().apply {
        repeat(lineCount) {
            this.moveTo(0f, spaceBetweenHorizontalSeparators * it)
            this.lineTo(width * 1f, spaceBetweenHorizontalSeparators * it)
        }
    }

    private val timeList = (TIMELINE_START..TIMELINE_END).map {
        String.format(locale, timeFormatPattern, it)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (index in 0..<lineCount) {
            linePath.moveTo(
                verticalLineOffset - 15,
                (spaceBetweenHorizontalSeparators * index) + paddingVertical
            )
            linePath.lineTo(
                width * 1f,
                (spaceBetweenHorizontalSeparators * index) + paddingVertical
            )
            canvas.drawPath(linePath, separatorPaint)
        }

        for (index in 1..<lineCount) {
            canvas.drawText(
                timeList[index],
                verticalLineOffset / 2,
                (spaceBetweenHorizontalSeparators * index) + 10 + paddingVertical,
                timePeriodPaint
            )
        }

        canvas.drawLine(
            verticalLineOffset,
            0f,
            verticalLineOffset,
            totalHeight + paddingVertical,
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
            canvas.drawCircle(verticalLineOffset, currentTimeLineOffset, 10f, currentTimePaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = (lineCount * spaceBetweenHorizontalSeparators) + paddingVertical
        setMeasuredDimension(width, height.toInt())
    }

    private fun calculateCurrentTimeLineOffset(): Float {
        var result: Float = 0f
        result += selectedDateTime.hour * spaceBetweenHorizontalSeparators
        result += spaceBetweenHorizontalSeparators * selectedDateTime.minute / 60
        return result
    }

}