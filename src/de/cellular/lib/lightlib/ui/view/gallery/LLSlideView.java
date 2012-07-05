/**
 * Copyright (C) 2012 Cellular GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cellular.lib.lightlib.ui.view.gallery;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import de.cellular.lib.lightlib.R;
import de.cellular.lib.lightlib.log.LLL;

/**
 * A sliding view draws bitmaps with finger or without finger under control of timer.
 * <p>
 * Client developer can't use the view directly.
 * <p>
 * <strong>Weakness of current versions</strong>
 * <li>{@link LLSlideView} can only show the items whose height is larger than width.</li>
 * <li>Data source should have equal width and height.</li>
 * <p>
 * 
 * @version <strong>1.0.1</strong> <li>Add</li>
 *          <p>
 *          {@link LLSlideView#appendImage(Bitmap, int)}
 *          <p>
 *          {@link LLSlideView#getCurrentPosition()}
 *          <p>
 *          Fixed bug that the single loaded item will disappear.
 * 
 * @version <strong>1.0</strong> <li>just a beginning</li>
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 * 
 */
class LLSlideView extends View implements ComponentCallbacks, OnGestureListener, OnClickListener
{
    private static final int                   PLUS                = 1;
    private static final int                   MINUS               = -1;
    private static final int                   MOVE_UNIT           = 15;
    private static final int                   WIDTH_BETWEEN_ITEMS = 7;

    private View                               mParent;

    // -------------------------------------------------------
    // Swipable with finger
    // -------------------------------------------------------

    private boolean                            mSwipable           = true;
    private GestureDetector                    gestureScanner;
    private boolean                            mShouldFling;

    // -------------------------------------------------------
    // Moving
    // -------------------------------------------------------

    private Handler                            mMoveHandler        = new Handler();
    private Moving                             moving;
    private int                                mDirection          = PLUS;
    private int                                mCurrentPosition;

    // -------------------------------------------------------
    // Auto rolling and moving
    // -------------------------------------------------------
    private Timer                              mAutoRollTimer;
    private int                                mAutoRate;

    // -------------------------------------------------------
    // Draw bitmaps
    // -------------------------------------------------------

    private int                                mCount;
    private DisplayMetrics                     metrics;
    private int                                mMaxWidthOfBitmaps;
    private int                                mLeft               = 0;
    private Paint                              mPaint              = new Paint();
    private Bitmap                             mCurBmp;
    private Bitmap                             mNxtBmp;
    private Bitmap                             mPrevImg;

    private List<Bitmap>                       mBitmaps            = new ArrayList<Bitmap>();

    // -------------------------------------------------------
    // Bottom indicator
    // -------------------------------------------------------

    private boolean                            mCanClickButton     = true;
    private ImageButton                        mArrowLeftMovingRight;
    private Button                             mTriggerLeftMovingRight;
    private ImageButton                        mArrowMovingLeft;
    private Button                             mTriggerMovingLeft;
    private ViewGroup                          mDots;
    private Drawable                           mDotSelected;
    private Drawable                           mDotUnselected;

    // -------------------------------------------------------
    // Events / onEvents
    // -------------------------------------------------------

    private LLSlideView.OnItemClickListener    mOnItemClickListener;
    private LLSlideView.OnItemScrollListener   mOnItemScrollListener;
    private LLSlideView.OnItemScrolledListener mOnItemScrolledListener;

    /**
     * Event when user clicks on the shown items.
     * 
     * @author Chris Xinyue Zhao <hasszhao@gmail.com>
     * @since 1.0
     * 
     */
    public interface OnItemClickListener {
        /**
         * User clicked view.
         * <p>
         * It could be < 0, because user could click view while item was moving.
         * <p>
         * Please check before handling event.
         * 
         * @param _location
         *            the position of major item.
         * @since 1.0
         */
        public void onItemClicked( int _location );
    }

    /**
     * Event when item<strong>s</strong> are moving.
     * 
     * @author Chris Xinyue Zhao <hasszhao@gmail.com>
     * @since 1.0
     * 
     */
    public interface OnItemScrollListener {
        /**
         * Item<strong>s</strong>'re moving.
         * <p>
         * It could be < 0, because the view show the last item and begin to show the first one automatically(swipable==false).
         * <p>
         * Please check before handling event.
         * 
         * @param _location
         *            the position of current item.
         * @since 1.0
         */
        public void onItemScroll( int _location );
    }

    /**
     * Event when the view has stopped moving item<strong>s</strong>.
     * 
     * @author Chris Xinyue Zhao <hasszhao@gmail.com>
     * @since 1.0
     * 
     */
    public interface OnItemScrolledListener {
        /**
         * Item<strong>s</strong>'re stopped moving. Show a major item.
         * <p>
         * It could be < 0, because the view show the last item and begin to show the first one automatically(swipable==false).
         * <p>
         * Please check before handling event.
         * 
         * @param _location
         *            the position of the major item.
         * @since 1.0
         */
        public void onItemScrolled( int _location );
    }

    /**
     * Set listener for click event.
     * 
     * @param _listener
     * @since 1.0
     */
    public void setOnItemClickListener( LLSlideView.OnItemClickListener _listener ) {
        mOnItemClickListener = _listener;
    }

    /**
     * Set listener for scroll event.
     * 
     * @param _listener
     * @since 1.0
     */
    public void setOnItemScrollListener( LLSlideView.OnItemScrollListener _listener ) {
        mOnItemScrollListener = _listener;
    }

    /**
     * Set listener for scrolled event.
     * 
     * @param _listener
     * @since 1.0
     */
    public void setOnItemScrolledListener( LLSlideView.OnItemScrolledListener _listener ) {
        mOnItemScrolledListener = _listener;
    }

    /**
     * Firing event for clicking on the view.
     * 
     * @since 1.0
     */
    private void onItemClicked() {
        if( mOnItemClickListener != null && mCount > 0 ) {
            mOnItemClickListener.onItemClicked( mCurrentPosition );
        }
    }

    /**
     * Firing event while item<strong>s</strong>'re moving.
     * 
     * @since 1.0
     */
    private void onItemScroll() {
        if( mOnItemScrollListener != null ) {
            mOnItemScrollListener.onItemScroll( mCurrentPosition );
        }
    }

    /**
     * Firing event when item<strong>s</strong>'re stopped.
     * 
     * @since 1.0
     */
    private void onItemScrolled() {
        if( mOnItemScrolledListener != null ) {
            mOnItemScrolledListener.onItemScrolled( mCurrentPosition );
        }
    }

    // -------------------------------------------------------
    // Else Functions
    // -------------------------------------------------------

    /**
     * Creating a sliding view.
     * 
     * @param _cxt
     *            Context
     * @param _parent
     *            View(group) that contains the sliding view.
     * @param isSwipable
     *            to decide whether the internal timer should be started to show all items automatically.
     * @since 1.0
     */
    public LLSlideView( Context _cxt, View _parent, boolean isSwipable ) {
        this( _cxt );

        metrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService( Context.WINDOW_SERVICE )).getDefaultDisplay().getMetrics(
                metrics );
        mSwipable = isSwipable;
        if( mSwipable ) {
            gestureScanner = new GestureDetector( this );
        }

        mDots = (ViewGroup) _parent.findViewById( R.id.ll_gallery_indicator_dots );

        mArrowLeftMovingRight = (ImageButton) _parent.findViewById( R.id.ll_gallery_arrow_left );
        mArrowLeftMovingRight.setOnClickListener( this );
        mArrowMovingLeft = (ImageButton) _parent.findViewById( R.id.ll_gallery_arrow_right );
        mArrowMovingLeft.setOnClickListener( this );

        mTriggerLeftMovingRight = (Button) _parent.findViewById( R.id.ll_gallery_trigger_left );
        mTriggerLeftMovingRight.setOnClickListener( this );
        mTriggerMovingLeft = (Button) _parent.findViewById( R.id.ll_gallery_trigger_right );
        mTriggerMovingLeft.setOnClickListener( this );

        mParent = _parent;
        mCurrentPosition = 0;
        updateIndicator();
    }

    /**
     * @deprecated The view can't be created in XML or used directly.
     * @param _context
     * @param _attrs
     * @param _defStyle
     * @since 1.0
     */
    private LLSlideView( Context _context, AttributeSet _attrs, int _defStyle ) {
        super( _context, _attrs, _defStyle );
    }

    /**
     * @deprecated The view can't be created in XML or used directly.
     * @param _context
     * @param _attrs
     * @since 1.0
     */
    private LLSlideView( Context _context, AttributeSet _attrs ) {
        super( _context, _attrs );
    }

    /**
     * @deprecated the view can't be created in xml or used directly.
     * @param _context
     * @since 1.0
     */
    private LLSlideView( Context _context ) {
        super( _context );
    }

    /**
     * Optimize for ViewPager.
     * <p>
     * Let the view be sliding when it's shown on a viewpager.
     * 
     * @param _me
     *            MotionEvent
     * @since 1.0
     */
    private void requestDisallowParentInterceptTouchEvent( MotionEvent _me ) {
        switch( _me.getAction() )
        {
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent( true );
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent( false );
            break;
        }
    }

    @Override
    public boolean onTouchEvent( MotionEvent _me ) {
        if( mSwipable ) {
            requestDisallowParentInterceptTouchEvent( _me );
            if( !mShouldFling && _me.getAction() == MotionEvent.ACTION_UP ) {
                moveItem();
            }
            mShouldFling = false;
            return gestureScanner.onTouchEvent( _me );
        }
        else {
            return super.onTouchEvent( _me );
        }
    }

    @Override
    public boolean onScroll(
            MotionEvent _e1,
            MotionEvent _e2,
            float _distanceX,
            float _distanceY ) {
        mLeft = (int) (_e2.getX() - _e1.getX());
        drawItem();
        return true;
    }

    /**
     * Make view invalidate and the onDraw will be callbaced.
     * <p>
     * It fires {@link LLSlideView#onItemScroll()}.
     * 
     * @see {@link LLSlideView#onItemScroll()}.
     * @since 1.0
     */
    private void drawItem() {
        onItemScroll();
        invalidate();
    }

    @Override
    public boolean onDown( MotionEvent e ) {
        return true;
    }

    @Override
    public void onSizeChanged( int _w, int _h, int _oldw, int _oldh ) {
        super.onSizeChanged( _w, _h, _oldw, _oldh );
    }

    @Override
    public boolean onFling(
            MotionEvent _e1,
            MotionEvent _e2,
            float _velocityX,
            float _velocityY ) {
        mShouldFling = true;
        mDirection = (_velocityX < 0) ? PLUS : MINUS;
        moveItem();
        return false;
    }

    @Override
    public void onLongPress( MotionEvent e ) {
    }

    @Override
    public void onShowPress( MotionEvent e ) {
    }

    @Override
    public boolean onSingleTapUp( MotionEvent e ) {
        if( mSwipable ) {
            onItemClicked();
        }
        return true;
    }

    /**
     * Set item source.
     * 
     * @param _bitmaps
     *            data source
     * @param _maxWidth
     *            max-width that each item can be shown.
     * @since 1.0
     */
    public void setImages( List<Bitmap> _bitmaps, int _maxWidth ) {
        if( _bitmaps == null ) {
            LLL.e( "Bitmap source is NULL." );
        }
        else {
            mBitmaps = _bitmaps;
            mMaxWidthOfBitmaps = _maxWidth;
            mCount = mBitmaps.size();
            mCurrentPosition = 0;

            if( mCount > 0 )
                mCurBmp = mBitmaps.get( mCurrentPosition );
            if( mCount > 1 )
                mNxtBmp = mBitmaps.get( mCurrentPosition + 1 );

            mParent.findViewById( R.id.ll_gallery_pb ).setVisibility( View.GONE );
            invalidate();
            updateIndicator();
            setOnClickListener( this );

            if( !mSwipable ) {
                startAutoRollTimer();
            }
        }
    }

    /**
     * Append an item
     * 
     * @param _bitmap
     *            data source
     * @param _maxWidth
     *            max width
     * @since 1.0.1
     */
    public void appendImage( Bitmap _bitmap, int _maxWidth ) {
        if( _bitmap == null ) {
            LLL.e( "Bitmap source is NULL." );
        }
        else {
            mParent.findViewById( R.id.ll_gallery_pb ).setVisibility( View.GONE );
            mMaxWidthOfBitmaps = _maxWidth;
            mCount++;
            mBitmaps.add( _bitmap );
            mCurrentPosition = mCount - 1;

            if( mCount == 1 ) {
                mCurBmp = mBitmaps.get( mCurrentPosition );
            }
            else {
                mPrevImg = mBitmaps.get( mCurrentPosition - 1 );
                mCurBmp = mBitmaps.get( mCurrentPosition );
            }

            invalidate();
            updateIndicator();
            setOnClickListener( this );
        }
    }

    /**
     * Update the indicator when an item is selected as major.
     * 
     * @since 1.0
     */
    private void updateIndicator() {
        if( mBitmaps != null && mDots != null && mDotUnselected != null && mDotSelected != null ) {
            if( mDots.getChildCount() > 0 )
                mDots.removeAllViews();

            int cnt = mBitmaps.size();
            for( int i = 0; i < cnt; i++ ) {
                ImageView ivDot = new ImageView( getContext() );
                if( mCurrentPosition == i ) {
                    ivDot.setImageDrawable( mDotUnselected );// R.drawable.paginator_hero_inactive );
                }
                else {
                    ivDot.setImageDrawable( mDotSelected );// R.drawable.paginator_hero_active );
                }
                ivDot.setPadding( 0, 0, 5, 0 );
                mDots.addView( ivDot );
                LinearLayout.LayoutParams params = (LayoutParams) ivDot.getLayoutParams();
                params.gravity = Gravity.CENTER;
                ivDot.setLayoutParams( params );
            }
            mArrowLeftMovingRight.setVisibility( (mCurrentPosition <= 0) ? View.INVISIBLE : View.VISIBLE );
            mArrowMovingLeft.setVisibility( (mCurrentPosition >= mCount - 1) ? View.INVISIBLE : View.VISIBLE );
            mTriggerLeftMovingRight.setVisibility( (mCurrentPosition <= 0) ? View.INVISIBLE : View.VISIBLE );
            mTriggerMovingLeft.setVisibility( (mCurrentPosition >= mCount - 1) ? View.INVISIBLE : View.VISIBLE );
        }
    }

    /**
     * Draw item on the view.
     * <p>
     * {@link Moving} provides an illusion that an item is moving animatedly.
     * 
     * @since 1.0
     */
    @Override
    public void onDraw( Canvas c ) {
        super.onDraw( c );
        if( mCurBmp != null ) {
            c.drawBitmap( mCurBmp,
                    mLeft,
                    0,
                    mPaint );
        }
        if( mNxtBmp != null ) {
            c.drawBitmap( mNxtBmp,
                    mLeft + mMaxWidthOfBitmaps + WIDTH_BETWEEN_ITEMS,
                    0,
                    mPaint );
        }
        if( mPrevImg != null ) {
            c.drawBitmap( mPrevImg,
                    mLeft - mMaxWidthOfBitmaps - WIDTH_BETWEEN_ITEMS,
                    0,
                    mPaint );
        }
    }

    /**
     * Helper class that provides an illusion that an item moves animatedly.
     * 
     * @since 1.0
     */
    private class Moving implements Runnable
    {
        @Override
        public void run() {
            int stop = calcBound();

            // ------------------------------------------
            // Begin to draw bitmap
            // ------------------------------------------
            start();

            // -----------------------------------------------------------------
            // Stop animation when the left edge reach the bound of gallery.
            // -----------------------------------------------------------------

            if( isOutOfBound( stop ) ) {
                mLeft = 0;
                if( (mSwipable && mCurrentPosition > MINUS && mCurrentPosition < mBitmaps.size())
                        ||
                        (mCurrentPosition >= -1 && mCurrentPosition <= mBitmaps.size()) ) {
                    calcCurrentPosition();
                }

                // ------------------------------------------
                // End of drawing a bitmap
                // ------------------------------------------
                stop();
            }
            else {
                mMoveHandler.post( this );
            }
        }

        private void calcCurrentPosition() {
            mCurrentPosition += (mDirection == PLUS) ? PLUS : MINUS;
            mCurrentPosition = (mCurrentPosition == -1) ? 0 : mCurrentPosition;
            mCurrentPosition = (mCurrentPosition == mBitmaps.size()) ? mCurrentPosition - 1 : mCurrentPosition;

            mCurBmp = mBitmaps.get( mCurrentPosition );
            mNxtBmp = (mBitmaps.size() > mCurrentPosition + 1) ? mBitmaps.get( mCurrentPosition + 1 ) : null;
            mPrevImg = (mCurrentPosition - 1 > MINUS) ? mBitmaps.get( mCurrentPosition - 1 ) : null;
        }

        private boolean isOutOfBound( int stop ) {
            return (mLeft < -(stop * metrics.density) && mCurrentPosition + 1 != mBitmaps.size())
                    ||
                    (mLeft > (stop * metrics.density) && mCurrentPosition != 0);
        }

        private int calcBound() {
            int stop = 0;
            if( mDirection == PLUS && mCurrentPosition + 1 < mBitmaps.size() ) {
                mLeft -= MOVE_UNIT;
                stop = 320;
            }
            else if( mDirection == PLUS && mCurrentPosition + 1 == mBitmaps.size() ) {
                mLeft += MOVE_UNIT;
                stop = 0;
            }
            else if( mDirection == MINUS && mCurrentPosition != 0 ) {
                mLeft += MOVE_UNIT;
                stop = 320;
            }
            else if( mDirection == MINUS && mCurrentPosition == 0 ) {
                mLeft -= MOVE_UNIT;
                stop = 0;
            }
            return stop;
        }

        private void start() {
            drawItem();
            mCanClickButton = false;
        }

        private void stop() {
            updateIndicator();
            stopMoving();
            mCanClickButton = true;
        }
    }

    @Override
    public void onClick( View _v ) {
        if( _v == this && !mSwipable ) {
            onItemClicked();
        }
        else if( mCanClickButton ) {
            if( _v.getId() == R.id.ll_gallery_arrow_left || _v.getId() == R.id.ll_gallery_trigger_left ) {
                moveLeft();
            }
            else if( _v.getId() == R.id.ll_gallery_arrow_right || _v.getId() == R.id.ll_gallery_trigger_right ) {
                moveRight();
            }
            // switch( _v.getId() )
            // {
            // case R.id.ll_gallery_arrow_left:
            // case R.id.ll_gallery_trigger_left:
            // moveLeft();
            // break;
            // case R.id.ll_gallery_arrow_right:
            // case R.id.ll_gallery_trigger_right:
            // moveRight();
            // break;
            // }
        }
    }

    /**
     * Move item
     * 
     * @param _direction
     *            a moving direction
     * @since 1.0
     */
    private void toNextItem( int _direction ) {
        mDirection = _direction;
        moveItem();
    }

    /**
     * Start moving item after stopping handler first.
     * 
     * @since 1.0
     */
    private void moveItem() {
        if( mCount > 1 ) {
            stopMoving();
            mMoveHandler.post( moving = new Moving() );
        }
    }

    /**
     * Stop handler to move item
     * 
     * @since 1.0
     */
    private void stopMoving() {
        if( moving != null ) {
            mMoveHandler.removeCallbacks( moving );
            onItemScrolled();
        }
    }

    /**
     * Move item left.
     * 
     * @since 1.0
     */
    private void moveLeft() {
        toNextItem( MINUS );
    }

    /**
     * Move item right.
     * 
     * @since 1.0
     */
    private void moveRight() {
        toNextItem( PLUS );
    }

    /**
     * Set "dot" on the indicator for the selected item.
     * 
     * @param _dotSelected
     *            a Drawable object
     * @since 1.0
     */
    public void setDotSelected( Drawable _dotSelected ) {
        mDotSelected = _dotSelected;
    }

    /**
     * Set "dot" on the indicator for the unselected item.
     * 
     * @param _dotUnselected
     * @since 1.0
     */
    public void setDotUnselected( Drawable _dotUnselected ) {
        mDotUnselected = _dotUnselected;
    }

    /**
     * If set true, the view can't be controlled by finger and roll items through timer.
     * 
     * @param _swipable
     *            true->roll item automatically.
     * @since 1.0
     */
    public void setSwipable( boolean _swipable ) {
        mSwipable = _swipable;
    }

    /**
     * Stop timer for moving items automatically.
     * 
     * @since 1.0
     */
    private void stopAutoRollTimer() {
        if( mAutoRollTimer != null ) {
            mAutoRollTimer.cancel();
            mAutoRollTimer.purge();
            mAutoRollTimer = null;
        }
    }

    /**
     * Start timer to control moving items automatically.
     * 
     * @since 1.0
     */
    private void startAutoRollTimer() {
        if( mCount > 1 ) {
            stopAutoRollTimer();
            mAutoRollTimer = new Timer( true );
            mAutoRollTimer.scheduleAtFixedRate( new TimerTask() {
                @Override
                public void run()
                {
                    if( mCurrentPosition == mCount - 1 ) {
                        mCurrentPosition = -1;
                    }
                    moveRight();
                }
            }, mAutoRate, mAutoRate );
        }
    }

    /**
     * Set a rate when the view shows items automatically.
     * 
     * @param _autoRate
     * @since 1.0
     */
    public void setAutoRate( int _autoRate ) {
        mAutoRate = _autoRate;
    }

    @Override
    public void onConfigurationChanged( Configuration _newConfig ) {
        stopMoving();
        stopAutoRollTimer();
    }

    @Override
    public void onLowMemory() {
        stopMoving();
        stopAutoRollTimer();
    }

    /**
     * Get current item's position.
     * 
     * @return the currentPosition
     * @since 1.0.1
     */
    public int getCurrentPosition() {
        return mCurrentPosition;
    }
}
