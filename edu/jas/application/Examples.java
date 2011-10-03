/*
 * $Id: Examples.java 3163 2010-06-03 19:06:31Z kredel $
 */

package edu.jas.application;

import org.apache.log4j.BasicConfigurator;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;

import edu.jas.structure.Product;
import edu.jas.structure.ProductRing;

import edu.jas.gb.GroebnerBase;
import edu.jas.gb.GroebnerBasePseudoSeq;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.gb.GBFactory;
import edu.jas.gb.RGroebnerBasePseudoSeq;
import edu.jas.gb.RReductionSeq;
import edu.jas.kern.ComputerThreads;

import edu.jas.arith.BigRational;
import edu.jas.arith.BigInteger;

//import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;



/**
 * Examples for application usage.
 * @author Heinz Kredel.
 */

public class Examples {

    /**
     * main.
     */
    public static void main (String[] args) {
        BasicConfigurator.configure();
        //example1();
        //example2();
        //example3();
        //example4();
        example5();
    }


    /**
     * example1.
     * cyclic n-th roots polynomial systems.
     *
     */
    public static void example1() {
        int n = 4;

        BigInteger fac = new BigInteger();
        GenPolynomialRing<BigInteger> ring
            = new GenPolynomialRing<BigInteger>(fac,n); //,var);
        System.out.println("ring = " + ring + "\n");

        List<GenPolynomial<BigInteger>> cp = new ArrayList<GenPolynomial<BigInteger>>( n ); 
        for ( int i = 1; i <= n; i++ ) {
            GenPolynomial<BigInteger> p = cyclicPoly(ring, n, i);
            cp.add( p );
            System.out.println("p["+i+"] = " +  p);
            System.out.println();
        }
        System.out.println("cp = " + cp + "\n");

        List<GenPolynomial<BigInteger>> gb;
        //GroebnerBase<BigInteger> sgb = new GroebnerBaseSeq<BigInteger>();
        GroebnerBase<BigInteger> sgb = GBFactory.getImplementation(fac);
        gb = sgb.GB( cp );
        System.out.println("gb = " + gb);

    }

    static GenPolynomial<BigInteger> cyclicPoly(GenPolynomialRing<BigInteger> ring, int n, int i) {

        List<? extends GenPolynomial<BigInteger> > X 
            = /*(List<GenPolynomial<BigInteger>>)*/ ring.univariateList();

        GenPolynomial<BigInteger> p = ring.getZERO();
        for ( int j = 1; j <= n; j++ ) {
            GenPolynomial<BigInteger> pi = ring.getONE();
            for ( int k = j; k < j+i; k++ ) {
                pi = pi.multiply( X.get( k % n ) );
            }
            p = p.sum( pi );
            if ( i == n ) {
                p = p.subtract( ring.getONE() );
                break;
            }
        }
        return p;
    }


    /**
     * example2.
     * abtract types: List<GenPolynomial<Product<Residue<BigRational>>>>.
     *
     */
    public static void example2() {
        List<GenPolynomial<Product<Residue<BigRational>>>> L = null;
        L = new ArrayList<GenPolynomial<Product<Residue<BigRational>>>>();

        BigRational bfac = new BigRational(1);
        GenPolynomialRing<BigRational> pfac = null;
        pfac = new GenPolynomialRing<BigRational>(bfac,3);

        List<GenPolynomial<BigRational>> F = null;
        F = new ArrayList<GenPolynomial<BigRational>>();

        GenPolynomial<BigRational> p = null;
        for ( int i = 0; i < 2; i++) {
            p = pfac.random(5,4,3,0.4f);
            if ( !p.isConstant() ) {
                F.add(p);
            }
        }
        //System.out.println("F = " + F);

        Ideal<BigRational> id = new Ideal<BigRational>(pfac,F);
        id.doGB();
        if ( id.isONE() || id.isZERO() ) {
            System.out.println("id zero or one = " + id);
            return;
        }
        ResidueRing<BigRational> rr = new ResidueRing<BigRational>(id);
        System.out.println("rr = " + rr);

        ProductRing<Residue<BigRational>> pr = null;
        pr = new ProductRing<Residue<BigRational>>(rr,3);

        String[] vars = new String[] { "a", "b" };
        GenPolynomialRing<Product<Residue<BigRational>>> fac;
        fac = new GenPolynomialRing<Product<Residue<BigRational>>>(pr,2,vars);

        GenPolynomial<Product<Residue<BigRational>>> pp;
        for ( int i = 0; i < 1; i++) {
            pp = fac.random(2,4,4,0.4f);
            if ( !pp.isConstant() ) {
                L.add(pp);
            }
        }
        System.out.println("L = " + L);

        //PolynomialList<Product<Residue<BigRational>>> Lp = null;
        //Lp = new PolynomialList<Product<Residue<BigRational>>>(fac,L);
        //System.out.println("Lp = " + Lp);

        GroebnerBase<Product<Residue<BigRational>>> bb 
            = new RGroebnerBasePseudoSeq<Product<Residue<BigRational>>>(pr);

        System.out.println("isGB(L) = " + bb.isGB(L));

        List<GenPolynomial<Product<Residue<BigRational>>>> G = null;

        G = bb.GB(L);
        System.out.println("G = " + G);
        System.out.println("isGB(G) = " + bb.isGB(G));

        ComputerThreads.terminate();

    }


    /**
     * example3.
     * abtract types: GB of List<GenPolynomial<Residue<BigRational>>>.
     *
     */
    public static void example3() {
        List<GenPolynomial<Residue<BigRational>>> L = null;
        L = new ArrayList<GenPolynomial<Residue<BigRational>>>();

        BigRational bfac = new BigRational(1);
        GenPolynomialRing<BigRational> pfac = null;
        pfac = new GenPolynomialRing<BigRational>(bfac,2);

        List<GenPolynomial<BigRational>> F = null;
        F = new ArrayList<GenPolynomial<BigRational>>();

        GenPolynomial<BigRational> p = null;
        for ( int i = 0; i < 2; i++) {
            p = pfac.random(5,5,5,0.4f);
            //p = pfac.parse("x0^2 -2" );
            if ( !p.isConstant() ) {
                F.add(p);
            }
        }
        //System.out.println("F = " + F);

        Ideal<BigRational> id = new Ideal<BigRational>(pfac,F);
        id.doGB();
        if ( id.isONE() || id.isZERO() ) {
            System.out.println("id zero or one = " + id);
            return;
        }
        ResidueRing<BigRational> rr = new ResidueRing<BigRational>(id);
        System.out.println("rr = " + rr);

        String[] vars = new String[] { "a", "b" };
        GenPolynomialRing<Residue<BigRational>> fac;
        fac = new GenPolynomialRing<Residue<BigRational>>(rr,2,vars);

        GenPolynomial<Residue<BigRational>> pp;
        for ( int i = 0; i < 2; i++) {
            pp = fac.random(2,4,6,0.2f);
            if ( !pp.isConstant() ) {
                L.add(pp);
            }
        }
        System.out.println("L = " + L);

        GroebnerBase<Residue<BigRational>> bb;
        //bb = new GroebnerBasePseudoSeq<Residue<BigRational>>(rr);
        bb = GBFactory.getImplementation(rr);

        System.out.println("isGB(L) = " + bb.isGB(L));

        List<GenPolynomial<Residue<BigRational>>> G = null;

        G = bb.GB(L);
        System.out.println("G = " + G);
        System.out.println("isGB(G) = " + bb.isGB(G));

        ComputerThreads.terminate();
    }


    /**
     * example4.
     * abtract types: comprehensive GB of List<GenPolynomial<GenPolynomial<BigRational>>>.
     *
     */
    public static void example4() {
        int kl = 2;
        int ll = 3;
        int el = 3;
        float q = 0.2f; //0.4f
        GenPolynomialRing<BigRational> cfac;
        GenPolynomialRing<GenPolynomial<BigRational>> fac;

        List<GenPolynomial<GenPolynomial<BigRational>>> L;

        ComprehensiveGroebnerBaseSeq<BigRational> bb;

        GenPolynomial<GenPolynomial<BigRational>> a;
        GenPolynomial<GenPolynomial<BigRational>> b;
        GenPolynomial<GenPolynomial<BigRational>> c;

        BigRational coeff = new BigRational(kl);
        String[] cv = { "a", "b" }; 
        cfac = new GenPolynomialRing<BigRational>(coeff,2,cv);
        String[] v = { "x", "y" }; 
        fac = new GenPolynomialRing<GenPolynomial<BigRational>>(cfac,2,v);
        bb = new ComprehensiveGroebnerBaseSeq<BigRational>(coeff);

        L = new ArrayList<GenPolynomial<GenPolynomial<BigRational>>>();

        a = fac.random(kl, ll, el, q );
        b = fac.random(kl, ll, el, q );
        c = a; //c = fac.random(kl, ll, el, q );

        if ( a.isZERO() || b.isZERO() || c.isZERO() ) {
            return;
        }

        L.add(a);
        System.out.println("CGB exam L = " + L );
        L = bb.GB( L );
        System.out.println("CGB( L )   = " + L );
        System.out.println("isCGB( L ) = " + bb.isGB(L) );

        L.add(b);
        System.out.println("CGB exam L = " + L );
        L = bb.GB( L );
        System.out.println("CGB( L )   = " + L );
        System.out.println("isCGB( L ) = " + bb.isGB(L) );

        L.add(c);
        System.out.println("CGB exam L = " + L );
        L = bb.GB( L );
        System.out.println("CGB( L )   = " + L );
        System.out.println("isCGB( L ) = " + bb.isGB(L) );

        ComputerThreads.terminate();
    }


    /**
     * example5.
     * comprehensive GB of List<GenPolynomial<GenPolynomial<BigRational>>>
     * and GB for regular ring.
     */
    public static void example5() {
        int kl = 2;
        int ll = 4;
        int el = 3;
        float q = 0.3f; //0.4f
        GenPolynomialRing<BigRational> cfac;
        GenPolynomialRing<GenPolynomial<BigRational>> fac;

        List<GenPolynomial<GenPolynomial<BigRational>>> L;

        ComprehensiveGroebnerBaseSeq<BigRational> bb;

        GenPolynomial<GenPolynomial<BigRational>> a;
        GenPolynomial<GenPolynomial<BigRational>> b;
        GenPolynomial<GenPolynomial<BigRational>> c;

        BigRational coeff = new BigRational(kl);
        String[] cv = { "a", "b" }; 
        cfac = new GenPolynomialRing<BigRational>(coeff,2,cv);
        String[] v = { "x", "y" }; 
        fac = new GenPolynomialRing<GenPolynomial<BigRational>>(cfac,2,v);
        bb = new ComprehensiveGroebnerBaseSeq<BigRational>(coeff);

        L = new ArrayList<GenPolynomial<GenPolynomial<BigRational>>>();

        a = fac.random(kl, ll, el, q );
        b = fac.random(kl, ll, el, q );
        c = a; //c = fac.random(kl, ll, el, q );

        if ( a.isZERO() || b.isZERO() || c.isZERO() ) {
            return;
        }

        L.add(a);
        L.add(b);
        L.add(c);
        System.out.println("CGB exam L = " + L );
        GroebnerSystem<BigRational> sys = bb.GBsys( L );
        boolean ig = bb.isGB(sys.getCGB());
        System.out.println("CGB( L )   = " + sys.getCGB() );
        System.out.println("isCGB( L ) = " + ig );

        List<GenPolynomial<Product<Residue<BigRational>>>> Lr, bLr;
        RReductionSeq<Product<Residue<BigRational>>> res = new RReductionSeq<Product<Residue<BigRational>>>();

        Lr = PolyUtilApp.<BigRational> toProductRes(sys.list);
        bLr = res.booleanClosure(Lr);

        System.out.println("booleanClosed(Lr)   = " + bLr );

        if ( bLr.size() > 0 ) { 
            GroebnerBase<Product<Residue<BigRational>>> rbb 
               = new RGroebnerBasePseudoSeq<Product<Residue<BigRational>>>(bLr.get(0).ring.coFac);
            System.out.println("isRegularGB(Lr) = " + rbb.isGB(bLr));
        }

        ComputerThreads.terminate();
    }

}
