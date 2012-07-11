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
package de.cellular.lib.lightlib.backend.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import de.cellular.lib.lightlib.backend.LLRequest;
import de.cellular.lib.lightlib.backend.LLRequestImage;
import de.cellular.lib.lightlib.log.LLL;
import de.cellular.lib.lightlib.ui.view.gallery.LLAsyncGallery;

/**
 * 
 * An implementation of {@link ILLRequestResponsible}
 * <p>
 * <strong>Known subclasses are</strong>
 * <p>
 * {@link LLAsyncGallery}
 * <p>
 * {@link LLRequestResponsibleSimpleObject}<br>
 * (A convenient way with which the client decorates a {@link ILLRequestResponsible} object. )
 * 
 * @see {@link http://en.wikipedia.org/wiki/Decorator_pattern}
 *      <p>
 *      To know more about the Decorator pattern from GOF.
 * 
 * @version 1.0
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 * 
 */
public abstract class LLRequestResponsibleObject extends Handler implements ILLRequestResponsible {
    private ILLRequestResponsible mDeleget;

    /**
     * Instantiates a new LLRequestResponsibleObject.
     * 
     * @since 1.0
     */
    public LLRequestResponsibleObject() {
        super();
    }

    /**
     * Decorates an ILLRequestResponsible object, it could be a UI like an Activity, Fragment, or non-UI things.
     * 
     * @param _deleget
     *            the ILLRequestResponsible implementing object.
     * @since 1.0
     */
    public LLRequestResponsibleObject( ILLRequestResponsible _deleget ) {
        super();
        mDeleget = _deleget;
    }

    /**
     * @deprecated Instantiates a new lL request responsible object.
     * @since 1.0
     */
    protected LLRequestResponsibleObject( Callback _callback ) {
        super( _callback );
    }

    /**
     * @deprecated Instantiates a new lL request responsible object.
     * @since 1.0
     */
    protected LLRequestResponsibleObject( Looper _looper, Callback _callback ) {
        super( _looper, _callback );
    }

    /**
     * @deprecated Instantiates a new lL request responsible object.
     * @since 1.0
     */
    protected LLRequestResponsibleObject( Looper _looper ) {
        super( _looper );
    }

    @Override
    public void handleMessage( Message _msg ) {
        switch( _msg.what )
        {
            case LLRequest.REQUEST_FAILED:
                onRequestFailed( _msg );
                if( mDeleget != null ) {
                    mDeleget.onRequestFailed( _msg );
                }
            break;
            case LLRequest.REQUEST_SUCCESSED:
                onRequestSuccessed( _msg );
                if( mDeleget != null ) {
                    mDeleget.onRequestSuccessed( _msg );
                }
            break;
            case LLRequest.REQUEST_ABORTED:
                onRequestAborted( _msg );
                if( mDeleget != null ) {
                    mDeleget.onRequestAborted( _msg );
                }
            break;
            case LLRequestImage.REQUEST_IMAGE_SUCCESSED:
                onRequestImageSuccessed( _msg );
                if( mDeleget != null ) {
                    mDeleget.onRequestImageSuccessed( _msg );
                }
            break;
            case LLRequestImage.REQUEST_IMAGE_FAILED:
                onRequestImageFailed( _msg );
                if( mDeleget != null ) {
                    mDeleget.onRequestImageFailed( _msg );
                }
            break;
            default:
                LLL.i( ":| Unkown event." );
            break;
        }

        onRequestFinished( _msg );
        if( mDeleget != null ) {
            mDeleget.onRequestFinished( _msg );
        }
    }

    @Override
    public abstract void onRequestFinished( Message _msg );

    @Override
    public abstract void onRequestImageFailed( Message _msg );

    @Override
    public abstract void onRequestImageSuccessed( Message _msg );

    @Override
    public abstract void onRequestAborted( Message _msg );

    @Override
    public abstract void onRequestSuccessed( Message _msg );

    @Override
    public abstract void onRequestFailed( Message _msg );
}