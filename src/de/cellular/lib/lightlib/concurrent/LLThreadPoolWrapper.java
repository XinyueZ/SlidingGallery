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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;

import android.content.ComponentCallbacks;
import android.content.res.Configuration;

/**
 * Factory and adapter of {@link LLThreadPool}
 * 
 * @author Chris.Z
 * 
 */
public class LLThreadPoolWrapper implements ComponentCallbacks
{
    /**
     * Loading thread pool
     */
    private LLThreadPool mPool;

    /**
     * Prevent from creating a wrapper directly.
     */
    private LLThreadPoolWrapper()
    {

    }

    /**
     * Creates a new ThreadPoolExecutor with the given initial parameters. _corePoolSize <= _maximumPoolSize
     * 
     * @param _corePoolSize
     *            the max number of threads to keep executing and running in the pool, even if they are idle.
     * @param _maximumPoolSize
     *            the maximum number of threads to allow in the pool, reduce the reusing of threads.
     * @param _queueSize
     *            max size for the queue that holding tasks.
     * @param _rejectedHandler
     *            callback when the task can never be executed because of the full of queue
     * @return
     */
    public static LLThreadPoolWrapper newPausableThreadPool( int _corePoolSize, int _maximumPoolSize,
            int _queueSize, RejectedExecutionHandler _rejectedHandler )
    {
        LLThreadPoolWrapper wrapper = new LLThreadPoolWrapper();
        wrapper.mPool = new LLThreadPool( _corePoolSize, _maximumPoolSize,
                120, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>( _queueSize ), _rejectedHandler
                ); 
        return wrapper;
    }

    /**
     * Creates a new ThreadPoolExecutor with the given initial parameters. _corePoolSize <= _maximumPoolSize
     * 
     * @param _corePoolSize
     *            the max number of threads to keep executing and running in the pool, even if they are idle.
     * @param _maximumPoolSize
     *            the maximum number of threads to allow in the pool, reduce the reusing of threads.
     * @param _rejectedHandler
     *            callback when the task can never be executed because of the full of queue
     * @return
     */
    public static LLThreadPoolWrapper newPausableThreadPool( int _corePoolSize, int _maximumPoolSize,
            RejectedExecutionHandler _rejectedHandler )
    {
        LLThreadPoolWrapper wrapper = new LLThreadPoolWrapper();
        wrapper.mPool = new LLThreadPool( _corePoolSize, _maximumPoolSize,
                120, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), _rejectedHandler
                );
        return wrapper;
    }

    /**
     * Pause the thread pool so that the pool can not start executing tasks suddenly. Calling resumePool to restart paused pool.
     */
    public void pausePool()
    {
        getPool().pausePool();
    }

    /**
     * Resume the pool to execute tasks.
     */
    public void resumePool()
    {
        getPool().resumePool();
    }

    /**
     * Suspend current running {@link LLTask}s.
     */
    public void suspendTasks()
    {
        getPool().suspendTasks();
    }

    /**
     * Proceed current suspended running {@link LLTask}s.
     */
    public void proceedTasks()
    {
        getPool().proceedTasks();
    }
    
    /**
     * Execute a task
     * @param _task
     */
    public void execute( Runnable _task )
    {
        getPool().execute( _task );
    }
    

    /**
     * Execute a task
     * @param <T>
     * @param _task
     */
    public Future<?> submit( Callable<?> _task )
    {
        return  getPool().submit( _task );
    }

    /**
     * Return the Thread-Pool
     * 
     * @return
     */
    public LLThreadPool getPool()
    {
        return mPool;
    }

    @Override
    public void onConfigurationChanged( Configuration _newConfig )
    {

    }

    @Override
    public void onLowMemory()
    {
        shutdownAndAwaitTermination( getPool() );
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        shutdownAndAwaitTermination( getPool() );
    }

    /**
     * Shutdown pool and exclude to submiting new tasks.
     */
    public void shutdownPool()
    {
        if( !getPool().isShutdown() )
            shutdownAndAwaitTermination( getPool() );
    }

    /**
     * Shuts down an ExecutorService in two phases, first by calling shutdown to reject incoming tasks, and then calling shutdownNow, if necessary, to cancel any lingering tasks
     * 
     * @see http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html
     * @param pool
     */
    private void shutdownAndAwaitTermination( ExecutorService pool )
    {
        pool.shutdown(); // Disable new tasks from being submitted
        try
        {
            // Wait a while for existing tasks to terminate
            if( !pool.awaitTermination( 60, TimeUnit.SECONDS ) )
            {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if( !pool.awaitTermination( 60, TimeUnit.SECONDS ) )
                    System.err.println( "Pool did not terminate" );
            }
        }
        catch( InterruptedException ie )
        {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
