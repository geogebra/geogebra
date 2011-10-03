/*
 * $Id: TaggedSocketChannel.java 3076 2010-04-15 21:00:37Z kredel $
 */

package edu.jas.util;


import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;


/**
 * TaggedSocketChannel provides a communication channel with message tags for
 * Java objects using TCP/IP sockets.
 * @author Heinz Kredel.
 */
public class TaggedSocketChannel extends Thread {


    private static final Logger logger = Logger.getLogger(TaggedSocketChannel.class);


    private static final boolean debug = true || logger.isDebugEnabled();


    /**
     * Flag if receiver is running.
     */
    private volatile boolean isRunning = false;


    /**
     * End message.
     */
    private final static String DONE = "TaggedSocketChannel Done";


    /**
     * Blocked threads count.
     */
    private final AtomicInteger blockedCount;


    /**
     * Underlying socket channel.
     */
    protected final SocketChannel sc;


    /**
     * Queues for each message tag.
     */
    protected final Map<Integer, BlockingQueue> queues;


    /**
     * Constructs a tagged socket channel on the given socket channel s.
     * @param s A socket channel object.
     */
    public TaggedSocketChannel(SocketChannel s) {
        sc = s;
        blockedCount = new AtomicInteger(0);
        queues = new HashMap<Integer, BlockingQueue>();
        synchronized (queues) {
            this.start();
            isRunning = true;
        }
    }


    /**
     * Get the SocketChannel
     */
    public SocketChannel getSocket() {
        return sc;
    }


    /**
     * Sends an object.
     * @param tag message tag
     * @param v object to send
     * @throws IOException
     */
    public void send(Integer tag, Object v) throws IOException {
        if (tag == null) {
            throw new IllegalArgumentException("tag " + tag + " not allowed");
        }
        if (v instanceof Exception) {
            throw new IllegalArgumentException("message " + v + " not allowed");
        }
        TaggedMessage tm = new TaggedMessage(tag, v);
        sc.send(tm);
    }


    /**
     * Receive an object.
     * @param tag message tag
     * @return object received
     * @throws InterruptedException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object receive(Integer tag) throws InterruptedException, IOException, ClassNotFoundException {
        BlockingQueue tq = null;
        int i = 0;
        do {
            synchronized (queues) {
                tq = queues.get(tag);
                if (tq == null) {
                    if ( ! isRunning ) { // avoid dead-lock
                        throw new IOException("receiver not running for " + this);
                    }
                    //tq = new LinkedBlockingQueue();
                    //queues.put(tag, tq);
                    try {
                        logger.info("receive wait, tag = " + tag);
                        i = blockedCount.incrementAndGet();
                        queues.wait();
                    } catch (InterruptedException e) {
                        logger.info("receive wait exception, tag = " + tag + ", blockedCount = " + i);
                        throw e;
                    } finally {
                        i = blockedCount.decrementAndGet();
                    }
                }
            }
        } while ( tq == null );
        Object v = null;
        try {
            i = blockedCount.incrementAndGet();
            v = tq.take();
        } finally {
            i = blockedCount.decrementAndGet();
        }
        if ( v instanceof IOException ) {
            throw (IOException) v;
        }
        if ( v instanceof ClassNotFoundException ) {
            throw (ClassNotFoundException) v;
        }
        if ( v instanceof Exception ) {
            throw new RuntimeException(v.toString());
        }
        return v;
    }


    /**
     * Closes the channel.
     */
    public void close() {
        terminate();
    }


    /**
     * To string.
     * @see java.lang.Thread#toString()
     */
    @Override
    public String toString() {
        return "socketChannel(" + sc + ", tags = " + queues.keySet() + ")";
        //return "socketChannel(" + sc + ", tags = " + queues.keySet() + ", values = " + queues.values() + ")";
    }


    /**
     * Number of tags.
     * @return size of key set.
     */
    public int tagSize() {
        return queues.keySet().size();
    }


    /**
     * Number of messages.
     * @return sum of all messages in queues.
     */
    public int messages() {
        int m = 0;
        for ( BlockingQueue tq : queues.values() ) {
            m += tq.size();
        }
        return m;
    }


    /**
     * Run receive() in an infinite loop.
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        if (sc == null) {
            isRunning = false;
            return; // nothing to do
        }
        isRunning = true;
        while (isRunning) {
            try {
                Object r = null;
                try {
                    logger.debug("waiting for tagged object");
                    r = sc.receive();
                    if (this.isInterrupted()) {
                        //r = new InterruptedException();
                        isRunning = false;
                    }
                } catch (IOException e) {
                    r = e;
                } catch (ClassNotFoundException e) {
                    r = e;
                } catch (Exception e) {
                    r = e;
                }
                //logger.debug("Socket = " +s);
                logger.debug("object recieved");
                if (r instanceof TaggedMessage) {
                    TaggedMessage tm = (TaggedMessage) r;
                    BlockingQueue tq = null;
                    synchronized (queues) {
                        tq = queues.get(tm.tag);
                        if (tq == null) {
                            tq = new LinkedBlockingQueue();
                            queues.put(tm.tag, tq);
                            queues.notifyAll();
                        }
                    }
                    tq.put(tm.msg);
                } else if ( r instanceof Exception ){
                    if (debug) {
                        logger.info("exception " + r);
                    }
                    synchronized (queues) { // deliver to all queues
                        isRunning = false;
                        for ( BlockingQueue q : queues.values() ) {
                            final int bc = blockedCount.get();
                            for ( int i = 0; i <= bc; i++ ) { // one more
                                q.put(r);
                            }
                            if (bc > 0) {
                                logger.info("put exception to queue, blockedCount = " + bc);
                            }
                        }
                        queues.notifyAll();
                    }
                    //return;
                } else {
                    if (debug) {
                        logger.info("no tagged message and no exception " + r);
                    }
                    synchronized (queues) { // deliver to all queues
                        isRunning = false;
                        Exception e;
                        if ( r.equals(DONE) ) {
                            e = new Exception("DONE message");
                        } else {
                            e = new IllegalArgumentException("no tagged message and no exception '" + r + "'");
                        }
                        for ( BlockingQueue q : queues.values() ) {
                            final int bc = blockedCount.get();
                            for ( int i = 0; i <= bc; i++ ) { // one more
                                q.put(e);
                            }
                            if (bc > 0) {
                                logger.info("put '" + e.toString() + "' to queue, blockedCount = " + bc);
                            }
                        }
                        queues.notifyAll();
                    }
                    if ( r.equals(DONE) ) {
                         logger.info("run terminating by request");
                         try {
                             sc.send(DONE); // terminate other end
                         } catch (IOException e) {
                             logger.warn("send other done failed " + e);
                         }
                         return;
                    }
                }
            } catch (InterruptedException e) {
                // unfug Thread.currentThread().interrupt();
                //logger.debug("ChannelFactory IE terminating");
                if (debug) {
                    logger.info("exception " + e);
                }
                synchronized (queues) { // deliver to all queues
                    isRunning = false;
                    for ( BlockingQueue q : queues.values() ) {
                        try {
                            final int bc = blockedCount.get();
                            for ( int i = 0; i <= bc; i++ ) { // one more
                                q.put(e);
                            }
                            if (bc > 0) {
                                logger.info("put interrupted to queue, blockCount = " + bc);
                            }
                        } catch (InterruptedException ignored) {
                        }
                    }
                    queues.notifyAll();
                }
                //return via isRunning
            }
        }
        if (this.isInterrupted()) {
            Exception e = new InterruptedException("terminating via interrupt");
            synchronized (queues) { // deliver to all queues
                for ( BlockingQueue q : queues.values() ) {
                    try {
                        final int bc = blockedCount.get();
                        for ( int i = 0; i <= bc; i++ ) { // one more
                            q.put(e);
                        }
                        if (bc > 0) {
                            logger.info("put terminating via interrupt to queue, blockCount = " + bc);
                        }
                    } catch (InterruptedException ignored) {
                    }
                }
                queues.notifyAll();
            }
        }
        logger.info("run terminated");
    }


    /**
     * Terminate the TaggedSocketChannel.
     */
    public void terminate() {
        isRunning = false;
        this.interrupt();
        if (sc != null) {
            //sc.close();
            try {
                sc.send(DONE);
            } catch (IOException e) {
                logger.warn("send done failed " + e);
            }
            logger.debug(sc + " not yet closed");
        }
        this.interrupt();
        synchronized(queues) {
            isRunning = false;
            for (Entry<Integer, BlockingQueue> tq : queues.entrySet()) {
                BlockingQueue q = tq.getValue();
                if (q.size() != 0) {
                    logger.info("queue for tag " + tq.getKey() + " not empty " + q);
                } 
                int bc = 0;
                try {
                    bc = blockedCount.get();
                    for ( int i = 0; i <= bc; i++ ) { // one more
                        q.put(new IOException("queue terminate"));
                    }
                } catch (InterruptedException ignored) {
                }
                if ( bc > 0 ) {
                    logger.info("put IO-end to queue for tag " + tq.getKey() + ", blockCount = " + bc);
                }
            }
            queues.notifyAll();
        }
        try {
            this.join();
        } catch (InterruptedException e) {
            // unfug Thread.currentThread().interrupt();
        }
        logger.info("terminated");
    }

}


/**
 * TaggedMessage container.
 * @author kredel
 * 
 */
class TaggedMessage implements Serializable {


    public final Integer tag;


    public final Object msg;


    /**
     * Constructor.
     * @param tag message tag
     * @param msg message object
     */
    public TaggedMessage(Integer tag, Object msg) {
        this.tag = tag;
        this.msg = msg;
    }

}
