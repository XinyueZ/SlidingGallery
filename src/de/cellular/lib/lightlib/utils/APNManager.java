package de.cellular.lib.lightlib.utils;

import java.lang.reflect.Method;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.text.TextUtils;
import de.cellular.lib.lightlib.log.LL;

/**
 * Manager for operating APN data on the phone.
 * 
 * @author Chris. Z inspired by {@link http://blogs.msdn.com/b/zhengpei/archive/2009/10/13/managing-apn-data-in-google-android.aspx}
 * 
 */
public class APNManager {
    public static final int     OPERATION_FAILED  = 0x89;
    private static final String TAG               = APNManager.class.getSimpleName();
    private Context             mContext;

    /*
     * Information of all APNs Details can be found in com.android.providers.telephony.TelephonyProvider
     */
    public static final Uri     APN_TABLE_URI     =
                                                          Uri.parse( "content://telephony/carriers" );
    /*
     * Information of the preferred APN
     */
    public static final Uri     PREFERRED_APN_URI =
                                                          Uri.parse( "content://telephony/carriers/preferapn" );
    private static APNManager   sInstance;

    /**
     * Singleton method
     * 
     * @param _context
     * @return
     */
    public static APNManager getInstance( Context _context ) {
        if( sInstance == null ) {
            synchronized( APNManager.class ) {
                if( sInstance == null ) {
                    sInstance = new APNManager( _context );
                }
            }
        }
        return sInstance;
    }

    private APNManager( Context _context ) {
        mContext = _context;
    }

    private static String getSerialNum() {
        try {
            Class<?> c = Class.forName( "android.os.SystemProperties" );
            Method get = c.getMethod( "get", String.class, String.class );
            return (String) (get.invoke( c, "gsm.sim.operator.numeric", "unknown" ));
        }
        catch( Exception ignored ) {
            return null;
        }
    }

    /**
     * Adds the apn. When there exists the content that has the _name, do update otherwise insert new.
     * 
     * @param _name
     *            the _name
     * @param _keyValues
     *            the _key values
     */
    public int addAPN( String _name, ContentValues _keyValues ) {
        if( findByName( _name ) ) {
            return updateAPN( _name, _keyValues );
        }
        else {
            return insertAPN( _keyValues );
        }
    }

    /**
     * Insert a new APN entry into the system APN table Require an apn name, and the apn address. More can be added. Return an id (_id) that is automatically generated for the new apn entry.
     * 
     * 
     * The following three field values are for testing in Android emulator only The APN setting page UI will ONLY display APNs whose 'numeric' filed is TelephonyProperties.PROPERTY_SIM_OPERATOR_NUMERIC. On Android emulator, this value is 310260, where 310
     * is mcc, and 260 mnc. With these field values, the newly added apn will appear in system UI.
     * 
     * @param _keyValues
     *            the ContentValues.
     * 
     * 
     * 
     * @see To get more information about getting umeric of a SIM card. http://blog.163.com/tony_8014/blog/static/1833321972011926105948171/
     */
    public int insertAPN( ContentValues _keyValues ) {
        String serialnum = getSerialNum();
        if( !TextUtils.isEmpty( serialnum ) ) {
            int id = -1;
            try {
                ContentResolver resolver = mContext.getContentResolver();
                _keyValues.put( "numeric", serialnum );
                Uri newRow = resolver.insert( APN_TABLE_URI, _keyValues );
                if( newRow != null ) {
                    LL.d( TAG, "Newly added APN.." );
                }
            }
            catch( SQLException e ) {
                return OPERATION_FAILED;
            }
            return id;
        }
        else {
            return OPERATION_FAILED;
        }
    }

    private String makeWhere( String _name ) {
        return new StringBuilder().append( "name='" ).append( _name ).append( '\'' ).toString();
    }

    /**
     * Update APN
     * 
     * The following three field values are for testing in Android emulator only The APN setting page UI will ONLY display APNs whose 'numeric' filed is TelephonyProperties.PROPERTY_SIM_OPERATOR_NUMERIC. On Android emulator, this value is 310260, where 310
     * is mcc, and 260 mnc. With these field values, the newly added apn will appear in system UI.
     * 
     * @param _keyValues
     *            the ContentValues.
     * @return
     */
    public int updateAPN( String _name, ContentValues _keyValues ) {
        String serialnum = getSerialNum();
        if( !TextUtils.isEmpty( serialnum ) ) {
            int id = -1;
            try {
                _keyValues.put( "numeric", serialnum );
                ContentResolver resolver = mContext.getContentResolver();
                int updatedRows = resolver.update( APN_TABLE_URI, _keyValues, makeWhere( _name ), null );
                if( updatedRows > 0 ) {
                    LL.d( TAG, "Updated APN:" + _name );
                }
            }
            catch( SQLException e ) {
                return OPERATION_FAILED;
            }
            return id;
        }
        else {
            return OPERATION_FAILED;
        }
    }

    private boolean findByName( String _name ) {
        String serialnum = getSerialNum();
        if( !TextUtils.isEmpty( serialnum ) ) {
            try {
                ContentResolver resolver = mContext.getContentResolver();
                Cursor cursor = resolver.query( APN_TABLE_URI, new String[] { "name" }, makeWhere( _name ), null, null );
                int count = cursor.getCount();
                if( cursor != null ) {
                    cursor.close();
                }
                return count > 0;
            }
            catch( SQLException e ) {
                return false;
            }
        }
        else {
            return false;
        }
    }
}
