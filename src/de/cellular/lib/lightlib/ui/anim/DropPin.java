package de.cellular.lib.lightlib.ui.anim;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import de.cellular.lib.lightlib.R;

public class DropPin implements AnimationListener, OnClickListener {
    private RelativeLayout mContainer;
    private ImageView      mImageView;
    private AnimationSet   mAnimSet;
    private MapView        mapView;
    private Context        mContext;
    private GeoPoint       mPoint;

    // private OverlayItem mOverlayItem;

    public interface OnPinDroppedListener {
        void onPinDropped();
        // void onPinDropped( OverlayItem _item );
    }

    private OnPinDroppedListener mOnPinDroppedListener;

    public DropPin( MapView _mapView, Context _context ) {
        super();
        mapView = _mapView;
        mContext = _context;
        init();
    }

    private void init() {
        mAnimSet = new AnimationSet( true );
        mAnimSet.setAnimationListener( this );
        TranslateAnimation translateAnimation = new TranslateAnimation( 0.0f, 0.0f, -400.0f, 0.0f );
        translateAnimation.setDuration( 1000 );
        mAnimSet.addAnimation( translateAnimation );
        mContainer = (RelativeLayout) View
                .inflate( mContext, R.layout.helper_layout_drop_pin, null );
        mImageView = (ImageView) mContainer.findViewById( R.id.marker_img_view );
        mapView.addView( mContainer );
        mContainer.setOnClickListener( this );
        mapView.invalidate();
    }

    public void drop( GeoPoint _geoPoint, Drawable _drawable, OnPinDroppedListener _listener ) {
        // mOverlayItem = _item;
        mPoint = _geoPoint;
        mImageView.setImageDrawable( _drawable );
        if( _drawable instanceof AnimationDrawable ) {
            mImageView.setSelected( true );
            AnimationDrawable animation = (AnimationDrawable) _drawable;
            animation.setVisible( true, true );
            animation.start();
        }
        mOnPinDroppedListener = _listener;
        ((MapView.LayoutParams) mContainer.getLayoutParams()).alignment = MapView.LayoutParams.BOTTOM_CENTER;
        ((MapView.LayoutParams) mContainer.getLayoutParams()).point = _geoPoint;
        mImageView.startAnimation( mAnimSet );
        mapView.invalidate();
    }

    @Override
    public void onAnimationEnd( Animation _animation ) {
        if( mOnPinDroppedListener != null ) {
            mOnPinDroppedListener.onPinDropped();
            // mOnPinDroppedListener.onPinDropped( mOverlayItem );
        }
    }

    @Override
    public void onAnimationRepeat( Animation _animation ) {

    }

    @Override
    public void onAnimationStart( Animation _animation ) {
    }

    @Override
    public void onClick( View _v ) {
        if( mPoint != null ) {
            mapView.getController().animateTo( mPoint );
        }
    }
}
