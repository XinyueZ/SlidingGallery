package de.cellular.lib.location.base;
 

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import de.cellular.lib.geo.LocationDV;
import de.cellular.lib.geo.LocationDV.InValidLocationDVException;
import de.cellular.lib.lightlib.log.LL;
import de.cellular.lib.location.LocationUpdateManager;

public abstract class LocationUpdateBroadcastReceiver extends BroadcastReceiver
{

    private Intent                         providerDisabledIntent = new Intent( LocationUpdateManager.ACTION_ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED );
    private static OnLocationUpdateListener listenerLocationUpdate;

    /**
     * Location Update Event
     * 
     * @param locationDV
     */
    protected void onLocationUpated( LocationDV locationDV )
    {
        LL.d( "-- update receiver type[" + this.getClass().getSimpleName() + "]->location[" + locationDV.toString() + "]" );
        notifyLocationUpdate( locationDV );
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive( Context context, Intent intent )
    {
        try
        {
            // send broadcast when provider disabled
            handleProviderDisable( context, intent );
            // fire location update event
            onLocationUpated( LocationDV.valueOf( (Location) intent.getExtras().get( LocationManager.KEY_LOCATION_CHANGED ) ) );

        }
        catch( InValidLocationDVException e )
        {
            LL.e( this.getClass().getSimpleName() + " " + e.toString() );
        }
    }

    /**
     * Send a system broadcast when provider disable
     * 
     * @param context
     * @param intent
     */
    private void handleProviderDisable( Context context, Intent intent )
    {
        LL.d( "->handle disabled provider event" );
        String providerEnabledKey = LocationManager.KEY_PROVIDER_ENABLED;
        if( intent.hasExtra( providerEnabledKey ) )
        {
            if( !intent.getBooleanExtra( providerEnabledKey, true ) )
            {
                LL.d( "->send disabled provider event" );
                context.sendBroadcast( providerDisabledIntent );
                LL.d( "<-send disabled provider event" );
            }
        }
        LL.d( "<-handle disabled provider event" );
    }

    /**
     * Set location update listener
     * 
     * @param listenerLocationUpdate
     */
    public static void setListenerLocationUpdate( OnLocationUpdateListener l )
    {
        listenerLocationUpdate = l;
    }

    /**
     * Tell the listener who watches location update that location has been changed.
     * 
     * @param locationDV
     */
    private void notifyLocationUpdate( LocationDV locationDV )
    {
        LL.d( "->notify location udpate in " + this.getClass().getSimpleName() );
        if( listenerLocationUpdate != null )
        {
            listenerLocationUpdate.onLocationUpdate( locationDV );
        }
        else
        {
            LL.e( "listenerLocationUpdate is NULL" );
        }
        LL.d( "<-notify location udpate in " + this.getClass().getSimpleName() );
    }

}
