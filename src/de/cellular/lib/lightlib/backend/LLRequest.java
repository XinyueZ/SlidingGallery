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
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.WebView;
import de.cellular.lib.lightlib.backend.base.LLRequestResponsibleObject;
import de.cellular.lib.lightlib.log.LLL;

/**
 * The abortable request object based on HttpClient. <br>
 * Corresponding with a {@link LLRequestResponsibleObject} object.
 * 
 * <p>
 * <strong>Known subclasses are</strong>
 * <p>
 * {@link LLRequestFile}
 * 
 * @version 1.0
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 * 
 */
public class LLRequest extends AsyncTask<Object, Object, Exception>
{
    private HttpRequestBase              mHttpRequestBase;

    protected static final int           TIME_OUT = 15 * 1000;
    protected String                     mUserAgent;
    protected LLRequestResponsibleObject mHandler;
    protected Context                    mContext;

    /**
     * The Method for Request Mode.
     * 
     * @since 1.0
     */
    public enum Method
    {
        /**
         * HTTP-GET mode.
         * 
         * @since 1.0
         */
        GET,

        /**
         * HTTP-POST mode. You can override {@link #onWritePostBody}
         * 
         * @since 1.0
         */
        POST,
    }

    private Method          mMethod;
    public static final int REQUEST_FAILED    = 0x34;
    public static final int REQUEST_SUCCESSED = 0x35;
    public static final int REQUEST_ABORTED   = 0x36;

    /**
     * Instantiates a new {@link LLRequest}.
     * 
     * @since 1.0
     * 
     * @param _context
     *            the Context
     * @param _handler
     *            the {@link LLRequestResponsibleObject} object that can response to the request.
     * @param _method
     *            the request {@link Method}.
     */
    protected LLRequest( Context _context, LLRequestResponsibleObject _handler, Method _method )
    {
        mUserAgent = new WebView( _context ).getSettings().getUserAgentString();
        mHandler = _handler;
        mMethod = _method;
        mContext = _context;
    }

    /**
     * Creates a {@link DefaultHttpClient} object.
     * 
     * @since 1.0
     * @param _ALLOW_ALL_HOSTNAME_VERIFIER_FOR_SSL
     *            true allow all hostname verifier for ssl.
     * @return the {@link DefaultHttpClient} object
     */
    public static DefaultHttpClient createHttpClient( boolean _ALLOW_ALL_HOSTNAME_VERIFIER_FOR_SSL ) {
        return createHttpClient( null, _ALLOW_ALL_HOSTNAME_VERIFIER_FOR_SSL );
    }

    /**
     * Creates a {@link DefaultHttpClient} object.
     * 
     * @since 1.0
     * @param _credsProvider
     *            the object contains connect credential info like: User, Pwd, Host etc.
     * @param _ALLOW_ALL_HOSTNAME_VERIFIER_FOR_SSL
     *            true allow all hostname verifier for ssl.
     * @return the {@link DefaultHttpClient} object
     */
    public static DefaultHttpClient createHttpClient( CredentialsProvider _credsProvider,
            boolean _ALLOW_ALL_HOSTNAME_VERIFIER_FOR_SSL )
    {
        // -------------------------------------------------------------------
        // Example for _credsProvider
        //
        // String usr = getUser();
        // String pwd = getPassword();
        // DefaultHttpClient httpclient = new DefaultHttpClient(conMgr, params);
        // CredentialsProvider credsProvider = new BasicCredentialsProvider();
        // credsProvider.setCredentials(new AuthScope(host, port), new UsernamePasswordCredentials(usr, pwd));
        // -------------------------------------------------------------------

        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout( params, TIME_OUT );
        HttpConnectionParams.setSoTimeout( params, TIME_OUT );
        HttpProtocolParams.setVersion( params, HttpVersion.HTTP_1_1 );
        HttpProtocolParams.setContentCharset( params, HTTP.DEFAULT_CONTENT_CHARSET );
        HttpProtocolParams.setUseExpectContinue( params, true );

        SchemeRegistry schReg = new SchemeRegistry();
        PlainSocketFactory plainSocketFactory = PlainSocketFactory.getSocketFactory();
        SSLSocketFactory sslSocketFactory = null;

        if( _ALLOW_ALL_HOSTNAME_VERIFIER_FOR_SSL ) {
            try {
                KeyStore trustStore = KeyStore.getInstance( KeyStore.getDefaultType() );
                trustStore.load( null, null );
                sslSocketFactory = new EasySSLSocketFactory( trustStore );
            }
            catch( Exception _e ) {
                LLL.e( _e.toString() );
                sslSocketFactory = SSLSocketFactory.getSocketFactory();
            }
            sslSocketFactory
                    .setHostnameVerifier( org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER );
        }
        else {
            sslSocketFactory = SSLSocketFactory.getSocketFactory();
        }
        schReg.register( new Scheme( "http", plainSocketFactory, 80 ) );
        schReg.register( new Scheme( "https", sslSocketFactory, 443 ) );
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager( params, schReg );

        DefaultHttpClient httpclient = new DefaultHttpClient( conMgr, params );
        if( _credsProvider != null ) {
            httpclient.setCredentialsProvider( _credsProvider );
        }
        return httpclient;
    }

    /**
     * Creates the {@link HttpRequestBase} object.
     * 
     * @param _urlStr
     *            the url of target in {@link String}
     * @return the {@link HttpRequestBase}, it could be GET oder POST.
     */
    private HttpRequestBase createHttpRequest( String _urlStr ) {
        switch( mMethod )
        {
            case GET:
                return new HttpGet( _urlStr );
            case POST:
                return new HttpPost( _urlStr );
            default:
                return null;
        }
    }

    @Override
    protected void onCancelled() {
        if( mHttpRequestBase != null && !mHttpRequestBase.isAborted() ) {
            mHttpRequestBase.abort();
        }
        if( mHandler != null ) {
            mHandler.sendEmptyMessage( REQUEST_ABORTED );
        }
    }

    @Override
    protected Exception doInBackground( Object... _params ) {
        Exception ret = null;

        if( _params == null ) {
            LLL.e( ":( Request must have a URL." );
        }
        else {
            DefaultHttpClient client = (DefaultHttpClient) _params[0];
            if( client != null ) {
                String urlstr = (String) _params[1];
                if( !TextUtils.isEmpty( urlstr ) ) {
                    LLL.i( ":| Request: " + urlstr );
                    mHttpRequestBase = createHttpRequest( urlstr );

                    // ----------------------------------------
                    // Set header
                    // ----------------------------------------
                    onAppendHeaders( mHttpRequestBase );

                    // ----------------------------------------
                    // Set cookies
                    // ----------------------------------------

                    StringBuilder cookies = new StringBuilder();
                    String onCookies = onWriteCookies();
                    String someCookies = (String) _params[2];
                    if( !TextUtils.isEmpty( onCookies ) ) {
                        cookies.append( onCookies );
                    }
                    if( !TextUtils.isEmpty( someCookies ) ) {
                        cookies.append( someCookies );
                    }
                    String cookiesStr = cookies.toString();
                    if( !TextUtils.isEmpty( cookiesStr ) ) {
                        mHttpRequestBase.setHeader( "Cookie", cookiesStr );
                    }
                    else {
                        LLL.d( ":| Empty Cookies on the request." );
                    }
                    
                    // ----------------------------------------
                    // Set body
                    // ----------------------------------------

                    if( mHttpRequestBase instanceof HttpPost ) {
                        List<NameValuePair> keyValues = onWritePostBody();
                        if( keyValues != null ) {
                            try {
                                ((HttpPost) mHttpRequestBase).setEntity( new UrlEncodedFormEntity( keyValues ) );
                            }
                            catch( UnsupportedEncodingException _e ) {
                                LLL.d( ":| Ignore setting data on HTTP-POST" );
                            }
                        }
                        else {
                            LLL.d( ":| Empty body on HTTP-POST" );
                        }
                    }

                    // ----------------------------------------
                    // Do request
                    // ----------------------------------------

                    try {
                        HttpResponse response = client.execute( mHttpRequestBase );
                        if( (mHttpRequestBase != null && mHttpRequestBase.isAborted()) || mHttpRequestBase == null ) {
                            onEmptyResponse( new LLBaseResponse( urlstr, client, response ) );
                        }
                        else {
                            onResponse( LLResponse.createInstance( urlstr, client, response ) );
                        }
                    }
                    catch( Exception _e ) {
                        // Abort request. Aborted request could also fire an exception.
                        if( mHttpRequestBase != null && !mHttpRequestBase.isAborted() ) {
                            mHttpRequestBase.abort();
                        }
                        LLL.e( ":) The exception has been caught: " + _e.toString() );
                        ret = new LLRequestException( _e, urlstr );
                    }
                }
                else {
                    LLL.e( ":( Unknown URL String for " + getClass().getSimpleName() + "." );
                }
            }
            else {
                LLL.e( ":( An HttpClient is required." );
            }
        }
        return ret;
    }

    /**
     * Handler when an empty response comes. The fellow codes show when the handler will be triggered(see {@link #doInBackground(Object... )}).
     * <p>
     * if( (mHttpRequestBase != null && mHttpRequestBase.isAborted()) || mHttpRequestBase == null )
     * 
     * @param _r
     *            the {@link LLBaseResponse} or a decorated subclass of it that contains information after requesting.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected void onEmptyResponse( LLBaseResponse _r ) throws IOException {
        LLL.i( ":| " + getClass().getSimpleName() + " empty response:" + _r.toString() );
        // Free http's thing i.e stream and client.
        // If error at releasing, the request seems falid as well.
        _r.release();
    }

    /**
     * Default handler for responding request. <br>
     * In the default implementation the {@link #finishResponse(int, LLBaseResponse)} is called that means the client can do super.onResponse(_r) when non-special message(just a {@link #REQUEST_SUCCESSED}) will be sent to the client. If a subclass of
     * {@link LLResponse} like {@link LLImageResponse} sends a message specifies to it i.e {@link LLImageResponse}, then the {@link #finishResponse(int, LLBaseResponse)} must be called.
     * 
     * @param _r
     *            the {@link LLBaseResponse} or a decorated subclass of it that contains information after requesting.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected void onResponse( LLBaseResponse _r ) throws IOException {
        finishResponse( REQUEST_SUCCESSED, _r );
    }

    /**
     * Finish response by freeing resource and sending message to the client <br>
     * This method can(must) be called after overriding {@link #onResponse(LLBaseResponse)}.
     * 
     * @param _msg
     *            the message that will be sent through {@link #mHandler}
     * @param _r
     *            the {@link LLBaseResponse} or a decorated subclass of it that contains information after requesting.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected void finishResponse( int _msg, LLBaseResponse _r ) throws IOException {
        LLL.i( ":) " + getClass().getSimpleName() + " is successfully:" + _r.toString() );
        if( mHandler != null ) {
            Message msg = Message.obtain( mHandler, _msg, _r );
            msg.sendToTarget();
        }
        // Free http's thing i.e stream and client.
        // If error at releasing, the request seems falid as well.
        _r.release();
    }

    @Override
    protected void onPostExecute( Exception _result ) {
        if( _result != null ) {
            LLL.e( ":( Faild response." );
            if( mHandler != null ) {
                Message.obtain( mHandler, REQUEST_FAILED, _result ).sendToTarget();
            }
        }
    }

    /**
     * Handler when append to write some cookies in subclass.
     * 
     * @return the cookie in string.
     */
    protected String onWriteCookies() {
        return null;
    }

    /**
     * Handler when some headers will be added on to request. The default version should be called to tell the backend server that the client is Android.
     * 
     * @param _req
     *            the {@link HttpRequestBase} object
     */
    protected void onAppendHeaders( HttpRequestBase _req ) {
        if( !TextUtils.isEmpty( mUserAgent ) ) {
            _req.setHeader( "User-Agent", mUserAgent );
        }
        _req.setHeader( "Accept-Encoding", "gzip" );
    }

    /**
     * Handler when a POST request need body.
     *
     * @return the {@link List} that are written in request body.
     */
    protected List<NameValuePair> onWritePostBody() {
        // List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        // _nameValuePairs.add(new BasicNameValuePair("id", "12345"));
        // _nameValuePairs.add(new BasicNameValuePair("stringdata", "Hi"));
        return null;
    }

    /**
     * Wrap "new" a {@link LLRequest} object
     *
     * @param _context the Context
     * @param _handler the {@link LLRequestResponsibleObject} object
     * @param _method the request {@link Method}
     * @param _url the target url in {@link String}
     * @param _someCookies the cookies in {@link String}
     * @return the created {@link LLRequest} object.
     */
    public static LLRequest start(
            Context _context,
            LLRequestResponsibleObject _handler,
            Method _method,
            String _url,
            String _someCookies ) {
        LLRequest r = new LLRequest( _context, _handler, _method );
        r.execute( createHttpClient( false ), _url, _someCookies );
        return r;
    }

    /**
     * Abort the request.
     */
    public void abort() {
        if( mHttpRequestBase != null && !mHttpRequestBase.isAborted() ) {
            mHttpRequestBase.abort();
        }
    }
}
