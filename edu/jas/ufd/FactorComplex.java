/*
 * $Id: FactorComplex.java 2947 2009-12-30 12:48:35Z kredel $
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Complex;
import edu.jas.structure.ComplexRing;
import edu.jas.structure.RingFactory;


/**
 * Complex coefficients factorization algorithms. This class implements
 * factorization methods for polynomials over Complex numbers via the algebraic
 * number C(i) over rational numbers or over (prime) modular integers. <b>Note:</b>
 * Decomposition to linear factors is only via absolute factorization since
 * Complex are not the analytic complex numbers.
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public class FactorComplex<C extends GcdRingElem<C>> extends FactorAbsolute<Complex<C>> {


    private static final Logger logger = Logger.getLogger(FactorComplex.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Factorization engine for algebraic coefficients.
     */
    public final FactorAbstract<AlgebraicNumber<C>> factorAlgeb;


    /**
     * Complex algebraic factory.
     */
    public final AlgebraicNumberRing<C> afac;


    /**
     * No argument constructor. <b>Note:</b> can't use this constructor.
     */
    protected FactorComplex() {
        throw new IllegalArgumentException("don't use this constructor");
    }


    /**
     * Constructor.
     * @param fac complex number factory.
     */
    public FactorComplex(RingFactory<Complex<C>> fac) { // why is this constructor required?
        this((ComplexRing<C>) fac);
    }


    /**
     * Constructor.
     * @param fac complex number factory.
     */
    public FactorComplex(ComplexRing<C> fac) {
        super(fac);
        GenPolynomialRing<C> pfac = new GenPolynomialRing<C>(fac.ring, 1, new TermOrder(TermOrder.INVLEX),
                new String[] { "I" });
        GenPolynomial<C> I = pfac.univariate(0, 2L).sum(pfac.getONE());
        afac = new AlgebraicNumberRing<C>(I, true); // must indicate field
        this.factorAlgeb = FactorFactory.<C> getImplementation(afac);
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree GenPolynomial&lt;AlgebraicNumber&lt;C&gt;&gt;.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<Complex<C>>> baseFactorsSquarefree(GenPolynomial<Complex<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<Complex<C>>> factors = new ArrayList<GenPolynomial<Complex<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<Complex<C>> pfac = P.ring; // CC[x]
        if (pfac.nvar > 1) {
            throw new RuntimeException("only for univariate polynomials");
        }
        ComplexRing<C> cfac = (ComplexRing<C>) pfac.coFac;
        if (!afac.ring.coFac.equals(cfac.ring)) {
            throw new RuntimeException("coefficient rings do not match");
        }
        Complex<C> ldcf = P.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            P = P.monic();
            factors.add(pfac.getONE().multiply(ldcf));
        }
        //System.out.println("\nP = " + P);
        GenPolynomialRing<AlgebraicNumber<C>> pafac = new GenPolynomialRing<AlgebraicNumber<C>>(afac, pfac);
        GenPolynomial<AlgebraicNumber<C>> A = PolyUtil.<C> algebraicFromComplex(pafac, P);
        //System.out.println("A = " + A);
        List<GenPolynomial<AlgebraicNumber<C>>> afactors = factorAlgeb.baseFactorsSquarefree(A);
        if (debug) {
            // System.out.println("complex afactors = " + afactors);
            logger.info("complex afactors = " + afactors);
        }
        for (GenPolynomial<AlgebraicNumber<C>> pa : afactors) {
            GenPolynomial<Complex<C>> pc = PolyUtil.<C> complexFromAlgebraic(pfac, pa);
            factors.add(pc);
        }
        //System.out.println("cfactors = " + factors);
        return factors;
    }

}
