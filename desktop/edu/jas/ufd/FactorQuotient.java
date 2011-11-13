/*
 * $Id: FactorQuotient.java 2723 2009-07-09 20:08:47Z kredel $
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;


/**
 * Rational function coefficients factorization algorithms. This class
 * implements factorization methods for polynomials over rational functions,
 * that is, with coefficients from class <code>application.Quotient</code>.
 * @author Heinz Kredel
 */

public class FactorQuotient<C extends GcdRingElem<C>> extends FactorAbstract<Quotient<C>> {


    private static final Logger logger = Logger.getLogger(FactorQuotient.class);


    private final boolean debug = true || logger.isInfoEnabled();


    /**
     * Factorization engine for normal coefficients.
     */
    protected final FactorAbstract<C> nengine;


    /**
     * No argument constructor.
     */
    protected FactorQuotient() {
        throw new IllegalArgumentException("don't use this constructor");
    }


    /**
     * Constructor.
     * @param fac coefficient quotient ring factory.
     */
    public FactorQuotient(QuotientRing<C> fac) {
        super(fac);
        nengine = FactorFactory.<C> getImplementation(fac.ring.coFac);
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<Quotient<C>>> baseFactorsSquarefree(GenPolynomial<Quotient<C>> P) {
        return factorsSquarefree(P);
    }


    /**
     * GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<Quotient<C>>> factorsSquarefree(GenPolynomial<Quotient<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<Quotient<C>>> factors = new ArrayList<GenPolynomial<Quotient<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<Quotient<C>> pfac = P.ring;
        GenPolynomial<Quotient<C>> Pr = P;
        Quotient<C> ldcf = P.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            //System.out.println("ldcf = " + ldcf);
            Pr = Pr.monic();
        }
        QuotientRing<C> qi = (QuotientRing<C>) pfac.coFac;
        GenPolynomialRing<C> ci = qi.ring;
        GenPolynomialRing<GenPolynomial<C>> ifac = new GenPolynomialRing<GenPolynomial<C>>(ci, pfac);
        GenPolynomial<GenPolynomial<C>> Pi = PolyUfdUtil.<C> integralFromQuotientCoefficients(ifac, Pr);
        //System.out.println("Pi = " + Pi);

        // factor in C[x_1,...,x_n][y_1,...,y_m]
        List<GenPolynomial<GenPolynomial<C>>> irfacts = nengine.recursiveFactorsSquarefree(Pi);
        if (logger.isInfoEnabled()) {
            logger.info("irfacts = " + irfacts);
        }
        if (irfacts.size() <= 1) {
            factors.add(P);
            return factors;
        }
        List<GenPolynomial<Quotient<C>>> qfacts = PolyUfdUtil.<C> quotientFromIntegralCoefficients(pfac,
                irfacts);
        //System.out.println("qfacts = " + qfacts);
        //qfacts = PolyUtil.monic(qfacts);
        //System.out.println("qfacts = " + qfacts);
        if (!ldcf.isONE()) {
            GenPolynomial<Quotient<C>> r = qfacts.get(0);
            qfacts.remove(r);
            r = r.multiply(ldcf);
            qfacts.add(0, r);
        }
        if (logger.isInfoEnabled()) {
            logger.info("qfacts = " + qfacts);
        }
        factors.addAll(qfacts);
        return factors;
    }

}
