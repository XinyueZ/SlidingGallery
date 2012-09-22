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

package de.cellular.lib.lightlib.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import de.cellular.lib.lightlib.log.LL;
/**
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 *
 */
public class FileHelper 
{
    private static final int CELL_SIZE = 1024;

    private Context          mContext;
    private boolean          sdcardAvailabilityDetected;
    private boolean          sdcardAvailable; 

    public FileHelper( Context _context ) {
        super();
        mContext = _context; 
    }

    public void writeFile( InputStream _instream, File _toFile ) throws FileNotFoundException, IOException
    {
        byte[] buffer = new byte[CELL_SIZE];
        int offset = 0;
        RandomAccessFile wrotenFile = new RandomAccessFile( _toFile, "rwd" );
        wrotenFile.seek( 0 );
        while( (offset = _instream.read( buffer, 0, 1024 )) != -1 )
        {
            wrotenFile.write( buffer, 0, offset );
        }
        wrotenFile.close();
    }

    private boolean isSDCardAvailable()
    {
        if( !sdcardAvailabilityDetected )
        {
            sdcardAvailable = detectSDCardAvailability();
            sdcardAvailabilityDetected = true;
        }
        return sdcardAvailable;
    }

    @SuppressLint("SdCardPath")
    private boolean detectSDCardAvailability()
    {
        boolean result = false;
        try
        {
            Date now = new Date();
            long times = now.getTime();
            String fileName = "/sdcard/" + times + ".test";
            File file = new File( fileName );
            result = file.createNewFile();
            file.delete();
        }
        catch( Exception e )
        {
            LL.e(  ":( SD card can't be used!" );
        }
        finally
        {
            sdcardAvailabilityDetected = true;
            sdcardAvailable = result;
        }
        return result;
    }

    public String getTargetPath() throws IOException
    {
        StringBuilder dirName = new StringBuilder();
        if( isSDCardAvailable() )
        {
            dirName.append( Environment.getExternalStorageDirectory() );
            dirName.append( '/' );
            dirName.append( mContext.getPackageName() );
            dirName.append( "/file_downloader/" );
        }
        else
        {
            dirName.append( "/data/data/" );
            dirName.append( mContext.getPackageName() );
            dirName.append( "/file_downloader/" );
        }

        File dir = new File( dirName.toString() );
        if( !dir.exists() || !dir.isDirectory() )
        {
            if( !dir.mkdirs() )
                throw new IOException( ":) Can't create target directory to store downloaded files." );
        }

        return dirName.toString();
    }
}
