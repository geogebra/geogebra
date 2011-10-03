package org.mathpiper.builtin.library.jas;

//------------------------------------------------------------------------
//          Factor Polynomial over Integers, using JAS Library
//          Version for interfacing with MathPiper
//             Initial version:  05/24/2010
//------------------------------------------------------------------------
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import edu.jas.ufd.FactorInteger;
import edu.jas.arith.BigInteger;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.util.StringUtil;

//------------------------------------------------------------------------

public class JFactorsPolyInt {

    private boolean debug = false;   
    private String ringName;    
    private BigInteger  bint;
    private GenPolynomialRing<BigInteger> polyRing;
    private GenPolynomial<BigInteger> poly; 
    private FactorInteger fEngine;    
    private SortedMap<GenPolynomial,Long> factorsMap;
    
    // -----  CONSTRUCTORS  -----
    
    // no-argument constructor -- not to be used
    protected JFactorsPolyInt() {
    }
    
    // two-argument constructor -- 
    //      specify polynomial as string, and varaible-names as string
    //                     polyString looks like this:      "3*x^2-5*x+4"
    //                     varNames string looks like this: "x,y"
    public JFactorsPolyInt(String polyString, String varNames) {
        if ( debug ) {
            System.out.println("JFactorsPolyInt   " + polyString + "   " + varNames);
        }
        String [] varList = varNames.split(",");
        bint     = new BigInteger(1);
        GenPolynomialRing<BigInteger> bintRing = new GenPolynomialRing<BigInteger>(bint,varList);
        poly     = bintRing.parse(polyString);
        fEngine  = new FactorInteger();
    }

    
    // factorization of this.poly
    public SortedMap<GenPolynomial,Long> factors() {
        factorsMap = fEngine.factors(poly);
        return factorsMap;       
    }
    
    
    // reducibility of this.poly
    public boolean isReducible() {
        return fEngine.isReducible(poly);
    }
   
    
    // termination of all working threads
    public void terminate(){
        ComputerThreads.terminate();
    }
        
/*
    // M A I N
    public static void main(String[] args) {
        
        boolean iDebug = false;
        long        T1 = System.currentTimeMillis(); 
        
        String polyString = args[0];
        String varNames   = args[1];
        
        if ( iDebug ) {
            System.out.println("    poly " + polyString);
            System.out.println("    vars " + varNames);
            System.out.flush();
        }
        
        JFactorsPolyInt jPoly = new JFactorsPolyInt(polyString,varNames);
        SortedMap<GenPolynomial,Long> factorsMap = jPoly.factors();
        System.out.println("\nfactorsMap: " + factorsMap);
        
        System.out.println("\nisReducible: " + jPoly.isReducible());

        jPoly.terminate();
        
        long T2 = System.currentTimeMillis(); 
        float elapsedTimeSec = (T2-T1)/1000F;    
        System.out.println("  elapsed time :  " + elapsedTimeSec + " sec\n");

    }
*/

}//end class JFactorsPolyInt

