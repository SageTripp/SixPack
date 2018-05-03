package com.sagetripp.sixpack.view

import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListener
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import com.sagetripp.sixpack.R
import com.sagetripp.sixpack.extension.empty
import kotlinx.android.synthetic.main.mtf_layout.view.*


class MaterialTextField : FrameLayout {
    protected var inputMethodManager: InputMethodManager? = null

    /**
     * 输入框
     */
    var editText: EditText? = null
    /**
     * 标题边距
     */
    protected var labelTopMargin = -1
    /**
     * 是否展开
     */
    var isExpanded = false

    /**
     * 动画时间
     */
    protected var ANIMATION_DURATION = -1
    /**
     * 获取焦点的时候是否打开输入法
     */
    protected var OPEN_KEYBOARD_ON_FOCUS = true
    /**
     * 标题颜色,color资源ID
     */
    @ColorRes
    protected var labelColor = -1
    /**
     * 图标资源,drawable资源ID
     */
    protected var imageDrawableId = -1
    /**
     * 卡片折叠高度
     */
    protected var cardCollapsedHeight = -1
    /**
     * 是否获取到焦点
     */
    protected var hasFocus = false
        protected set(value) {
            field = value
            if (value) {
                //当获取到焦点时,展开卡片
                expand()
            } else {
                if (editText?.empty() ?: true)
                    reduce()
            }
        }
    protected var backgroundColar = -1

    protected var reducedScale = 0.2f

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        handleAttributes(context, attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        handleAttributes(context, attrs)
        init()
    }

    protected fun init() {
        inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    fun toggle() {
        if (isExpanded) {
            reduce()
        } else {
            expand()
        }
    }

    fun reduce() {
        if (isExpanded && editText?.empty() ?: true) {
            val heightInitial = resources.getDimensionPixelOffset(R.dimen.mtf_cardHeight_final)

            with(ViewCompat.animate(mtf_label)) {
                alpha(1f)
                scaleX(1f)
                scaleY(1f)
                translationY(0f)
                duration = ANIMATION_DURATION.toLong()
            }
            with(ViewCompat.animate(mtf_image)) {
                alpha(0f)
                scaleX(0.4f)
                scaleY(0.4f)
                duration = ANIMATION_DURATION.toLong()
            }
            with(ViewCompat.animate(editText)) {
                alpha(1f)
                duration = ANIMATION_DURATION.toLong()
                setUpdateListener { view ->
                    //percentage
                    mtf_card.layoutParams?.height =
                            (view.alpha * (heightInitial - cardCollapsedHeight) + cardCollapsedHeight).toInt()
                    mtf_card.requestLayout()
                }
                setListener(object : ViewPropertyAnimatorListener {
                    override fun onAnimationStart(view: View) {
                        if (isExpanded) {
                            editText?.visibility = View.VISIBLE
                        }
                    }

                    override fun onAnimationEnd(view: View) {
                        if (!isExpanded) {
                            editText?.visibility = View.INVISIBLE
                        }
                    }

                    override fun onAnimationCancel(view: View) {}
                })
            }

            with(ViewCompat.animate(mtf_card)) {
                alpha(1f)
                scaleY(reducedScale)
                duration = ANIMATION_DURATION.toLong()
            }
            isExpanded = false
        }
        editText?.apply {
            if (hasFocus()) {
                inputMethodManager?.hideSoftInputFromWindow(windowToken, 0)
            }
        }
    }

    fun expand() {
        if (!isExpanded) {
            with(ViewCompat.animate(editText)) {
                alpha(1f)
                duration = ANIMATION_DURATION.toLong()
            }

            with(ViewCompat.animate(mtf_card)) {
                scaleY(1f)
                alpha(0.4f)
                duration = ANIMATION_DURATION.toLong()
            }

            with(ViewCompat.animate(mtf_label)) {
                //                alpha = 0.4f
                scaleX(0.7f)
                scaleY(0.7f)
                translationY((-labelTopMargin).toFloat())
                duration = ANIMATION_DURATION.toLong()
            }

            with(ViewCompat.animate(mtf_image)) {
                alpha(1f)
                scaleX(1f)
                scaleY(1f)
                duration = ANIMATION_DURATION.toLong()
            }

            if (OPEN_KEYBOARD_ON_FOCUS) {
                inputMethodManager?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            }

            isExpanded = true
            editText?.postDelayed({
                editText?.requestFocusFromTouch()
                inputMethodManager?.showSoftInput(editText, 0)
            }, 30)
        }
    }

    override fun setBackgroundColor(color: Int) {
        this.backgroundColar = color
    }

    protected fun handleAttributes(context: Context, attrs: AttributeSet) {
        try {
            val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.MaterialTextField)

            run {
                ANIMATION_DURATION =
                        styledAttrs.getInteger(
                                R.styleable.MaterialTextField_mtf_animationDuration,
                                400)

                OPEN_KEYBOARD_ON_FOCUS =
                        styledAttrs.getBoolean(
                                R.styleable.MaterialTextField_mtf_openKeyboardOnFocus,
                                false)

                labelColor =
                        styledAttrs.getColor(
                                R.styleable.MaterialTextField_mtf_labelColor,
                                -1)

                imageDrawableId =
                        styledAttrs.getResourceId(
                                R.styleable.MaterialTextField_mtf_image,
                                -1)

                cardCollapsedHeight =
                        styledAttrs.getDimensionPixelOffset(
                                R.styleable.MaterialTextField_mtf_cardCollapsedHeight,
                                context.resources.getDimensionPixelOffset(R.dimen.mtf_cardHeight_initial))

                hasFocus =
                        styledAttrs.getBoolean(
                                R.styleable.MaterialTextField_mtf_hasFocus,
                                false)

                backgroundColar =
                        styledAttrs.getColor(
                                R.styleable.MaterialTextField_mtf_backgroundColor,
                                -1)
            }

            styledAttrs.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    protected fun findEditTextChild(): EditText? {
        return if (childCount > 0 && getChildAt(0) is EditText) {
            getChildAt(0) as EditText
        } else null
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        editText = findEditTextChild()
        if (editText == null) {
            return
        }
        addView(LayoutInflater.from(context).inflate(R.layout.mtf_layout, this, false))

        removeView(editText)
        mtf_editTextLayout.addView(editText)
        editText?.onWindowFocusChanged(true)
        editText?.isFocusableInTouchMode = true

        mtf_label.apply {
            pivotX = 0f
            pivotY = 0f
            text = editText?.hint ?: ""
            editText?.hint = ""
        }

        mtf_card.apply {
            if (backgroundColar != -1) {
                setBackgroundColor(backgroundColar)
            }
            val expandedHeight = resources.getDimensionPixelOffset(R.dimen.mtf_cardHeight_final)
            val reducedHeight = cardCollapsedHeight
            reducedScale = (reducedHeight * 1.0 / expandedHeight).toFloat()
            scaleY = reducedScale
            pivotY = expandedHeight.toFloat()
        }


        mtf_image.apply {
            alpha = 0f
            scaleX = 0.4f
            scaleY = 0.4f
        }

        editText?.apply {
            alpha = 0f
            setBackgroundColor(Color.TRANSPARENT)
            setOnFocusChangeListener { view, hasFocus ->
                this@MaterialTextField.hasFocus = hasFocus
            }
        }


        labelTopMargin = FrameLayout.LayoutParams::class.java.cast(mtf_label.layoutParams).topMargin

        customizeFromAttributes()

        this.setOnClickListener { toggle() }
        hasFocus = hasFocus()
    }

    protected fun customizeFromAttributes() {
        if (labelColor != -1) {
            mtf_label.setTextColor(labelColor)
        }

        if (imageDrawableId != -1) {
            mtf_image.setImageDrawable(ContextCompat.getDrawable(context, imageDrawableId))
        }
    }

}