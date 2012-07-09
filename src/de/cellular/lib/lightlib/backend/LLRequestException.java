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


@SuppressWarnings("serial")
public class LLRequestException extends Exception {
    private Exception mExp;
    private String    mUrlStr;
    private Object    mTag;

    public LLRequestException( Exception _e, String _urlStr ) {
        super();
        mExp = _e;
        mUrlStr = _urlStr;
    }
  
    /**
     * @return the tag
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * @param _tag
     *            the tag to set
     */
    public void setTag( Object _tag ) {
        mTag = _tag;
    }

    /**
     * @return the url
     */
    public String getUrlStr() {
        return mUrlStr;
    }

    /**
     * @param _urlStr
     *            the url to set
     */
    public void setUrlStr( String _urlStr ) {
        mUrlStr = _urlStr;
    }

    @Override
    public String toString() {
        if( mExp != null ){
            return mExp.toString();
        }
        else{
            return "LLRequestException on " + getUrlStr();
        }
    }
}
