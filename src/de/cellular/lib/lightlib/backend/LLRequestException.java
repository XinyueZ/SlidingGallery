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

/**
 * The Class LLRequestException.
 * 
 * @version 1.0
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 */
@SuppressWarnings("serial")
public class LLRequestException extends Exception {
    private Exception mExp;
    private String    mUrlStr;
    private Object    mTag;

    /**
     * Instantiates a new {@link LLRequestException}.
     * 
     * @since 1.0
     * @param _e
     *            the original exception.
     * @param _urlStr
     *            the url in {@link String} of a request
     */
    public LLRequestException( Exception _e, String _urlStr ) {
        super();
        mExp = _e;
        mUrlStr = _urlStr;
    }

    /**
     * Gets more info about this exception
     * 
     * @since 1.0
     * @return the tag
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * Sets more information(objects) that associates with this exception
     * 
     * @since 1.0
     * @param _tag
     *            the info
     */
    public void setTag( Object _tag ) {
        mTag = _tag;
    }

    /**
     * Gets url
     * 
     * @since 1.0
     * @return the url
     */
    public String getUrlStr() {
        return mUrlStr;
    }

    /**
     * Sets url
     * 
     * @since 1.0
     * @param _urlStr
     *            the url in {@link String} of a request
     */
    public void setUrlStr( String _urlStr ) {
        mUrlStr = _urlStr;
    }

    @Override
    public String toString() {
        if( mExp != null ) {
            return mExp.toString();
        }
        else {
            return "LLRequestException on " + getUrlStr();
        }
    }
}
