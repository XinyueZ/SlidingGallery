package de.cellular.lib.location.base;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.location.Criteria;
import android.location.LocationManager;

public abstract class LocationUpdateRequester
{

    protected static int      DEFAULT_RADIUS = 150;
    protected static int      MAX_DISTANCE   = DEFAULT_RADIUS / 2;
    protected static long     MAX_TIME       = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

    protected LocationManager locationManager;

    protected LocationUpdateRequester(LocationManager locationManager)
    {
        this.locationManager = locationManager;
    }

    public abstract void requestGPSUpdates( long minTime, long minDistance,
             PendingIntent pendingIntent );

    public abstract void requestNetworkUpdates( long minTime, long minDistance,
            PendingIntent pendingIntent );

    public abstract void requestUnknownBestUpdates( long minTime, long minDistance, Criteria criteria,
            PendingIntent pendingIntent );

    public abstract void requestPassiveLocationUpdates( long minTime, long minDistance,
            PendingIntent pendingIntent );
}
