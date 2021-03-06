package de.cellular.lib.geo;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.maps.GeoPoint;

/**
 * Wrapper for location object.
 * 
 * @author Xinyue
 * 
 */
public class LocationDV
{
    private static final int FIFTEEN_MINUTES    = 15 * 60 * 1000;
    private static final int TWO_HUNDRED_METERS = 200;

    public static class InValidLocationDVException extends Exception
    {

        private static final long serialVersionUID = -1197893685829822372L;

        public InValidLocationDVException(String detailMessage)
        {
            super( detailMessage );
        }

    }

    enum ProviderDV
    {
        GPS_PROVIDER,
        NETWORK_PROVIDER,
        UNKNOWN_PROVIDER,
        NULL
    }

    /**
     * Updated Android location object provided by receiver. See GPSLocationChangedReceiver and NetworkLocationChangedReceiver.
     */
    private Location location;
    /**
     * Flag. If current location is caught by network provider then true.
     */
    private boolean  isNetworkProvider;
    /**
     * Flag. If current location is caught by GPS provider then true.
     */
    private boolean  isGPSProvider;

    /**
     * latitude * 1E6
     */
    private long     latitudeE6;

    /**
     * longitude * 1E6
     */
    private long     longitudeE6;

    private LocationDV()
    {
    }

    /**
     * Convert location to locationDV
     * 
     * @param location
     * @return
     * @throws InValidLocationDVException
     */
    public static LocationDV valueOf( Location location ) throws InValidLocationDVException
    {
        LocationDV ret = new LocationDV();

        ret.location = location;
        if( ret.hasValidLocation() )
        {
            ret.isGPSProvider = LocationManager.GPS_PROVIDER.equals( ret.location.getProvider() );
            ret.isNetworkProvider = LocationManager.NETWORK_PROVIDER.equals( ret.location.getProvider() );
            ret.latitudeE6 = (long) (ret.location.getLatitude() * 1E6);
            ret.longitudeE6 = (long) (ret.location.getLongitude() * 1E6);
        }
        else
        {
            throw new InValidLocationDVException( "hasValidLocation() return true" );

        }

        return ret;
    }

    /**
     * If current location is caught by network provider then true.
     * 
     * @return boolean
     */
    public boolean isNetworkProvider() throws InValidLocationDVException
    {
        if( !hasValidLocation() )
            throw new InValidLocationDVException( "hasValidLocation() return true" );

        return isNetworkProvider;
    }

    /**
     * If current location is caught by GPS provider then true.
     * 
     * @return boolean
     */
    public boolean isGPSProvider() throws InValidLocationDVException
    {
        if( !hasValidLocation() )
            throw new InValidLocationDVException( "hasValidLocation() return true" );

        return isGPSProvider;
    }

    /**
     * Get latitude
     * 
     * @return double
     */
    public double getLatitude() throws InValidLocationDVException
    {
        if( !hasValidLocation() )
            throw new InValidLocationDVException( "hasValidLocation() return true" );

        return this.location.getLatitude();
    }

    /**
     * Get longitude
     * 
     * @return double
     */
    public double getLongitude() throws InValidLocationDVException
    {
        if( !hasValidLocation() )
            throw new InValidLocationDVException( "hasValidLocation() return true" );

        return this.location.getLongitude();
    }

    /**
     * Get latitude
     * 
     * @return long
     */
    public long getLatitudeE6() throws InValidLocationDVException
    {
        if( !hasValidLocation() )
            throw new InValidLocationDVException( "hasValidLocation() return true" );

        return latitudeE6;
    }

    /**
     * Get longitude
     * 
     * @return long
     */
    public long getLongitudeE6() throws InValidLocationDVException
    {
        if( !hasValidLocation() )
            throw new InValidLocationDVException( "hasValidLocation() return true" );

        return longitudeE6;
    }

    /**
     * Test whether Contains a null location object
     * 
     * @return boolean
     */
    public boolean hasValidLocation()
    {
        return this.location != null;
    }

    /**
     * Get accuracy
     * 
     * @return float
     * @throws InValidLocationDVException
     */
    public float getAccuracy() throws InValidLocationDVException
    {
        if( !hasValidLocation() )
            throw new InValidLocationDVException( "hasValidLocation() return true" );

        if( this.location.hasAccuracy() )
            return this.location.getAccuracy();
        else
            return 0;
    }

    /**
     * Get provider name
     * 
     * @return String provider name
     */
    public ProviderDV provider()
    {
        if( location != null )
            return isNetworkProvider ? ProviderDV.NETWORK_PROVIDER : (isGPSProvider ? ProviderDV.GPS_PROVIDER : ProviderDV.UNKNOWN_PROVIDER);

        return ProviderDV.NULL;
    }

    @Override
    public String toString()
    {
        if( !hasValidLocation() )
            return "Has invalid location";

        return this.location.toString();
    }

    /**
     * Convert location coordinate to mapView's coordinate
     * 
     * @param latitude
     * @param longitude
     * @return
     */
    public static GeoPoint latLonToPoint( double latitude, double longitude )
    {
        Double lat = latitude * 1E6d;
        Double lng = longitude * 1E6d;
        GeoPoint point = new GeoPoint( lat.intValue(), lng.intValue() );
        return point;
    }

    /**
     * Convert location coordinate to mapView's coordinate
     * 
     * @return GeoPoint
     * @throws InValidLocationDVException
     */
    public GeoPoint toGeoPoint() throws InValidLocationDVException
    {
        if( !hasValidLocation() )
            throw new InValidLocationDVException( "hasValidLocation() return true" );

        Double lat = location.getLatitude() * 1E6d;
        Double lng = location.getLongitude() * 1E6d;
        GeoPoint point = new GeoPoint( lat.intValue(), lng.intValue() );
        return point;
    }

    /**
     * Compare to locationDV. When locationDV is better, the return value is locationDV, otherwise is "this".
     * 
     * @param locationDV
     * @return LocationDV.
     */
    public LocationDV compareTo( LocationDV locationDV )
    {
        Location loc1 = this.location;
        Location loc2 = locationDV.location;

        /* Null location will be discarded. */
        if( loc1 == null )
        {
            return locationDV;
        }

        if( loc2 == null )
        {
            return this;
        }

        /* Compare two locations with time */
        // Check whether the new location fix is newer or older
        long timeDelta = loc1.getTime() - loc2.getTime();
        boolean isSignificantlyNewer = timeDelta > FIFTEEN_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -FIFTEEN_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if( isSignificantlyNewer )
        {
            return this;
            // If the new location is more than two minutes older, it must be worse
        }
        else if( isSignificantlyOlder )
        {
            return locationDV;
        }

        // Check whether the new location fix is more or less accurate
        // small value accuracy is better.
        int accuracyDelta = (int) (loc1.getAccuracy() - loc2.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > TWO_HUNDRED_METERS;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider( loc1.getProvider(),
                loc2.getProvider() );

        // Determine location quality using a combination of timeliness and accuracy
        if( isMoreAccurate )
        {
            return this;
        }
        else if( isNewer && !isLessAccurate )
        {
            return this;
        }
        else if( isNewer && !isSignificantlyLessAccurate && isFromSameProvider )
        {
            return this;
        }
        return locationDV;
    }

    /**
     * Checks whether two providers are the same
     * 
     * @param provider1
     * @param provider2
     * @return boolean
     */
    private static boolean isSameProvider( String provider1, String provider2 )
    {
        if( provider1 == null )
        {
            return provider2 == null;
        }
        return provider1.equals( provider2 );
    }

}
