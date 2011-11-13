/*
 * $Id: FactorRational.java 2688 2009-07-04 13:55:47Z kredel $
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;


/**
 * Rational number coefficients factorization algorithms.
 * This class implements factorization methods for polynomials over rational numbers.
 * @author Heinz Kredel
 */

public class FactorRational extends FactorAbsolute<BigRational> {


    private static final Logger logger = Logger.getLogger(FactorRational.class);


    private final boolean debug = true || logger.isInfoEnabled();


    /**
     * Factorization engine for integer base coefficients.
     */
    protected final FactorAbstract<BigInteger> iengine;


    /**
     * No argument constructor. 
     */
    protected FactorRational() {
        super( BigRational.ONE );
        iengine = FactorFactory.getImplementation( BigInteger.ONE );
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<BigRational>> baseFactorsSquarefree(GenPolynomial<BigRational> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<BigRational>> factors = new ArrayList<GenPolynomial<BigRational>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<BigRational> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new RuntimeException(this.getClass().getName() + " only for univariate polynomials");
        }
        GenPolynomial<BigRational> Pr = P;
        BigRational ldcf = P.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            //System.out.println("ldcf = " + ldcf);
            Pr = Pr.monic();
        }
        BigInteger bi = BigInteger.ONE;
        GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(bi, pfac);
        GenPolynomial<BigInteger> Pi = PolyUtil.integerFromRationalCoefficients(ifac, Pr);
        if (debug) {
            logger.info("Pi = " + Pi);
        }
        List<GenPolynomial<BigInteger>> ifacts = iengine.baseFactorsSquarefree(Pi);
        if (logger.isInfoEnabled()) {
            logger.info("ifacts = " + ifacts);
        }
        if (ifacts.size() <= 1) {
            factors.add(P);
            return factors;
        }
        List<GenPolynomial<BigRational>> rfacts = PolyUtil.fromIntegerCoefficients(pfac, ifacts);
        //System.out.println("rfacts = " + rfacts);
        rfacts = PolyUtil.monic(rfacts);
        //System.out.println("rfacts = " + rfacts);
        if ( !ldcf.isONE() ) {
            GenPolynomial<BigRational> r = rfacts.get(0);
            rfacts.remove(r);
            r = r.multiply(ldcf);
            rfacts.add(0, r);
        }
        if (logger.isInfoEnabled()) {
            logger.info("rfacts = " + rfacts);
        }
        factors.addAll(rfacts);
        return factors;
    }

}
