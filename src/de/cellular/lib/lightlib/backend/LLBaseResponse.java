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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import de.cellular.lib.lightlib.log.LLL;

/**
 * A base class for all response which are returned by {@link LLRequest}. It is not an abstract class for empty response.
 * 
 * @see http://en.wikipedia.org/wiki/Null_object_pattern for more about "Null Object pattern".
 * 
 *      <strong>Known subclasses are</strong>
 *      <p>
 *      {@link LLResponse}
 *      <p>
 * @version 1.0
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 * 
 */
public class LLBaseResponse
{
    protected DefaultHttpClient mClient;
    protected HttpResponse      mResponse;
    private String              mUrlStr;
    private Map<String, Object> mTags = new HashMap<String, Object>();

    /**
     * Instantiates a new {@link LLBaseResponse}.
     * 
     * @since 1.0
     * @param _urlStr
     *            the target url in {@link String}
     * @param _client
     *            the {@link DefaultHttpClient} with which we fired {@link LLRequest}.
     * @param _response
     *            the {@link HttpResponse} implementing object.
     */
    public LLBaseResponse( String _urlStr, DefaultHttpClient _client, HttpResponse _response ) {
        mUrlStr = _urlStr;
        mClient = _client;
        mResponse = _response;
    }

    /**
     * Instantiates a new {@link LLBaseResponse}. 
     * @since 1.0
     */
    protected LLBaseResponse() {
    }

    /**
     * Release resource.
     * 
     * @since 1.0
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void release() throws IOException {
        if( mClient != null ) {
            mClient.getConnectionManager().shutdown();
            mClient = null;
        }

        LLL.i( ":| <------" + getClass().getSimpleName() + " has been released ------>" );
    }

    /**
     * Gets the cookies' list.
     * 
     * @since 1.0
     * @return the cookies
     */
    public List<Cookie> getCookies() {
        return null;
    }

    /**
     * Gets the input stream.
     * 
     * @since 1.0
     * @return the input stream
     */
    public InputStream getInputStream() {
        return null;
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
}
