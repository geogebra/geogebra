/*
 * $Id: DistributedListServer.java 2920 2009-12-25 16:50:47Z kredel $
 */

package edu.jas.util;

import java.io.IOException;
import java.io.Serializable;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

//import edu.unima.ky.parallel.ChannelFactory;
//import edu.unima.ky.parallel.SocketChannel;


/**
 * Server for the distributed version of a list.
 * @author Heinz Kredel
 * @todo redistribute list for late comming clients, removal of elements.
 */
public class DistributedListServer extends Thread {

    private static final Logger logger = Logger.getLogger(DistributedListServer.class);

    public final static int DEFAULT_PORT = ChannelFactory.DEFAULT_PORT + 99;
    protected final ChannelFactory cf;

    protected List<Broadcaster> servers;

    private boolean goon = true;
    private Thread mythread = null;

    private Counter listElem = null;
    protected final SortedMap<Counter,Object> theList;


    /**
     * Constructs a new DistributedListServer.
     */ 

    public DistributedListServer() {
        this(DEFAULT_PORT);
    }

    /**
     * DistributedListServer.
     * @param port to run server on.
     */
    public DistributedListServer(int port) {
        this( new ChannelFactory(port) );
    }

    /**
     * DistributedListServer.
     * @param cf ChannelFactory to use.
     */
    public DistributedListServer(ChannelFactory cf) {
        listElem = new Counter(0);
        this.cf = cf;
        servers = new ArrayList<Broadcaster>();
        theList = new TreeMap<Counter,Object>();
    }


    /**
     * main.
     * Usage: DistributedListServer &lt;port&gt;
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if ( args.length < 1 ) {
            System.out.println("Usage: DistributedListServer <port>");
        } else {
            try {
                port = Integer.parseInt( args[0] );
            } catch (NumberFormatException e) {
            }
        }
        (new DistributedListServer(port)).run();
        // until CRTL-C
    }


    /**
     * thread initialization and start.
     */ 
    public void init() {
        this.start();
    }


    /**
     * main server method.
     */ 
    @Override
     public void run() {
        SocketChannel channel = null;
        Broadcaster s = null;
        mythread = Thread.currentThread();
        Entry e;
        Object n;
        Object o;
        while (goon) {
            // logger.debug("list server " + this + " go on");
            try {
                channel = cf.getChannel();
                logger.debug("dls channel = "+channel);
                if ( mythread.isInterrupted() ) {
                    goon = false;
                    //logger.info("list server " + this + " interrupted");
                } else {
                    s = new Broadcaster(channel,servers,listElem,theList);
                    int ls = 0;
                    synchronized (servers) {
                        servers.add( s );
                        ls = theList.size();
                        s.start();
                    }
                    //logger.debug("server " + s + " started");
                    if ( ls > 0 ) {
                        logger.info("sending " + ls + " list elements");
                        synchronized (theList) {
                            Iterator it = theList.entrySet().iterator();
                            for ( int i = 0; i < ls; i++ ) {
                                e = (Entry)it.next();
                                n = e.getKey();
                                o = e.getValue();
                                try {
                                    s.sendChannel( n,o );
                                } catch (IOException ioe) {
                                    // stop s
                                }
                            }
                        } 
                    }
                }
            } catch (InterruptedException end) {
                goon = false;
                Thread.currentThread().interrupt();
            }
        }
        //logger.debug("listserver " + this + " terminated");
    }


    /**
     * terminate all servers.
     */ 
    public void terminate() {
        goon = false;
        logger.debug("terminating ListServer");
        if ( cf != null ) cf.terminate();
        if ( servers != null ) {
            Iterator it = servers.iterator();
            while ( it.hasNext() ) {
                Broadcaster br = (Broadcaster) it.next();
                br.closeChannel();
                try { 
                    while ( br.isAlive() ) {
                        //System.out.print(".");
                        br.interrupt(); 
                        br.join(100);
                    }
                    //logger.debug("server " + br + " terminated");
                } catch (InterruptedException e) { 
                    Thread.currentThread().interrupt();
                }
            }
            servers = null;
        }
        logger.debug("Broadcasters terminated");
        if ( mythread == null ) return;
        try { 
            while ( mythread.isAlive() ) {
                // System.out.print("-");
                mythread.interrupt(); 
                mythread.join(100);
            }
            //logger.debug("server " + mythread + " terminated");
        } catch (InterruptedException e) { 
            Thread.currentThread().interrupt();
        }
        mythread = null;
        logger.debug("ListServer terminated");
    }


    /**
     * number of servers.
     */ 
    public int size() {
        return servers.size();
    }

}


/**
 * Class for holding the list index used a key in TreeMap.
 * Implemented since Integer has no add() method.
 * Must implement Comparable so that TreeMap works with correct ordering.
 */ 

class Counter implements Serializable, Comparable<Counter> {

    private int value;


    /**
     * Counter.
     */
    public Counter() {
        this(0);
    }


    /**
     * Counter.
     * @param v
     */
    public Counter(int v) {
        value = v;
    }


    /**
     * intValue.
     * @return the value.
     */
    public int intValue() {
        return value;
    }


    /**
     * add.
     * @param v
     */
    public void add(int v) { // synchronized elsewhere
        value += v;
    }


    /**
     * equals.
     * @param ob an Object.
     * @return true if this is equal to o, else false.
     */
    @Override
     public boolean equals(Object ob) {
        if ( ! (ob instanceof Counter) ) {
           return false;
        }
        return 0 == compareTo( (Counter)ob );
    }


    /**
     * compareTo.
     * @param c a Counter.
     * @return 1 if (this &lt; c), 0 if (this == c), -1 if (this &gt; c).
     */
    public int compareTo(Counter c) {
        int x = c.intValue();
        if ( value > x ) { 
            return 1;
        }
        if ( value < x ) { 
            return -1;
        }
        return 0;
    }


    /**
     * toString.
     */  
    @Override
     public String toString() {
        return "Counter("+value+")";
    }

}


/**
 * Thread for broadcasting all incoming objects to the list clients.
 */ 

class Broadcaster extends Thread /*implements Runnable*/ {

    private static final Logger logger = Logger.getLogger(Broadcaster.class);
    private final SocketChannel channel;
    private final List bcaster;
    private Counter listElem;
    private final SortedMap<Counter,Object> theList;


    /**
     * Broadcaster.
     * @param s SocketChannel to use.
     * @param p list of broadcasters.
     * @param le counter
     * @param sm SortedMap with counter value pairs.
     */
    public Broadcaster(SocketChannel s, List p, Counter le, SortedMap<Counter,Object> sm) {
        channel = s;
        bcaster = p;
        listElem = le;
        theList = sm;
    } 


    /**
     * closeChannel.
     */
    public void closeChannel() {
        channel.close();
    }


    /**
     * sendChannel.
     * @param n counter.
     * @param o value.
     * @throws IOException
     */
    public void sendChannel(Object n, Object o) throws IOException {
        synchronized (channel) {
            channel.send(n);
            channel.send(o);
        }
    }


    /**
     * broadcast.
     * @param o object to store and send.
     */
    public void broadcast(Object o) {
        Counter li = null;
        synchronized (listElem) {
            listElem.add(1);
            li = new Counter( listElem.intValue() );
        }
        synchronized (theList) {
            theList.put( li, o );
        }
        synchronized (bcaster) {
            Iterator it = bcaster.iterator();
            while ( it.hasNext() ) {
                Broadcaster br = (Broadcaster) it.next();
                try {
                    br.sendChannel(li,o);
                    //System.out.println("bcast: "+o+" to "+x.channel);
                } catch (IOException e) {
                    try { 
                        br.closeChannel();
                        while ( br.isAlive() ) {
                            br.interrupt(); 
                            br.join(100);
                        }
                    } catch (InterruptedException u) { 
                        Thread.currentThread().interrupt();
                    }
                    bcaster.remove( br );
                }
            }
        }
    }


    /**
     * run.
     */
    @Override
     public void run() {
        Object o;
        boolean goon = true;
        while (goon) {
            try {
                o = channel.receive();
                //System.out.println("receive: "+o+" from "+channel);
                broadcast(o);
                if ( this.isInterrupted() ) {
                    goon = false;
                }
            } catch (IOException e) {
                goon = false;
            } catch (ClassNotFoundException e) {
                goon = false;
                e.printStackTrace();

            }
        }
        logger.debug("broadcaster terminated "+this);
        channel.close();
    }


    /**
     * toString.
     * @return a string representation of this.
     */
    @Override
     public String toString() {
        return "Broadcaster("+channel+","+bcaster.size()+","+listElem+")";
    }

}
