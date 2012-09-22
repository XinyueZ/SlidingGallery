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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import de.cellular.lib.lightlib.log.LL;

/**
 * {@link LLHttpClientResponse} encapsulates the necessary information for the corresponding {@link LLRequest}.
 * <p>
 * The class and it's subclasses can't be initialized. However they(exclude {@link LLHttpClientResponse}) could be decorated with a {@link LLHttpClientResponse} instance that is created after the {@link DefaultHttpClient} has finished execution.
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
public class LLHttpClientResponse extends LLHttpClientBaseResponse {
    protected List<Cookie> mCookies;

    /**
     * Creates the instance of {@link LLHttpClientResponse}
     * 
     * @since 1.0
     * @param _urlStr
     *            the target url in {@link String}
     * @param _client
     *            the {@link DefaultHttpClient} with which we fired {@link LLRequest}.
     * @param _response
     *            the {@link HttpResponse} implementing object.
     * @return the created {@link LLHttpClientResponse}.
     * @throws IllegalStateException
     *             the illegal state exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static LLHttpClientResponse createInstance( String _urlStr, DefaultHttpClient _client, HttpResponse _response )
            throws IllegalStateException, IOException {
        if( _response != null ) {
            LLHttpClientResponse r = new LLHttpClientResponse( _urlStr, _client, _response );
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
            LL.e( ":( Can't create an instance with NULL HttpResponse." );
            return null;
        }
    }

    /**
     * Instantiates a new {@link LLHttpClientResponse}.
     * 
     * @since 1.0
     * @param _urlStr
     *            the target url in {@link String}
     * @param _client
     *            the {@link DefaultHttpClient} with which we fired {@link LLRequest}.
     * @param _response
     *            the {@link HttpResponse} implementing object.
     */
    private LLHttpClientResponse( String _urlStr, DefaultHttpClient _client, HttpResponse _response ) {
        super( _urlStr, _client, _response );
    }

    /**
     * Instantiates a new {@link LLHttpClientResponse}.
     * 
     * @since 1.0
     */
    public LLHttpClientResponse() {
        super();
    }
 
    /**
     * Gets the org.apache.http.cookie.Cookies.
     *
     * @return the cookies   
     */
    public List<Cookie> getCookies() {
        return mCookies;
    }
}
