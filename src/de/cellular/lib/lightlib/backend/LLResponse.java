/**
 * Copyright (C) 2012 Cellular GmbH 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cellular.lib.lightlib.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.text.TextUtils;
import de.cellular.lib.lightlib.log.LLL;

/**
 * {@link LLResponse} encapsulates the necessary information for the corresponding {@link LLRequest}.
 * <p>
 * The class and it's subclasses can't be initialized. However they(exclude {@link LLResponse}) could be decorated with a {@link LLResponse} instance that is created after the {@link DefaultHttpClient} has finished execution.
 * <p>
 * <strong>Known subclasses are</strong>
 * <p>
 * {@link LLImageResponse}
 * <p>
 * {@link LLFileResponse}
 * <p>
 * 
 * @version 1.0
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 * 
 */
public class LLResponse extends LLBaseResponse
{
    private InputStream  mStream;
    private List<Cookie> mCookies;
    private String       mCachedText;

    /**
     * Creates the instance of {@link LLResponse}
     * 
     * @since 1.0
     * @param _urlStr
     *            the target url in {@link String}
     * @param _client
     *            the {@link DefaultHttpClient} with which we fired {@link LLRequest}.
     * @param _response
     *            the {@link HttpResponse} implementing object.
     * @return the created {@link LLResponse}.
     * @throws IllegalStateException
     *             the illegal state exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static LLResponse createInstance( String _urlStr, DefaultHttpClient _client, HttpResponse _response )
            throws IllegalStateException, IOException {
        if( _response != null ) {
            LLResponse r = new LLResponse( _urlStr, _client, _response );
            int statusCode = r.mResponse.getStatusLine().getStatusCode();
            HttpEntity entity = r.mResponse.getEntity();
            if( statusCode == HttpStatus.SC_OK && entity != null ) {
                InputStream instream = entity.getContent();
                Header contentType = r.mResponse.getFirstHeader( "Content-Type" );
                Header encoding = r.mResponse.getFirstHeader( "Content-Encoding" );

                if( (encoding != null && encoding.getValue().equalsIgnoreCase( "gzip" ))
                        || (contentType != null && contentType.getValue().equalsIgnoreCase( "application/x-gzip" )) ) {
                    r.mStream = new GZIPInputStream( instream );
                }
                else {
                    r.mStream = instream;
                }
                if( r.mClient.getCookieStore() != null ) {
                    r.mCookies = r.mClient.getCookieStore().getCookies();
                }
                return r;
            }
            return null;
        }
        else {
            LLL.e( ":( Can't create an instance with NULL HttpResponse." );
            return null;
        }
    }
 

    /**
     * Instantiates a new {@link LLResponse}.
     * 
     * @since 1.0
     * @param _urlStr
     *            the target url in {@link String}
     * @param _client
     *            the {@link DefaultHttpClient} with which we fired {@link LLRequest}.
     * @param _response
     *            the {@link HttpResponse} implementing object.
     */
    private LLResponse( String _urlStr, DefaultHttpClient _client, HttpResponse _response ) {
        super( _urlStr, _client, _response );
    }

    /**
     * Instantiates a new {@link LLResponse}.
     * @since 1.0
     */
    protected LLResponse() {
        super();
    }


    @Override
    public String toString() {
        return "Response@" + getUrlStr();
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
            LLL.w( ":| Try to get a cached text after calling release." );
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

    @Override
    public void release() throws IOException {
        if( mStream != null ) {
            mStream.close();
            mStream = null;
        }
        super.release();
    }

    @Override
    public List<Cookie> getCookies() {
        return mCookies;
    }

    @Override
    public InputStream getInputStream() {
        return mStream;
    }
}
