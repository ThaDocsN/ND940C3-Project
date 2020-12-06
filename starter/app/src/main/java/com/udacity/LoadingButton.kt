package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var txtString:String
    private var btnColor:Int
    private var txtColor:Int
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }


    init {
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


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawButton(canvas)
        drawText(canvas)

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
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}