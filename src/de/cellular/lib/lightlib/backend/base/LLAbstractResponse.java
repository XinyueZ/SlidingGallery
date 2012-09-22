package de.cellular.lib.lightlib.backend.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import de.cellular.lib.lightlib.log.LL;

/**
 * The abstract structure for a response.
 * 
 * @author Chris. Z <hasszhao@gmail.com>
 */
public abstract class LLAbstractResponse {
    protected String              mUrlStr;
    protected InputStream         mStream;
    protected String              mCachedText;
    protected Map<String, Object> mTags = new HashMap<String, Object>();

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + getUrlStr();
    }

    /**
     * Gets the tags for more information from the response.
     * 
     * @since 1.0
     * @return the key-value collection that contains info of response.
     */
    public Map<String, Object> getTags() {
        return mTags;
    }

    /**
     * Sets more information on the response in the tag.
     * 
     * @since 1.0
     * @param _key
     *            the _key
     * @param _value
     *            the _value
     */
    public void setTag( String _key, Object _value ) {
        mTags.put( _key, _value );
    }

    /**
     * Gets the target url in {@link String}.
     * 
     * @since 1.0
     * @return the url str
     */
    public String getUrlStr() {
        return mUrlStr;
    }

    /**
     * Gets the input stream.
     * 
     * @since 1.0
     * @return the input stream
     */
    public InputStream getInputStream() {
        return mStream;
    }

    /**
     * Release resource
     * 
     * @since 1.0
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void release() throws IOException {
        if( mStream != null ) {
            mStream.close();
            mStream = null;
        }
    }
    
    /**
     * Cach content. <br>
     * <strong>After this method is called, {@link #mStream} can't be used.</strong>
     * 
     * @since 1.0
     * @return the string
     */
    public String cachContent() {
        if( mStream == null ) {
            LL.w( ":| Try to get a cached text after calling release." );
            return null;
        }
        else if( TextUtils.isEmpty( mCachedText ) ) {
            try {
                BufferedReader br = new BufferedReader( new InputStreamReader( mStream ), 1024 * 1024 );
                StringBuilder sb = new StringBuilder();
                String line = null;

                while( (line = br.readLine()) != null ) {
                    sb.append( line );
                }

                br.close();
                return mCachedText = sb.toString();
            }
            catch( Exception _e ) {
                return null;
            }
        }
        else {
            return mCachedText;
        }
    }
}
