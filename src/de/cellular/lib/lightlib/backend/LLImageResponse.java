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
import java.util.Map;

import org.apache.http.cookie.Cookie;

import android.graphics.Bitmap;

/**
 * The Class LLImageResponse that decorates a {@link LLResponse}. Additional to {@link LLResponse} that it contains a ref to the decoded {@link Bitmap}.
 * 
 * @version 1.0
 * @see {@link http://en.wikipedia.org/wiki/Decorator_pattern}
 *      <p>
 *      To know more about the Decorator pattern from GOF.
 * @see {@link LLRequestImage#readStreamToBitmap(LLImageResponse)} for more info.
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 */
public class LLImageResponse extends LLResponse
{
    private Bitmap         mBitmap;
    private LLBaseResponse mBaseResponse;

    /**
     * Instantiates a new {@link LLImageResponse}.
     * 
     * @since 1.0
     * @param _baseResponse
     *            the response
     */
    public LLImageResponse( LLBaseResponse _baseResponse ) {
        super();
        mBaseResponse = _baseResponse;
    }

    /**
     * Gets the decoded {@link Bitmap}.
     * 
     * @since 1.0
     * @see {@link LLRequestImage#readStreamToBitmap(LLImageResponse)} for more info.
     * 
     * @return the bitmap
     */
    public Bitmap getBitmap() {
        return mBitmap;
    }

    /**
     * Sets the {@link Bitmap}.
     * 
     * @since 1.0
     * @see {@link LLRequestImage#readStreamToBitmap(LLImageResponse)} for more info.
     * 
     * @param _bitmap
     *            the decoded bitmap
     */
    public void setBitmap( Bitmap _bitmap ) {
        mBitmap = _bitmap;
    }

    @Override
    public List<Cookie> getCookies() {
        return mBaseResponse.getCookies();
    }

    @Override
    public InputStream getInputStream() {
        return mBaseResponse.getInputStream();
    }

    @Override
    public String getUrlStr() {
        return mBaseResponse.getUrlStr();
    }

    @Override
    public void release() throws IOException {
        mBaseResponse.release();
    }

    @Override
    public String toString() {
        return "Image-Response@" + getUrlStr();
    }

    @Override
    public Map<String, Object> getTags() {
        return mBaseResponse.getTags();
    }

    @Override
    public void setTag( String _key, Object _value ) {
        mBaseResponse.setTag( _key, _value );
    }
}
