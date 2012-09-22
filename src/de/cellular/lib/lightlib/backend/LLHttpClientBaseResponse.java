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

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

import de.cellular.lib.lightlib.backend.base.LLAbstractResponse;
import de.cellular.lib.lightlib.log.LL;

/**
 * A base class for all response which are returned by {@link LLRequest}. It is not an abstract class for empty response.
 * 
 * @see http://en.wikipedia.org/wiki/Null_object_pattern for more about "Null Object pattern".
 * 
 *      <strong>Known subclasses are</strong>
 *      <p>
 *      {@link LLHttpClientResponse}
 *      <p>
 * @version 1.0
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 * 
 */
public class LLHttpClientBaseResponse extends LLAbstractResponse {
    protected DefaultHttpClient mClient;
    protected HttpResponse      mResponse;

    /**
     * Instantiates a new {@link LLHttpClientBaseResponse}.
     * 
     * @since 1.0
     * @param _urlStr
     *            the target url in {@link String}
     * @param _client
     *            the {@link DefaultHttpClient} with which we fired {@link LLRequest}.
     * @param _response
     *            the {@link HttpResponse} implementing object.
     */
    public LLHttpClientBaseResponse( String _urlStr, DefaultHttpClient _client, HttpResponse _response ) {
        mUrlStr = _urlStr;
        mClient = _client;
        mResponse = _response;
    }

    /**
     * Instantiates a new {@link LLHttpClientBaseResponse}. 
     * @since 1.0
     */
    protected LLHttpClientBaseResponse() {
    }

    /**
     * Release resource.
     * 
     * @since 1.0
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void release() throws IOException {
        super.release();
        if( mClient != null ) {
            mClient.getConnectionManager().shutdown();
            mClient = null;
        }

        LL.i( ":| <------" + getClass().getSimpleName() + " has been released ------>" );
    } 
}
