package com.example.prayerapp.widget.expandableLayout

//import android.animation.ValueAnimator
//import android.content.Context
//import android.content.res.Configuration
//import android.os.Bundle
//import android.os.Parcelable
//import android.util.AttributeSet
//import android.view.View
//import android.view.animation.Interpolator
//import android.widget.FrameLayout
//import androidx.interpolator.view.animation.FastOutSlowInInterpolator
//import com.example.prayerapp.R
//import kotlin.math.max
//import kotlin.math.min
//import kotlin.math.roundToInt
//
//class ExpandableLayout : FrameLayout {
//
//    companion object {
//        const val KEY_SUPER_STATE = "super_state"
//        const val KEY_EXPANSION = "expansion"
//        const val HORIZONTAL = 0
//        const val VERTICAL = 1
//        private const val DEFAULT_DURATION = 300
//    }
//
//    private var duration = DEFAULT_DURATION
//    private var parallax = 0f
//    private var expansion = 0f
//    private var orientation = VERTICAL
//    var state = State.COLLAPSED
//    private var interpolator: Interpolator = FastOutSlowInInterpolator()
//    private var animator: ValueAnimator? = null
//    private var listener: OnExpansionUpdateListener? = null
//
//    constructor(context: Context) : super(context)
//    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
//        attrs?.let {
//            val a = context.obtainStyledAttributes(it, R.styleable.ExpandableLayout)
//            duration = a.getInt(R.styleable.ExpandableLayout_el_duration, DEFAULT_DURATION)
//            expansion = if (a.getBoolean(R.styleable.ExpandableLayout_el_expanded, false)) 1f else 0f
//            orientation = a.getInt(R.styleable.ExpandableLayout_android_orientation, VERTICAL)
//            parallax = a.getFloat(R.styleable.ExpandableLayout_el_parallax, 1f)
//            a.recycle()
//            state = if (expansion == 0f) State.COLLAPSED else State.EXPANDED
//            setParallax(parallax)
//        }
//    }
//
//    override fun onSaveInstanceState(): Parcelable {
//        val superState = super.onSaveInstanceState()
//        val bundle = Bundle()
//        expansion = if (isExpanded()) 1f else 0f
//        bundle.putFloat(KEY_EXPANSION, expansion)
//        bundle.putParcelable(KEY_SUPER_STATE, superState)
//        return bundle
//    }
//
//    override fun onRestoreInstanceState(parcelable: Parcelable) {
//        val bundle = parcelable as Bundle
//        expansion = bundle.getFloat(KEY_EXPANSION)
//        state = if (expansion == 1f) State.EXPANDED else State.COLLAPSED
//        val superState = bundle.getParcelable<Parcelable>(KEY_SUPER_STATE)
//        super.onRestoreInstanceState(superState)
//    }
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        val width = measuredWidth
//        val height = measuredHeight
//        val size = if (orientation == HORIZONTAL) width else height
//        visibility = if (expansion == 0f && size == 0) View.GONE else View.VISIBLE
//        val expansionDelta = size - (size * expansion).roundToInt()
//        if (parallax > 0) {
//            val parallaxDelta = expansionDelta * parallax
//            for (i in 0 until childCount) {
//                val child = getChildAt(i)
//                if (orientation == HORIZONTAL) {
//                    val direction = if (layoutDirection == LAYOUT_DIRECTION_RTL) 1 else -1
//                    child.translationX = (direction * parallaxDelta).toFloat()
//                } else {
//                    child.translationY = (-parallaxDelta).toFloat()
//                }
//            }
//        }
//        if (orientation == HORIZONTAL) {
//            setMeasuredDimension(width - expansionDelta, height)
//        } else {
//            setMeasuredDimension(width, height - expansionDelta)
//        }
//    }
//
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        animator?.cancel()
//        super.onConfigurationChanged(newConfig)
//    }
//
//    fun isExpanded(): Boolean {
//        return state == State.EXPANDING || state == State.EXPANDED
//    }
//
//    fun toggle() {
//        toggle(true)
//    }
//
//    fun toggle(animate: Boolean) {
//        if (isExpanded()) {
//            collapse(animate)
//        } else {
//            expand(animate)
//        }
//    }
//
//    fun expand() {
//        expand(true)
//    }
//
//    fun expand(animate: Boolean) {
//        setExpanded(true, animate)
//    }
//
//    fun collapse() {
//        collapse(true)
//    }
//
//    fun collapse(animate: Boolean) {
//        setExpanded(false, animate)
//    }
//
//    fun setExpanded(expand: Boolean) {
//        setExpanded(expand, true)
//    }
//
//    fun setExpanded(expand: Boolean, animate: Boolean) {
//        if (expand == isExpanded()) {
//            return
//        }
//        val targetExpansion = if (expand) 1f else 0f
//        if (animate) {
//            animateSize(targetExpansion)
//        } else {
//            setExpansion(targetExpansion)
//        }
//    }
//
//    fun getDuration(): Int {
//        return duration
//    }
//
//    fun setInterpolator(interpolator: Interpolator) {
//        this.interpolator = interpolator
//    }
//
//    fun setDuration(duration: Int) {
//        this.duration = duration
//    }
//
//    fun getExpansion(): Float {
//        return expansion
//    }
//
//    fun setExpansion(expansion: Float) {
//        if (this.expansion == expansion) {
//            return
//        }
//        val delta = expansion - this.expansion
//        state = when {
//            expansion == 0f -> State.COLLAPSED
//            expansion == 1f -> State.EXPANDED
//            delta < 0 -> State.COLLAPSING
//            else -> State.EXPANDING
//        }
//        visibility = if (state == State.COLLAPSED) View.GONE else View.VISIBLE
//        this.expansion = expansion
//        requestLayout()
//        listener?.onExpansionUpdate(expansion, state)
//    }
//
//    fun getParallax(): Float {
//        return parallax
//    }
//
//    fun setParallax(parallax: Float) {
//        this.parallax = min(1f, max(0f, parallax))
//    }
//
//    fun getOrientation(): Int {
//        return orientation
//    }
//
//    fun setOrientation(orientation: Int) {
//        require(!(orientation < 0 || orientation > 1)) { "Orientation must be either 0 (horizontal) or 1 (vertical)" }
//        this.orientation = orientation
//    }
//
//    fun setOnExpansionUpdateListener(listener: OnExpansionUpdateListener?) {
//        this.listener = listener
//    }
//
//    private fun animateSize(targetExpansion: Float) {
//        animator?.cancel()
//        animator = ValueAnimator.ofFloat(expansion, targetExpansion)
//        animator?.interpolator = interpolator
//        animator?.duration = duration.toLong()
//        animator?.addUpdateListener { valueAnimator -> setExpansion(valueAnimator.animatedValue as Float) }
//        animator?.start()
//    }
//
//    enum class State {
//        EXPANDED, COLLAPSED, EXPANDING, COLLAPSING
//    }
//
//    interface OnExpansionUpdateListener {
//        fun onExpansionUpdate(expansionFraction: Float, state: State)
//    }
//}
