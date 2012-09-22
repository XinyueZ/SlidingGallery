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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import de.cellular.lib.lightlib.backend.base.LLAbstractResponse;

/**
 * The Class LLFileResponse that decorates a {@link LLAbstractResponse}. Additional to {@link LLAbstractResponse} that it contains a ref to the downloaded file.
 * 
 * @version 1.0
 * @see {@link http://en.wikipedia.org/wiki/Decorator_pattern}
 *      <p>
 *      To know more about the Decorator pattern from GOF.
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 */
public class LLFileResponse extends LLHttpClientResponse {
    private File           mOutput;
    private LLAbstractResponse mBaseResponse;

    /**
     * Instantiates a new {@link LLFileResponse}.
     * 
     * @since 1.0
     * @param _baseResponse
     *            the response
     * @param _file
     *            the downloaded {@link File}
     */
    public LLFileResponse( LLHttpClientBaseResponse _baseResponse, File _file ) {
        super();
        mBaseResponse = _baseResponse;
        mOutput = _file;
    }

    /**
     * Gets the downloaded {@link File}.
     * 
     * @since 1.0
     * @return the downloaded file
     */
    public File getOutputFile() {
        return mOutput;
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
