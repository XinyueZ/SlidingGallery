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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manage pausing and resuming of a thread-pool. The Pool can accept {@link LLTask} that could be suspended and processed while running.
 * 
 * http://docs.oracle.com/javase/1.5.0/docs/api/java/util/concurrent/ThreadPoolExecutor.html
 * 
 * @author Tatiana Rybnikova, Chris.Z
 * */
public class LLThreadPool extends ThreadPoolExecutor
{
    private boolean            isPaused;
    private ReentrantLock      mPauseLock            = new ReentrantLock();
    private Condition          isUnpaused              = mPauseLock.newCondition();
    /**
     * A list that contains all {@link LLTask} that could be suspended at executing time.
     */
    private List<LLTask> exectingPausableTasks = new LinkedList<LLTask>();

    /**
     * Creates a new ThreadPoolExecutor with the given initial parameters. _corePoolSize <= _maximumPoolSize
     * 
     * @param _corePoolSize
     *            the max number of threads to keep executing and running in the pool, even if they are idle.
     * @param _maximumPoolSize
     *            the maximum number of threads to allow in the pool, reduce the reusing of threads.
     * @param _keepAliveTime
     *            when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.
     * @param _unit
     *            the time unit for the keepAliveTime argument.
     * @param _workQueue
     *            the queue to use for holding tasks before they are executed. This queue will hold only the Runnable tasks submitted by the execute method.
     * @param _threadFactory
     *            the factory to use when the executor creates a new thread.
     * @param _rejectedHandler
     *            callback when the task can never be executed because of the full of queue
     */
    public LLThreadPool( int _corePoolSize, int _maximumPoolSize, long _keepAliveTime, TimeUnit _unit,
            BlockingQueue<Runnable> _workQueue, RejectedExecutionHandler _rejectedHandler )
    {
        super(
                _corePoolSize,
                _maximumPoolSize,
                _keepAliveTime,
                _unit,
                _workQueue,
                new ThreadFactory()
                {
                    @Override
                    public Thread newThread( Runnable r )
                    {
                        Thread t = new Thread( r );
                        t.setPriority( Thread.NORM_PRIORITY - 1 );
                        return t;
                    }
                }, _rejectedHandler );
    }

    /**
     * In this override version we can store {@link LLTask}.
     * */
    @Override
    protected void beforeExecute( Thread _t, Runnable _r )
    {
        super.beforeExecute( _t, _r );

        if( _r instanceof LLTask )
            exectingPausableTasks.add( (LLTask) _r );

        mPauseLock.lock();
        try
        {
            while( isPaused )
                isUnpaused.await();
        }
        catch( InterruptedException ie )
        {
            _t.interrupt();
        }
        finally
        {
            mPauseLock.unlock();
        }
    }

    /**
     * In this override version we can remove {@link LLTask}.
     * */
    @Override
    protected void afterExecute( Runnable _r, Throwable _t )
    {
        super.afterExecute( _r, _t );
        if( _r instanceof LLTask )
            exectingPausableTasks.remove( _r );
    }

    /**
     * Suspend current running {@link LLTask}s.
     */
    public void suspendTasks()
    {
        for( LLTask r : exectingPausableTasks )
        {
            synchronized( this )
            {
                r.suspend();
            }
        }
    }

    /**
     * Proceed current suspended running {@link LLTask}s.
     */
    public void proceedTasks()
    {
        for( LLTask r : exectingPausableTasks )
        {
            synchronized( this )
            {
                r.proceed();
            }
        }
    }

    /**
     * Pause the thread pool so that the pool can not start executing tasks suddenly. Calling resumePool to restart paused pool.
     */
    public void pausePool()
    {
        mPauseLock.lock();
        try
        {
            isPaused = true;
        }
        finally
        {
            mPauseLock.unlock();
        }
    }

    /**
     * Resume the pool to execute tasks.
     */
    public void resumePool()
    {
        mPauseLock.lock();
        try
        {
            isPaused = false;
            isUnpaused.signalAll();
        }
        finally
        {
            mPauseLock.unlock();
        }
    }
}
