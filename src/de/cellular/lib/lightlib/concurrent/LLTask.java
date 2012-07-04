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

package de.cellular.lib.lightlib.concurrent;


/**
 * A task that can be suspended and proceed.
 * 
 * @see http://javawiki.sowas.com/doku.php?id=java:thread-pausieren
 * @see http://www.javamex.com/tutorials/wait_notify_how_to.shtml
 * 
 * @author Chris.Z
 * 
 */
public abstract class LLTask implements Runnable
{
    /**
     * Flag for suspending.
     */
    private boolean mSuspend = false;

    /**
     * Virtual object to wait for suspended task.
     */
    private Object  mLock    = new Object();

    /**
     * A block stone in the task.
     * 
     * @throws InterruptedException
     */
    protected final void stone() throws InterruptedException
    {
        synchronized( mLock )
        {
            while( mSuspend )
            {
                mLock.wait();
            }
        }
    }

    /**
     * Suspend the task
     */
    public final void suspend()
    {
        synchronized( mLock )
        {
            mSuspend = true;
        }
    }

    /**
     * Proceed the task
     */
    public final void proceed()
    {
        synchronized( mLock )
        {
            mSuspend = false;
            mLock.notify();
        }
    }
}
