/*
 * $Id: GBDist.java 2412 2009-02-07 12:17:54Z kredel $
 */

package edu.jas.gb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;
import edu.jas.util.ExecutableChannels;
import edu.jas.util.DistThreadPool;
import edu.jas.util.RemoteExecutable;

/**
 * Setup to run a distributed GB example.
 * @author Heinz Kredel
 */

public class GBDist<C extends RingElem<C>> {


    /**
     * machine file to use.
     */
    private final String mfile;


    /**
     * Number of threads to use.
     */
    protected final int threads;


    /**
     * Server port to use.
     */
    protected final int port;


    /**
     * GB algorithm to use.
     */
    private final GroebnerBaseDistributed<C> bbd;


    /**
     * Distributed thread pool to use.
     */
    private final DistThreadPool dtp;


    /**
     * Constructor.
     * @param threads number of threads respectivly processes.
     * @param mfile name of the machine file.
     * @param port for GB server. 
     */
    public GBDist(int threads, 
                  String mfile, 
                  int port) {
        this.threads = threads;
        if ( mfile == null || mfile.length() == 0 ) {
             this.mfile = "../util/machines";
        } else {
             this.mfile = mfile;
        }
        this.port = port;
        bbd = new GroebnerBaseDistributed<C>(threads,port);
        dtp = new DistThreadPool(threads,mfile);
    }


    /**
     * Execute a distributed GB example.
     * Distribute clients and start master.
     * Obsolete version.
     * @param F list of polynomials
     * @return GB(F) a Groebner base for F.
    public List<GenPolynomial<C>> 
           executeOld(List<GenPolynomial<C>> F) {
        final int numc = threads;
        List<GenPolynomial<C>> G = null;
        ExecutableChannels ec = null;
        try {
            ec = new ExecutableChannels( mfile );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return G;
        }
        try {
            ec.open(numc);
        } catch (IOException e) {
            e.printStackTrace();
            return G;
        }
        GBClient<C> gbc 
          = new GBClient<C>( ec.getMasterHost(), ec.getMasterPort() );
        try {
            for ( int i = 0; i < numc; i++ ) {
                ec.send( i, gbc );
            }
        } catch (IOException e) {
            e.printStackTrace();
            return G;
        }
        G = bbd.GB( F );
        try {
            for ( int i = 0; i < numc; i++ ) {
               Object o = ec.receive( i );
            }
        } catch (IOException e) {
            e.printStackTrace();
            return G;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return G;
        }
        ec.close();
        bbd.terminate();
        return G;
    }
     */


    /**
     * Execute a distributed GB example.
     * Distribute clients and start master.
     * @param F list of polynomials
     * @return GB(F) a Groebner base for F.
     */
    public List<GenPolynomial<C>> execute( List<GenPolynomial<C>> F ) {
        String master = dtp.getEC().getMasterHost();
        int port =  dtp.getEC().getMasterPort();
        GBClient<C> gbc = new GBClient<C>( master, port );
        for ( int i = 0; i < threads; i++ ) {
            // schedule remote clients
            dtp.addJob( gbc );
        }
        // run master
        List<GenPolynomial<C>> G = bbd.GB( F );
        return G;
    }


   /**
    * Terminates the distributed thread pools.
    * @param shutDown true, if shut-down of the 
    * remote executable servers is requested, 
    * false, if remote executable servers stay alive.
    */
    public void terminate(boolean shutDown) {
        bbd.terminate();
        dtp.terminate(shutDown);
    }

}


/**
 * Objects of this class are to be send to a ExecutableServer.
 */

class GBClient<C extends RingElem<C>> implements RemoteExecutable {

    String host;
    int port;

    /**
     * GBClient.
     * @param host
     * @param port
     */
    public GBClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

     
    /**
     * run.
     */
    public void run() {
        GroebnerBaseDistributed<C> bbd;
        bbd = new GroebnerBaseDistributed<C>(1,null,port);
        try {
            bbd.clientPart(host);
        } catch (IOException ignored) {
        }
        bbd.terminate();
    }

}
