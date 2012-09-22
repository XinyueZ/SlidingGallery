package de.cellular.lib.lightlib.ui.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * Animation support for View.Visible/Gone
 * 
 * @author Xinyue Zhao
 * 
 */
public class SetUIVisibility implements AnimationListener {
    private View[] mViews;
    private int    mVisibility;
 
    
    public SetUIVisibility( int _visibility, View... _views ) {
        super();
        mViews = _views;
        mVisibility = _visibility; 
    }
    
    
    public void toggle( Animation _anim ) {
        _anim.setAnimationListener( this );
        for( View v : mViews ) {
            v.setAnimation( _anim ); 
            v.setVisibility( mVisibility );
        } 
        _anim.startNow();
    }
    @Override
    public void onAnimationEnd( Animation _arg0 ) {
        for( View v : mViews ) {
            v.setVisibility( mVisibility );
        }
    }

    @Override
    public void onAnimationRepeat( Animation _animation ) {
    }

    @Override
    public void onAnimationStart( Animation _animation ) {
    }
}
