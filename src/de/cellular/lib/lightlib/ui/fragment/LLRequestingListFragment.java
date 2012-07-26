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

package de.cellular.lib.lightlib.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import de.cellular.lib.lightlib.backend.base.ILLRequestResponsible;
import de.cellular.lib.lightlib.log.LLL;
import de.cellular.lib.lightlib.ui.anim.Rotate3dAnimation;

/**
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 * 
 */
public abstract class LLRequestingListFragment extends ListFragment implements ILLRequestResponsible
{
    private static long FLIP_ANIMATION_DURATION_IN  = 300;
    private static long FLIP_ANIMATION_DURATION_OUT = 300;

    @Override
    public void onActivityCreated( Bundle _savedInstanceState ) {
        super.onActivityCreated( _savedInstanceState );
        LLL.d( getClass().getSimpleName() + "::onActivityCreated" );
    }

    @Override
    public View onCreateView( LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState ) {
        LLL.d( getClass().getSimpleName() + "::onCreateView" );
        return null;
    }

    @Override
    public void onSaveInstanceState( Bundle _outState ) {
        super.onSaveInstanceState( _outState );
        LLL.d( getClass().getSimpleName() + "::onSaveInstanceState" );
    }

    @Override
    public void onHiddenChanged( boolean _hidden ) {
        LLL.d( getClass().getSimpleName() + "::onHiddenChanged" );
        super.onHiddenChanged( _hidden );
    }

    @Override
    public void onViewCreated( View _view, Bundle _savedInstanceState ) {
        LLL.d( getClass().getSimpleName() + "::onViewCreated" );
        super.onViewCreated( _view, _savedInstanceState );
    }

    @Override
    public void onInflate( Activity _activity, AttributeSet _attrs, Bundle _savedInstanceState ) {
        LLL.d( getClass().getSimpleName() + "::onInflate" );
        super.onInflate( _activity, _attrs, _savedInstanceState );
    }

    @Override
    public void onCreate( Bundle _savedInstanceState ) {
        super.onCreate( _savedInstanceState );
        LLL.d( getClass().getSimpleName() + "::onCreate" );
    }

    @Override
    public void onPause() {
        super.onPause();
        LLL.d( getClass().getSimpleName() + "::onPause" );
    }

    @Override
    public void onResume() {
        super.onResume();
        LLL.d( getClass().getSimpleName() + "::onResume" );
    }

    @Override
    public void onStart() {
        super.onStart();
        LLL.d( getClass().getSimpleName() + "::onStart" );

//        startRotate3dAnimationIn();
    }

    @Override
    public void onStop() {
        super.onStop();
        LLL.d( getClass().getSimpleName() + "::onStop" );

//        startRotate3dAnimationOut();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LLL.d( getClass().getSimpleName() + "::onDestroy" );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LLL.d( getClass().getSimpleName() + "::onDestroyView" );
    }

    @Override
    public void onAttach( Activity _activity ) {
        super.onAttach( _activity );
        LLL.d( getClass().getSimpleName() + "::onAttach" );
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LLL.d( getClass().getSimpleName() + "::onDetach" );
    }

    

    @Override
    public abstract void onRequestFinished( Message _msg );

    @Override
    public abstract void onRequestImageFailed( Message _msg );

    @Override
    public abstract void onRequestImageSuccessed( Message _msg );

    @Override
    public abstract void onRequestAborted( Message _msg );

    @Override
    public abstract void onRequestSuccessed( Message _msg );

    @Override
    public abstract void onRequestFailed( Message _msg );

    // -------------------------------------------------------
    // 3rd party animations
    // -------------------------------------------------------

    protected void startRotate3dAnimationIn() {
        // Initialize the animations.
        Display display = ((WindowManager) getActivity().getSystemService( Context.WINDOW_SERVICE ))
                .getDefaultDisplay();

        int h2 = display.getHeight() / 2;
        int w2 = display.getWidth() / 2;

        Rotate3dAnimation outAnim = new Rotate3dAnimation( 0, 90, w2, h2, 0.0f, false );
        outAnim.setDuration( FLIP_ANIMATION_DURATION_OUT );
        outAnim.setAnimationListener( new AnimationListener() {
            @Override
            public void onAnimationStart( Animation _animation ) {
            }

            @Override
            public void onAnimationRepeat( Animation _animation ) {
            }

            @Override
            public void onAnimationEnd( Animation _animation ) {
                if( getView() != null )
                    getView().setVisibility( View.VISIBLE );
            }
        } );
        Rotate3dAnimation inAnim = new Rotate3dAnimation( -90, 0, w2, h2, 0.0f, false );
        inAnim.setDuration( FLIP_ANIMATION_DURATION_IN );
        inAnim.setStartOffset( FLIP_ANIMATION_DURATION_OUT );
        inAnim.setInterpolator( AnimationUtils.loadInterpolator( getActivity(),
                android.R.anim.decelerate_interpolator ) );
        outAnim.setInterpolator( AnimationUtils.loadInterpolator( getActivity(),
                android.R.anim.accelerate_interpolator ) );

        if( getView() != null )
            getView().startAnimation( inAnim );
    }

    protected void startRotate3dAnimationOut() {
        // Initialize the animations.
        Display display = ((WindowManager) getActivity().getSystemService( Context.WINDOW_SERVICE ))
                .getDefaultDisplay();
        int h2 = display.getHeight() / 2;
        int w2 = display.getWidth() / 2;

        Rotate3dAnimation outAnim = new Rotate3dAnimation( 0, 90, w2, h2, 0.0f, false );
        outAnim.setDuration( FLIP_ANIMATION_DURATION_OUT );
        outAnim.setAnimationListener( new AnimationListener() {
            @Override
            public void onAnimationStart( Animation _animation ) {
            }

            @Override
            public void onAnimationRepeat( Animation _animation ) {
            }

            @Override
            public void onAnimationEnd( Animation _animation ) {
                if( getView() != null )
                    getView().setVisibility( View.GONE );
            }
        } );

        Rotate3dAnimation inAnim = new Rotate3dAnimation( -90, 0, w2, h2, 0.0f, false );
        inAnim.setDuration( FLIP_ANIMATION_DURATION_IN );
        inAnim.setStartOffset( FLIP_ANIMATION_DURATION_OUT );
        inAnim.setInterpolator( AnimationUtils.loadInterpolator( getActivity(),
                android.R.anim.decelerate_interpolator ) );
        outAnim.setInterpolator( AnimationUtils.loadInterpolator( getActivity(),
                android.R.anim.accelerate_interpolator ) );

        if( getView() != null )
            getView().startAnimation( outAnim );
    }
}
