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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import de.cellular.lib.lightlib.backend.base.LLRequestResponsibleObject;
import de.cellular.lib.lightlib.log.LLL;
import de.cellular.lib.lightlib.utils.UIUtils;

/**
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 * 
 */
public class LLRequestImage extends LLRequestFile
{
    public static final int REQUEST_IMAGE_SUCCESSED = 0x37;
    public static final int REQUEST_IMAGE_FAILED    = 0x38;

    public static class RequestedSize
    {
        public int reqWidth;
        public int reqHeight;
    }

    private RequestedSize mReqSize;

    private LLRequestImage( Context _context, LLRequestResponsibleObject _handler, Method _method, RequestedSize _reqSize ) {
        super( _context, _handler, _method );
        mReqSize = _reqSize;
    }

    @Override
    protected void onResponse( LLBaseResponse _r ) {
        try {
            LLImageResponse ret = new LLImageResponse( _r );
            readStreamToBitmap( ret );
            LLL.i( ":) Reading Bitmap successfully." );

            finishResponse( REQUEST_IMAGE_SUCCESSED, ret );
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
                Message msg = Message.obtain( mHandler, REQUEST_IMAGE_FAILED,
                        new LLRequestException( _e, _r.getUrlStr() ) );
                msg.sendToTarget();
            }
        }
    }

    private void readStreamToBitmap( LLImageResponse _r ) throws IOException {
        if( _r.getInputStream() != null ) {
            Bitmap retBp = null;
            try {
                retBp = BitmapFactory.decodeStream( _r.getInputStream() );
                if( retBp == null ) {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream( _r.getInputStream(), null, options );
                    options.inSampleSize = calculateInSampleSize( options, mReqSize.reqWidth, mReqSize.reqHeight );
                    options.inJustDecodeBounds = false;
                    retBp = BitmapFactory.decodeStream( _r.getInputStream(), null, options );
                    if( retBp != null ) {
                        LLL.i( ":) Decoded stream with some options successfully." );
                    }
                    else {
                        throw new Exception();
                    }
                }
            }
            catch( Exception _e ) {
                LLL.e( ":( Faild, try to download the object." );
                String fname = System.currentTimeMillis() + ".tmp";
                File tmpFile = createOutputFile( _r, fname );
                try {
                    retBp = BitmapFactory.decodeFile( tmpFile.getAbsolutePath() );
                    if( retBp == null )   {
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile( tmpFile.getAbsolutePath(), options );
                        options.inSampleSize = calculateInSampleSize( options, mReqSize.reqWidth,  mReqSize.reqHeight );
                        options.inJustDecodeBounds = false;
                        retBp = BitmapFactory.decodeFile( tmpFile.getAbsolutePath(), options );
                        if( retBp != null ) {
                            LLL.i( ":) Decoded file with some options successfully." );
                        }
                        else {
                            throw new Exception();
                        } 
                    }
                }
                catch( Exception _ee ) {
                    LLL.e( ":( Give up! The Bitmap can't be decoded definitly." );
                }
            } 
            // Scale the image and cache it.
            retBp = doScalingImage( retBp );
            LLImageCache.setSendungImage( _r.getUrlStr(), retBp );
            _r.setBitmap( retBp );
        }
    }

    /**
     * Get new size of being produced bitmap
     * 
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @see http://developer.android.com/intl/zh-CN/training/displaying-bitmaps/load-bitmap.html#load-bitmap
     * @return inSampleSize
     */
    public static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight ) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if( height > reqHeight || width > reqWidth ) {
            if( width > height ) {
                inSampleSize = Math.round( (float) height / (float) reqHeight );
            }
            else {
                inSampleSize = Math.round( (float) width / (float) reqWidth );
            }
        }
        return inSampleSize;
    }

    public static LLRequestImage start(
            Context _context,
            LLRequestResponsibleObject _handler,
            Method _method,
            String _url,
            RequestedSize _reqSize,
            String _someCookies ) {
        LLRequestImage r = new LLRequestImage( _context, _handler, _method, _reqSize );
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
                    mReqSize.reqWidth );
        else
            return UIUtils.scaleImageWH(
                    _bitmap,
                    mReqSize.reqWidth );
    }
}
