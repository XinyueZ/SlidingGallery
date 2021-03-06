package de.cellular.lib.geo;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.maps.GeoPoint;

import de.cellular.lib.lightlib.log.LL;

/**
 * Wrapper Address object
 * 
 * @author Xinyue
 * 
 */
public class AddressDV
{
    public static class InvalidAddressDVException extends Exception
    {

        private static final long serialVersionUID = 6992157271848510046L;

        public InvalidAddressDVException(String detailMessage)
        {
            super( detailMessage );
        }

    }

    /**
     * Android address object
     */
    private Address address;

    private double  latitudeE6;

    private double  longitudeE6;

    private AddressDV()
    {

    }

    /**
     * Latitude * 1000000
     * 
     * @return Latitude * 1000000
     * @throws InvalidAddressDVException
     */
    public double getLatitudeE6() throws InvalidAddressDVException
    {
        if( hasInvalidAddress() )
            throw new InvalidAddressDVException( "hasInvalidAddress() return true" );
        return this.latitudeE6;
        // return this.address.getLatitude() * 1E6;
    }

    /**
     * Longitude * 1000000
     * 
     * @return Longitude * 1000000
     * @throws InvalidAddressDVException
     */
    public double getLongitudeE6() throws InvalidAddressDVException
    {
        if( hasInvalidAddress() )
            throw new InvalidAddressDVException( "hasInvalidAddress() return true" );
        return this.longitudeE6;
        // return this.address.getLongitude() * 1E6;
    }

    /**
     * Latitude
     * 
     * @return Latitude
     * @throws InvalidAddressDVException
     */
    public double getLatitude() throws InvalidAddressDVException
    {
        if( hasInvalidAddress() )
            throw new InvalidAddressDVException( "hasInvalidAddress() return true" );
        return this.address.getLatitude();
    }

    /**
     * Longitude
     * 
     * @return Longitude
     * @throws InvalidAddressDVException
     */
    public double getLongitude() throws InvalidAddressDVException
    {
        if( hasInvalidAddress() )
            throw new InvalidAddressDVException( "hasInvalidAddress() return true" );
        return this.address.getLongitude();
    }

    /**
     * Convert address to addressDV
     * 
     * @param address
     * @return
     * @throws InvalidAddressDVException
     */
    public static AddressDV valueOf( Address address ) throws InvalidAddressDVException
    {
        AddressDV ret = new AddressDV();

        ret.address = address;
        if( !ret.hasInvalidAddress() )
        {
            ret.editStreetNumberInAddress();
            ret.latitudeE6 = ret.address.getLatitude() * 1E6;
            ret.longitudeE6 = ret.address.getLongitude() * 1E6;
        }
        else
        {
            throw new InvalidAddressDVException( "hasInvalidAddress() return true" );
        }
        return ret;
    }

    /**
     * Convert GeoPoint to addressDV
     * 
     * @param Context
     * @param GeoPoint
     * @return
     * @throws InvalidAddressDVException
     */
    public static AddressDV valueOf( Context context, GeoPoint geoPoint ) throws InvalidAddressDVException
    {
        // convert GeoPoint to address
        Address address = null;
        try
        {
            /* post address request on Geocoder from Google Inc. */
            Geocoder geoCoder = new Geocoder(
                    context );
            List<Address> addresses = geoCoder.getFromLocation(
                    geoPoint.getLatitudeE6() / 1E6,
                    geoPoint.getLongitudeE6() / 1E6, 1 );

            /* get an address from list, often getting the first one */
            if( !addresses.isEmpty() )
            {
                LL.w( "Address list is empty." );
                address = addresses.listIterator().next();
            }
            LL.d( "Address list has been found." );
        }
        /* any error we ignore this GeoPoint to get address */
        catch( IOException e )
        {
            throw new InvalidAddressDVException( "Error while post request on Geocoder." );
        }
        catch( Exception e )
        {
            throw new InvalidAddressDVException( "Error while post request on Geocoder." );
        }

        /* call another version of valueOf further to create a DV version */
        return AddressDV.valueOf( address );

    }

    public boolean hasInvalidAddress()
    {
        return this.address == null;
    }

    /**
     * Format street in "xxxx num"
     */
    private void editStreetNumberInAddress()
    {
        /* split street name with name to lines */
        String[] streetWithNumber = address.getAddressLine( 0 ).trim().split( " " );

        if( streetWithNumber.length > 1 )
        {
            address.setFeatureName( "" );
            address.setThoroughfare( "" );
            StringBuilder streetName = new StringBuilder();

            for( String s : streetWithNumber )
            {

                if( s.matches( "[0-9|-]*" ) )
                {
                    /* find number from lines */
                    address.setFeatureName( s );
                }
                else
                {
                    /* find rest of street name */
                    streetName.append( s );
                    streetName.append( " " );
                }
            }
            address.setThoroughfare( streetName.toString() );
        }
        else
        {
            // street number is empty
            address.setFeatureName( "" );
        }
    }

    /**
     * Get street name with house number
     * 
     * @return String
     * @throws InvalidAddressDVException
     */
    public String getFullStreet() throws InvalidAddressDVException
    {
        return getStreet() + " " + getHouseNumber();
    }

    /**
     * Get steet name without house number
     * 
     * @return String
     * @throws InvalidAddressDVException
     */
    public String getStreet() throws InvalidAddressDVException
    {
        if( hasInvalidAddress() )
            throw new InvalidAddressDVException( "hasInvalidAddress() return true" );

        return address.getThoroughfare();
    }

    /**
     * Get house number
     * 
     * @return String
     * @throws InvalidAddressDVException
     */
    public String getHouseNumber() throws InvalidAddressDVException
    {
        if( hasInvalidAddress() )
            throw new InvalidAddressDVException( "hasInvalidAddress() return true" );

        return address.getFeatureName();
    }

    /**
     * Get post code
     * 
     * @return String
     * @throws InvalidAddressDVException
     */
    public String getPostalCode() throws InvalidAddressDVException
    {
        if( hasInvalidAddress() )
            throw new InvalidAddressDVException( "hasInvalidAddress() return true" );

        return address.getPostalCode();
    }

    /**
     * Get city
     * 
     * @return String
     * @throws InvalidAddressDVException
     */
    public String getCity() throws InvalidAddressDVException
    {
        if( hasInvalidAddress() )
            throw new InvalidAddressDVException( "hasInvalidAddress() return true" );

        return address.getLocality();
    }

    /**
     * Get city name with zip-code
     * 
     * @return String
     * @throws InvalidAddressDVException
     */
    public String getFullCity() throws InvalidAddressDVException
    {
        return getCity() + " " + getPostalCode();
    }
 

}
