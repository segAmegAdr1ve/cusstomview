package com.example.cusstomview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.DiscretePathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withSave
import androidx.core.view.isVisible
import kotlin.io.path.Path

class KView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {

    private val paintStroke = Paint()
    private val paintBackground = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL

        /*
        shader = LinearGradient(
            width * 0.25f,
            width * 0.5f,
            width * 0.1f,
            width * 0.7f,
            Color.BLUE,
            Color.YELLOW,
            Shader.TileMode.MIRROR
        )*/

    }
    private val paintCircle = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private val paintText = Paint().apply {
        typeface = Typeface.MONOSPACE
        color = Color.BLACK
        textSize = 24f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBackground.apply {
            setShader(
                LinearGradient(
                    width * 0.25f,
                    width * 0.5f,
                    width * 0.1f,
                    width * 0.7f,
                    Color.BLUE,
                    Color.YELLOW,
                    Shader.TileMode.MIRROR
                )
            )
        })
        /*
        paintStroke.style = Paint.Style.STROKE
        paintStroke.strokeWidth = 0f
        paintStroke.color = Color.BLUE
        canvas.drawRect((width/2).toFloat(), 0f, width.toFloat(), height.toFloat(), paintStroke)*/

        canvas.drawCircle(width * 0.25f, height * 0.75f, 50f, paintCircle)
        canvas.drawCircle(width * 0.75f, height * 0.75f, 50f, paintCircle)
        canvas.drawOval(width * 0.35f, height * 0.1f, width * 0.65f, height * 0.9f, paintCircle)
        canvas.drawCircle(width * 0.5f, width * 0.25f, 50f, paintCircle)
        canvas.drawText("big dick back in town", width * 0.1f, height * 0.05f, paintText)


    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val myListenet = object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(p0: MotionEvent): Boolean {
                return true
            }
        }

        val detector = GestureDetector(context, myListenet)

        detector.onTouchEvent(event ?: return false ).let {
            //isVisible = false
            alpha = 0F
            Handler(Looper.getMainLooper()).postDelayed({
                //isVisible = true
                alpha = 1f
            }, 1000)
            /*repeat(10) { index ->
                Handler(Looper.getMainLooper()).postDelayed({
                    //isVisible = true
                    alpha = (index/10).toFloat()
                }, 100 * index.toLong())
            }*/
        }
        return true
    }
}

class BroadcastView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {
    val a = Canvas(Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888,))
    val rect = Rect(12,12,12,12)
    val point = Point(12,12)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paintRect = Paint().apply {
            color = Color.YELLOW
            style = Paint.Style.FILL
        }
        val paintRectOutlined = Paint().apply {
            color = Color.YELLOW
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        val paintCircle = Paint().apply {
            //xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            color = Color.CYAN
        }
        canvas.drawColor(Color.RED)
        canvas.drawRoundRect(width * 0.1f, height * 0.1f,width * 0.9f,height * 0.9f, 40f,40f ,paintRect)
        canvas.drawRoundRect(width * 0.05f, height * 0.05f,width * 0.95f,height * 0.95f, 40f,40f ,paintRectOutlined)
        canvas.drawCircle(width * 0.5f,height * 0.5f, 50f, paintCircle)
        canvas.drawCircle(width * 0.6f,height * 0.6f, 50f, paintCircle.apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        })
        val path = Path().apply {
            moveTo(10f, 10f)
            lineTo(100f,10f)
            lineTo(100f, 100f)

            close()
        }
        canvas.drawPath(path, paintRect.apply {
            style = Paint.Style.STROKE
            strokeWidth = 10f
            color = Color.DKGRAY
            pathEffect = DiscretePathEffect(10f, 10f)
        })

        canvas.drawPath(path.apply { offset(100f, 100f) }, paintRect.apply {
            style = Paint.Style.STROKE
            strokeWidth = 5f
            color = Color.DKGRAY
            pathEffect = DashPathEffect(floatArrayOf(10f, 15f), 10f)
        })
    }
}