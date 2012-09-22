package de.cellular.lib.location;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import de.cellular.lib.geo.LocationDV;
import de.cellular.lib.geo.LocationDV.InValidLocationDVException;
import de.cellular.lib.lightlib.log.LL;
import de.cellular.lib.location.base.LocationUpdateRequester;
import de.cellular.lib.location.base.OnLocationUpdateListener;
import de.cellular.lib.location.receiver.BestLocationChangeReceiver;
import de.cellular.lib.location.receiver.GPSLocationChangedReceiver;
import de.cellular.lib.location.receiver.NetworkLocationChangedReceiver;

/**
 * LocationUpdateManager
 * 
 * @author xinyue
 * 
 */
public class LocationUpdateManager implements OnLocationUpdateListener
{
    public static final int    WORSE_ACCURACY_METERS                           = 200;
    public static final int    WORST_ACCURACY_METERS                           = 500;
    public static final int    LOCATION_UPDATE_SECONDS                         = 3000;
    public static final int    LOCATION_UPDATE_METERS                          = 0;
    public static final String ACTION_ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED = "com.gmail.at.hasszhao.active_location_update_provider_disabled";

    private static final int   ONE_MINUTE                                      = 60 * 1000;

    private Handler            h                                               = new Handler();

    /**
     * Listener for starting location tracing
     * 
     * @author xinyue
     * 
     */
    public interface OnStartLocationTracingListener
    {
        /**
         * start location tracing
         */
        void onStartLocationTracing();
    }

    private OnStartLocationTracingListener onStartLocationTracingListener;

    /**
     * Listener for stopping location tracing
     * 
     * @author xinyue
     * 
     */
    public interface OnStopLocationTracingListener
    {
        /**
         * stop location tracing
         */
        void onStopLocationTracing();
    }

    private OnStopLocationTracingListener onStopLocationTracingListener;

    /**
     * Context
     */
    private Context                       context;

    /**
     * Criteria with which we can get typical location provider.
     */
    private Criteria                      criteria;

    /**
     * Intent requiring unknown best location.
     */
    private PendingIntent                 pendingIntentUnknownBestLocationListener;

    /**
     * Intent requiring GPS location.
     */
    private PendingIntent                 pendingIntentGPSistener;

    /**
     * Intent requiring Network location.
     */
    private PendingIntent                 pendingIntentNetworkLocationListener;

    /**
     * Object which wrappers LocationManager of different version of android due to handle Location update request.
     */
    private LocationUpdateRequester       locationUpdateRequester;

    /**
     * Handle event when provider dies
     */
    private IntentFilter                  intentFilterLocationProviderDisabled = new IntentFilter( LocationUpdateManager.ACTION_ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED );

    /**
     * flag. Unknown best location update
     */
    private boolean                       canUnknownBestLocatonUpdate          = false;
    /**
     * Request unknown best location
     */
    private Intent                        intentUnknownBestLocationUpdate;
    /**
     * Request GPS event
     */
    private Intent                        intentGPSLocationUpdate;

    /**
     * Request Network-location event
     */
    private Intent                        intentNetworkLocationUpdate;

    /**
     * Lister watches update on this class when location is updated.
     */
    private OnLocationUpdateListener       listenerLocationUpdate;

    /**
     * Flag whether location is tracing. If location is being searched, the app can not start searching double or more.
     */
    private boolean                       isLocationTrackingRunning            = false;

    /**
     * LocationManager
     */
    private LocationManager               locationManager;

    /**
     * Timer to control GPS function. @see ONE_MINUTE
     */
    private Timer                         timerTaxiradar;
    private TimerTask                     timerTaskTaxiRadar;

    private BroadcastReceiver             receiverLocationProviderDisabled     = new BroadcastReceiver()
                                                                               {
                                                                                   @Override
                                                                                   public void onReceive( Context context, Intent intent )
                                                                               {
                                                                                   LL.d( "->get  disabled provider event" );
                                                                                   boolean providerDisabled = !intent.getBooleanExtra( LocationManager.KEY_PROVIDER_ENABLED, false );
                                                                                   if( providerDisabled )
                                                                               {
                                                                                   LL.d( "->Disabled provider event new start location tracing" );
                                                                                   stopLocationTracing();
                                                                                   startLocationTracing();
                                                                                   LL.d( "<-Disabled provider event new start location tracing" );
                                                                               }
                                                                               LL.d( "<-get  disabled provider event" );
                                                                           }
                                                                               };

    private LocationListener              listenerUnknownBestLocationProvider  = new LocationListener()
                                                                               {
                                                                                   public void onLocationChanged( Location l )
                                                                               {
                                                                               }

                                                                                   public void onProviderDisabled( String provider )
                                                                               {
                                                                               }

                                                                                   public void onStatusChanged( String provider, int status, Bundle extras )
                                                                               {
                                                                               }

                                                                                   public void onProviderEnabled( String provider )
                                                                               {
                                                                                   LL.d( "->find unknown best location provider, stop tracing and new start" );
                                                                                   stopLocationTracing();
                                                                                   startLocationTracing();
                                                                                   LL.d( "<-find unknown best location provider, stop tracing and new start" );
                                                                               }
                                                                               };

    private LocationUpdateManager(Context context)
    {
        this.context = context;

        this.intentUnknownBestLocationUpdate = new Intent( this.context, BestLocationChangeReceiver.class );
        BestLocationChangeReceiver.setListenerLocationUpdate( this );

        this.intentGPSLocationUpdate = new Intent( this.context, GPSLocationChangedReceiver.class );
        GPSLocationChangedReceiver.setListenerLocationUpdate( this );

        this.intentNetworkLocationUpdate = new Intent( this.context, NetworkLocationChangedReceiver.class );
        NetworkLocationChangedReceiver.setListenerLocationUpdate( this );

        this.locationManager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
        this.locationUpdateRequester = PlatformSpecificImplementationFactory.getLocationUpdateRequester( locationManager );
        this.criteria = new Criteria();
        this.criteria.setAccuracy( Criteria.ACCURACY_FINE );

    }
    
    private static LocationUpdateManager sInstance;
    public static LocationUpdateManager getInstance( Context context ) {
        if( sInstance == null ) {
            synchronized( LocationUpdateManager.class ) {
                if( sInstance == null ) {
                    sInstance = new LocationUpdateManager( context );
                }
            }
        } 
        return sInstance;
    }

    /**
     * GPS runs in ONE_MINUTE after starting.
     */
    private void startGPSTimer()
    {
        timerTaxiradar = new Timer();
        timerTaskTaxiRadar = new TimerTask()
        {
            @Override
            public void run()
            {
                /* Time is up */
                stopLocationTracing();
            }
        };

        timerTaxiradar.schedule( timerTaskTaxiRadar, ONE_MINUTE );
    }

    /**
     * Reset GPS timer
     */
    private void resetGPSTimer()
    {
        if( timerIsRunning() )
        {
            timerTaskTaxiRadar.cancel();
            timerTaxiradar.cancel();
            timerTaxiradar.purge();
            timerTaxiradar = null;
        }
    }

    /**
     * Is GPS timer being used then return true.
     * 
     * @return boolean
     */
    private boolean timerIsRunning()
    {
        return timerTaxiradar != null && timerTaskTaxiRadar != null;
    }

    /**
     * Open and start location tracing, also setting best location while starting.
     */
    public void startLocationTracing()
    {
        LL.d( "->start location tracing if possiable" );
        if( !isLocationTrackingRunning )
        {
            LL.d( "->start" );
            onStartLocationTracing();

            /* Get LocationManager and set Criteria */
            isLocationTrackingRunning = true;// Flag,can not start again if it is running.

            /* Get current better location */
            findLatestLocation();

            /*
             * Searching an unknown best location provider will be handled. It is very interesting. This provider can be the one who is not better replacing the one who is best but does not exist.
             */
            searchUnknowBestProviderListener();

            /*
             * Force to listen unknown best event Selected LocationManager::requestBestUpdates is used.
             */
            initUnknownBestLocationListener();
            /*
             * Force to listen GPS event Selected LocationManager::requestGPSUpdates is used.
             */
            initGPSListener();

            /*
             * Force to listen network location event Selected LocationManager::requestNetworkUpdates is used.
             */
            initNetworkLocationListener();

            /*
             * Handle "Provider disable". One of providers has been disabled, so restart (recall startLocationTracking) in "receiverLocationProviderDisabled".
             */
            context.registerReceiver( receiverLocationProviderDisabled, intentFilterLocationProviderDisabled );
            LL.d( "<-start" );

            // Create a timer to control GPS tracing,if GPS is global available.
            resetGPSTimer();
            startGPSTimer();

        }
        else
        {
            LL.d( "--location tracing is running, can not start" );
        }
        LL.d( "<-start location tracing if possiable" );
    }

    /**
     * If the best is not available, call this and find the better provider again.
     * 
     * @see listenerBestInactiveLocationProvider
     **/
    private void searchUnknowBestProviderListener()
    {
        LL.d( "->connect to listen unknown best provider" );
        String bestProvider = locationManager.getBestProvider( criteria, false );// false,return the best although doesn't exist.
        String bestAvailableProvider = locationManager.getBestProvider( criteria, true );// true,return the best only does exist.
        if( bestProvider != null && !bestProvider.equals( bestAvailableProvider ) && listenerUnknownBestLocationProvider != null )
        {
            canUnknownBestLocatonUpdate = true;
            locationManager.requestLocationUpdates(
                    bestProvider,
                    0,
                    0,
                    listenerUnknownBestLocationProvider,
                    context.getMainLooper()
                    );
            LL.d( "--find an unknown provider set canUnknownBestLocatonUpdate to TRUE" );
        }
        else
        {
            canUnknownBestLocatonUpdate = false;
            LL.d( "--Unknown best provider is impossiable,set canUnknownBestLocatonUpdate to FALSE" );
        }
        LL.d( "<-connect to listen unknown best provider" );
    }

    /**
     * Init network location listener
     */
    private void initNetworkLocationListener()
    {
        LL.d( "->connect to listen network location" );
        if( locationManager.getAllProviders().contains( LocationManager.NETWORK_PROVIDER ) )
        {
            pendingIntentNetworkLocationListener = PendingIntent.getBroadcast(
                    context,
                    0,
                    intentNetworkLocationUpdate,
                    PendingIntent.FLAG_UPDATE_CURRENT
                    );
            locationUpdateRequester.requestNetworkUpdates(
                    LocationUpdateManager.LOCATION_UPDATE_SECONDS,
                    LocationUpdateManager.LOCATION_UPDATE_METERS,
                    pendingIntentNetworkLocationListener
                    );
        }
        else
        {
            LL.d( "--Network location provider is impossiable" );
        }
        LL.d( "<-connect to listen network location" );
    }

    /**
     * Init best location listener
     */
    private void initUnknownBestLocationListener()
    {
        LL.d( "->connect to listen best unknown best location" );

        if( canUnknownBestLocatonUpdate )
        {
            pendingIntentUnknownBestLocationListener = PendingIntent.getBroadcast(
                    context,
                    0,
                    intentUnknownBestLocationUpdate,
                    PendingIntent.FLAG_UPDATE_CURRENT
                    );
            locationUpdateRequester.requestUnknownBestUpdates(
                    LocationUpdateManager.LOCATION_UPDATE_SECONDS,
                    LocationUpdateManager.LOCATION_UPDATE_METERS,
                    criteria,
                    pendingIntentUnknownBestLocationListener
                    );
            LL.d( "--unknown best provider is  available, canUnknownBestLocatonUpdate is TRUE" );
        }
        else
        {
            LL.d( "--unknown best provider is not available, canUnknownBestLocatonUpdate is FALSE" );
        }

        LL.d( "<-connect to listen best unknown best location" );

    }

    /**
     * Init gps listener
     */
    private void initGPSListener()
    {
        LL.d( "->connect to listen GPS" );
        if( locationManager.getAllProviders().contains( LocationManager.GPS_PROVIDER ) )
        {
            pendingIntentGPSistener = PendingIntent.getBroadcast(
                    context,
                    0,
                    intentGPSLocationUpdate,
                    PendingIntent.FLAG_UPDATE_CURRENT
                    );
            locationUpdateRequester.requestGPSUpdates(
                    LocationUpdateManager.LOCATION_UPDATE_SECONDS,
                    LocationUpdateManager.LOCATION_UPDATE_METERS,
                    pendingIntentGPSistener
                    );
        }
        else
        {
            LL.d( "--GPS provider is impossiable" );
        }
        LL.d( "<-connect to listen GPS" );
    }

    /**
     * Get current better locationDV
     * 
     * @param locationManager
     */
    private void findLatestLocation()
    {
        LL.d( "->inited location" );
        /* Set best location when start */
        LocationDV lastKnownGpsLocation;
        try
        {
            lastKnownGpsLocation = LocationDV.valueOf( locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER ) );
        }
        catch( InValidLocationDVException e )
        {
            LL.e( e.toString() );
            return;
        }

        LocationDV lastKnownNetworkLocation;
        try
        {
            lastKnownNetworkLocation = LocationDV.valueOf( locationManager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER ) );
        }
        catch( InValidLocationDVException e )
        {
            LL.e( e.toString() );
            return;
        }

        LocationDV betterLastKnown = lastKnownGpsLocation.compareTo( lastKnownNetworkLocation );

        if( betterLastKnown != null )
        {
            notifyLocationUpdate( betterLastKnown );

        }
    }

    /**
     * Stop location tracing
     * 
     * @see ThisApplication i.e endTask()
     */
    public void stopLocationTracing()
    {
        LL.d( "->stop location tracing" );
        if( isLocationTrackingRunning )
        {
            LL.d( "--stop location tracing" );
            isLocationTrackingRunning = false;
            resetGPSTimer();

            stopUnknownBestLocationProviderListener( locationManager );
            stopGpsLocationListener( locationManager );
            stopLocationProviderDisabledListener();
            stopNetworkLocationListener( locationManager );

            onStopLocationTracing();
        }
        LL.d( "<-stop location tracing" );
    }

    /**
     * Stop listening provider disabled event
     */
    private void stopLocationProviderDisabledListener()
    {
        try
        {
            if( receiverLocationProviderDisabled != null )
            {
                context.unregisterReceiver( receiverLocationProviderDisabled );
            }
        }
        catch( Throwable t )
        {
            LL.e( t.toString() );
        }
        finally
        {
            receiverLocationProviderDisabled = null;
        }
    }

    /**
     * Stop listening GPS event
     * 
     * @param locationManager
     */
    private void stopGpsLocationListener( LocationManager locationManager )
    {
        try
        {
            if( pendingIntentGPSistener != null )
            {
                locationManager.removeUpdates( pendingIntentGPSistener );
            }
        }
        catch( Throwable t )
        {
            LL.e( t.toString() );
        }
        finally
        {
            pendingIntentGPSistener = null;
        }
    }

    /**
     * Stop listening network location event
     * 
     * @param locationManager
     */
    private void stopNetworkLocationListener( LocationManager locationManager )
    {
        try
        {
            if( pendingIntentNetworkLocationListener != null )
            {
                locationManager.removeUpdates( pendingIntentNetworkLocationListener );
            }
        }
        catch( Throwable t )
        {
            LL.e( t.toString() );
        }
        finally
        {
            pendingIntentNetworkLocationListener = null;
        }
    }

    /**
     * Stop listening unknown best location
     * 
     * @param locationManager
     */
    private void stopUnknownBestLocationProviderListener( LocationManager locationManager )
    {
        try
        {
            if( listenerUnknownBestLocationProvider != null )
            {
                locationManager.removeUpdates( listenerUnknownBestLocationProvider );
                if( pendingIntentUnknownBestLocationListener != null )
                    locationManager.removeUpdates( pendingIntentUnknownBestLocationListener );
            }
        }
        catch( Throwable t )
        {
            LL.e( t.toString() );
        }
        finally
        {
            listenerUnknownBestLocationProvider = null;
        }
    }

    /**
     * Set listener for location update
     * 
     * @param listenerLocationUpdate
     */
    public void setListenerLocationUpdate( OnLocationUpdateListener listenerLocationUpdate )
    {
        this.listenerLocationUpdate = listenerLocationUpdate;
    }

    /**
     * Tell the listener who watches location update that location has been changed.
     * 
     * @param locationDV
     */
    private void notifyLocationUpdate( LocationDV locationDV )
    {
        LL.d( "->notify location udpate in location update manager" );
        if( listenerLocationUpdate != null )
        {
            LL.d( "<-inited location: " + locationDV.toString() );
            listenerLocationUpdate.onLocationUpdate( locationDV );
        }
        else
        {
            LL.e( "listenerLocationUpdate is NULL" );
        }
        LL.d( "<-notify location udpate in location update manager" );
    }

    @Override
    public void onLocationUpdate( LocationDV locationDV )
    {
        notifyLocationUpdate( locationDV );
    }

    /**
     * Set an OnStartLocationTracingListener
     * 
     * @param onStartLocationTracingListener
     */
    public void setOnStartLocationTracingListener( OnStartLocationTracingListener onStartLocationTracingListener )
    {
        this.onStartLocationTracingListener = onStartLocationTracingListener;
    }

    /**
     * Event fired while start location tracing
     */
    private void onStartLocationTracing()
    {
        if( null != onStartLocationTracingListener )
            onStartLocationTracingListener.onStartLocationTracing();
    }

    /**
     * Set an OnStopLocationTracingListener
     * 
     * @param onStopLocationTracingListener
     */
    public void setOnStopLocationTracingListener( OnStopLocationTracingListener onStopLocationTracingListener )
    {
        this.onStopLocationTracingListener = onStopLocationTracingListener;
    }

    /**
     * Event fired while stop location tracing
     */
    private void onStopLocationTracing()
    {
        if( null != onStopLocationTracingListener )
        {
            h.post( new Runnable()
            {
                @Override
                public void run()
                {
                    // maybe fired on UI thread
                    onStopLocationTracingListener.onStopLocationTracing();
                }
            } );

        }
    }

}
