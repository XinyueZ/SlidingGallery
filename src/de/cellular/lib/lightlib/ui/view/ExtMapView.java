package de.cellular.lib.lightlib.ui.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import de.cellular.lib.lightlib.log.LL;

/**
 * ExtMapView supports double click on map and zoom in User can extends this class and rewrite @see OnDbClicked or @see setOnDbClickedListener to handle.
 * 
 * @author xinyue
 * 
 */
public class ExtMapView extends MapView
{
    private static final int _300MS              = 300;
    private static final int INVAILD             = -1;
    /**
     * Last touch time
     */
    private long             lastTouchTime       = INVAILD;
    /**
     * flag, listen to map moving
     */
    private boolean          stopListenMapMoving = false;

    private int              oldZoomLevel        = -1;
    private GeoPoint         oldCenterGeoPoint;

    public interface OnPanAndZoomListener {
        void onPan();

        void onZoomedIn( int _oldZoomLevel, int _zoomLevel );

        void onZoomedOut( int _oldZoomLevel, int _zoomLevel );
    }

    private OnPanAndZoomListener mOnPanAndZoomListener;

    public void setOnPanListener( OnPanAndZoomListener listener ) {
        mOnPanAndZoomListener = listener;
    }

    /**
     * Listener when map is moving
     * 
     * @author xinyue
     * 
     */
    public interface OnMapMovingListener
    {
        void onMapMoving( MapView mapView );
    }

    private List<OnMapMovingListener> onMapMovingListeners = new ArrayList<ExtMapView.OnMapMovingListener>();

    /**
     * Listener when map stops moving
     * 
     * @author xinyue
     * 
     */
    public interface OnMapStopMovingListener
    {
        void onMapStopMoving( GeoPoint geoPointCenter );
    }

    private List<OnMapStopMovingListener> onMapStopMovingListeners = new ArrayList<OnMapStopMovingListener>();

    /**
     * Listener when finger down and moving
     * 
     * @author xinyue
     * 
     */
    public interface OnFingerDragMovingListener
    {
        void onFingerDragMoving( MotionEvent ev, MapView mapView );
    }

    private OnFingerDragMovingListener onFingerDragMovingListener;

    /**
     * Listener when finger up
     * 
     * @author xinyue
     * 
     */
    public interface OnFingerUpListener
    {
        void onFingerUp( MotionEvent ev, MapView mapView );
    }

    private OnFingerUpListener onFingerUpListener;

    /**
     * Listener when long finger down
     * 
     * @author xinyue
     * 
     */
    public interface OnFingerDownListener
    {
        void onFingerDown( MotionEvent ev, MapView mapView );
    }

    private OnFingerDownListener onFingerDownListener;

    /**
     * Listener gives external user the chance to do more.
     * 
     * @author xinyue
     * 
     */
    public interface OnDbClickedListener
    {
        void onDbClicked( MotionEvent ev, MapView mapView );
    }

    private OnDbClickedListener onDbClickedListener;

    public ExtMapView( Context context, AttributeSet attrs, int defStyle )
    {
        super( context, attrs, defStyle );
        startListeningMapStopMoving();
    }

    public ExtMapView( Context context, AttributeSet attrs )
    {
        super( context, attrs );
        startListeningMapStopMoving();
    }

    public ExtMapView( Context context, String apiKey )
    {
        super( context, apiKey );
        startListeningMapStopMoving();
    }

    /**
     * Release recourse
     */
    public void release()
    {
        /* free event listeners, it was not necessary but I do it */
        this.onMapMovingListeners.clear();
        this.onMapStopMovingListeners.clear();
    }

    /**
     * Start a thread to look up whether map stops moving and fire @see onMapStopMoving event
     */
    private void startListeningMapStopMoving()
    {
        final Handler h = new Handler();
        Executors.newSingleThreadExecutor().submit( new Runnable()
        {
            @Override
            public void run()
            {
                GeoPoint current = null;
                GeoPoint previous = null;
                boolean onceFiredStopMoving = false; // flag looks whether onMapStopMoving had been fired.
                boolean onceFiredMoving = false;

                while( !stopListenMapMoving )
                {
                    current = ExtMapView.this.getMapCenter();

                    /* map's center does not move */
                    if( !areDifferentGeoPoints( previous, current ) )
                    {
                        onceFiredMoving = false;

                        /* fire onMapStopMoving for map's stopping, but just one time */
                        if( !onceFiredStopMoving )
                        {
                            h.post( new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    onMapStopMoving();
                                }
                            } );

                            onceFiredStopMoving = true;// the onMapStopMoving will not be fired when point of center does not change more.
                        }
                        // do nothing because event had been fired
                    }
                    /* map's center changes continually */
                    else
                    {
                        onceFiredStopMoving = false;

                        /* fire onMapMoving for map's moving, but just one time when each time map is from stop to movement. */
                        if( !onceFiredMoving )
                        {
                            h.post( new Runnable()
                            {

                                @Override
                                public void run()
                                {
                                    onMapMoving();
                                }
                            } );

                            onceFiredMoving = true; // the onMapMoving will not be fired when point of center moves more.
                        }

                    }

                    previous = current;

                    try
                    {
                        // not too fast! so we wait
                        TimeUnit.MILLISECONDS.sleep( _300MS );
                    }
                    catch( InterruptedException e )
                    {
                        LL.e( e.toString() );
                    }
                }
            }
        } );
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent( MotionEvent ev )
    {
        switch( ev.getAction() )
        {
            case MotionEvent.ACTION_DOWN:
                long thisTime = System.currentTimeMillis();
                if( thisTime - lastTouchTime < 250 )
                {
                    // Double tap
                    LL.d( "ExMapView::onInterceptTouchEvent::ACTION_DOWN(DbClick)" );
                    lastTouchTime = INVAILD;
                    onDbClicked( ev );
                }
                else
                {
                    /* slowly and avoiding handling. */
                    LL.d( "ExMapView::onInterceptTouchEvent::ACTION_DOWN(Click)" );
                    lastTouchTime = thisTime;
                    onFingerDown( ev );
                }
            break;
            case MotionEvent.ACTION_MOVE:
                LL.d( "ExMapView::onInterceptTouchEvent::ACTION_MOVE" );
            break;
            case MotionEvent.ACTION_UP:
                LL.d( "ExMapView::onInterceptTouchEvent::ACTION_UP" );
            break;
        }
        return false;
    }

    private static boolean areDifferentGeoPoints( GeoPoint _old, GeoPoint _new ) {
        return(_old == null || (_old.getLatitudeE6() != _new.getLatitudeE6()) || (_old.getLongitudeE6() != _new
                .getLongitudeE6()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.android.maps.MapView#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent( MotionEvent ev )
    {
        switch( ev.getAction() )
        {
            case MotionEvent.ACTION_UP:
                /* fing is up */
                LL.d( "ExMapView::onTouchEvent::ACTION_UP" );
                onFingerUp( ev );
                GeoPoint centerGeoPoint = this.getMapCenter();
                if( areDifferentGeoPoints( oldCenterGeoPoint, centerGeoPoint ) ) {
                    if( mOnPanAndZoomListener != null ) {
                        mOnPanAndZoomListener.onPan();
                    }
                }
                oldCenterGeoPoint = this.getMapCenter();
            break;
            case MotionEvent.ACTION_MOVE:
                LL.d( "ExMapView::onTouchEvent::ACTION_MOVE" );
                onFingerDragMoving( ev );
            break;
            case MotionEvent.ACTION_DOWN:
                LL.d( "ExMapView::onTouchEvent::ACTION_DOWN" );
            break;
        }
        try {
            return super.onTouchEvent( ev );
        }
        catch( Exception _e ) {
            return false;
        }
    }

    @Override
    protected void dispatchDraw( Canvas canvas ) {
        super.dispatchDraw( canvas );
        if( oldZoomLevel == -1 ) {
            oldZoomLevel = getZoomLevel();
        }
        if( oldZoomLevel < getZoomLevel() ) {
            if( mOnPanAndZoomListener != null ) {
                mOnPanAndZoomListener
                        .onZoomedIn( oldZoomLevel, getZoomLevel() );
            }
        }
        if( oldZoomLevel > getZoomLevel() ) {
            if( mOnPanAndZoomListener != null ) {
                mOnPanAndZoomListener.onZoomedOut( oldZoomLevel,
                        getZoomLevel() );
            }
        }
    }

    /**
     * Double click event for extends.
     * 
     * @param ev
     */
    protected void onDbClicked( MotionEvent ev )
    {
        getController().zoomInFixing( (int) ev.getX(), (int) ev.getY() );
        if( onDbClickedListener != null )
            onDbClickedListener.onDbClicked( ev, this );
    }

    /**
     * Long finger down for extends
     * 
     * @param ev
     */
    protected void onFingerDown( MotionEvent ev )
    {
        if( this.onFingerDownListener != null )
            this.onFingerDownListener.onFingerDown( ev, this );
    }

    /**
     * Finger up for extends
     * 
     * @param ev
     */
    protected void onFingerUp( MotionEvent ev )
    {
        if( this.onFingerUpListener != null )
            this.onFingerUpListener.onFingerUp( ev, this );
    }

    /**
     * Finger down and moving
     * 
     * @param ev
     */
    protected void onFingerDragMoving( MotionEvent ev )
    {
        if( this.onFingerDragMovingListener != null )
            this.onFingerDragMovingListener.onFingerDragMoving( ev, this );

    }

    /**
     * Fired when map stops moving
     */
    protected void onMapStopMoving()
    {
        for( OnMapStopMovingListener l : onMapStopMovingListeners )
            l.onMapStopMoving( this.getMapCenter() );
    }

    /**
     * Fired when map is moving
     */
    protected void onMapMoving()
    {
        for( OnMapMovingListener l : onMapMovingListeners )
            l.onMapMoving( this );
    }

    /* setting for listeners below */
    public void setOnDbClickedListener( OnDbClickedListener onDbClickedListener )
    {
        this.onDbClickedListener = onDbClickedListener;
    }

    public void setOnFingerDownListener( OnFingerDownListener onFingerDownListener )
    {
        this.onFingerDownListener = onFingerDownListener;
    }

    public void setOnFingerUpListener( OnFingerUpListener onFingerUpListener )
    {
        this.onFingerUpListener = onFingerUpListener;
    }

    public void addOnMapStopMovingListener( OnMapStopMovingListener onMapStopMovingListener )
    {
        this.onMapStopMovingListeners.add( onMapStopMovingListener );
    }

    public void removeOnMapStopMovingListener( OnMapStopMovingListener onMapStopMovingListener )
    {
        this.onMapStopMovingListeners.remove( onMapStopMovingListener );
    }

    public void addOnMapMovingListener( OnMapMovingListener onMapMovingListener )
    {
        this.onMapMovingListeners.add( onMapMovingListener );
    }

    public void removeOnMapMovingListener( OnMapMovingListener onMapMovingListener )
    {
        this.onMapMovingListeners.remove( onMapMovingListener );
    }

    public void setOnFingerDragMovingListener( OnFingerDragMovingListener onFingerDragMovingListener )
    {
        this.onFingerDragMovingListener = onFingerDragMovingListener;
    }

}
