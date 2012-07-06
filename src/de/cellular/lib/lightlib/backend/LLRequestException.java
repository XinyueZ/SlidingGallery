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

import java.io.PrintStream;
import java.io.PrintWriter;

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
 
    protected LLRequestException( String _detailMessage, Throwable _throwable ) {
        super( _detailMessage, _throwable );
    }

    protected LLRequestException( String _detailMessage ) {
        super( _detailMessage );
    }

    protected LLRequestException( Throwable _throwable ) {
        super( _throwable );
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

    /**
     * @param _o
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object _o ) {
        return mExp.equals( _o );
    }

    /**
     * @return
     * @see java.lang.Throwable#fillInStackTrace()
     */
    public Throwable fillInStackTrace() {
        return mExp.fillInStackTrace();
    }

    /**
     * @return
     * @see java.lang.Throwable#getCause()
     */
    public Throwable getCause() {
        return mExp.getCause();
    }

    /**
     * @return
     * @see java.lang.Throwable#getLocalizedMessage()
     */
    public String getLocalizedMessage() {
        return mExp.getLocalizedMessage();
    }

    /**
     * @return
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage() {
        return mExp.getMessage();
    }

    /**
     * @return
     * @see java.lang.Throwable#getStackTrace()
     */
    public StackTraceElement[] getStackTrace() {
        return mExp.getStackTrace();
    }

    /**
     * @return
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return mExp.hashCode();
    }

    /**
     * @param _throwable
     * @return
     * @see java.lang.Throwable#initCause(java.lang.Throwable)
     */
    public Throwable initCause( Throwable _throwable ) {
        return mExp.initCause( _throwable );
    }

    /**
     * 
     * @see java.lang.Throwable#printStackTrace()
     */
    public void printStackTrace() {
        mExp.printStackTrace();
    }

    /**
     * @param _err
     * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
     */
    public void printStackTrace( PrintStream _err ) {
        mExp.printStackTrace( _err );
    }

    /**
     * @param _err
     * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
     */
    public void printStackTrace( PrintWriter _err ) {
        mExp.printStackTrace( _err );
    }

    /**
     * @param _trace
     * @see java.lang.Throwable#setStackTrace(java.lang.StackTraceElement[])
     */
    public void setStackTrace( StackTraceElement[] _trace ) {
        mExp.setStackTrace( _trace );
    }

    /**
     * @return
     * @see java.lang.Throwable#toString()
     */
    public String toString() {
        return mExp.toString();
    }
}
