/*
 * $Id: ExecutableChannels.java 1772 2008-05-07 20:51:08Z kredel $
 */

package edu.jas.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

//import edu.unima.ky.parallel.ChannelFactory;
//import edu.unima.ky.parallel.SocketChannel;

/**
 * ExecutableChannels
 * used to receive and execute classes.
 * @author Heinz Kredel
 */


public class ExecutableChannels {

    private static final Logger logger = Logger.getLogger(ExecutableChannels.class);


    /**
     * default port.
     */
    protected final static int DEFAULT_PORT = 7114; //ChannelFactory.DEFAULT_PORT;


    /**
     * default machine file.
     */
    protected final static String DEFAULT_MFILE = "examples/machines.test";


    protected final ChannelFactory cf;


    protected SocketChannel[] channels = null;


    protected String[] servers = null;


    protected int[] ports = null;


    /**
     * Internal constructor.
     */
    protected ExecutableChannels() {
        cf = new ChannelFactory();
    }


    /**
     * Constructor from array of server:port strings.
     * @param srvs A String array.
     */
    public ExecutableChannels(String[] srvs) {
        this();
        if ( srvs == null ) {
            return;
        }
        servers = new String[ srvs.length ];
        ports = new int[ srvs.length ];
        for ( int i = 0; i < srvs.length; i++ ) {
            setServerPort( i, srvs[i] );
        }
    }


    /**
     * Constructor from machine file.
     * @param mfile
     * @throws FileNotFoundException.
     */
    public ExecutableChannels(String mfile) throws FileNotFoundException {
        this();
        if ( mfile == null || mfile.length() == 0 ) {
           mfile = DEFAULT_MFILE;
        }
        BufferedReader in = new BufferedReader( new FileReader( mfile ) );
        String line = null;
        List<String> list = new ArrayList<String>();
        int x;
        try {
            while (true) {
               if ( !in.ready() ) {
                  break;
               }
               line = in.readLine();
               if ( line == null ) {
                  break;
               }
               x = line.indexOf("#");
               if ( x >= 0 ) {
                   line = line.substring(0,x);
               }
               line = line.trim();
               if ( line.length() == 0 ) {
                   continue;
               }
               list.add(line);
            }
        } catch (IOException ignored) {
        } finally {
            try {
                in.close();
            } catch (IOException ignore) {
            }
        }
        logger.debug("list.size() in " + mfile + " = " + list.size());
        if ( list.size() == 0 ) {
            return;
        }
        servers = new String[ list.size() ];
        ports = new int[ list.size() ];
        for ( int i = 0; i < servers.length; i++ ) {
            setServerPort( i, list.get( i ) );
        }
    }


    /* 
     * internal method
     */
    protected void setServerPort(int i, String srv) {
        int x = srv.indexOf(":");
        ports[i] = DEFAULT_PORT;
        if ( x < 0 ) {
           servers[i] = srv;
        } else {
           servers[i] = srv.substring(0,x);
           String p = srv.substring(x+1,srv.length());
           try { 
                ports[i] = Integer.parseInt( p );
           } catch (NumberFormatException ignored) {
           } 
        }
    }


   /**
    * String representation.
    */ 
    @Override
     public String toString() {
        StringBuffer s = new StringBuffer("ExecutableChannels(");
        if ( servers != null ) {
           for ( int i = 0; i < servers.length; i++ ) {
               s.append( servers[i] + ":" + ports[i] );
               if ( i < servers.length-1 ) {
                  s.append(" ");
               }
           }
        }
        if ( channels != null ) {
           s.append(" channels = ");
           for ( int i = 0; i < channels.length; i++ ) {
               s.append( channels[i] );
               if ( i < channels.length-1 ) {
                  s.append(" ");
               }
           }
        }
        s.append(")");
        return s.toString();
    }


   /**
    * number of servers.
    */ 
    public int numServers() {
        if ( servers != null ) {
           return servers.length;
        } else {
            return -1;
        }
    }


   /**
    * get master host.
    */ 
    public String getMasterHost() {
        if ( servers != null && servers.length > 0 ) {
           return servers[0];
        } else {
            return null;
        }
    }


   /**
    * get master port.
    */ 
    public int getMasterPort() {
        if ( ports != null && ports.length > 0 ) {
           return ports[0];
        } else {
            return 0;
        }
    }


   /**
    * number of channels.
    */ 
    public int numChannels() {
        if ( channels != null ) {
           return channels.length;
        } else {
            return -1;
        }
    }


   /**
    * open, setup of SocketChannels. 
    * @throws IOException.
    */
    public void open() throws IOException {
        logger.debug("opening " + servers.length + " channels");
        if ( servers.length <= 1 ) {
            throw new IOException("to few servers");
        }
        channels = new SocketChannel[ servers.length-1 ];
        for ( int i = 1; i < servers.length; i++ ) {
            channels[i-1] = cf.getChannel( servers[i], ports[i] );
        }
    }


   /**
    * open, setup of SocketChannels. 
    * If nc &gt; servers.length open in round robin fashion.
    * @param nc number of channels to open.
    * @throws IOException.
    */
    public void open(int nc) throws IOException {
        logger.debug("opening " + nc + " channels");
        if ( servers.length <= 1 ) {
            throw new IOException("to few servers");
        }
        channels = new SocketChannel[ nc ];
        int j = 1; // 0 is master
        for ( int i = 0; i < channels.length; i++ ) {
            if ( j >= servers.length ) { // modulo #servers
                j = 1;
            }
            channels[i] = cf.getChannel( servers[j], ports[j] );
            j++;
       }
    }


   /**
    * close all channels and ChannelFactory.
    */ 
    public void close() {
        logger.debug("closing ExecutableChannels");
        if ( cf != null ) {
           cf.terminate();
        }
        if ( channels != null ) {
           for ( int i = 0; i < channels.length; i++ ) {
               if ( channels[i] != null ) {
                  try {
                      channels[i].send( ExecutableServer.STOP );
                  } catch (IOException e) {
                      e.printStackTrace();
                  } finally {
                      channels[i].close();
                  }
                  channels[i] = null;
               }
           }
           channels = null;
        }
        logger.debug("ExecuteChannels closed");
    }


   /**
    * getChannel.
    * @param i channel number.
    */ 
    public SocketChannel getChannel(int i) {
        if ( channels != null && 0 <= i && i < channels.length ) {
           return channels[i];
        } else {
            return null;
        }
    }


   /**
    * getChannels.
    */ 
    /*package*/ SocketChannel[] getChannels() {
        return channels;
    }


   /**
    * send on channel i.
    * @param i channel number.
    * @param o object to send.
    */ 
    public void send(int i, Object o) throws IOException {
        if ( channels != null && 0 <= i && i < channels.length ) {
           channels[i].send(o);
        } 
    }


   /**
    * recieve on channel i.
    * @param i channel number.
    * @return object recieved.
    */ 
    public Object receive(int i) throws IOException, ClassNotFoundException {
        if ( channels != null && 0 <= i && i < channels.length ) {
           return channels[i].receive();
        } else {
            return null;
        }

    }

}
