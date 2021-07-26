/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.snackBar

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.TextUtils
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.accessibility.AccessibilityEventCompat
import com.handlService.app.R


/**
 * View that provides quick feedback about an operation in a small popup at the base of the screen
 */
class Snackbar private constructor(context: Context) : SnackbarLayout(context) {

    private val mUndefinedColor = -10000
    private val mUndefinedDrawable = -10000

    var type = SnackbarType.SINGLE_LINE
        private set
    private var mDuration = SnackbarDuration.LENGTH_LONG
    var text: CharSequence? = null
        private set
    private var snackbarText: TextView? = null
    private var snackbarAction: TextView? = null
    var color = mUndefinedColor
        private set
    var textColor = mUndefinedColor
        private set

    /**
     * bottom of the [Activity].
     */
    var offset: Int = 0
        private set
    private var mLineColor: Int? = null
    private var mPhonePosition = SnackbarPosition.BOTTOM
    private var mWidePosition = SnackbarPosition.BOTTOM_CENTER
    private var mDrawable = mUndefinedDrawable
    private var mMarginTop = 0
    private var mMarginBottom = 0
    private var mMarginLeft = 0
    private var mMarginRight = 0
    private var mSnackbarStart: Long = 0
    private var mSnackbarFinish: Long = 0
    private var mTimeRemaining: Long = -1
    var actionLabel: CharSequence? = null
        private set
    var actionColor = mUndefinedColor
        private set
    var isShowAnimated = true
        private set
    var isDismissAnimated = true
        private set
    private var mIsReplacePending = false
    private var mIsShowingByReplace = false
    private var mCustomDuration: Long = -1
    private var mActionClickListener: ActionClickListener? = null
    private var mActionSwipeListener: ActionSwipeListener? = null
    private var mShouldAllowMultipleActionClicks: Boolean = false

    /**
     * @return whether the action button has been clicked. In other words, this method will let
     * you know if {
     * was called. This is useful, for instance, if you want to know during
     */
    var isActionClicked: Boolean = false
        private set
    private var mShouldDismissOnActionClicked = true
    private var mEventListener: EventListener? = null
    private var mTextTypeface: Typeface? = null
    private var mActionTypeface: Typeface? = null

    /**
     */
    var isShowing = false
        private set
    private var mCanSwipeToDismiss = true

    /**
     * @return true if this  is dismissing.
     */
    var isDimissing = false
        private set
    private val mWindowInsets = Rect()
    private val mDisplayFrame = Rect()
    private val mDisplaySize = Point()
    private val mRealDisplaySize = Point()
    private var mTargetActivity: Activity? = null
    private var mMaxWidthPercentage: Float? = null
    private var mUsePhoneLayout: Boolean = false
    private val mDismissRunnable = Runnable { dismiss() }
    private val mRefreshLayoutParamsMarginsRunnable = Runnable { refreshLayoutParamsMargins() }

    private val isIndefiniteDuration: Boolean
        get() = duration == SnackbarDuration.LENGTH_INDEFINITE.duration

    val lineColor: Int
        get() = mLineColor!!

    val duration: Long
        get() = if (mCustomDuration.toInt() == -1) mDuration.duration else mCustomDuration

    /**
     * @return true only if both dismiss and show animations are enabled
     */
    val isAnimated: Boolean
        get() = isShowAnimated && isDismissAnimated

    /**
     * @return false if this  has been dismissed
     */
    val isDismissed: Boolean
        get() = !isShowing

    enum class SnackbarDuration private constructor(val duration: Long) {
        LENGTH_SHORT(2000), LENGTH_LONG(3500), LENGTH_INDEFINITE(-1)
    }

    enum class SnackbarPosition private constructor(val layoutGravity: Int) {
        TOP(Gravity.TOP), BOTTOM(Gravity.BOTTOM), BOTTOM_CENTER(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
    }

    init {

        // inject helper view to use onWindowSystemUiVisibilityChangedCompat() event
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            addView(SnackbarHelperChildViewJB(getContext()))
        }
    }

    /**
     * Sets the type of [Snackbar] to be displayed.
     *
     * @param type the [SnackbarType] of this instance
     * @return
     */
    fun type(type: SnackbarType): Snackbar {
        this.type = type
        return this
    }

    /**
     * Sets the text to be displayed in this [Snackbar]
     *
     * @param text
     * @return
     */
    fun text(text: CharSequence): Snackbar {
        this.text = text
        if (snackbarText != null) {
            snackbarText!!.text = this.text
        }
        return this
    }

    /**
     * Sets the text to be displayed in this [Snackbar]
     *
     * @param resId
     * @return
     */
    fun text(@StringRes resId: Int): Snackbar {
        return text(context.getText(resId))
    }

    /**
     * Sets the background color of this [Snackbar]
     *
     * @param color
     * @return
     */
    fun color(color: Int): Snackbar {
        this.color = color
        return this
    }

    /**
     * Sets the background color of this [Snackbar]
     *
     * @param resId
     * @return
     */
    fun colorResource(@ColorRes resId: Int): Snackbar {
        return color(resources.getColor(resId))
    }

    /**
     * Sets the background drawable of this [Snackbar]
     *
     * @param resId
     * @return
     */
    fun backgroundDrawable(@DrawableRes resId: Int): Snackbar {
        mDrawable = resId
        return this
    }

    /**
     * Sets the text color of this [Snackbar]
     *
     * @param textColor
     * @return
     */
    fun textColor(textColor: Int): Snackbar {
        this.textColor = textColor
        return this
    }

    /**
     * Sets the text color of this [Snackbar]
     *
     * @param resId
     * @return
     */
    fun textColorResource(@ColorRes resId: Int): Snackbar {
        return textColor(resources.getColor(resId))
    }

    /**
     * Sets the text color of this [Snackbar]'s top line, or null for none
     *
     * @param lineColor
     * @return
     */
    fun lineColor(lineColor: Int?): Snackbar {
        mLineColor = lineColor
        return this
    }

    /**
     * Sets the text color of this [Snackbar]'s top line
     *
     * @param resId
     * @return
     */
    fun lineColorResource(@ColorRes resId: Int): Snackbar {
        return lineColor(resources.getColor(resId))
    }

    /**
     * Sets the action label to be displayed, if any. Note that if this is not set, the action
     * button will not be displayed
     *
     * @param actionButtonLabel
     * @return
     */
    fun actionLabel(actionButtonLabel: CharSequence): Snackbar {
        actionLabel = actionButtonLabel
        if (snackbarAction != null) {
            snackbarAction!!.text = actionLabel
        }
        return this
    }

    /**
     * Sets the action label to be displayed, if any. Note that if this is not set, the action
     * button will not be displayed
     *
     * @param resId
     * @return
     */
    fun actionLabel(@StringRes resId: Int): Snackbar {
        return actionLabel(context.getString(resId))
    }

    /**
     * Set the position of the [Snackbar]. Note that if this is not set, the default is to
     * show the snackbar to the bottom of the screen.
     *
     * @param position
     * @return
     */
    fun position(position: SnackbarPosition): Snackbar {
        mPhonePosition = position
        return this
    }

    /**
     * Set the position for wide screen (tablets | desktop) of the [Snackbar]. Note that if this is not set, the default is to
     * show the snackbar to the bottom | center of the screen.
     *
     * @param position A
     * @return A [Snackbar] instance to make changing
     */
    fun widePosition(position: SnackbarPosition): Snackbar {
        mWidePosition = position
        return this
    }

    /**
     * Sets all the margins of the [Snackbar] to the same value, in pixels
     *
     * @param margin
     * @return
     */
    fun margin(margin: Int): Snackbar {
        return margin(margin, margin, margin, margin)
    }

    /**
     * Sets the margins of the [Snackbar] in pixels such that the left and right are equal, and the top and bottom are equal
     *
     * @param marginLR
     * @param marginTB
     * @return
     */
    fun margin(marginLR: Int, marginTB: Int): Snackbar {
        return margin(marginLR, marginTB, marginLR, marginTB)
    }

    /**
     * Sets all the margin of the [Snackbar] individually, in pixels
     *
     * @param marginLeft
     * @param marginTop
     * @param marginRight
     * @param marginBottom
     * @return
     */
    fun margin(marginLeft: Int, marginTop: Int, marginRight: Int, marginBottom: Int): Snackbar {
        mMarginLeft = marginLeft
        mMarginTop = marginTop
        mMarginBottom = marginBottom
        mMarginRight = marginRight

        return this
    }

    /**
     * Sets the color of the action button label. Note that you must set a button label with
     * [Snackbar.actionLabel] for this button to be displayed
     *
     * @param actionColor
     * @return
     */
    fun actionColor(actionColor: Int): Snackbar {
        this.actionColor = actionColor
        return this
    }

    /**
     * Sets the color of the action button label. Note that you must set a button label with
     * [Snackbar.actionLabel] for this button to be displayed
     *
     * @param resId
     * @return
     */
    fun actionColorResource(@ColorRes resId: Int): Snackbar {
        return actionColor(resources.getColor(resId))
    }

    /**
     * Determines whether this [Snackbar] should dismiss when the action button is touched
     *
     * @param shouldDismiss
     * @return
     */
    fun dismissOnActionClicked(shouldDismiss: Boolean): Snackbar {
        mShouldDismissOnActionClicked = shouldDismiss
        return this
    }

    /**
     * Sets the listener to be called when the [Snackbar] action is
     * selected. Note that you must set a button label with
     * [Snackbar.actionLabel] for this button to be displayed
     *
     * @param listener
     * @return
     */
    fun actionListener(listener: ActionClickListener): Snackbar {
        mActionClickListener = listener
        return this
    }


    /**
     * Sets the listener to be called when the [Snackbar] is dismissed by swipe.
     *
     * @param listener
     * @return
     */
    fun swipeListener(listener: ActionSwipeListener): Snackbar {
        mActionSwipeListener = listener
        return this
    }

    /**
     * Determines whether this [Snackbar] should allow the action button to be
     * clicked multiple times
     *
     * @param shouldAllow
     * @return
     */
    fun allowMultipleActionClicks(shouldAllow: Boolean): Snackbar {

        mShouldAllowMultipleActionClicks = shouldAllow
        return this
    }

    /**
     * Sets the listener to be called when the [Snackbar] is dismissed.
     *
     * @param listener
     * @return
     */
    fun eventListener(listener: EventListener): Snackbar {
        mEventListener = listener
        return this
    }

    /**
     * Sets on/off both show and dismiss animations for this [Snackbar]
     *
     * @param withAnimation
     * @return
     */
    fun animation(withAnimation: Boolean): Snackbar {
        isShowAnimated = withAnimation
        isDismissAnimated = withAnimation
        return this
    }

    /**
     * Sets on/off show animation for this [Snackbar]
     *
     * @param withAnimation
     * @return
     */
    fun showAnimation(withAnimation: Boolean): Snackbar {
        isShowAnimated = withAnimation
        return this
    }

    /**
     * Sets on/off dismiss animation for this [Snackbar]
     *
     * @param withAnimation
     * @return
     */
    fun dismissAnimation(withAnimation: Boolean): Snackbar {
        isDismissAnimated = withAnimation
        return this
    }

    /**
     * Determines whether this  can be swiped off from the screen
     *
     * @param canSwipeToDismiss
     * @return
     */
    fun swipeToDismiss(canSwipeToDismiss: Boolean): Snackbar {
        mCanSwipeToDismiss = canSwipeToDismiss
        return this
    }

    /**
     * Sets the duration of this [Snackbar]. See
     * [Snackbar.SnackbarDuration] for available options
     *
     * @param duration
     * @return
     */
    fun duration(duration: SnackbarDuration): Snackbar {
        mDuration = duration
        return this
    }

    /**
     * Sets a custom duration of this [Snackbar]
     *
     * @param duration custom duration. Value must be greater than 0 or it will be ignored
     * @return
     */
    fun duration(duration: Long): Snackbar {
        mCustomDuration = if (duration > 0) {
            duration
        } else {
            mCustomDuration
        }
        return this
    }

    /**
     * Attaches this [Snackbar] to an AbsListView (ListView, GridView, ExpandableListView) so
     * it dismisses when the list is scrolled
     *
     * @param absListView
     * @return
     */
    fun attachToAbsListView(absListView: AbsListView): Snackbar {
        absListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                dismiss()
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int,
                                  totalItemCount: Int) {
            }
        })

        return this
    }

    /**
     * Attaches this [Snackbar] to a RecyclerView so it dismisses when the list is scrolled
     *
     * @param recyclerView The RecyclerView instance to attach to.
     * @return
     */
    fun attachToRecyclerView(recyclerView: View): Snackbar {

        try {
            Class.forName("android.support.v7.widget.RecyclerView")

            // We got here, so now we can safely check
            RecyclerUtil.setScrollListener(this, recyclerView)
        } catch (ignored: ClassNotFoundException) {
            throw IllegalArgumentException("RecyclerView not found. Did you add it to your dependencies?")
        }

        return this
    }

    /**
     * Use a custom typeface for this Snackbar's text
     *
     * @param typeface
     * @return
     */
    fun textTypeface(typeface: Typeface): Snackbar {
        mTextTypeface = typeface
        return this
    }

    /**
     * Use a custom typeface for this Snackbar's action label
     *
     * @param typeface
     * @return
     */
    fun actionLabelTypeface(typeface: Typeface): Snackbar {
        mActionTypeface = typeface
        return this
    }

    private fun init(context: Context, targetActivity: Activity?, parent: ViewGroup, usePhoneLayout: Boolean): ViewGroup.MarginLayoutParams {
        val layout = LayoutInflater.from(context)
                .inflate(R.layout.sb__template, this, true) as SnackbarLayout
        layout.orientation = LinearLayout.VERTICAL

        val res = resources
        color = if (color != mUndefinedColor) {
            color
        } else {
            res.getColor(R.color.sb__background)
        }
        offset = res.getDimensionPixelOffset(R.dimen.sb__offset)
        mUsePhoneLayout = usePhoneLayout
        val scale = res.displayMetrics.density

        val divider = layout.findViewById<View>(R.id.sb__divider)

        val params: ViewGroup.MarginLayoutParams
        if (mUsePhoneLayout) {
            // Phone
            layout.minimumHeight = dpToPx(type.minHeight, scale)
            layout.setMaxHeight(dpToPx(type.maxHeight, scale))
            layout.setBackgroundColor(color)
            params = createMarginLayoutParams(
                    parent, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, mPhonePosition)

            if (mLineColor != null) {
                divider.setBackgroundColor(mLineColor!!)
            } else {
                divider.visibility = View.GONE
            }
        } else {
            // Tablet/desktop
            type = SnackbarType.SINGLE_LINE // Force single-line
            layout.minimumWidth = res.getDimensionPixelSize(R.dimen._280dp)
            layout.setMaxWidth(if (mMaxWidthPercentage == null) {
                res.getDimensionPixelSize(R.dimen.sb__max_width)
            } else {
                DisplayCompat.getWidthFromPercentage(targetActivity!!, mMaxWidthPercentage)
            })
            layout.setBackgroundResource(R.drawable.sb__bg)
            val bg = layout.background as GradientDrawable
            bg.setColor(color)

            params = createMarginLayoutParams(
                    parent, FrameLayout.LayoutParams.MATCH_PARENT, dpToPx(type.maxHeight, scale), mWidePosition)

            if (mLineColor != null) {
                divider.setBackgroundResource(R.drawable.sb__divider_bg)
                val dbg = divider.background as GradientDrawable
                dbg.setColor(mLineColor!!)
            } else {
                divider.visibility = View.GONE
            }
        }

        if (mDrawable != mUndefinedDrawable) {
            setBackgroundDrawable(layout, res.getDrawable(mDrawable))
        }
        snackbarText = layout.findViewById<View>(R.id.sb__text) as TextView
        snackbarText!!.text = text
        snackbarText!!.typeface = mTextTypeface

        if (textColor != mUndefinedColor) {
            snackbarText!!.setTextColor(textColor)
        }

        snackbarText!!.maxLines = type.maxLines

        snackbarAction = layout.findViewById<View>(R.id.sb__action) as TextView
        if (!TextUtils.isEmpty(actionLabel)) {
            requestLayout()
            snackbarAction!!.text = actionLabel
            snackbarAction!!.typeface = mActionTypeface

            if (actionColor != mUndefinedColor) {
                snackbarAction!!.setTextColor(actionColor)
            }

            snackbarAction!!.setOnClickListener {
                if (mActionClickListener != null) {

                    // Before calling the onActionClicked() callback, make sure:
                    // 1) The snackbar is not dismissing
                    // 2) If we aren't allowing multiple clicks, that this is the first click
                    if (!isDimissing && (!isActionClicked || mShouldAllowMultipleActionClicks)) {

                        mActionClickListener!!.onActionClicked(this@Snackbar)
                        isActionClicked = true
                    }
                }
                if (mShouldDismissOnActionClicked) {
                    dismiss()
                }
            }
            snackbarAction!!.maxLines = type.maxLines
        } else {
            snackbarAction!!.visibility = View.GONE
        }

        val inner = layout.findViewById<View>(R.id.sb__inner)
        inner.isClickable = true

        if (mCanSwipeToDismiss && res.getBoolean(R.bool.sb__is_swipeable)) {
            inner.setOnTouchListener(SwipeDismissTouchListener(this, "",
                    object : SwipeDismissTouchListener.DismissCallbacks {
                        override fun canDismiss(token: Any): Boolean {
                            return true
                        }

                        override fun onDismiss(view: View, token: Any) {
                            if (mActionSwipeListener != null) {
                                mActionSwipeListener!!.onSwipeToDismiss()
                            }
                            dismiss(false)
                        }

                        override fun pauseTimer(shouldPause: Boolean) {
                            if (isIndefiniteDuration) {
                                return
                            }
                            if (shouldPause) {
                                removeCallbacks(mDismissRunnable)

                                mSnackbarFinish = System.currentTimeMillis()
                            } else {
                                mTimeRemaining -= mSnackbarFinish - mSnackbarStart

                                startTimer(mTimeRemaining)
                            }
                        }
                    }))
        }

        return params
    }


    private fun updateWindowInsets(targetActivity: Activity?, outInsets: Rect) {
        outInsets.bottom = 0
        outInsets.right = outInsets.bottom
        outInsets.top = outInsets.right
        outInsets.left = outInsets.top

        if (targetActivity == null) {
            return
        }

        val decorView = targetActivity.window.decorView as ViewGroup
        val display = targetActivity.windowManager.defaultDisplay

        val isTranslucent = isNavigationBarTranslucent(targetActivity)
        val isHidden = isNavigationBarHidden(decorView)

        val dispFrame = mDisplayFrame
        val realDispSize = mRealDisplaySize
        val dispSize = mDisplaySize

        decorView.getWindowVisibleDisplayFrame(dispFrame)

        DisplayCompat.getRealSize(display, realDispSize)
        DisplayCompat.getSize(display, dispSize)

        if (dispSize.x < realDispSize.x) {
            // navigation bar is placed on right side of the screen
            if (isTranslucent || isHidden) {
                val navBarWidth = realDispSize.x - dispSize.x
                val overlapWidth = realDispSize.x - dispFrame.right
                outInsets.right = Math.max(Math.min(navBarWidth, overlapWidth), 0)
            }
        } else if (dispSize.y < realDispSize.y) {
            // navigation bar is placed on bottom side of the screen

            if (isTranslucent || isHidden) {
                val navBarHeight = realDispSize.y - dispSize.y
                val overlapHeight = realDispSize.y - dispFrame.bottom
                outInsets.bottom = Math.max(Math.min(navBarHeight, overlapHeight), 0)
            }
        }
    }

    fun showByReplace(targetActivity: Activity) {
        mIsShowingByReplace = true
        show(targetActivity)
    }

    fun showByReplace(parent: ViewGroup) {
        mIsShowingByReplace = true
        show(parent, shouldUsePhoneLayout(parent.context))
    }

    fun showByReplace(parent: ViewGroup, usePhoneLayout: Boolean) {
        mIsShowingByReplace = true
        show(parent, usePhoneLayout)
    }

    /**
     * Displays the [Snackbar] at the bottom of the
     * [Activity] provided.
     *
     * @param targetActivity
     */
    fun show(targetActivity: Activity) {
        val root = targetActivity.findViewById<View>(android.R.id.content) as ViewGroup
        val usePhoneLayout = shouldUsePhoneLayout(targetActivity)
        val params = init(targetActivity, targetActivity, root, usePhoneLayout)
        updateLayoutParamsMargins(targetActivity, params)
        showInternal(targetActivity, params, root)
    }

    /**
     * Displays the [Snackbar] at the bottom of the
     * [ViewGroup] provided.
     *
     * @param parent
     * @param usePhoneLayout
     */
    @JvmOverloads
    fun show(parent: ViewGroup, usePhoneLayout: Boolean = shouldUsePhoneLayout(parent.context)) {
        val params = init(parent.context, null, parent, usePhoneLayout)
        updateLayoutParamsMargins(null, params)
        showInternal(null, params, parent)
    }

    fun maxWidthPercentage(maxWidthPercentage: Float): Snackbar {
        mMaxWidthPercentage = maxWidthPercentage
        return this
    }

    private fun showInternal(targetActivity: Activity?, params: ViewGroup.MarginLayoutParams, parent: ViewGroup) {
        parent.removeView(this)

        // We need to make sure the Snackbar elevation is at least as high as
        // any other child views, or it will be displayed underneath them
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (i in 0 until parent.childCount) {
                val otherChild = parent.getChildAt(i)
                val elvation = otherChild.elevation
                if (elvation > elevation) {
                    elevation = elvation
                }
            }
        }
        parent.addView(this, params)

        bringToFront()

        // As requested in the documentation for bringToFront()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            parent.requestLayout()
            parent.invalidate()
        }

        isShowing = true
        mTargetActivity = targetActivity

        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                viewTreeObserver.removeOnPreDrawListener(this)
                if (mEventListener != null) {
                    if (mIsShowingByReplace) {
                        mEventListener!!.onShowByReplace(this@Snackbar)
                    } else {
                        mEventListener!!.onShow(this@Snackbar)
                    }
                    if (!isShowAnimated) {
                        mEventListener!!.onShown(this@Snackbar)
                        mIsShowingByReplace = false // reset flag
                    }
                }
                return true
            }
        })

        if (!isShowAnimated) {
            if (shouldStartTimer()) {
                startTimer()
            }
            return
        }

        val slideIn = AnimationUtils.loadAnimation(context, getInAnimationResource(mPhonePosition))
        slideIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                if (mEventListener != null) {
                    mEventListener!!.onShown(this@Snackbar)
                    mIsShowingByReplace = false // reset flag
                }

                focusForAccessibility(snackbarText)

                post {
                    mSnackbarStart = System.currentTimeMillis()

                    if (mTimeRemaining.toInt() == -1) {
                        mTimeRemaining = duration
                    }
                    if (shouldStartTimer()) {
                        startTimer()
                    }
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        startAnimation(slideIn)
    }

    private fun focusForAccessibility(view: View?) {
        val event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_VIEW_FOCUSED)

        AccessibilityEventCompat.asRecord(event).setSource(view)
        try {
            view!!.sendAccessibilityEventUnchecked(event)
        } catch (e: IllegalStateException) {
            // accessibility is off.
        }

    }

    private fun shouldStartTimer(): Boolean {
        return !isIndefiniteDuration
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun isNavigationBarHidden(root: ViewGroup): Boolean {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return false
        }

        val viewFlags = root.windowSystemUiVisibility
        return viewFlags and View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION == View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

    private fun isNavigationBarTranslucent(targetActivity: Activity): Boolean {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return false
        }

        val flags = targetActivity.window.attributes.flags
        return flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION != 0
    }

    private fun startTimer() {
        postDelayed(mDismissRunnable, duration)
    }

    private fun startTimer(duration: Long) {
        postDelayed(mDismissRunnable, duration)
    }

    fun dismissByReplace() {
        mIsReplacePending = true
        dismiss()
    }

    fun dismiss() {
        dismiss(isDismissAnimated)
    }

    private fun dismiss(animate: Boolean) {
        if (isDimissing) {
            return
        }

        isDimissing = true

        if (mEventListener != null && isShowing) {
            if (mIsReplacePending) {
                mEventListener!!.onDismissByReplace(this@Snackbar)
            } else {
                mEventListener!!.onDismiss(this@Snackbar)
            }
        }

        if (!animate) {
            finish()
            return
        }

        val slideOut = AnimationUtils.loadAnimation(context, getOutAnimationResource(mPhonePosition))
        slideOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                post { finish() }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        startAnimation(slideOut)
    }

    private fun finish() {
        clearAnimation()
        val parent = parent as ViewGroup
        parent?.removeView(this)
        if (mEventListener != null && isShowing) {
            mEventListener!!.onDismissed(this)
        }
        isShowing = false
        isDimissing = false
        mIsReplacePending = false
        mTargetActivity = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        isShowing = false

        if (mDismissRunnable != null) {
            removeCallbacks(mDismissRunnable)
        }
        if (mRefreshLayoutParamsMarginsRunnable != null) {
            removeCallbacks(mRefreshLayoutParamsMarginsRunnable)
        }
    }

    internal fun dispatchOnWindowSystemUiVisibilityChangedCompat(visible: Int) {
        onWindowSystemUiVisibilityChangedCompat(visible)
    }

    protected fun onWindowSystemUiVisibilityChangedCompat(visible: Int) {
        if (mRefreshLayoutParamsMarginsRunnable != null) {
            post(mRefreshLayoutParamsMarginsRunnable)
        }
    }

    protected fun refreshLayoutParamsMargins() {
        if (isDimissing) {
            return
        }

        val parent = parent as ViewGroup ?: return

        val params = layoutParams as ViewGroup.MarginLayoutParams

        updateLayoutParamsMargins(mTargetActivity, params)

        layoutParams = params
    }

    protected fun updateLayoutParamsMargins(targetActivity: Activity?, params: ViewGroup.MarginLayoutParams) {
        if (mUsePhoneLayout) {
            // Phone
            params.topMargin = mMarginTop
            params.rightMargin = mMarginRight
            params.leftMargin = mMarginLeft
            params.bottomMargin = mMarginBottom
        } else {
            // Tablet/desktop
            params.topMargin = mMarginTop
            params.rightMargin = mMarginRight
            params.leftMargin = mMarginLeft + offset
            params.bottomMargin = mMarginBottom + offset
        }

        // Add bottom/right margin when navigation bar is hidden or translucent
        updateWindowInsets(targetActivity, mWindowInsets)

        params.rightMargin += mWindowInsets.right
        params.bottomMargin += mWindowInsets.bottom
    }

    fun shouldDismissOnActionClicked(): Boolean {
        return mShouldDismissOnActionClicked
    }

    companion object {

        fun with(context: Context): Snackbar {
            return Snackbar(context)
        }

        private fun createMarginLayoutParams(viewGroup: ViewGroup, width: Int, height: Int, position: SnackbarPosition): ViewGroup.MarginLayoutParams {
            if (viewGroup is FrameLayout) {
                val params = FrameLayout.LayoutParams(width, height)
                params.gravity = position.layoutGravity
                return params
            } else if (viewGroup is RelativeLayout) {
                val params = RelativeLayout.LayoutParams(width, height)

                if (position == SnackbarPosition.TOP) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
                } else {
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                }

                return params
            } else if (viewGroup is LinearLayout) {
                val params = LinearLayout.LayoutParams(width, height)
                params.gravity = position.layoutGravity
                return params
            } else {
                throw IllegalStateException("Requires FrameLayout or RelativeLayout for the parent of Snackbar")
            }
        }

        internal fun shouldUsePhoneLayout(context: Context?): Boolean {
            return context?.resources?.getBoolean(R.bool.sb__is_phone) ?: true
        }

        private fun dpToPx(dp: Int, scale: Float): Int {
            return (dp * scale + 0.5f).toInt()
        }

        /**
         * @param snackbarPosition
         * @return the animation resource used by this  instance
         * to enter the view
         */
        @AnimRes
        fun getInAnimationResource(snackbarPosition: SnackbarPosition): Int {
            return if (snackbarPosition == SnackbarPosition.TOP) {
                R.anim.sb__top_in
            } else {
                R.anim.sb__bottom_in
            }
        }

        /**
         * @param snackbarPosition
         * @return the animation resource used by this  instance
         * to exit the view
         */
        @AnimRes
        fun getOutAnimationResource(snackbarPosition: SnackbarPosition): Int {
            return if (snackbarPosition == SnackbarPosition.TOP) {
                R.anim.sb__top_out
            } else {
                R.anim.sb__bottom_out
            }
        }

        /**
         * Set a Background Drawable using the appropriate Android version api call
         *
         * @param view
         * @param drawable
         */
        fun setBackgroundDrawable(view: View, drawable: Drawable) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackgroundDrawable(drawable)
            } else {
                view.background = drawable
            }
        }
    }
}
/**
 * Displays the [Snackbar] at the bottom of the
 * [ViewGroup] provided.
 *
 * @param parent
 */
