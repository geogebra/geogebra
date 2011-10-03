/*
 * $Id: SocketChannel.java 2771 2009-08-05 20:10:49Z kredel $
 */

//package edu.unima.ky.parallel;
package edu.jas.util;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;


/**
 * SocketChannel provides a communication channel for Java objects using TCP/IP
 * sockets. Refactored for java.util.concurrent.
 * @author Akitoshi Yoshida
 * @author Heinz Kredel.
 */
public class SocketChannel {


    /*
     * Input stream from the socket.
     */
    private final ObjectInputStream in;


    /*
     * Output stream to the socket.
     */
    private final ObjectOutputStream out;


    /*
     * Underlying socket.
     */
    private final Socket soc;


    /**
     * Constructs a socket channel on the given socket s.
     * @param s A socket object.
     */
    public SocketChannel(Socket s) throws IOException {
        soc = s;
        out = new ObjectOutputStream(s.getOutputStream());
        out.flush();
        in = new ObjectInputStream(s.getInputStream());
    }


    /**
     * Get the Socket
     */
    public Socket getSocket() {
        return soc;
    }


    /**
     * Sends an object
     */
    public void send(Object v) throws IOException {
        synchronized (out) {
            out.writeObject(v);
            out.flush();
        }
    }


    /**
     * Receives an object
     */
    public Object receive() throws IOException, ClassNotFoundException {
        Object v = null;
        synchronized (in) {
            v = in.readObject();
        }
        return v;
    }


    /**
     * Closes the channel.
     */
    public void close() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
        if (soc != null) {
            try {
                soc.close();
            } catch (IOException e) {
            }
        }
    }


    /**
     * to string
     */
    @Override
    public String toString() {
        return "socketChannel(" + soc + ")";
    }

}
