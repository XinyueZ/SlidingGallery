package de.cellular.lib.location;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import de.cellular.lib.location.base.ILastLocationFinder;

public class GingerbreadLastLocationFinder implements ILastLocationFinder
{

    private LocationManager _locationManager;

    public GingerbreadLastLocationFinder(Context context)
    {
        _locationManager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
    }

    public Location getLastBestLocation( int minDistance, long minTime )
    {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        List<String> matchingProviders = _locationManager.getAllProviders();
        for( String provider : matchingProviders )
        {
            Location location = _locationManager.getLastKnownLocation( provider );
            if( location != null )
            {
                float accuracy = location.getAccuracy();
                long time = location.getTime();

                if( (time < minTime && (location.hasAccuracy() && accuracy < bestAccuracy)) )
                {
                    bestResult = location;
                    bestAccuracy = accuracy;
                }
            }
        }
        return bestResult;
    }
}
