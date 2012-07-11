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

import de.cellular.lib.lightlib.backend.LLRequest;
import de.cellular.lib.lightlib.ui.fragment.LLRequestingFragment;
import android.os.Message;

/**
 * 
 * Responsible for LLRequests
 * <p>
 * <strong>Known subclasses are</strong>
 * <p>
 * {@link LLRequestingFragment}
 * <p>
 * {@link LLRequestResponsibleObject}
 * 
 * @version 1.0
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 * 
 */
public interface ILLRequestResponsible {
    
    /**
     * Handler on request finished.
     *
     * @param _msg the {@link Message} sent from {@link LLRequest}s.
     * @since 1.0
     */
    void onRequestFinished( Message _msg );

    /**
     * Handler on request image failed.
     *
     * @param _msg the {@link Message} sent from {@link LLRequest}s.
     * @since 1.0
     */
    void onRequestImageFailed( Message _msg );

    /**
     * Handler on request image successfully.
     *
     * @param _msg the {@link Message} sent from {@link LLRequest}s.
     * @since 1.0
     */
    void onRequestImageSuccessed( Message _msg );

    /**
     * Handler on request being aborted.
     *
     * @param _msg the {@link Message} sent from {@link LLRequest}s.
     * @since 1.0
     */
    void onRequestAborted( Message _msg );

    /**
     * Handler on request is successfully.
     *
     * @param _msg the {@link Message} sent from {@link LLRequest}s.
     * @since 1.0
     */
    void onRequestSuccessed( Message _msg );

    /**
     * Handler on request being failed.
     *
     * @param _msg the {@link Message} sent from {@link LLRequest}s.
     * @since 1.0
     */
    void onRequestFailed( Message _msg );
}
