package com.ale.ltechecker.guielements

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView


class ResizeTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {
    companion object {
        const val minTextSize = 5.0f
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        resizeText()
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        resizeText()
    }

    private fun resizeText() {
        if (width == 0) return
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        val ratio = width / paint.measureText(text.toString())
        if (ratio <= 1.0f) {
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                Math.max(minTextSize, (textSize * ratio)-0.2f)
            )
        }
    }
}