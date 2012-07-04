/*
 * Copyright (C) 2012 Chris Xinyue Zhao <hasszhao@gmail.com>
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

    public interface OnItemClickListener {
        public void onItemClick( int _location );
    }

    public interface OnItemScrollListener {
        public void onItemScroll( int _location );
    }

    public interface OnItemScrolledListener {
        public void onItemScrolled( int _location );
    }

    public void setOnItemClickListener( LLSlideView.OnItemClickListener _listener ) {
        mOnItemClickListener = _listener;
    }

    public void setOnItemScrollListener( LLSlideView.OnItemScrollListener _onItemScrollListener ) {
        mOnItemScrollListener = _onItemScrollListener;
    }

    public void setOnItemScrolledListener( LLSlideView.OnItemScrolledListener _onItemScrolledListener ) {
        mOnItemScrolledListener = _onItemScrolledListener;
    }

    private void onItemClicked() {
        if( mOnItemClickListener != null && mCount > 0 ) {
            mOnItemClickListener.onItemClick( mCurrentPosition );
        }
    }

    private void onItemScroll() {
        if( mOnItemScrollListener != null ) {
            mOnItemScrollListener.onItemScroll( mCurrentPosition );
        }
    }

    private void onItemScrolled() {
        if( mOnItemScrolledListener != null ) {
            mOnItemScrolledListener.onItemScrolled( mCurrentPosition );
        }
    }

    // -------------------------------------------------------
    // Else Functions
    // -------------------------------------------------------

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
        updateIndicator(   );
    }

    private LLSlideView( Context _context, AttributeSet _attrs, int _defStyle ) {
        super( _context, _attrs, _defStyle );
    }

    private LLSlideView( Context _context, AttributeSet _attrs ) {
        super( _context, _attrs );
    }

    private LLSlideView( Context _context ) {
        super( _context );
    }

    /**
     * Opetmize for ViewPager, if the Gallery is installed on a Page of ViewPager and slidable by finger.
     * 
     * @param _me
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

    public void setImages( List<Bitmap> _bitmaps, int _maxWidth ) {
        if( _bitmaps == null ) {
            LLL.e( "Image source is NULL." );
        }
        else
        {
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
            updateIndicator(   );
            setOnClickListener( this );

            if( !mSwipable ) {
                startAutoRollTimer();
            }
        }
    }

    private void updateIndicator(     ) {
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
                params.gravity=Gravity.CENTER;
                ivDot.setLayoutParams( params );
            }
            mArrowLeftMovingRight.setVisibility( (mCurrentPosition <= 0) ? View.INVISIBLE : View.VISIBLE );
            mArrowMovingLeft.setVisibility( (mCurrentPosition >= mCount - 1) ? View.INVISIBLE : View.VISIBLE );
            mTriggerLeftMovingRight.setVisibility( (mCurrentPosition <= 0) ? View.INVISIBLE : View.VISIBLE );
            mTriggerMovingLeft.setVisibility( (mCurrentPosition >= mCount - 1) ? View.INVISIBLE : View.VISIBLE );
        }
    }

    /**
     * Draw current bitmap on the gallery. {@link Moving} provides an illusion that an item is moving animatedly. Draw 3 bitmap as more as possible.
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
     * Provide an illusion that an item moves animatedly.
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
            updateIndicator( );
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

    private void toNextItem( int _direction ) {
        mDirection = _direction;
        moveItem();
    }

    private void moveItem() {
        if( mCount > 0 ) {
            stopMoving();
            mMoveHandler.post( moving = new Moving() );
        }
    }

    private void stopMoving() {
        if( moving != null ) {
            mMoveHandler.removeCallbacks( moving );
            onItemScrolled();
        }
    }

    public void moveLeft() {
        toNextItem( MINUS );
    }

    public void moveRight() {
        toNextItem( PLUS );
    }

    public void setDotSelected( Drawable _dotSelected ) {
        mDotSelected = _dotSelected;
    }

    public void setDotUnselected( Drawable _dotUnselected ) {
        mDotUnselected = _dotUnselected;
    }

    public void setSwipable( boolean _swipable ) {
        mSwipable = _swipable;
    }

    private void stopAutoRollTimer() {
        if( mAutoRollTimer != null ) {
            mAutoRollTimer.cancel();
            mAutoRollTimer.purge();
            mAutoRollTimer = null;
        }
    }

    private void startAutoRollTimer() {
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
}
