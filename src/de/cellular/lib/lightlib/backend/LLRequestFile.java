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

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Handler;

import de.cellular.lib.lightlib.io.FileHelper;

public class LLRequestFile extends LLRequest {

    public LLRequestFile( Context _context, Handler _handler, Method _method ) {
        super( _context, _handler, _method );
    }

    protected File createOutputFile( LLBaseResponse _r, String _toFileName ) throws IOException {
        FileHelper fileHelper = new FileHelper( mContext );
        File file = new File( fileHelper.getTargetPath(), _toFileName );
        fileHelper.writeFile( _r.getInputStream(), file );
        return file;
    }
}
