package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import com.udacity.ButtonState.*
import kotlin.math.min
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var txtString:String
    private var btnColor:Int
    private var txtColor:Int

    private var progress    = 0f
    private val xSpacing    = 200f
    private val ySpacing    = 45f
    private var paint       = Paint(Paint.ANTI_ALIAS_FLAG)

    private var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(Completed) { _, _, new ->
        when(new){
            Clicked -> setToast(context, "Clicked")
            Loading -> showCircle = true
            Completed -> setToast(context, "Completed")
        }
    }

    private fun setToast(context: Context, msg:String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    private var showCircle:Boolean by Delegates.observable(false){_,_, newValue ->
        if (newValue){
            valueAnimator = ValueAnimator.ofFloat(0f,1f).apply {
                addUpdateListener {
                    progress = animatedValue as Float
                    invalidate()
                }
                repeatMode  = ValueAnimator.RESTART
                repeatCount = ValueAnimator.INFINITE
                duration    = 5000
                start()
            }
            txtString = "Downloading"
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

    override fun performClick(): Boolean {
        super.performClick()
        buttonState = Loading
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawButton(canvas)
        drawText(canvas)
        if (showCircle){
            buttonState = Clicked
            canvas.drawRect(0f,0f, createProgressBar(), measuredHeight.toFloat(), paint)
            drawText(canvas)
            drawCircle(canvas)
        }
    }

    private fun createProgressBar(): Float {
        val progressBar = progress * measuredWidth
        paint.color     = Color.RED
        paint.style     = Paint.Style.FILL
        return progressBar
    }

    private fun drawCircle(canvas: Canvas) {
        paint.apply {
            color = Color.YELLOW
            style = Paint.Style.FILL
        }

        val arc         = measuredHeight/2
        val side        = (measuredWidth/2) + xSpacing
        val top         = (measuredHeight/2) - ySpacing
        val smallCircle = progress * 360f

        canvas.drawArc(
            side,
            top,
            side + arc,
            top + arc,
            0f, smallCircle, true, paint)

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
        setMeasuredDimension(w, h)
    }
}