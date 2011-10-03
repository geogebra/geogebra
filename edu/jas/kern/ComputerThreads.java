/*
 * $Id: ComputerThreads.java 3159 2010-05-29 18:42:56Z kredel $
 */

package edu.jas.kern;


import java.util.List;

//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.Future;
//import java.util.concurrent.Callable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;


/**
 * ComputerThreads,
 * provides global thread / executor service.
 * @author Heinz Kredel
 * @usage To obtain a reference to the thread pool use 
 * <code>ComputerThreads.getPool()</code>. 
 * Once a pool has been created it must be shutdown to exit JAS with
 * <code>ComputerThreads.terminate()</code>. 
 */

public class ComputerThreads {

    private static final Logger logger = Logger.getLogger(ComputerThreads.class);
    // private boolean debug = logger.isInfoEnabled(); //logger.isInfoEnabled();


    /**
      * Flag for thread usage.  
      * <b>Note:</b> Only introduced because
      * Google app engine does not support threads.
      * @see edu.jas.ufd.GCDFactory#getProxy(edu.jas.structure.RingFactory)
      */
    public static boolean NO_THREADS = false;


    /**
      * Number of processors.
      */
    public static final int N_CPUS = Runtime.getRuntime().availableProcessors();


    /*
      * Core number of threads.
      * N_CPUS x 1.5, x 2, x 2.5, min 3, ?.
      */
    public static final int N_THREADS = ( N_CPUS < 3 ? 3 : N_CPUS + N_CPUS/2 );
    //public static final int N_THREADS = ( N_CPUS < 3 ? 5 : 3*N_CPUS );


    /*
      * Queue capacity.
      */
    //public static final int Q_CAPACITY = 1000; // 10000


    /*
      * WorkQueue.
      */
    //private static BlockingQueue<Runnable> workpile; 


    /*
      * Saturation policy.
      */
    //public static final RejectedExecutionHandler REH = new ThreadPoolExecutor.CallerRunsPolicy();
    //public static final RejectedExecutionHandler REH = new ThreadPoolExecutor.AbortPolicy();


    /**
      * ExecutorService thread pool.
      */
    //static ThreadPoolExecutor pool = null;
    static ExecutorService pool = null;


    /**
     * No public constructor.
     */
    private ComputerThreads() {
    }


    /**
     * Test if a pool is running.
     * @return true if a thread pool has been started or is running, else false.
     */
    public static synchronized boolean isRunning() {
        if ( pool == null ) {
            return false;
        }
        if ( pool.isTerminated() || pool.isShutdown() ) {
            return false;
        }
        return true;
    }


    /**
     * Get the thread pool.
     * @return pool ExecutorService.
     */
    public static synchronized ExecutorService getPool() {
        if ( pool == null ) {
            // workpile = new ArrayBlockingQueue<Runnable>(Q_CAPACITY);
            pool = Executors.newFixedThreadPool(N_THREADS);
//             pool = new ThreadPoolExecutor(N_CPUS, N_THREADS,
//                                           100L, TimeUnit.MILLISECONDS,
//                                           workpile, REH);
//             pool = new ThreadPoolExecutor(N_CPUS, N_THREADS,
//                                           1000L, TimeUnit.MILLISECONDS,
//                                           workpile);
        }
        //System.out.println("pool_init = " + pool);
        return pool;
        //return Executors.unconfigurableExecutorService(pool);

            /* not useful, is not run from jython
            final GCDProxy<C> proxy = this;
            Runtime.getRuntime().addShutdownHook( 
                             new Thread() {
                                 public void run() {
                                        logger.info("running shutdown hook");
                                        proxy.terminate();
                                 }
                             }
            );
            */
    }


    /**
     * Stop execution.
     */
    public static synchronized void terminate() {
        if ( pool == null ) {
           return;
        }
        if ( pool instanceof ThreadPoolExecutor ) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor)pool;
            //logger.info("task queue size         " + Q_CAPACITY);
            //logger.info("reject execution handler" + REH.getClass().getName());
            logger.info("number of CPUs            " + N_CPUS);
            logger.info("core number of threads    " + N_THREADS);
            logger.info("current number of threads " + tpe.getPoolSize());
            logger.info("maximal number of threads " + tpe.getLargestPoolSize());
            BlockingQueue<Runnable> workpile = tpe.getQueue();
            if ( workpile != null ) {
                logger.info("queued tasks              " + workpile.size() );
            }
            List<Runnable> r = tpe.shutdownNow();
            if ( r.size() != 0 ) {
                logger.info("unfinished tasks          " + r.size() );
            }
            logger.info("number of sheduled tasks  " + tpe.getTaskCount());
            logger.info("number of completed tasks " + tpe.getCompletedTaskCount());
        }
        pool = null;
        //workpile = null;
    }

}
