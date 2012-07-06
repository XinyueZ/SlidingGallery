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
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.WebView;

import de.cellular.lib.lightlib.log.LLL;
/**
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 *
 */
public class LLRequest extends AsyncTask<Object, Object, Exception>
{
    private HttpRequestBase    mHttpRequestBase;

    protected static final int TIME_OUT = 15 * 1000;
    protected String           mUserAgent;
    protected Handler          mHandler;
    protected Context          mContext;

    public enum Method {
        GET,
        POST,
    }

    private Method          mMethod;
    public static final int REQUEST_FAILED    = 0x34;
    public static final int REQUEST_SUCCESSED = 0x35;
    public static final int REQUEST_ABORTED   = 0x36;

    protected LLRequest( Context _context, Handler _handler, Method _method )
    {
        mUserAgent = new WebView( _context ).getSettings().getUserAgentString();
        mHandler = _handler;
        mMethod = _method;
        mContext = _context;
    }

    static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance( "TLS" );

        public MySSLSocketFactory( KeyStore truststore ) throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super( truststore );

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted( X509Certificate[] chain, String authType ) throws CertificateException {
                }

                public void checkServerTrusted( X509Certificate[] chain, String authType ) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            sslContext.init( null, new TrustManager[] { tm }, null );
        }
    }

    public static DefaultHttpClient createHttpClient( boolean _ALLOW_ALL_HOSTNAME_VERIFIER_FOR_SSL ) {
        return createHttpClient( null, _ALLOW_ALL_HOSTNAME_VERIFIER_FOR_SSL );
    }

    private static DefaultHttpClient createHttpClient( CredentialsProvider _credsProvider,
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

    private HttpRequestBase createHttpRequestBase( String _urlStr ) {
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
                    mHttpRequestBase = createHttpRequestBase( urlstr );

                    // ----------------------------------------
                    // Set header
                    // ----------------------------------------

                    if( !TextUtils.isEmpty( mUserAgent ) ) {
                        mHttpRequestBase.setHeader( "User-Agent", mUserAgent );
                    }
                    String someCookies = (String) _params[2];
                    if( !TextUtils.isEmpty( someCookies ) ) {
                        mHttpRequestBase.setHeader( "Cookie", someCookies );
                    }
                    onAppendHeaders( mHttpRequestBase );

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

                    try {
                        HttpResponse response = client.execute( mHttpRequestBase );
                        if( (mHttpRequestBase != null && mHttpRequestBase.isAborted()) || mHttpRequestBase == null ) {
                            onEmptyResponse( new LLBaseResponse( urlstr ) );
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
                        ret = _e;
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

    protected void onEmptyResponse( LLBaseResponse _r ) throws IOException {
        LLL.w( ":| " + getClass().getSimpleName() + " empty response." );
        // Free http's thing i.e stream and client.
        // If error at releasing, the request seems falid as well.
        _r.release();
    }

    protected void onResponse( LLBaseResponse _r ) throws IOException { 
        onResponse( REQUEST_SUCCESSED, _r ); 
    }

    protected void onResponse( int _msg, LLBaseResponse _r ) throws IOException {
        LLL.i( ":) " + getClass().getSimpleName() + " is successfully." );
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

    protected void onAppendHeaders( HttpRequestBase _req ) {
        _req.setHeader( "Accept-Encoding", "gzip" );
    }

    protected List<NameValuePair> onWritePostBody() {
        // List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        // _nameValuePairs.add(new BasicNameValuePair("id", "12345"));
        // _nameValuePairs.add(new BasicNameValuePair("stringdata", "Hi"));
        return null;
    }

    public static LLRequest start(
            Context _context,
            Handler _handler,
            Method _method,
            String _url,
            String _someCookies ) {
        LLRequest r = new LLRequest( _context, _handler, _method );
        r.execute( createHttpClient( false ), _url, _someCookies );
        return r;
    }

    public void abort() {
        if( mHttpRequestBase != null && !mHttpRequestBase.isAborted() ) {
            mHttpRequestBase.abort();
        }
    }
}
