package de.cellular.lib.location.base;

import android.location.Location;

public interface ILastLocationFinder
{
    public Location getLastBestLocation( int minDistance, long minTime );

    // public void setChangedLocationListener(LocationListener l);
    //
    // public void cancel();
}
