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
package de.cellular.lib.lightlib.ui.view.gallery;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import de.cellular.lib.lightlib.backend.LLImageResponse;
import de.cellular.lib.lightlib.backend.LLRequest;
import de.cellular.lib.lightlib.backend.LLRequestException;
import de.cellular.lib.lightlib.backend.LLRequestImage;
import de.cellular.lib.lightlib.backend.LLRequestImage.RequestedSize;
import de.cellular.lib.lightlib.backend.base.LLRequestResponsibleObject;
import de.cellular.lib.lightlib.ui.view.gallery.LLGallery.CommentPosition;
import de.cellular.lib.lightlib.ui.view.gallery.LLGallery.OnItemClickListener;
import de.cellular.lib.lightlib.ui.view.gallery.LLGallery.OnItemScrollListener;
import de.cellular.lib.lightlib.ui.view.gallery.LLGallery.OnItemScrolledListener;
import de.cellular.lib.lightlib.ui.view.gallery.base.ILLGallery;

/**
 * Decorator for {@link LLGallery} that user can pass an array of URLs and the Gallery shows all items asynchronized.
 * 
 * @see {@link http://en.wikipedia.org/wiki/Decorator_pattern}
 *      <p>
 *      To know more about the Decorator pattern from GOF.
 * @version <strong>1.0.3 </strong> <li>Removed unused functions after changing {@link LLGallery} to 1.0.5</li>
 * @version <strong>1.0.2</strong> <li>Fixed bug that the comment can't be shown with correct index and be shown lazily.</li>
 *   
 * @version <strong>1.0.1</strong> <li>Added comments.</li>
 *          <p>
 *          Because a comment of a bitmap must be shown later(All bitmaps are loaded async in {@link #load(RequestedSize)}).
 *          Here I introduce {@link CommentHelper} to store comments that are loaded later
 *          <br>
 *          and change {@link Queue} to {@link List} that stored {@link Uri}s, so that the {@link LLAsyncGallery} can get a correct comment for a loaded bitmap with an index.
 *          <p>
 * @see {@link #appendBitmapFromMessage(Message), knows that how a comment links to the correct bitmap.
 *      <p>
 *      <strong>1.0</strong>
 *      <li>just a beginning</li>
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 * 
 */
public class LLAsyncGallery extends LLRequestResponsibleObject implements ComponentCallbacks, ILLGallery
{
    private volatile LLGallery     mGallery;
    private List<Uri>              mUris             = new LinkedList<Uri>();
    private Map<String, LLRequest> mConsumedRequests = new HashMap<String, LLRequest>();

    static class CommentHelper {
        public int             commentViewId;
        public CommentPosition commentPosition;
        public List<String>    comments = new ArrayList<String>();
    }

    private final static CommentHelper sCommentHelper = new CommentHelper();

    /**
     * Instantiates a new {@link LLAsyncGallery}.
     * 
     * @since 1.0
     * @param _gallery
     *            the _gallery that will be decorated.
     */
    public LLAsyncGallery( LLGallery _gallery ) {
        mGallery = _gallery;
    }

    /**
     * Gets the uris.
     * 
     * @since 1.0
     * @return the uris
     */
    public List<Uri> getUris() {
        return mUris;
    }

    /**
     * Sets the images urls and load async.
     * 
     * @since 1.0
     * @param _uris
     *            the Uris of the data source.
     * @param _reqSize
     *            the size of that will be requested.
     */
    public void setImages( Uri[] _uris, RequestedSize _reqSize ) {
        mUris.addAll( Arrays.asList( _uris ) );
        load( _reqSize );
    }

    /**
     * Sets the images urls and load async.
     * 
     * @since 1.0
     * @param _urlStrs
     *            the urls in string of the data source.
     * @param _reqSize
     *            the size of that will be requested.
     */
    public void setImages( String[] _urlStrs, RequestedSize _reqSize ) {
        for( String urlStr : _urlStrs ) {
            if( !TextUtils.isEmpty( urlStr ) ) {
                mUris.add( Uri.parse( urlStr ) );
            }
        }
        load( _reqSize );
    }

    /**
     * Sets the images urls and load async.
     * 
     * @since 1.0
     * @param _URLs
     *            the URLs of the data source.
     * @param _reqSize
     *            the size of that will be requested.
     */
    public void setImages( URL[] _URLs, RequestedSize _reqSize ) {
        for( URL url : _URLs ) {
            if( url != null ) {
                mUris.add( Uri.parse( url.toString() ) );
            }
        }
        load( _reqSize );
    }

    /**
     * Sets the images urls and load async.
     * 
     * @since 1.0
     * @param _URIs
     *            the URIs of the data source.
     * @param _reqSize
     *            the size of that will be requested.
     */
    public void setImages( URI[] _URIs, RequestedSize _reqSize ) {
        for( URI uri : _URIs ) {
            if( uri != null ) {
                mUris.add( Uri.parse( uri.toString() ) );
            }
        }
        load( _reqSize );
    }

    /**
     * Load images.
     * 
     * @since 1.0
     * @param _reqSize
     *            the size of that will be requested.
     */
    private void load( RequestedSize _reqSize ) {
        for( Uri uri : mUris ) {
            request( uri.toString(), _reqSize );
        }
    }

    /**
     * Request uris.
     * 
     * @since 1.0
     * @param _uriStr
     *            the required uri.
     * @param _reqSize
     *            the size of that will be requested.
     */
    private void request( String _uriStr, RequestedSize _reqSize ) {
        LLRequest req = LLRequestImage.start(
                mGallery.getContext(),
                this,
                LLRequest.Method.GET,
                _uriStr.toString(),
                _reqSize,
                null );
        mConsumedRequests.put( _uriStr, req );
    }

    /**
     * Release unused {@link LLRequest}.
     * 
     * @since 1.0
     */
    public void release() {
        for( LLRequest req : mConsumedRequests.values() ) {
            if( req != null ) {
                req.abort();
            }
        }
    }

    /**
     * Sets the consumed request to null for error.
     * 
     * @since 1.0
     * @param _msg
     *            the _msg from {@link LLRequestImage} or {@link LLRequest} .
     */
    private void setConsumedRequestToNullForError( Message _msg ) {
        if( _msg.obj instanceof LLRequestException ) {
            LLRequestException exp = (LLRequestException) _msg.obj;
            mConsumedRequests.put( exp.getUrlStr(), null );
        }
    }

    /**
     * Append bitmap from message.
     * 
     * @since 1.0
     * @param _msg
     *            the _msg from {@link LLRequestImage}
     */
    private void appendBitmapFromMessage( Message _msg ) {
        if( _msg.obj instanceof LLImageResponse ) {
            LLImageResponse response = (LLImageResponse) _msg.obj;
            synchronized( this ) {
                this.appendImage( response.getBitmap() );
                int i = 0;
                for( Uri uri : mUris ) {
                    if( TextUtils.equals( uri.toString(), response.getUrlStr() ) ) {
                        mGallery.addComments(
                                sCommentHelper.commentViewId,
                                sCommentHelper.commentPosition,
                                new String[] { sCommentHelper.comments.get( i ) } );
                        break;
                    }
                    i++;
                }
                mConsumedRequests.put( response.getUrlStr(), null );
            }
        }
    }

    @Override
    public void onConfigurationChanged( Configuration _newConfig ) {
        release();
    }

    @Override
    public void onLowMemory() {
        release();
    }

    @Override
    public void onRequestFinished( Message _msg ) {

    }

    @Override
    public void onRequestFailed( Message _msg ) {
        setConsumedRequestToNullForError( _msg );
    }

    @Override
    public void onRequestImageFailed( Message _msg ) {
        setConsumedRequestToNullForError( _msg );
    }

    @Override
    public void onRequestImageSuccessed( Message _msg ) {
        appendBitmapFromMessage( _msg );
    }

    @Override
    public void onRequestAborted( Message _msg ) {

    }

    @Override
    public void onRequestSuccessed( Message _msg ) {

    }

    @Override
    public void setImages( List<Bitmap> _bitmaps ) {
        mGallery.setImages( _bitmaps );
    }

    @Override
    public void appendImage( Bitmap _bmp ) {
        mGallery.appendImage( _bmp );
    }

    @Override
    public void appendComment( String _comment ) {
        mGallery.appendComment( _comment );
    }

    @Override
    public void addComments( int _commentViewId, String[] _comments ) {
        // mGallery.addComments( _commentViewId, _comments );
        this.addComments( _commentViewId, CommentPosition.BOTTOM, _comments );
    }

    @Override
    public void addComments( int _commentViewId, CommentPosition _pos, String[] _comments ) {
        // mGallery.addComments( _commentViewId, _pos, null );
        // mGallery.getCommentsView().setVisibility( View.GONE );
        sCommentHelper.commentViewId = _commentViewId;
        sCommentHelper.commentPosition = _pos;
        sCommentHelper.comments.addAll( Arrays.asList( _comments ) );
    }

    @Override
    public void showComment( int _location ) {
        mGallery.showComment( _location );
    };

    @Override
    public void setOnItemClickListener( OnItemClickListener _listener ) {
        mGallery.setOnItemClickListener( _listener );
    }

    @Override
    public void setOnItemScrollListener( OnItemScrollListener _listener ) {
        mGallery.setOnItemScrollListener( _listener );
    }

    @Override
    public void setOnItemScrolledListener( OnItemScrolledListener _listener ) {
        mGallery.setOnItemScrolledListener( _listener );
    }

    @Override
    public View getCommentsView() {
        return mGallery.getCommentsView();
    }
}
