package de.cellular.lib.lightlib.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;

/**
 * The Class DataUtils.
 */
public class DataUtils {
    public static String encode( String _keywords )
    {
        try
        {
            return URLEncoder.encode( _keywords, "UTF-8" );
        }
        catch( UnsupportedEncodingException _e1 )
        {
            return new String( _keywords.trim().replace( " ", "%20" ).replace( "&", "%26" )
                    .replace( ",", "%2c" ).replace( "(", "%28" ).replace( ")", "%29" )
                    .replace( "!", "%21" ).replace( "=", "%3D" ).replace( "<", "%3C" )
                    .replace( ">", "%3E" ).replace( "#", "%23" ).replace( "$", "%24" )
                    .replace( "'", "%27" ).replace( "*", "%2A" ).replace( "-", "%2D" )
                    .replace( ".", "%2E" ).replace( "/", "%2F" ).replace( ":", "%3A" )
                    .replace( ";", "%3B" ).replace( "?", "%3F" ).replace( "@", "%40" )
                    .replace( "[", "%5B" ).replace( "\\", "%5C" ).replace( "]", "%5D" )
                    .replace( "_", "%5F" ).replace( "`", "%60" ).replace( "{", "%7B" )
                    .replace( "|", "%7C" ).replace( "}", "%7D" ) );
        }
    }

    public static String formatDistance( double dist )
    {
        DecimalFormat df = null;
        String diststr = "";

        if( dist >= 1000.0 )
        {
            dist = dist / 1000.0;
            df = new DecimalFormat( "####.0" );
            diststr = df.format( dist ) + " km";
        }
        else
        {
            df = new DecimalFormat( "####" );
            diststr = df.format( dist ) + " m";
        }

        return diststr;
    }

    public static String formatDistanceWithoutUnit( double dist )
    {
        DecimalFormat df = null;
        String diststr = "";

        if( dist >= 1000.0 )
        {
            dist = dist / 1000.0;
            df = new DecimalFormat( "####.0" );
            diststr = df.format( dist );
        }
        else
        {
            df = new DecimalFormat( "####" );
            diststr = df.format( dist );
        }

        return diststr;
    }

    public static String calcMinutesSeconds( double t )
    {
        long timeInSeconds = Math.round( t );
        // Logger.i(TAG, "Duration = " + t + ".........................");
        long hours, minutes, seconds;
        hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds - (hours * 3600);
        minutes = timeInSeconds / 60;
        timeInSeconds = timeInSeconds - (minutes * 60);
        seconds = timeInSeconds;
        if( hours == 0 && minutes == 0 && seconds == 0 )
            return "soon";
        else
        {
            StringBuilder sb = new StringBuilder();
            // sb.append(hours);
            // sb.append(":");
            sb.append( toDateString( minutes ) );
            sb.append( ":" );
            sb.append( toDateString( seconds ) );
            return sb.toString();
        }
    }

    private static String toDateString( long time )
    {
        if( time < 10 )
        {
            return "0" + time;
        }
        return String.valueOf( time );
    }

    public static String calcHourMinutesSeconds( double t )
    {
        long timeInSeconds = Math.round( t );
        // Logger.i(TAG, "Duration = " + t + ".........................");
        long hours, minutes, seconds;
        hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds - (hours * 3600);
        minutes = timeInSeconds / 60;
        timeInSeconds = timeInSeconds - (minutes * 60);
        seconds = timeInSeconds;
        if( hours == 0 && minutes == 0 && seconds == 0 )
            return "soon";
        else
        {
            StringBuilder sb = new StringBuilder();
            sb.append( hours );
            sb.append( ":" );
            sb.append( toDateString( minutes ) );
            sb.append( ":" );
            sb.append( toDateString( seconds ) );
            return sb.toString();
        }
    }
}
