package org.mathpiper.builtin.library.jas;

//------------------------------------------------------------------------
//          Factoring polynomials over Ring of Integer
//          Version for interfacing with MathPiper
//             (sherm experiments in here)
//------------------------------------------------------------------------
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import edu.jas.ufd.Factorization;
import edu.jas.ufd.FactorFactory;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
//import edu.jas.arith.BigComplex;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.util.StringUtil;

//-----------------------------------------------
public class JasAccess2 {

    private boolean debug = false;

    private BigInteger bi;

    private Factorization<BigInteger> fEngineBI;
    
    private GenPolynomial<BigInteger> polyp;

    public JasAccess2() {
        // define the "nominal" BigInteger as type prototype
        bi = new BigInteger(1);

        // create a factorization engine suitable for BigInteger coefficient type
        fEngineBI = FactorFactory.getImplementation(bi);
        
    }//end constructor.

    

    public Set factorPolyInt(String poly, String vars) {
        if (debug) {
            System.out.println(" poly = " + poly);
            System.out.println(" vars = " + vars);
        }

        //  convert string of variable names to array of strings as required
        String[] jvars = StringUtil.variableList(vars);
        int nvars = jvars.length;
        if (debug) {
            System.out.print("\n number of variables: ");
            System.out.println(nvars);
            for (int i = 0; i < nvars; i++) {
                System.out.print(" " + jvars[i]);
            }
            System.out.println();
        }

        // make sure term-order is INVLEX, as required
        //TermOrder to = new TermOrder(TermOrder.INVLEX);
        //if (debug) {
        //    System.out.println(" term-order = " + to);
        //}


        Factorization fEngine = fEngineBI;
        if (debug) {
            System.out.println("\nFactorization:  fEngineBI = " + fEngineBI);
        }

        // create appropriate Ring for BigIntegers with specified variable names
        //GenPolynomialRing<BigInteger> biRing = new GenPolynomialRing<BigInteger>(bi, nvars, to, jvars);
        GenPolynomialRing<BigInteger> biRing = new GenPolynomialRing<BigInteger>(bi, nvars, jvars);
        if (debug) {
            System.out.println("polynomial ring = " + biRing);
            int nvars2 = biRing.nvar;
            System.out.println("    number of variables for ring = " + nvars2);
            String varNames = biRing.varsToString();
            System.out.println("    names  of variables for ring = " + varNames);
        }

        // ---  Create polynomial in chosen Ring, from given string  --
        if (debug) {
            System.out.println("\nstrPoly = " + poly);
        }
        polyp = biRing.parse(poly);
        //System.out.println("\npoly = " + polyp);
        if (debug) {
            int lenPoly = polyp.length();
            System.out.println("    length of poly = " + lenPoly);
            int numVars = polyp.numberOfVariables();
            System.out.println("    number of variables in poly = " + numVars);
            long degree = polyp.degree();
            System.out.println("    maximal degree of poly = " + degree);
        }

        // ---  JasAccess the polynomial  ---
        SortedMap<GenPolynomial<BigInteger>, Long> Sm = fEngineBI.factors(polyp);

        //     print info about factorization
        /*int numFactors = Sm.size();
        System.out.println("    number of factors: " + numFactors);
         */

        // ---  Print out all factors and their multiplicities  ---
        /*for (Map.Entry<GenPolynomial<BigInteger>, Long> f : Sm.entrySet()) {
            GenPolynomial<BigInteger> factor = f.getKey();
            Long multiplicity = f.getValue();
            System.out.println("   ( " + factor + "  ,  " + multiplicity + " )");
        }*/


        return (Set) Sm.entrySet();


    } // end method.


    
    public long maxDegree() {
        return this.polyp.degree();
    } // end method


    public void terminate()
    {
        ComputerThreads.terminate();
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }



    public static void main(String[] args) {
        JasAccess2 jas = new JasAccess2();

        jas.setDebug(true);

        Set resultSet = jas.factorPolyInt("x**2-9", "x");

        jas.terminate();

    }//end main.
    

}//end class.

