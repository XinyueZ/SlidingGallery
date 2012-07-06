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
 * 
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 * 
 */
public abstract class LLRequestResponsibleObject extends Handler implements ILLRequestResponsible {

    @Override
    public void handleMessage( Message _msg ) {

        switch( _msg.what )
        {
            case LLRequest.REQUEST_FAILED:
                onRequestFailed( _msg );
            break;
            case LLRequest.REQUEST_SUCCESSED:
                onRequestSuccessed( _msg );
            break;
            case LLRequest.REQUEST_ABORTED:
                onRequestAborted( _msg );
            break;
            case LLRequestImage.REQUEST_IMAGE_SUCCESSED:
                onRequestImageSuccessed( _msg );
            break;
            case LLRequestImage.REQUEST_IMAGE_FAILED:
                onRequestImageFailed( _msg );
            break;
            default:
                LLL.i( ":| Unkown event." );
            break;
        }

        onRequestFinished( _msg );

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
