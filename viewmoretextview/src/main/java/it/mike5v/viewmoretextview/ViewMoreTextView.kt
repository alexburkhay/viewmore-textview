package it.mike5v.viewmoretextview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.toSpannable
import androidx.core.text.toSpanned
import java.lang.Exception
import kotlin.math.max

/**
 * Created by Michele Quintavalle on 2020-01-15.
 */
class ViewMoreTextView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : AppCompatTextView(context!!, attrs, defStyleAttr) {

    companion object {
        const val ANIMATION_PROPERTY_MAX_HEIGHT = "maxHeight"
        const val ANIMATION_PROPERTY_ALPHA = "alpha"
        const val DEFAULT_ELLIPSIZED_TEXT = "..."
        const val MAX_VALUE_ALPHA = 255
        const val MIN_VALUE_ALPHA = 0
    }

    private var visibleLines: Int? = null
    private var isExpanded: Boolean? = null
    private var animationDuration: Int? = null
    private var foregroundColor: Int? = null
    private var ellipsizeText: String? = null
    private var initialValue: Spannable? = null
    private var isUnderlined: Boolean? = null
    private var ellipsizeTextColor: Int? = null

    private var isAnimating: Boolean = false
    private var isSettingInternalText: Boolean = false

    private var visibleText: Spannable? = null

    private var drawTry: Int = 0

    init {
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.ViewMoreTextView)
        visibleLines = attributes?.getInt(R.styleable.ViewMoreTextView_visibleLines, 0)
        isExpanded = attributes?.getBoolean(R.styleable.ViewMoreTextView_isExpanded, false)
        animationDuration = attributes?.getInt(R.styleable.ViewMoreTextView_duration, 1000)
        foregroundColor =
            attributes?.getColor(R.styleable.ViewMoreTextView_foregroundColor, Color.TRANSPARENT)
        ellipsizeText = attributes?.getString(R.styleable.ViewMoreTextView_ellipsizeText)
        isUnderlined = attributes?.getBoolean(R.styleable.ViewMoreTextView_isUnderlined, false)
        ellipsizeTextColor =
            attributes?.getColor(R.styleable.ViewMoreTextView_ellipsizeTextColor, Color.BLUE)
        setMaxLines(isExpanded!!)
        attributes?.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        /*if (initialValue.isNullOrBlank()) {
            initialValue = (text ?: "").toString()
        }*/

        val isForegroundSet = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || foreground != null
        val alreadySet = isForegroundSet
                && (maxLines == visibleLines && !isExpanded!! || isExpanded!! && maxLines == Integer.MAX_VALUE)
        if (!alreadySet || drawTry == 0) {
            drawTry = if (alreadySet) {
                drawTry + 1
            } else {
                0
            }

            setMaxLines(isExpanded!!)
            if (!isAnimating) {
                setEllipsizedText(isExpanded!!)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setForeground(isExpanded!!)
            }
        } else {
            drawTry = 0
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (!isSettingInternalText) {
            initialValue = (text ?: "").toSpannable()
            visibleText = null;
        }
        super.setText(text, type)
    }

    fun clear() {
        this.isAnimating = false
        this.setIsExpanded(false)
    }

    fun toggle() {
        if (visibleText().isAllTextVisible()) {
            return
        }

        isAnimating = true
        isExpanded = !isExpanded!!

        if (isExpanded!!)
            setEllipsizedText(isExpanded!!)

        val startHeight = measuredHeight
        setMaxLines(isExpanded!!)
        measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        val endHeight = measuredHeight

        animationSet(startHeight, endHeight).apply {
            duration = animationDuration?.toLong()!!
            start()

            addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    if (!isExpanded!!) {
                        setEllipsizedText(isExpanded!!)
                    }
                    isAnimating = false
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {
                    isAnimating = false
                }
                override fun onAnimationStart(animation: Animator?) {
                    isAnimating = true
                }
            })
        }
    }

    fun setVisibleLines(visibleLines: Int): ViewMoreTextView {
        this.visibleLines = visibleLines
        return this
    }

    fun setIsExpanded(isExpanded: Boolean): ViewMoreTextView {
        this.isExpanded = isExpanded
        return this
    }

    fun isTextExpanded(): Boolean = this.isExpanded!!

    fun setAnimationDuration(animationDuration: Int): ViewMoreTextView {
        this.animationDuration = animationDuration
        return this
    }

    fun setIsUnderlined(isUnderlined: Boolean): ViewMoreTextView {
        this.isUnderlined = isUnderlined
        return this
    }

    fun setEllipsizedText(ellipsizeText: String): ViewMoreTextView {
        this.ellipsizeText = ellipsizeText
        return this
    }

    fun setEllipsizedTextColor(ellipsizeTextColor: Int): ViewMoreTextView {
        this.ellipsizeTextColor = ellipsizeTextColor
        return this
    }

    fun setForegroundColor(foregroundColor: Int): ViewMoreTextView {
        this.foregroundColor = foregroundColor
        return this
    }

    private fun setEllipsizedText(isExpanded: Boolean) {
        if (initialValue?.isBlank()!!)
            return

        isSettingInternalText = true
        val vText = visibleText()
        text = if (isExpanded || vText.isAllTextVisible()) {
            initialValue
            /*if ((isExpanded || visibleText.isAllTextVisible()) && (text == null || !text.toString().equals(initialValue))) {
                text = initialValue*/
        } else {
            var ellLength = (ellipsizeText.orEmpty().length + DEFAULT_ELLIPSIZED_TEXT.length)
            var upperBound = vText.length

            if (vText.length > ellLength && visibleLines!! > 1) {
                if (layout.getLineWidth(visibleLines!! - 1) + paint.measureText(ellipsizeText + DEFAULT_ELLIPSIZED_TEXT) >= measuredWidth ) {
                     upperBound = vText.length - ellLength - 1 // keep -1 for some space at the end
                } else if (vText[vText.length - 1] == '\n') {
                    upperBound = vText.length - 1
                }
            } else {
                // short text case
                upperBound = vText.length - ellLength
                if (upperBound <= 0) {
                    val index = vText.lastIndexOf("\n")
                    upperBound = if (index != -1) {
                        index // keep see more on same line
                    } else {
                        vText.length
                    }
                }
            }

            SpannableStringBuilder(vText.subSequence(0, upperBound).toSpannable())
                .append(DEFAULT_ELLIPSIZED_TEXT)
                .append(ellipsizeText.orEmpty().span())
            /*if (text == null || !builder.toSpanned().toString().equals(text.toString())) {
                text = builder
            } else {
                return
            }*/
        }
        isSettingInternalText = false
    }

    private fun visibleText(): Spannable {
        if (visibleText != null)
            return visibleText!!

        var end = 0

        try {
            val builder = SpannableStringBuilder()
                .append(DEFAULT_ELLIPSIZED_TEXT)
                .append(ellipsizeText.orEmpty().span()).toString()
            for (i in 0 until visibleLines!!) {
                if (layout.getLineEnd(i) != 0) {
                    end = if ((i == visibleLines!! - 1) && layout.text.endsWith(builder)) {
                        layout.getLineEnd(i) - builder.length + 1
                    } else {
                        layout.getLineEnd(i)
                    }
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            return initialValue!!
        }

        visibleText = initialValue?.subSequence(0, end)!!.toSpannable()
        return visibleText!!
    }

    private fun setMaxLines(isExpanded: Boolean) {
        maxLines = if (!isExpanded) {
            visibleLines!!
        } else {
            Integer.MAX_VALUE
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setForeground(isExpanded: Boolean) {
        foreground = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(foregroundColor!!, Color.TRANSPARENT)
        )
        foreground.alpha = if (isExpanded) {
            MIN_VALUE_ALPHA
        } else {
            MAX_VALUE_ALPHA
        }
    }

    private fun animationSet(startHeight: Int, endHeight: Int): AnimatorSet {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofInt(
                        this@ViewMoreTextView,
                        ANIMATION_PROPERTY_MAX_HEIGHT,
                        startHeight,
                        endHeight
                    )
                )
            }
        }

        return AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofInt(
                    this@ViewMoreTextView,
                    ANIMATION_PROPERTY_MAX_HEIGHT,
                    startHeight,
                    endHeight
                ),
                ObjectAnimator.ofInt(
                    this@ViewMoreTextView.foreground,
                    ANIMATION_PROPERTY_ALPHA,
                    foreground.alpha,
                    MAX_VALUE_ALPHA - foreground.alpha
                )
            )
        }
    }

    private fun String.isAllTextVisible(): Boolean = this == text

    private fun Spannable.isAllTextVisible(): Boolean = this.toString() == text.toString()

    private fun String.span(): SpannableString =
        SpannableString(this).apply {
            setSpan(
                ForegroundColorSpan(ellipsizeTextColor!!),
                0,
                this.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (isUnderlined!!)
                setSpan(
                    UnderlineSpan(),
                    0,
                    this.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
        }

}