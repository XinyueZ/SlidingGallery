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

import org.apache.http.cookie.Cookie;

/**
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 *
 */
public class LLBaseResponse 
{ 
    private String            mUrlStr;
    private Map<String, Object> mTags = new HashMap<String, Object>();
    
    public LLBaseResponse( String _urlStr ) {
        super();
        mUrlStr = _urlStr;
    }

    public void release() throws IOException { 
    }

    public List<Cookie> getCookies() { 
        return null;
    }

    public InputStream getInputStream() { 
        return null;
    }

    public String getUrlStr() { 
        return mUrlStr;
    } 
    
    @Override
    public String toString() {
        return "Base-Response@" + mUrlStr;
    }

    
    public Map<String, Object> getTags() {
        return mTags;
    }

    public void setTag( String _key, Object _value ) {
        mTags.put( _key, _value );
    }
}
