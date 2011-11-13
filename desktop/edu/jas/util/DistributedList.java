/*
 * $Id: DistributedList.java 2920 2009-12-25 16:50:47Z kredel $
 */

package edu.jas.util;

import java.io.IOException;
import java.util.Iterator;
//import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

//import edu.unima.ky.parallel.ChannelFactory;
//import edu.unima.ky.parallel.SocketChannel;


/**
 * Distributed version of a List.
 * Implemented with a SortedMap / TreeMap to keep the sequence order of elements.
 * @author Heinz Kredel
 */

public class DistributedList /* implements List not jet */ {

    private static final Logger logger = Logger.getLogger(DistributedList.class);

    protected final SortedMap<Counter,Object> theList;
    protected final ChannelFactory cf;
    protected SocketChannel channel = null;
    protected Listener listener = null;


    /**
     * Constructor for DistributedList.
     * @param host name or IP of server host.
     */ 
    public DistributedList(String host) {
        this(host,DistributedListServer.DEFAULT_PORT);
    }


    /**
     * Constructor for DistributedList.
     * @param host name or IP of server host.
     * @param port of server.
     */
    public DistributedList(String host,int port) {
        this(new ChannelFactory(port+1),host,port);
    }


    /**
     * Constructor for DistributedList.
     * @param cf ChannelFactory to use.
     * @param host name or IP of server host.
     * @param port of server.
     */
    public DistributedList(ChannelFactory cf,String host,int port) {
        this.cf = cf;
        try {
            channel = cf.getChannel(host,port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("dl channel = " + channel);
        theList = new TreeMap<Counter,Object>();
        listener = new Listener(channel,theList);
        listener.start();
    }


    /**
     * Constructor for DistributedList.
     * @param sc SocketChannel to use.
     */
    public DistributedList(SocketChannel sc) {
        cf = null;
        channel = sc;
        theList = new TreeMap<Counter,Object>();
        listener = new Listener(channel,theList);
        listener.start();
    }


/**
 * Get the internal list, convert from Collection.
 */ 
    public List<Object> getList() {
        return new ArrayList<Object>( theList.values() );
    }


/**
 * Size of the (local) list.
 */ 
    public int size() {
        return theList.size();
    }


/**
 * Add object to the list and distribute to other lists.
 * Blocks until the object is send and received from the server
 * (actually it blocks until some object is received).
 * @param o
 */
    public synchronized void add(Object o) {
        int sz1 = theList.size() + 1;
        try {
            channel.send(o);
            //System.out.println("send: "+o+" @ "+listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while ( theList.size() < sz1 ) {
                this.wait(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }


/**
 * Terminate the list thread.
 */ 
    public void terminate() {
        if ( cf != null ) {
           cf.terminate();
        }
        if ( channel != null ) {
           channel.close();
        }
        //theList.clear();
        if ( listener == null ) { 
           return;
        }
        logger.debug("terminate " + listener);
        listener.setDone(); 
        try { 
             while ( listener.isAlive() ) {
                     listener.interrupt(); 
                     listener.join(100);
             }
        } catch (InterruptedException u) { 
             Thread.currentThread().interrupt();
        }
        listener = null;
    }


/**
 * Clear the List.
 * caveat: must be called on all clients.
 */ 
    public synchronized void clear() {
        theList.clear();
    }


/**
 * Is the List empty?
 */ 
    public boolean isEmpty() {
        return theList.isEmpty();
    }


/**
 * List iterator.
 */ 
    public Iterator iterator() {
        return theList.values().iterator();
    }

}


/**
 * Thread to comunicate with the list server.
 */

class Listener extends Thread {

    private SocketChannel channel;
    private SortedMap<Counter,Object> theList;
    private boolean goon;


    Listener(SocketChannel s, SortedMap<Counter,Object> list) {
        channel = s;
        theList = list;
    } 


    void setDone() {
        goon = false;
    }

    @Override
     public void run() {
        Counter n;
        Object o;
        goon = true;
        while (goon) {
            n = null;
            o = null;
            try {
                n = (Counter) channel.receive();
                if ( this.isInterrupted() ) {
                   goon = false;
                } else {
                   o = channel.receive();
                   //System.out.println("receive("+n+","+o+" @ "+Thread.currentThread());
                   if ( this.isInterrupted() ) {
                      goon = false;
                   }
                   theList.put(n,o);
                }
            } catch (IOException e) {
                   goon = false;
            } catch (ClassNotFoundException e) {
                   e.printStackTrace();
                   goon = false;
            }
        }
    }

}
