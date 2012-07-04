/*
 * Copyright (C) 2012 Chris Xinyue Zhao <hasszhao@gmail.com>
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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import de.cellular.lib.lightlib.log.LLL;
import de.cellular.lib.lightlib.utils.UIUtils;

public class LLRequestImage extends LLRequest
{
    private DisplayMetrics  metrics;

    public static final int REQUEST_IMAGE_SUCCESSED = 0x37;
    public static final int REQUEST_IMAGE_FAILED  = 0x38;

    private LLRequestImage( Context _context, Handler _handler, Method _method ) {
        super( _context, _handler, _method );
        metrics = new DisplayMetrics();
        ((WindowManager) _context.getSystemService( Context.WINDOW_SERVICE )).getDefaultDisplay().getMetrics(
                metrics );
    }

    @Override
    protected void onResponse( LLBaseResponse _r ) {
        try {
            LLImageResponse ret = new LLImageResponse( _r );
            readStreamToBitmap( ret );
            LLL.i( ":) Reading Bitmap successfully." );
            
            onResponse( REQUEST_IMAGE_SUCCESSED, ret ); 
        }
        catch( Exception _e )
        {
            LLL.e( ":( Error while handling Bitmap:" + _e.toString() );
            try {
                _r.release();
            }
            catch( IOException _e1 ) { 
                LLL.e( ":( Failed to release res:" + _e.toString() );  
            }
            // Info UI that it be failed.
            if( mHandler != null ) {
                mHandler.sendEmptyMessage( REQUEST_IMAGE_FAILED );
            }
        }
    }

    private void readStreamToBitmap( LLImageResponse _r ) throws IOException {
        if( _r.getInputStream() != null ) {
            // Create a bitmap from data.
            Bitmap retBp = BitmapFactory.decodeStream( _r.getInputStream() );
            if( retBp != null ) {
                // Scale the image and cache it.
                retBp = doScalingImage( retBp );
                LLImageCache.setSendungImage( _r.getUrlStr(), retBp );
                _r.setBitmap( retBp );
            }
        }
    }

    public static LLRequestImage start(
            Context _context,
            Handler _handler,
            Method _method,
            String _url,
            String _someCookies ) {
        LLRequestImage r = new LLRequestImage( _context, _handler, _method );
        r.execute( createHttpClient( false ), _url, _someCookies );
        return r;
    }

    /**
     * Scale bitmap
     * 
     * @param _bitmap
     *            Image has width > height.
     * @return
     */
    protected Bitmap doScalingImage( Bitmap _bitmap ) {
        if( _bitmap.getHeight() > _bitmap.getWidth() )
            return UIUtils.scaleImageHW(
                    _bitmap,
                    metrics.widthPixels );
        else
            return UIUtils.scaleImageWH(
                    _bitmap,
                    metrics.widthPixels );

    }
}