package de.cellular.lib.location;

import android.content.Context;
import android.location.LocationManager;
import de.cellular.lib.location.base.ILastLocationFinder;
import de.cellular.lib.location.base.LocationUpdateRequester;

public class PlatformSpecificImplementationFactory
{
    private static boolean SUPPORTS_GINGERBREAD = android.os.Build.VERSION.SDK_INT >= 0x00000009;

    // public static boolean SUPPORTS_HONEYCOMB =
    // android.os.Build.VERSION.SDK_INT >=
    // android.os.Build.VERSION_CODES.HONEYCOMB;
    // private static boolean SUPPORTS_FROYO = android.os.Build.VERSION.SDK_INT
    // >= android.os.Build.VERSION_CODES.FROYO;
    // private static boolean SUPPORTS_ECLAIR = android.os.Build.VERSION.SDK_INT
    // >= android.os.Build.VERSION_CODES.ECLAIR;

    public static ILastLocationFinder getLastLocationFinder( Context context )
    {
        return SUPPORTS_GINGERBREAD ? new GingerbreadLastLocationFinder( context ) : new LegacyLastLocationFinder( context );
    }

    public static LocationUpdateRequester getLocationUpdateRequester( LocationManager locationManager )
    {
        return SUPPORTS_GINGERBREAD ? new GingerbreadLocationUpdateRequester( locationManager ) : new LegacyLocationUpdateRequester( locationManager );// new
    }

}
