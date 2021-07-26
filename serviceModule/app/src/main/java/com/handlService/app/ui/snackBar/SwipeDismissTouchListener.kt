/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.snackBar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration

/**
 * A [View.OnTouchListener] that makes any [View] dismissible
 * when the user swipes (drags her finger) horizontally across the view.
 *
 *
 * @author Roman Nurik
 */
class SwipeDismissTouchListener
/**
 * Constructs a new swipe-to-dismiss touch listener for the given view.
 *
 * @param view     The view to make dismissable.
 * @param token    An optional token/cookie object to be passed through to the callback.
 * @param callbacks The callback to trigger when the user has indicated that she would like to
 * dismiss this view.
 */
(// Fixed properties
        private val mContainerView: View, private val mToken: String, private val mCallbacks: DismissCallbacks) : View.OnTouchListener {
    // Cached ViewConfiguration and system-wide constant values
    private val mSlop: Int
    private val mMinFlingVelocity: Int
    private val mMaxFlingVelocity: Int
    private val mAnimationTime: Long
    private var mViewWidth = 1 // 1 and not 0 to prevent dividing by zero

    // Transient properties
    private var mDownX: Float = 0.toFloat()
    private var mDownY: Float = 0.toFloat()
    private var mSwiping: Boolean = false
    private var mSwipingSlop: Int = 0
    private var mVelocityTracker: VelocityTracker? = null
    private var mTranslationX: Float = 0.toFloat()

    /**
     * The callback interface used by [SwipeDismissTouchListener] to inform its client
     * about a successful dismissal of the view for which it was created.
     */
    interface DismissCallbacks {
        /**
         * Called to determine whether the view can be dismissed.
         */
        fun canDismiss(token: Any): Boolean

        /**
         * Called when the user has indicated they she would like to dismiss the view.
         *
         * @param view  The originating [View] to be dismissed.
         * @param token The optional token passed to this object's constructor.
         */
        fun onDismiss(view: View, token: Any)

        /**
         * Called on touch down/up to indicate the [com.nispok.snackbar.Snackbar] dismissal
         * timer should or should not pause when the user is swiping the snack bar away.
         *
         * @param shouldPause Whether or not the dismissal timer should pause or not
         */
        fun pauseTimer(shouldPause: Boolean)
    }

    init {
        val vc = ViewConfiguration.get(mContainerView.context)
        mSlop = vc.scaledTouchSlop
        mMinFlingVelocity = vc.scaledMinimumFlingVelocity * 16
        mMaxFlingVelocity = vc.scaledMaximumFlingVelocity
        mAnimationTime = mContainerView.context.resources.getInteger(
                android.R.integer.config_shortAnimTime).toLong()
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        // offset because the view is translated during swipe
        motionEvent.offsetLocation(mTranslationX, 0f)

        if (mViewWidth < 2) {
            mViewWidth = view.width
        }

        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // TODO: ensure this is a finger, and set a flag
                mDownX = motionEvent.rawX
                mDownY = motionEvent.rawY
                if (mCallbacks.canDismiss(mToken)) {
                    mCallbacks.pauseTimer(true)
                    mVelocityTracker = VelocityTracker.obtain()
                    mVelocityTracker!!.addMovement(motionEvent)
                }
                return false
            }

            MotionEvent.ACTION_UP -> {
                mCallbacks.pauseTimer(false)
                val deltaX = motionEvent.rawX - mDownX
                mVelocityTracker!!.addMovement(motionEvent)
                mVelocityTracker!!.computeCurrentVelocity(1000)
                val velocityX = mVelocityTracker!!.xVelocity
                val absVelocityX = Math.abs(velocityX)
                val absVelocityY = Math.abs(mVelocityTracker!!.yVelocity)
                var dismiss = false
                var dismissRight = false
                if (Math.abs(deltaX) > mViewWidth / 2 && mSwiping) {
                    dismiss = true
                    dismissRight = deltaX > 0
                } else if (mMinFlingVelocity <= absVelocityX && absVelocityX <= mMaxFlingVelocity
                        && absVelocityY < absVelocityX
                        && absVelocityY < absVelocityX && mSwiping) {
                    // dismiss only if flinging in the same direction as dragging
                    dismiss = velocityX < 0 == deltaX < 0
                    dismissRight = mVelocityTracker!!.xVelocity > 0
                }
                if (dismiss) {
                    // dismiss
                    view.animate()
                            .translationX((if (dismissRight) mViewWidth else -mViewWidth).toFloat())
                            .alpha(0f)
                            .setDuration(mAnimationTime)
                            .setListener(null)
                    mContainerView.animate()
                            .alpha(0f)
                            .setDuration(mAnimationTime)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    performDismiss(view)
                                }
                            })
                } else if (mSwiping) {
                    // cancel
                    view.animate()
                            .translationX(0f)
                            .alpha(1f)
                            .setDuration(mAnimationTime)
                            .setListener(null)
                    mContainerView.animate()
                            .alpha(1f).duration = mAnimationTime
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }
                mTranslationX = 0f
                mDownX = 0f
                mDownY = 0f
                mSwiping = false
            }

            MotionEvent.ACTION_CANCEL -> {
                view.animate()
                        .translationX(0f)
                        .alpha(1f)
                        .setDuration(mAnimationTime)
                        .setListener(null)
                mContainerView.animate()
                        .alpha(1f).duration = mAnimationTime
                mVelocityTracker!!.recycle()
                mVelocityTracker = null
                mTranslationX = 0f
                mDownX = 0f
                mDownY = 0f
                mSwiping = false
            }

            MotionEvent.ACTION_MOVE -> {
                mVelocityTracker!!.addMovement(motionEvent)
                val deltaX = motionEvent.rawX - mDownX
                val deltaY = motionEvent.rawY - mDownY
                if (Math.abs(deltaX) > mSlop && Math.abs(deltaY) < Math.abs(deltaX) / 2) {
                    mSwiping = true
                    mSwipingSlop = if (deltaX > 0) mSlop else -mSlop

                    if (view.parent != null) {
                        view.parent.requestDisallowInterceptTouchEvent(true)
                    }

                    // Cancel listview's touch
                    val cancelEvent = MotionEvent.obtain(motionEvent)
                    cancelEvent.action = MotionEvent.ACTION_CANCEL or (motionEvent.actionIndex shl MotionEvent.ACTION_POINTER_INDEX_SHIFT)
                    view.onTouchEvent(cancelEvent)
                    cancelEvent.recycle()
                }

                if (mSwiping) {
                    mTranslationX = deltaX
                    view.translationX = deltaX - mSwipingSlop
                    // TODO: use an ease-out interpolator or such
                    view.alpha = Math.max(0f, Math.min(1f,
                            1f - 2f * Math.abs(deltaX) / mViewWidth))
                    mContainerView.alpha = Math.max(0.2f, Math.min(1f,
                            1f - Math.abs(deltaX) / mViewWidth))
                    return true
                }
            }
        }
        return false
    }

    private fun performDismiss(view: View) {
        mCallbacks.onDismiss(view, mToken)
    }
}