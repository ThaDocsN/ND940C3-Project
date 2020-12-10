package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.udacity.ButtonState.*
import timber.log.Timber
import kotlin.math.min
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var txtString:String
    private var btnColor:Int
    private var txtColor:Int

    private var radius     = 0.0f
    private var widthSize  = 0
    private var heightSize = 0
    private val xSpacing   = 200.0
    private val ySpacing   = 15.0
    private var paint      = Paint(Paint.ANTI_ALIAS_FLAG)

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(Completed) { p, old, new ->
        when(new){
            Clicked   -> showCircle = true
            Loading   -> println("Downloading")
            Completed -> println("Completed")
        }
    }

    private var showCircle:Boolean by Delegates.observable(false){_,_, newValue ->
        if (newValue){
            txtString = "Downloading"
            btnColor  = Color.BLUE
            invalidate()
        }
    }


    init {
        isClickable = true
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0,
            0
        ).apply {
            txtString = getString(R.styleable.LoadingButton_btnText).toString()
            btnColor  = getColor(R.styleable.LoadingButton_btnBackground, 0)
            txtColor  = getColor(R.styleable.LoadingButton_btnTextColor, 0)
            recycle()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = (min(w, h) / 2 * .6).toFloat()

    }

    override fun performClick(): Boolean {
        super.performClick()
        buttonState = Clicked
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawButton(canvas)
        drawText(canvas)
        if (showCircle){
            drawCircle(canvas)
        }
    }

    private fun drawCircle(canvas: Canvas) {
        paint.apply {
            color = Color.YELLOW
            style = Paint.Style.FILL
        }
        canvas.drawCircle((measuredWidth.toFloat() / 2 + xSpacing).toFloat(), (measuredHeight.toFloat() / 2 - ySpacing).toFloat(), radius, paint)
    }


    private fun drawText(canvas: Canvas) {
        paint.apply {
            color     = txtColor
            textAlign = Paint.Align.CENTER
            textSize  = 40.0F
        }
        canvas.drawText(txtString, measuredWidth.toFloat()/2, measuredHeight.toFloat()/2, paint)
    }

    private fun drawButton(canvas: Canvas) {
        paint.color = btnColor
        paint.style = Paint.Style.FILL
        canvas.drawColor(paint.color)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int    = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int    = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize  = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}