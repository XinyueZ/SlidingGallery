package de.cellular.lib.location;
 

import android.app.PendingIntent;
import android.location.Criteria;
import android.location.LocationManager;
import de.cellular.lib.lightlib.log.LL;
import de.cellular.lib.location.base.LocationUpdateRequester;

public class LegacyLocationUpdateRequester extends LocationUpdateRequester
{

    public LegacyLocationUpdateRequester(LocationManager locationManager)
    {
        super( locationManager );

    }

    @Override
    public void requestGPSUpdates( long minTime, long minDistance, PendingIntent pendingIntent )
    {
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, minTime, minDistance, pendingIntent );
    }

    @Override
    public void requestNetworkUpdates( long minTime, long minDistance, PendingIntent pendingIntent )
    {
        locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, minTime, minDistance, pendingIntent );
    }

    @Override
    public void requestPassiveLocationUpdates( long minTime, long minDistance, PendingIntent pendingIntent )
    {
    }

    @Override
    public void requestUnknownBestUpdates( long minTime, long minDistance, Criteria criteria,
            PendingIntent pendingIntent )
    {
        if( Criteria.ACCURACY_FINE != criteria.getAccuracy() )
        {
            LL.e( "Did not set ACCURACY_FINE for criteria's accuracy. This function needs it." );
        }
        else
        {
            String provider = locationManager.getBestProvider( criteria, true );
            if( provider != null )
                locationManager.requestLocationUpdates( provider, minTime, minDistance, pendingIntent );
        }
    }
}
