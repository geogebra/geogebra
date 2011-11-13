/*
 * $Id: RunGB.java 3063 2010-04-04 12:37:36Z kredel $
 */

package edu.jas.gb;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;
import edu.jas.util.ExecutableServer;


/**
 * Simple setup to run a GB example. <br />
 * Usage: RunGB [seq(+)|par(+)|dist(1)(+)|disthyb|cli] &lt;file&gt;
 * #procs/#threadsPerNode [machinefile]
 * @author Heinz Kredel
 */

public class RunGB {


    /**
     * main method to be called from commandline <br />
     * Usage: RunGB [seq|par(+)|dist(1)(+)|disthyb|cli] &lt;file&gt;
     * #procs/#threadsPerNode [machinefile]
     */

    public static void main(java.lang.String[] args) {

        BasicConfigurator.configure();

        String usage = "Usage: RunGB "
            + "[ seq | seq+ | par | par+ | dist | dist1 | dist+ | dist1+ | disthyb1 | cli [port] ] "
            + "<file> " + "#procs/#threadsPerNode " + "[machinefile]";
        if (args.length < 1) {
            System.out.println(usage);
            return;
        }

        boolean pairseq = false;
        String kind = args[0];
        String[] allkinds = new String[] { "seq", "seq+", "par", "par+", "dist", "dist1", "dist+", "dist1+",
                                           "disthyb1", "cli" };
        boolean sup = false;
        for (int i = 0; i < allkinds.length; i++) {
            if (kind.equals(allkinds[i])) {
                sup = true;
                if (kind.indexOf("+") >= 0) {
                    pairseq = true;
                }
            }
        }
        if (!sup) {
            System.out.println(usage);
            return;
        }

        boolean once = false;
        final int GB_SERVER_PORT = 7114;
        //inal int EX_CLIENT_PORT = GB_SERVER_PORT + 1000; 
        int port = GB_SERVER_PORT;

        if (kind.equals("cli")) {
            if (args.length >= 2) {
                try {
                    port = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.out.println(usage);
                    return;
                }
            }
            runClient(port);
            return;
        }

        String filename = null;
        if (!kind.equals("cli")) {
            if (args.length < 2) {
                System.out.println(usage);
                return;
            }
            filename = args[1];
        }

        int threads = 0;
        int threadsPerNode = 1;
        if (kind.startsWith("par") || kind.startsWith("dist")) {
            if (args.length < 3) {
                System.out.println(usage);
                return;
            }
            String tup = args[2];
            String t = tup;
            int i = tup.indexOf("/");
            if (i >= 0) {
                t = tup.substring(0, i).trim();
                tup = tup.substring(i + 1).trim();
                try {
                    threadsPerNode = Integer.parseInt(tup);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.out.println(usage);
                    return;
                }
            }
            try {
                threads = Integer.parseInt(t);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println(usage);
                return;
            }
        }

        String mfile = null;
        if (kind.startsWith("dist")) {
            if (args.length >= 4) {
                mfile = args[3];
            } else {
                mfile = "machines";
            }
        }

        Reader problem = null;
        try {
            problem = new FileReader(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(usage);
            return;
        }

        GenPolynomialTokenizer tok = new GenPolynomialTokenizer(problem);
        PolynomialList S = null;
        try {
            S = tok.nextPolynomialSet();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("S =\n" + S);

        if (kind.startsWith("seq")) {
            runSequential(S, pairseq);
        }
        if (kind.startsWith("par")) {
            runParallel(S, threads, pairseq);
        }
        if (kind.startsWith("dist1")) {
            runMasterOnce(S, threads, mfile, port, pairseq);
        } else if (kind.startsWith("disthyb1")) {
            runMasterOnceHyb(S, threads, threadsPerNode, mfile, port, pairseq);
        } else if (kind.startsWith("dist")) {
            runMaster(S, threads, mfile, port, pairseq);
        }
        System.exit(0);
    }


    @SuppressWarnings("unchecked")
    static void runMaster(PolynomialList S, int threads, String mfile, int port, boolean pairseq) {
        List L = S.list;
        List G = null;
        long t, t1;

        t = System.currentTimeMillis();
        System.out.println("\nGroebner base distributed (" + threads + ", " + mfile + ", " + port +  ") ...");
        GBDist gbd = null;
        GBDistSP gbds = null;
        if (pairseq) {
            gbds = new GBDistSP(threads, mfile, port);
        } else {
            gbd = new GBDist(threads, mfile, port);
        }
        t1 = System.currentTimeMillis();
        if (pairseq) {
            G = gbds.execute(L);
        } else {
            G = gbd.execute(L);
        }
        t1 = System.currentTimeMillis() - t1;
        if (pairseq) {
            gbds.terminate(false);
        } else {
            gbd.terminate(false);
        }
        S = new PolynomialList(S.ring, G);
        System.out.println("G =\n" + S);
        System.out.println("G.size() = " + G.size());
        t = System.currentTimeMillis() - t;
        if (pairseq) {
            System.out.print("d+ ");
        } else {
            System.out.print("d ");
        }
        System.out.println("= " + threads + ", time = " + t + " milliseconds, " + (t - t1) + " start-up");
        System.out.println("");
    }


    @SuppressWarnings("unchecked")
    static void runMasterOnce(PolynomialList S, int threads, String mfile, int port, boolean pairseq) {
        List L = S.list;
        List G = null;
        long t, t1;

        t = System.currentTimeMillis();
        System.out.println("\nGroebner base distributed[once] (" + threads + ", " + mfile + ", " + port +  ") ...");
        GBDist gbd = null;
        GBDistSP gbds = null;
        if (pairseq) {
            gbds = new GBDistSP(threads, mfile, port);
        } else {
            gbd = new GBDist(threads, mfile, port);
        }
        t1 = System.currentTimeMillis();
        if (pairseq) {
            G = gbds.execute(L);
        } else {
            G = gbd.execute(L);
        }
        t1 = System.currentTimeMillis() - t1;
        if (pairseq) {
            gbds.terminate(true);
        } else {
            gbd.terminate(true);
        }
        S = new PolynomialList(S.ring, G);
        System.out.println("G =\n" + S);
        System.out.println("G.size() = " + G.size());
        t = System.currentTimeMillis() - t;
        if (pairseq) {
            System.out.print("d+ ");
        } else {
            System.out.print("d ");
        }
        System.out.println("= " + threads + ", time = " + t + " milliseconds, " + (t - t1) + " start-up");
        System.out.println("");
    }


    @SuppressWarnings("unchecked")
    static void runMasterOnceHyb(PolynomialList S, int threads, int threadsPerNode, String mfile, int port,
                                 boolean pairseq) {
        List L = S.list;
        List G = null;
        long t, t1;

        t = System.currentTimeMillis();
        System.out.println("\nGroebner base distributed hybrid[once] (" + threads + "/" + threadsPerNode + ", " + mfile + ", " + port +  ") ...");
        GBDistHybrid gbd = null;
        //GBDistSP gbds = null; 
        if (pairseq) {
            System.out.println("... not implemented.");
            return;
            // gbds = new GBDistSP(threads, mfile, port);
        } else {
            gbd = new GBDistHybrid(threads, threadsPerNode, mfile, port);
        }
        t1 = System.currentTimeMillis();
        if (pairseq) {
            //G = gbds.execute( L );
        } else {
            G = gbd.execute(L);
        }
        t1 = System.currentTimeMillis() - t1;
        if (pairseq) {
            //gbds.terminate(true);
        } else {
            //gbd.terminate(true);
            gbd.terminate(false); // plus eventually killed by script
        }
        S = new PolynomialList(S.ring, G);
        System.out.println("G =\n" + S);
        System.out.println("G.size() = " + G.size());
        t = System.currentTimeMillis() - t;
        if (pairseq) {
            System.out.print("d+ ");
        } else {
            System.out.print("d ");
        }
        System.out.println("= " + threads + ", ppn = " + threadsPerNode + ", time = " + t + " milliseconds, " + (t - t1) + " start-up");
        System.out.println("");
    }


    static void runClient(int port) {
        System.out.println("\nGroebner base distributed client (" + port +  ") ...");

        ExecutableServer es = new ExecutableServer(port);
        es.init();
    }


    @SuppressWarnings("unchecked")
    static void runParallel(PolynomialList S, int threads, boolean pairseq) {
        List L = S.list;
        List G;
        long t;
        GroebnerBaseParallel bb = null;
        GroebnerBaseSeqPairParallel bbs = null;
        if (pairseq) {
            bbs = new GroebnerBaseSeqPairParallel(threads);
        } else {
            bb = new GroebnerBaseParallel(threads);
        }
        t = System.currentTimeMillis();
        System.out.println("\nGroebner base parallel (" + threads + ") ...");

        if (pairseq) {
            G = bbs.GB(L);
        } else {
            G = bb.GB(L);
        }
        S = new PolynomialList(S.ring, G);
        System.out.println("G =\n" + S);
        System.out.println("G.size() = " + G.size());
        t = System.currentTimeMillis() - t;

        if (pairseq) {
            System.out.print("p+ ");
        } else {
            System.out.print("p ");
        }
        System.out.println("= " + threads + ", time = " + t + " milliseconds");
        System.out.println("");
        if (pairseq) {
            bbs.terminate();
        } else {
            bb.terminate();
        }
    }


    @SuppressWarnings("unchecked")
    static void runSequential(PolynomialList S, boolean pairseq) {
        List L = S.list;
        List G;
        long t;
        GroebnerBase bb = null;
        if (pairseq) {
            bb = new GroebnerBaseSeqPairSeq();
        } else {
            bb = new GroebnerBaseSeq();
        }
        t = System.currentTimeMillis();
        System.out.println("\nGroebner base sequential ...");
        G = bb.GB(L);
        S = new PolynomialList(S.ring, G);
        System.out.println("G =\n" + S);
        System.out.println("G.size() = " + G.size());
        t = System.currentTimeMillis() - t;
        if (pairseq) {
            System.out.print("seq+, ");
        } else {
            System.out.print("seq, ");
        }
        System.out.println("time = " + t + " milliseconds");
        System.out.println("");
    }

}
