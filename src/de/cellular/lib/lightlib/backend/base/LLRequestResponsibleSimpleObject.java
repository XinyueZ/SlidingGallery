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

import android.os.Message;

/**
 * 
 * An implementation of {@link LLRequestResponsibleObject} the provides a convenient way with which the client decorates a {@link ILLRequestResponsible} object.
 * 
 * @version 1.0
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 * 
 */
public class LLRequestResponsibleSimpleObject extends LLRequestResponsibleObject
{
    
    /**
     * Instantiates a new LLRequestResponsibleSimpleObject.
     * 
     * @since 1.0
     */
    public LLRequestResponsibleSimpleObject() {
        super();
    } 

    /**
     * Decorates an ILLRequestResponsible object, it could be a UI like an Activity, Fragment, or non-UI things.
     * 
     * @param _deleget
     *            the ILLRequestResponsible implementing object.
     *            @since 1.0
     */
    public LLRequestResponsibleSimpleObject( ILLRequestResponsible _deleget ) {
        super( _deleget );
    }
 
    @Override
    public void onRequestFinished( Message _msg ) {
    };

    @Override
    public void onRequestImageFailed( Message _msg ) {
    };

    @Override
    public void onRequestImageSuccessed( Message _msg ) {
    };

    @Override
    public void onRequestAborted( Message _msg ) {
    };

    @Override
    public void onRequestSuccessed( Message _msg ) {
    };

    @Override
    public void onRequestFailed( Message _msg ) {
    };
}