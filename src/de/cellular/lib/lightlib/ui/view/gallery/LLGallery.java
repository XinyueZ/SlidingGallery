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

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import de.cellular.lib.lightlib.R;
import de.cellular.lib.lightlib.log.LLL;
import de.cellular.lib.lightlib.utils.UIUtils;

public class LLGallery extends RelativeLayout implements LLSlideView.OnItemClickListener,
        LLSlideView.OnItemScrolledListener, LLSlideView.OnItemScrollListener
{
    private LLSlideView                      mSlideView;
    private LLGallery.OnItemClickListener    mOnItemClickListener;
    private LLGallery.OnItemScrollListener   mOnItemScrollListener;
    private LLGallery.OnItemScrolledListener mOnItemScrolledListener;
    private String[]                         mComments;
    private int                              mUplift;

    public enum CommentPosition
    {
        TOP,
        BOTTOM
    }

    // -------------------------------------------------------
    // Events / onEvents
    // -------------------------------------------------------

    public interface OnItemClickListener {
        public void onItemClick( int _location );
    }

    public interface OnItemScrollListener {
        public void onItemScroll( int _location );
    }

    public interface OnItemScrolledListener {
        public void onItemScrolled( int _location );
    }

    public LLGallery( Context _context, AttributeSet _attrs ) {
        super( _context, _attrs );

        setBackgroundResource( android.R.color.transparent );

        final TypedArray a = _context.obtainStyledAttributes( _attrs, R.styleable.SlideGallery );

        int pbWidth = (int) a.getDimension( R.styleable.SlideGallery_progressSize,
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        int pbHeight = (int) a.getDimension( R.styleable.SlideGallery_progressSize,
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        Drawable pbSrc = a.getDrawable( R.styleable.SlideGallery_progressSrc );

        int arrowPaddingBottom = (int) a.getDimension( R.styleable.SlideGallery_arrowPaddingBottom, 0 );
        int arrowPaddingLeft = (int) a.getDimension( R.styleable.SlideGallery_arrowPaddingLeft, 0 );
        int arrowPaddingRight = (int) a.getDimension( R.styleable.SlideGallery_arrowPaddingRight, 0 );
        int arrowPaddingTop = (int) a.getDimension( R.styleable.SlideGallery_arrowPaddingTop, 0 );
        Drawable arrowLeftSrc = a.getDrawable( R.styleable.SlideGallery_arrowLeftSrc );
        Drawable arrowRightSrc = a.getDrawable( R.styleable.SlideGallery_arrowRightSrc );

        Drawable triggerLeftSrc = a.getDrawable( R.styleable.SlideGallery_triggerSrc );
        Drawable triggerRightSrc = a.getDrawable( R.styleable.SlideGallery_triggerSrc );
        int triggerWidth = (int) a.getDimension( R.styleable.SlideGallery_triggerWidth, 0 );

        int indicatorHeight = (int) a.getDimension( R.styleable.SlideGallery_indicatorHeight,
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        Drawable indicatorBackground = a.getDrawable( R.styleable.SlideGallery_indicatorBackground );
        Drawable dotSelected = a.getDrawable( R.styleable.SlideGallery_dotSelected );
        Drawable dotUnselected = a.getDrawable( R.styleable.SlideGallery_dotUnselected );
        int marquee = a.getInteger( R.styleable.SlideGallery_marquee, -1 );

        mUplift = (int) a.getDimension( R.styleable.SlideGallery_uplift, 0 );

        a.recycle();

        // -------------------------------------------
        // main layout (1)
        // -------------------------------------------

        final RelativeLayout mainLayout = new RelativeLayout( _context, _attrs );
        mainLayout.setId( R.id.ll_gallery_main_layout );
        addView( mainLayout );

        // -------------------------------------------
        // progress-bar(1)
        // -------------------------------------------

        final ProgressBar progressBar = new ProgressBar( _context );
        progressBar.setId( R.id.ll_gallery_pb );
        mainLayout.addView( progressBar );

        // -------------------------------------------
        // indicator (1)
        // -------------------------------------------

        final RelativeLayout indicatorLayout = new RelativeLayout( _context );
        indicatorLayout.setId( R.id.ll_gallery_indicator );
        addView( indicatorLayout );

        // -------------------------------------------
        // indicator dots (1)
        // -------------------------------------------

        final LinearLayout indicatorDotsLayout = new LinearLayout( _context );
        indicatorDotsLayout.setId( R.id.ll_gallery_indicator_dots );
        indicatorLayout.addView( indicatorDotsLayout );

        // -------------------------------------------
        // 2 buttons (1)
        // -------------------------------------------

        final ImageButton arrowLeft = new ImageButton( _context );
        arrowLeft.setId( R.id.ll_gallery_arrow_left );
        addView( arrowLeft );

        final ImageButton arrowRight = new ImageButton( _context );
        arrowRight.setId( R.id.ll_gallery_arrow_right );
        addView( arrowRight );

        // -------------------------------------------
        // 2 triggers (1)
        // -------------------------------------------

        final Button triggerLeft = new Button( _context );
        triggerLeft.setId( R.id.ll_gallery_trigger_left );
        addView( triggerLeft );

        final Button triggerRight = new Button( _context );
        triggerRight.setId( R.id.ll_gallery_trigger_right );
        addView( triggerRight );

        // -------------------------------------------
        // main layout (2)
        // -------------------------------------------

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainLayout.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.FILL_PARENT;
        params.height = RelativeLayout.LayoutParams.FILL_PARENT;
//        if( mUplift > 0 ) {
//            params.addRule( RelativeLayout.ABOVE, R.id.ll_gallery_indicator );
//            params.bottomMargin = mUplift;
//        }
        mainLayout.setLayoutParams( params );

        // -------------------------------------------
        // progress-bar(2)
        // -------------------------------------------

        params = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
        params.addRule( RelativeLayout.CENTER_IN_PARENT );
        params.width = pbWidth;
        params.height = pbHeight;
        progressBar.setLayoutParams( params );
        progressBar.setIndeterminateDrawable( pbSrc );

        // -------------------------------------------
        // SlideView (1)
        // -------------------------------------------

        mSlideView = new LLSlideView( _context, this, true );
        mSlideView.setId( R.id.ll_gallery_swipable_view );
        mainLayout.addView( mSlideView );
        params = (RelativeLayout.LayoutParams) mSlideView.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.FILL_PARENT;
        params.height = RelativeLayout.LayoutParams.FILL_PARENT;
        params.addRule( RelativeLayout.ALIGN_PARENT_LEFT );
        mSlideView.setLayoutParams( params );
        mSlideView.setDotSelected( dotSelected );
        mSlideView.setDotUnselected( dotUnselected );

        // -------------------------------------------
        // 2 triggers (2)
        // -------------------------------------------

        params = (RelativeLayout.LayoutParams) triggerLeft.getLayoutParams();
        params.width = triggerWidth;
        params.height = RelativeLayout.LayoutParams.FILL_PARENT;
        params.addRule( RelativeLayout.ALIGN_PARENT_LEFT );
        triggerLeft.setLayoutParams( params );
        if( triggerLeftSrc != null ) {
            triggerLeft.setBackgroundDrawable( triggerLeftSrc );
        }

        params = (RelativeLayout.LayoutParams) triggerRight.getLayoutParams();
        params.width = triggerWidth;
        params.height = RelativeLayout.LayoutParams.FILL_PARENT;
        params.addRule( RelativeLayout.ALIGN_PARENT_RIGHT );
        triggerRight.setLayoutParams( params );
        if( triggerRightSrc != null ) {
            triggerRight.setBackgroundDrawable( triggerRightSrc );
        }

        // -------------------------------------------
        // indicator (2)
        // -------------------------------------------

        params = (RelativeLayout.LayoutParams) indicatorLayout.getLayoutParams();
        params.addRule( RelativeLayout.ALIGN_PARENT_BOTTOM );
        params.width = RelativeLayout.LayoutParams.FILL_PARENT;
        params.height = indicatorHeight;
        indicatorLayout.setLayoutParams( params );
        if( indicatorBackground == null ) {
            indicatorLayout.setBackgroundResource( android.R.color.transparent );
        }
        else {
            indicatorLayout.setBackgroundDrawable( indicatorBackground );
        }
        indicatorLayout.setClickable( indicatorBackground != null );

        // -------------------------------------------
        // indicator dots (2)
        // -------------------------------------------

        indicatorDotsLayout.setOrientation( LinearLayout.HORIZONTAL );
        params = (RelativeLayout.LayoutParams) indicatorDotsLayout.getLayoutParams();
        // params.width = RelativeLayout.LayoutParams.FILL_PARENT;
        params.height = indicatorHeight;
        params.addRule( RelativeLayout.CENTER_IN_PARENT );
        indicatorDotsLayout.setLayoutParams( params );

        // -------------------------------------------
        // 2 buttons (2)
        // -------------------------------------------

        arrowLeft.setPadding( arrowPaddingLeft, arrowPaddingTop, arrowPaddingRight, arrowPaddingBottom );
        arrowLeft.setImageDrawable( arrowLeftSrc );
        arrowLeft.setBackgroundResource( android.R.color.transparent );
        params = (RelativeLayout.LayoutParams) arrowLeft.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        params.addRule( RelativeLayout.ALIGN_PARENT_LEFT );
        params.addRule( RelativeLayout.ALIGN_BOTTOM, mainLayout.getId() );
        arrowLeft.setLayoutParams( params );

        arrowRight.setPadding( arrowPaddingLeft, arrowPaddingTop, arrowPaddingRight, arrowPaddingBottom );
        arrowRight.setImageDrawable( arrowRightSrc );
        arrowRight.setBackgroundResource( android.R.color.transparent );
        params = (RelativeLayout.LayoutParams) arrowRight.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        params.addRule( RelativeLayout.ALIGN_PARENT_RIGHT );
        params.addRule( RelativeLayout.ALIGN_BOTTOM, mainLayout.getId() );
        arrowRight.setLayoutParams( params );

        // -------------------------------------------
        // SlideView (2)
        // -------------------------------------------

        // when scrolls automatically the swipable will be dismissed.
        if( marquee > 0 ) {
            mSlideView.setSwipable( false );
            mSlideView.setAutoRate( marquee );
        }

        mSlideView.setOnItemClickListener( this );
        mSlideView.setOnItemScrollListener( this );
        mSlideView.setOnItemScrolledListener( this );
    }

    public LLGallery( Context _context ) {
        super( _context );
    }

    public LLGallery( Context _context, AttributeSet _attrs, int _defStyle ) {
        this( _context, _attrs );
    }

    /**
     * Scale bitmap
     * 
     * @param _bitmap
     *            Image has width > height.
     * @param _width
     * @return
     */
    private Bitmap scaleImage( Bitmap _bitmap, int _width )
    {
        if( _bitmap.getHeight() > _bitmap.getWidth() ) {
            return UIUtils
                    .scaleImageHW(
                            _bitmap,
                            _width );
        }
        else {
            return UIUtils
                    .scaleImageWH(
                            _bitmap,
                            _width );
        }
    }

    public void setImages( List<Bitmap> _bitmaps ) {
        if( _bitmaps == null ) {
            LLL.e( "Image source is NULL." );
        }
        else {
            List<Bitmap> newBmps = new ArrayList<Bitmap>();

            // -------------------------------------------
            // Resize layout to fill the bitmap of
            // the max size.
            // -------------------------------------------

            int lastWidth = 0;
            int lastHeight = 0;
            int i = 0;
            for( Bitmap bmp : _bitmaps ) {
                if( bmp != null ) {
                    Bitmap newBmp = scaleImage( bmp, getWidth() );
                    lastWidth = Math.max( lastWidth, newBmp.getWidth() );
                    lastHeight = Math.max( lastHeight, newBmp.getHeight() );
                    newBmps.add( newBmp );
                }
                else {
                    mComments[i] = null;
                }
                i++;
            }

            setImages( newBmps, lastWidth, lastHeight );
        }
    }

    public void setImagesByWidth( List<Bitmap> _bitmaps, int _maxWidth ) {
        if( _bitmaps == null ) {
            LLL.e( "Image source is NULL." );
        }
        else {
            List<Bitmap> newBmps = new ArrayList<Bitmap>();

            // -------------------------------------------
            // Resize layout to fill the bitmap of
            // the max size.
            // -------------------------------------------

            int lastHeight = 0;
            int i = 0;
            for( Bitmap bmp : _bitmaps ) {
                if( bmp != null ) {
                    Bitmap newBmp = scaleImage( bmp, getWidth() );
                    lastHeight = Math.max( lastHeight, newBmp.getHeight() );
                    newBmps.add( newBmp );
                }
                else {
                    mComments[i] = null;
                }
                i++;
            }

            setImages( newBmps, _maxWidth, lastHeight );
        }
    }

    private void setImages( List<Bitmap> _bitmaps, int _maxWidth, int _maxHeight ) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = _maxWidth;
        params.height = _maxHeight;
        if( mUplift > 0 ) {
            params.height += mUplift;
        }
        setLayoutParams( params );

        // -------------------------------------------
        // Show bitmaps
        // -------------------------------------------
        if( _bitmaps.size() > 0 ) {
            mSlideView.setImages( _bitmaps, _maxWidth );
        }
    }

    public void setComment( ViewGroup _layout, String[] _comments ) {
        setComment( _layout, CommentPosition.BOTTOM, _comments );
    }

    public void setComment( ViewGroup _layout, CommentPosition _pos, String[] _comments ) {
        if( _layout != null ) {
            addView( _layout );
            mComments = _comments;

            LayoutParams params = (LayoutParams) _layout.getLayoutParams();

            if( CommentPosition.BOTTOM == _pos ) {
                params.addRule( RelativeLayout.ABOVE, R.id.ll_gallery_indicator );
            }
            else {
                if( CommentPosition.TOP == _pos ) {
                    params.addRule( RelativeLayout.ALIGN_PARENT_TOP );
                }
            }

            _layout.setLayoutParams( params );
            showComment( 0 );
        }
    }

    public void setOnItemClickListener( LLGallery.OnItemClickListener _listener ) {
        mOnItemClickListener = _listener;
    }

    public void setOnItemScrollListener( LLGallery.OnItemScrollListener _onItemScrollListener ) {
        mOnItemScrollListener = _onItemScrollListener;
    }

    public void setOnItemScrolledListener( LLGallery.OnItemScrolledListener _onItemScrolledListener ) {
        mOnItemScrolledListener = _onItemScrolledListener;
    }

    @Override
    public void onItemClick( int _location ) {
        if( mOnItemClickListener != null ) {
            mOnItemClickListener.onItemClick( _location );
        }
    }

    @Override
    public void onItemScroll( int _location ) {
        if( mOnItemScrollListener != null ) {
            mOnItemScrollListener.onItemScroll( _location );
        }
    }

    @Override
    public void onItemScrolled( int _location ) {
        showComment( _location );
        if( mOnItemScrolledListener != null ) {
            mOnItemScrolledListener.onItemScrolled( _location );
        }
    }

    private void showComment( final int _location ) {
        if( mComments != null && mComments.length > 0 && _location >= 0 ) {
            final TextView tv = (TextView) findViewById( R.id.ll_gallery_comment );
            if( !TextUtils.isEmpty( mComments[_location] ) ) {
                Activity activity = (Activity) getContext();
                activity.runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        tv.setText( mComments[_location] );
                    }
                } );
            }
            else {
                tv.setText( "..." );
            }
        }
    }
}
