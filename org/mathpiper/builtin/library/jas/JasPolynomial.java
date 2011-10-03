package org.mathpiper.builtin.library.jas;

//------------------------------------------------------------------------
//          Operations on JAS Polynomials of various types
//          Version for interfacing with MathPiper
//             Initial version:  05/13/2010
//             Modifications:    through 05/20/2010
//------------------------------------------------------------------------
import java.util.Collections;
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
import edu.jas.arith.BigComplex;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.util.StringUtil;

//------------------------------------------------------------------------

public class JasPolynomial {

    private boolean debug = true;
    
    private String ringName;
    
    private BigInteger  bint;
    private BigRational brat;
    private BigComplex  bcmplx;

    private GenPolynomialRing polyRing;
    //private GenPolynomialRing polyRingExt;
    
    private GenPolynomial poly;
    
    private Factorization fEngine;
    
    private SortedMap<GenPolynomial,Long> factorsMap;
    
    // -----  CONSTRUCTORS  -----
    
    // no-argument constructor -- not to be used
    protected JasPolynomial() {
    }

    
    // one-argument constructor -- specify polynomial Ring only
    public JasPolynomial(String ringType) {
        this(ringType,"x");
    }

    
    // two-argument constructor -- specify polynomial Ring and varaible-names string
    //                             varNames string looks like this: "x,y"
    public JasPolynomial(String ringType, String varNames) {
        this(ringType,varNames,"x^2-1");
    }

    
    // three-argument constructor -- 
    //          specify polynomial Ring, varaible-names string, and polynomial string
    //                         varNames string looks like this: "x,y"
    //                         polyString looks like this:      "3*x^2-5*x+4"
    public JasPolynomial(String ringType, String varNames, String polyString) {
        ringName = ringType;
        String [] varList = varNames.split(",");
        if (ringName.equals("Integer")) {
            bint     = new BigInteger(1);
            GenPolynomialRing<BigInteger> bintRing = new GenPolynomialRing<BigInteger>(bint,varList);
            polyRing = (GenPolynomialRing)bintRing;
            poly     = polyRing.parse(polyString);
            fEngine  = FactorFactory.getImplementation(bint);
        }
        else if ( ringName.equals("Rational")) {
            brat = new BigRational(1);
            GenPolynomialRing<BigRational> bratRing = new GenPolynomialRing<BigRational>(brat,varList);
            polyRing = (GenPolynomialRing)bratRing;
            poly     = polyRing.parse(polyString);
            fEngine  = FactorFactory.getImplementation(brat);
        }
        else if ( ringName.equals("Complex")) {
            bcmplx = new BigComplex(1);
            GenPolynomialRing<BigComplex> cmplxRing = new GenPolynomialRing<BigComplex>(bcmplx,varList);
            polyRing = (GenPolynomialRing)cmplxRing;
            poly     = polyRing.parse(polyString);
            fEngine  = FactorFactory.getImplementation(bcmplx);
        }
    }


    //  ------   ACCESSORS   ------ ------ ------ ------ ------ ------
    
    // Get

    public boolean isDebug() {
        return debug;
    }
    
    public GenPolynomialRing getRing() {
        return polyRing;
    }
    
    public GenPolynomial getPolynomial() {
        return poly;
    }
    
    public Factorization getFactorizationEngine() {
        return fEngine;
    }
    
    public String getRingVariables() {
        return polyRing.varsToString();
    }
    
    public boolean isIrreducible() {
        return fEngine.isIrreducible(poly);
    }
    
    public boolean isIrreducible( GenPolynomial p ) {
        return fEngine.isIrreducible(p);
    }
    
    
    // Set
    
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void addVars(String newVarsString) {
        String[] newVars = newVarsString.split(",");
        polyRing = polyRing.extend(newVars);
    }

    public void setPolynomial(String polyString) {
        poly = polyRing.parse(polyString);
    }

    public void setPolynomial(String polyString, String newPolyVars) {
        this.addVars(newPolyVars);
        poly = polyRing.parse(polyString);
    }
 
    
    
    // Other ------ --------------- --------------- -----------------   
    
    // factorization of this.poly
    public SortedMap<GenPolynomial,Long> factors() {
        if ( debug ) {
            System.out.println(" DEBUG:  in method factors()");
            System.out.flush();
        }
        factorsMap = fEngine.factors(poly);
        if ( debug ) {
            System.out.println("       map of factors:   " + factorsMap);
            System.out.flush();
        }
        return factorsMap;       
    }

    
    // factorization of a new poly
    
    public SortedMap<GenPolynomial,Long> factorNewPolynomial(String polyString) {
        if ( debug ) {
            System.out.println(" DEBUG:  in method factorNewPolynomial1()");
            System.out.flush();
        }
        setPolynomial(polyString);
        if ( debug ) {
            System.out.println("\n  the poly was changed to: " + getPolynomial().toScript());
            System.out.flush();
        }
        factorsMap = factors();
        return factorsMap;       
    }
    
    
    public SortedMap<GenPolynomial,Long> factorNewPolynomial(String polyString, String newPolyVars) {
        if ( debug ) {
            System.out.println(" DEBUG:  in method factorNewPolynomial2()");
            System.out.flush();
        }
        setPolynomial(polyString, newPolyVars);
        if ( debug ) {
            System.out.println("\n  the poly was changed to: " + getPolynomial().toScript());
            System.out.println("       the ring variables are " + getRing().varsToString());
            System.out.flush();           
        }
        factorsMap = factors();
        return factorsMap;       
    }
   
    
    // termination of all working threads
    public void terminate(){
        ComputerThreads.terminate();
    }
        

}//end class JasPolynomial

