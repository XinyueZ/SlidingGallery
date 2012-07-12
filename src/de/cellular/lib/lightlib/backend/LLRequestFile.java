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
import de.cellular.lib.lightlib.backend.base.LLRequestResponsibleObject;
import de.cellular.lib.lightlib.io.FileHelper;

/**
 * A subclass of {@link LLRequest}. It reads stream of response and pull with stream as a file. <strong>Known subclasses are</strong>
 * <p>
 * {@link LLRequestImage}
 * 
 * @version 1.0
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 * 
 */
public class LLRequestFile extends LLRequest {

    /**
     * Instantiates a new {@link LLRequestFile}.
     * 
     * @since 1.0
     * @param _context
     *            the Context
     * @param _handler
     *            the handler the accept message while requesting.
     * @param _method
     *            the {@link Method} of requesting.
     */
    public LLRequestFile( Context _context, LLRequestResponsibleObject _handler, Method _method ) {
        super( _context, _handler, _method );
    }

    /**
     * Creates the output file.
     * 
     * @since 1.0
     * @param _r
     *            the response after executing.
     * @param _toFileName
     *            the file name being saved.
     * @return the saved file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected File createOutputFile( LLBaseResponse _r, String _toFileName ) throws IOException {
        FileHelper fileHelper = new FileHelper( mContext );
        File file = new File( fileHelper.getTargetPath(), _toFileName );
        fileHelper.writeFile( _r.getInputStream(), file );
        return file;
    }
}
