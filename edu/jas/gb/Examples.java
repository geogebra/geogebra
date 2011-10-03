/*
 * $Id: Examples.java 3163 2010-06-03 19:06:31Z kredel $
 */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import edu.jas.application.Ideal;
import edu.jas.application.Residue;
import edu.jas.application.ResidueRing;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.gb.GroebnerBase;
import edu.jas.gb.GroebnerBasePseudoSeq;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;


/**
 * Examples for Groebner base usage.
 * @author Christoph Zengler.
 * @author Heinz Kredel.
 */
public class Examples {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        example1();
        example2();
        example3();
    }



    /**
     * example1.
     * Coefficients in Boolean residue class ring.
     *
     */
    public static void example1() {
        String[] vars = {"v3", "v2", "v1"};
        
        ModIntegerRing z2 = new ModIntegerRing(2);
        GenPolynomialRing<ModInteger> z2p = new GenPolynomialRing<ModInteger>(z2,vars.length,new TermOrder(TermOrder.INVLEX),vars);
        List<GenPolynomial<ModInteger>> fieldPolynomials = new ArrayList<GenPolynomial<ModInteger>>();
        
        //add v1^2 + v1, v2^2 + v2, v3^2 + v3 to fieldPolynomials
        for (int i=0; i<vars.length; i++) {
            GenPolynomial<ModInteger> var = z2p.univariate(i);
            fieldPolynomials.add(var.multiply(var).sum(var));
        }
                
        
        Ideal<ModInteger> fieldPolys = new Ideal<ModInteger>(z2p,fieldPolynomials);
        ResidueRing<ModInteger> ring = new ResidueRing<ModInteger>(fieldPolys);
        String[] mvars = {"mv3", "mv2", "mv1"};
        GenPolynomialRing<Residue<ModInteger>> ringp = new GenPolynomialRing<Residue<ModInteger>>(ring,mvars.length,mvars);
        
        List<GenPolynomial<Residue<ModInteger>>> polynomials = new ArrayList<GenPolynomial<Residue<ModInteger>>>();     
        
        GenPolynomial<Residue<ModInteger>> v1 = ringp.univariate(0);
        GenPolynomial<Residue<ModInteger>> v2 = ringp.univariate(1);
        GenPolynomial<Residue<ModInteger>> v3 = ringp.univariate(2);
        GenPolynomial<Residue<ModInteger>> notV1 = v1.sum(ringp.ONE);
        GenPolynomial<Residue<ModInteger>> notV2 = v2.sum(ringp.ONE);
        GenPolynomial<Residue<ModInteger>> notV3 = v3.sum(ringp.ONE);
        
        //v1*v2
        GenPolynomial<Residue<ModInteger>> p1 = v1.multiply(v2);
        
        //v1*v2 + v1 + v2 + 1
        GenPolynomial<Residue<ModInteger>> p2 = notV1.multiply(notV2);
        
        //v1*v3 + v1 + v3 + 1
        GenPolynomial<Residue<ModInteger>> p3 = notV1.multiply(notV3);
        
        polynomials.add(p1);
        polynomials.add(p2);
        polynomials.add(p3);
           
        //GroebnerBase<Residue<ModInteger>> gb = new GroebnerBasePseudoSeq<Residue<ModInteger>>(ring);
        GroebnerBase<Residue<ModInteger>> gb = GBFactory.getImplementation(ring);
        List<GenPolynomial<Residue<ModInteger>>> G = gb.GB(polynomials);
        
        System.out.println(G);
    }


    /**
     * example2.
     * Coefficients in Boolean residue class ring with cuppling of variables.
     *
     */
    public static void example2() {
        String[] vars = {"v3", "v2", "v1"};
        
        ModIntegerRing z2 = new ModIntegerRing(2);
        GenPolynomialRing<ModInteger> z2p = new GenPolynomialRing<ModInteger>(z2,vars.length,new TermOrder(TermOrder.INVLEX),vars);
        List<GenPolynomial<ModInteger>> fieldPolynomials = new ArrayList<GenPolynomial<ModInteger>>();
        
        //add v1^2 + v1, v2^2 + v2, v3^2 + v3 to fieldPolynomials
        for (int i=0; i<vars.length; i++) {
            GenPolynomial<ModInteger> var = z2p.univariate(i);
            fieldPolynomials.add(var.multiply(var).sum(var));
        }
                
        
        Ideal<ModInteger> fieldPolys = new Ideal<ModInteger>(z2p,fieldPolynomials);
        ResidueRing<ModInteger> ring = new ResidueRing<ModInteger>(fieldPolys);
        String[] mvars = {"mv3", "mv2", "mv1"};
        GenPolynomialRing<Residue<ModInteger>> ringp = new GenPolynomialRing<Residue<ModInteger>>(ring,mvars.length,mvars);
        
        List<GenPolynomial<Residue<ModInteger>>> polynomials = new ArrayList<GenPolynomial<Residue<ModInteger>>>();     
        
        GenPolynomial<Residue<ModInteger>> v1 = ringp.univariate(0);
        GenPolynomial<Residue<ModInteger>> v2 = ringp.univariate(1);
        GenPolynomial<Residue<ModInteger>> v3 = ringp.univariate(2);
        GenPolynomial<Residue<ModInteger>> notV1 = v1.sum(ringp.ONE);
        GenPolynomial<Residue<ModInteger>> notV2 = v2.sum(ringp.ONE);
        GenPolynomial<Residue<ModInteger>> notV3 = v3.sum(ringp.ONE);
        
        //v1*v2
        GenPolynomial<Residue<ModInteger>> p1 = v1.multiply(v2);
        
        //v1*v2 + v1 + v2 + 1
        GenPolynomial<Residue<ModInteger>> p2 = notV1.multiply(notV2);
        
        //v1*v3 + v1 + v3 + 1
        GenPolynomial<Residue<ModInteger>> p3 = notV1.multiply(notV3);
        
        polynomials.add(p1);
        polynomials.add(p2);
        polynomials.add(p3);

        List<Residue<ModInteger>> gens = ring.generators();
        System.out.println("gens = " + gens);
        GenPolynomial<Residue<ModInteger>> mv3v3 = v3.subtract(gens.get(1));
        GenPolynomial<Residue<ModInteger>> mv2v2 = v2.subtract(gens.get(2));
        GenPolynomial<Residue<ModInteger>> mv1v1 = v1.subtract(gens.get(3));

        System.out.println("mv3v3 = " + mv3v3);
        System.out.println("mv2v2 = " + mv2v2);
        System.out.println("mv1v1 = " + mv1v1);

        polynomials.add(mv3v3);
        polynomials.add(mv2v2);
        polynomials.add(mv1v1);
           
        //GroebnerBase<Residue<ModInteger>> gb = new GroebnerBasePseudoSeq<Residue<ModInteger>>(ring);
        GroebnerBase<Residue<ModInteger>> gb = GBFactory.getImplementation(ring);

        List<GenPolynomial<Residue<ModInteger>>> G = gb.GB(polynomials);
        
        System.out.println(G);
        
    }


    /**
     * example3.
     * Coefficients in Boolean ring and additional idempotent generators.
     *
     */
    public static void example3() {
        String[] vars = {"v3", "v2", "v1"};
        
        ModIntegerRing z2 = new ModIntegerRing(2);
        GenPolynomialRing<ModInteger> z2p = new GenPolynomialRing<ModInteger>(z2,vars.length,new TermOrder(TermOrder.INVLEX),vars);
        List<GenPolynomial<ModInteger>> fieldPolynomials = new ArrayList<GenPolynomial<ModInteger>>();
        
        //add v1^2 + v1, v2^2 + v2, v3^2 + v3 to fieldPolynomials
        for (int i=0; i<vars.length; i++) {
            GenPolynomial<ModInteger> var = z2p.univariate(i);
            fieldPolynomials.add(var.multiply(var).sum(var));
        }
                
        
        List<GenPolynomial<ModInteger>> polynomials = new ArrayList<GenPolynomial<ModInteger>>();     
        
        GenPolynomial<ModInteger> v1 = z2p.univariate(0);
        GenPolynomial<ModInteger> v2 = z2p.univariate(1);
        GenPolynomial<ModInteger> v3 = z2p.univariate(2);
        GenPolynomial<ModInteger> notV1 = v1.sum(z2p.ONE);
        GenPolynomial<ModInteger> notV2 = v2.sum(z2p.ONE);
        GenPolynomial<ModInteger> notV3 = v3.sum(z2p.ONE);
        
        //v1*v2
        GenPolynomial<ModInteger> p1 = v1.multiply(v2);
        
        //v1*v2 + v1 + v2 + 1
        GenPolynomial<ModInteger> p2 = notV1.multiply(notV2);
        
        //v1*v3 + v1 + v3 + 1
        GenPolynomial<ModInteger> p3 = notV1.multiply(notV3);
        
        polynomials.add(p1);
        polynomials.add(p2);
        polynomials.add(p3);

        polynomials.addAll(fieldPolynomials);

        //GroebnerBase<ModInteger> gb = new GroebnerBaseSeq<ModInteger>();
        GroebnerBase<ModInteger> gb = GBFactory.getImplementation(z2);

        List<GenPolynomial<ModInteger>> G = gb.GB(polynomials);
        
        System.out.println(G);
        
    }
        
}
