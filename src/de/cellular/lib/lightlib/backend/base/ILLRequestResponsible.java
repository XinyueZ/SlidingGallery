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
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 * 
 */
public interface ILLRequestResponsible {
    void onRequestFinished( Message _msg );

    void onRequestImageFailed( Message _msg );

    void onRequestImageSuccessed( Message _msg );

    void onRequestAborted( Message _msg );

    void onRequestSuccessed( Message _msg );

    void onRequestFailed( Message _msg );
}
