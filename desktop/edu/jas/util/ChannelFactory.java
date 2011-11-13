/*
 * $Id: ChannelFactory.java 2815 2009-09-19 09:51:47Z kredel $
 */

//package edu.unima.ky.parallel;
package edu.jas.util;


import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.BindException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue; //import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;


/**
 * ChannelFactory implements a symmetric and non blocking way of setting up
 * sockets on the client and server side. The constructor sets up a ServerSocket
 * and accepts and stores any Socket creation requests from clients. The created
 * Sockets can the be retrieved from the store without blocking. Refactored for
 * java.util.concurrent.
 * @author Akitoshi Yoshida
 * @author Heinz Kredel.
 * @see SocketChannel
 */
public class ChannelFactory extends Thread {


    private static final Logger logger = Logger.getLogger(ChannelFactory.class);


    /**
     * default port of socket.
     */
    public final static int DEFAULT_PORT = 4711;


    /**
     * port of socket.
     */
    private final int port;


    /**
     * BoundedBuffer for sockets.
     */
    //private BoundedBuffer buf = new BoundedBuffer(100);
    private final BlockingQueue<SocketChannel> buf;


    /**
     * local server socket.
     */
    private volatile ServerSocket srv;


    /**
     * is local server running.
     */
    private volatile boolean srvrun = false;


    /**
     * Constructs a ChannelFactory.
     * @param p port.
     */
    public ChannelFactory(int p) {
        // buf = new ArrayBlockingQueue<SocketChannel>(100); 
        buf = new LinkedBlockingQueue<SocketChannel>(/*infinite*/);
        if (p <= 0) {
            port = DEFAULT_PORT;
        } else {
            port = p;
        }
        try {
            srv = new ServerSocket(port);
            this.start();
            logger.info("server started on port " + port);
        } catch (BindException e) {
            srv = null;
            logger.warn("server not started, port used " + port);
        } catch (IOException e) {
            logger.debug("IOException " + e);
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return "" + this.getClass().getSimpleName() + "(" + srv + ", buf = " + buf.size() + ")";
    }


    /**
     * Constructs a ChannelFactory on the DEFAULT_PORT.
     */
    public ChannelFactory() {
        this(DEFAULT_PORT);
    }


    /**
     * Get a new socket channel from a server socket.
     */
    public SocketChannel getChannel() throws InterruptedException {
        // return (SocketChannel)buf.get();
        if (srv == null) {
            if (srvrun) {
                throw new IllegalArgumentException("dont call when no server listens");
            }
        }
        return buf.take();
    }


    /**
     * Get a new socket channel to a given host.
     * @param h hostname
     * @param p port
     */
    public SocketChannel getChannel(String h, int p) throws IOException {
        if (p <= 0) {
            p = port;
        }
        SocketChannel c = null;
        int i = 0;
        int delay = 5; // 50
        logger.debug("connecting to " + h);
        while (c == null) {
            try {
                c = new SocketChannel(new Socket(h, p));
            } catch (IOException e) {
                //System.out.println(e);
                // wait server ready
                i++;
                if (i % 50 == 0) {
                    delay += delay;
                    logger.info("Server on " + h + " not ready in " + delay + "ms");
                }
                System.out.println("Server on " + h + " not ready in " + delay + "ms");
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException w) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted during IO wait " + w);
                }
            }
        }
        logger.debug("connected, iter = " + i);
        return c;
    }


    /**
     * Run the servers accept() in an infinite loop.
     */
    @Override
    public void run() {
        if (srv == null) {
            return; // nothing to do
        }
        srvrun = true;
        while (true) {
            try {
                logger.info("waiting for connection");
                Socket s = srv.accept();
                if (this.isInterrupted()) {
                    //System.out.println("ChannelFactory interrupted");
                    srvrun = false;
                    return;
                }
                //logger.debug("Socket = " +s);
                logger.debug("connection accepted");
                SocketChannel c = new SocketChannel(s);
                buf.put(c);
            } catch (IOException e) {
                //logger.debug("ChannelFactory IO terminating");
          srvrun = false;
                return;
            } catch (InterruptedException e) {
                // unfug Thread.currentThread().interrupt();
                //logger.debug("ChannelFactory IE terminating");
                srvrun = false;
                return;
            }
        }
    }


    /**
     * Terminate the Channel Factory
     */
    public void terminate() {
        this.interrupt();
        try {
            if (srv != null) {
                srv.close();
          srvrun = false;
            }
            this.interrupt();
            while (!buf.isEmpty()) {
                logger.debug("closing unused SocketChannel");
                //((SocketChannel)buf.get()).close();
                buf.take().close();
            }
        } catch (IOException e) {
        } catch (InterruptedException e) {
            // Thread.currentThread().interrupt();
        }
        try {
            this.join();
        } catch (InterruptedException e) {
            // unfug Thread.currentThread().interrupt();
        }
        logger.debug("ChannelFactory terminated");
    }

}
