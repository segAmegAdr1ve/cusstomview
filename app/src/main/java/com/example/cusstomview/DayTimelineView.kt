package com.example.cusstomview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

class DayTimelineView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {

    // высота периода в 1 час
    private val spaceBetweenHorizontalSeparators = 150f
    private val paddingVertical = 150
    private val verticalLineOffset = 130f
    private val lineCount = 24
    private val dateCircleCenter = PointF(verticalLineOffset / 2f, spaceBetweenHorizontalSeparators / 2)
    private val dateCircleRadius = 40f
    private val totalHeight
        get() = spaceBetweenHorizontalSeparators * lineCount
    /** LocalDateTime.now by default*/
    var selectedDateTime: LocalDateTime = LocalDateTime.now().apply { Log.d("time", "hour: ${this.hour} minute: ${this.minute}") }
        set(value) {
            field = value
            currentDayOfMonth = selectedDateTime.dayOfMonth
            currentDayOfWeek = selectedDateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru"))
            invalidate()
        }
    private val currentTimeLineOffset = calculateCurrentTimeLineOffset()
    private var currentDayOfMonth: Int = selectedDateTime.dayOfMonth
    private var currentDayOfWeek = selectedDateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru"))

    private val currentDayOfWeekTextPaint = Paint().apply {
        color = Color.rgb(73,100,140)
        typeface = Typeface.MONOSPACE
        textSize = resources.getDimension(R.dimen.current_day_of_month_text_size)
        textAlign = Paint.Align.CENTER
    }

    private val currentDateTextPaint = Paint().apply {
        color = Color.WHITE
        typeface = Typeface.MONOSPACE
        textSize = resources.getDimension(R.dimen.current_date_text_size)
        textAlign = Paint.Align.CENTER
    }
    private val textBounds = Rect().apply {
        currentDateTextPaint.getTextBounds(currentDayOfWeek, 0, currentDayOfWeek.length, this)
    }
    private val textHeight = textBounds.height()

    private val circlePaint = Paint().apply {
        color = Color.rgb(73,100,140)
        style = Paint.Style.FILL
    }

    private val headerBackgroundPaint = Paint().apply {
        color = Color.rgb(235,235,245)
        style = Paint.Style.FILL
    }

    private val separatorPaint = Paint().apply {
        color = Color.rgb(170,170,170)
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

    private val timeList = (0..23).map {
        String.format(Locale("ru"),"%02d:00", it)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0f, 0f, width*1f, spaceBetweenHorizontalSeparators, headerBackgroundPaint)
        canvas.drawCircle(dateCircleCenter.x, dateCircleCenter.y, dateCircleRadius, circlePaint)
        canvas.drawText(currentDayOfMonth.toString(), dateCircleCenter.x, dateCircleCenter.y + textHeight/1.5f, currentDateTextPaint)
        canvas.drawText(currentDayOfWeek, dateCircleCenter.x, dateCircleCenter.y - dateCircleRadius - 5, currentDayOfWeekTextPaint )

        for (index in 1..<lineCount) {
            linePath.moveTo(verticalLineOffset - 15, (spaceBetweenHorizontalSeparators * index) + paddingVertical)
            linePath.lineTo(width * 1f, (spaceBetweenHorizontalSeparators * index) + paddingVertical)
            canvas.drawPath(linePath, separatorPaint)
        }

        for (index in 1..<lineCount) {
            canvas.drawText(
                timeList[index],
                verticalLineOffset/2,
                (spaceBetweenHorizontalSeparators * index) + 10 + paddingVertical,
                timePeriodPaint
            )
        }

        canvas.drawLine(verticalLineOffset, 0f, verticalLineOffset, totalHeight + paddingVertical, separatorPaint)
        canvas.drawLine(verticalLineOffset, currentTimeLineOffset, width*1f, currentTimeLineOffset, currentTimePaint )
        canvas.drawCircle(verticalLineOffset, currentTimeLineOffset, 10f, currentTimePaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d("measure", MeasureSpec.getMode(heightMeasureSpec).toString())
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = (lineCount * spaceBetweenHorizontalSeparators) + paddingVertical
        setMeasuredDimension(width, height.toInt())
    }

    private fun calculateCurrentTimeLineOffset(): Float {
        var result: Float = 0f
        result += spaceBetweenHorizontalSeparators // header
        result += selectedDateTime.hour * spaceBetweenHorizontalSeparators
        result += spaceBetweenHorizontalSeparators * selectedDateTime.minute / 60
        return result
    }

}