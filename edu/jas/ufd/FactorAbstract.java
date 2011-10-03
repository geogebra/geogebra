/*
 * $Id: FactorAbstract.java 3214 2010-07-06 10:28:30Z kredel $
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import edu.jas.kern.TimeStatus;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;
import edu.jas.util.KsubSet;


/**
 * Abstract factorization algorithms class. This class contains implementations
 * of all methods of the <code>Factorization</code> interface, except the
 * method for factorization of a squarefree polynomial. The methods to obtain
 * squarefree polynomials delegate the computation to the
 * <code>GreatestCommonDivisor</code> classes and are included for
 * convenience.
 * @param <C> coefficient type
 * @author Heinz Kredel
 * @see edu.jas.ufd.FactorFactory
 */

public abstract class FactorAbstract<C extends GcdRingElem<C>> implements Factorization<C> {


    private static final Logger logger = Logger.getLogger(FactorAbstract.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Gcd engine for base coefficients.
     */
    protected final GreatestCommonDivisorAbstract<C> engine;


    /**
     * Squarefree decompositon engine for base coefficients.
     */
    protected final SquarefreeAbstract<C> sengine;


    /**
     * No argument constructor.
     */
    protected FactorAbstract() {
        throw new IllegalArgumentException("don't use this constructor");
    }


    /**
     * Constructor.
     * @param cfac coefficient ring factory.
     */
    public FactorAbstract(RingFactory<C> cfac) {
        engine = GCDFactory.<C> getProxy(cfac);
        //engine = GCDFactory.<C> getImplementation(cfac);
        sengine = SquarefreeFactory.<C> getImplementation(cfac);
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getName();
    }


    /**
     * GenPolynomial test if is irreducible.
     * @param P GenPolynomial.
     * @return true if P is irreducible, else false.
     */
    public boolean isIrreducible(GenPolynomial<C> P) {
        if (!isSquarefree(P)) {
            return false;
        }
        List<GenPolynomial<C>> F = factorsSquarefree(P);
        if (F.size() == 1) {
            return true;
        } else if (F.size() > 2) {
            return false;
        } else { //F.size() == 2
            boolean cnst = false;
            for (GenPolynomial<C> p : F) {
                if (p.isConstant()) {
                    cnst = true;
                }
            }
            return cnst;
        }
    }


    /**
     * GenPolynomial test if a non trivial factorization exsists.
     * @param P GenPolynomial.
     * @return true if P is reducible, else false.
     */
    public boolean isReducible(GenPolynomial<C> P) {
        return !isIrreducible(P);
    }


    /**
     * GenPolynomial test if is squarefree.
     * @param P GenPolynomial.
     * @return true if P is squarefree, else false.
     */
    public boolean isSquarefree(GenPolynomial<C> P) {
        GenPolynomial<C> S = squarefreePart(P);
        GenPolynomial<C> Ps = basePrimitivePart(P);
        return Ps.equals(S);
    }


    /**
     * GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree and primitive! (respectively monic) GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1,...,r} p_i.
     */
    public List<GenPolynomial<C>> factorsSquarefree(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar == 1) {
            return baseFactorsSquarefree(P);
        }
        List<GenPolynomial<C>> factors = new ArrayList<GenPolynomial<C>>();
        if (P.isZERO()) {
            return factors;
        }
        long d = P.degree() + 1L;
        GenPolynomial<C> kr = PolyUfdUtil.<C> substituteKronecker(P, d);
        GenPolynomialRing<C> ufac = kr.ring;
        ufac.setVars( ufac.newVars( "zz" ) ); // side effects 
        if (debug) {
            logger.info("subs(P,d=" + d + ") = " + kr);
            //System.out.println("subs(P,d=" + d + ") = " + kr);
        }
        if (kr.degree(0) > 100) {
            logger.warn("Kronecker substitution has to high degree");
            TimeStatus.checkTime("degree > 100");
        }

        // factor Kronecker polynomial
        List<GenPolynomial<C>> ulist = new ArrayList<GenPolynomial<C>>();
        // kr might not be squarefree so complete factor univariate
        SortedMap<GenPolynomial<C>, Long> slist = baseFactors(kr);
        if (debug && !isFactorization(kr, slist)) {
            System.out.println("kr    = " + kr);
            System.out.println("slist = " + slist);
            throw new RuntimeException("no factorization");
        }
        for (GenPolynomial<C> g : slist.keySet()) {
            long e = slist.get(g);
            for (int i = 0; i < e; i++) { // is this really required? yes!
                ulist.add(g);
            }
        }
        //System.out.println("ulist = " + ulist);
        if (ulist.size() == 1 && ulist.get(0).degree() == P.degree()) {
            factors.add(P);
            return factors;
        }
        //wrong: List<GenPolynomial<C>> klist = PolyUfdUtil.<C> backSubstituteKronecker(pfac, ulist, d);
        //System.out.println("back(klist) = " + PolyUfdUtil.<C> backSubstituteKronecker(pfac, ulist, d));
        if (logger.isInfoEnabled()) {
            logger.info("ulist = " + ulist);
            //System.out.println("ulist = " + ulist);
        }
        // combine trial factors
        int dl = ulist.size() - 1; //(ulist.size() + 1) / 2;
        //System.out.println("dl = " + dl);
        int ti = 0;
        GenPolynomial<C> u = P;
        long deg = (u.degree() + 1L) / 2L; // max deg
        //System.out.println("deg = " + deg);
        for (int j = 1; j <= dl; j++) {
            KsubSet<GenPolynomial<C>> ps = new KsubSet<GenPolynomial<C>>(ulist, j);
            for (List<GenPolynomial<C>> flist : ps) {
                //System.out.println("flist = " + flist);
                GenPolynomial<C> utrial = ufac.getONE();
                for (int k = 0; k < flist.size(); k++) {
                    utrial = utrial.multiply(flist.get(k));
                }
                GenPolynomial<C> trial = PolyUfdUtil.<C> backSubstituteKronecker(pfac, utrial, d);
                if (trial.degree() > deg || trial.isConstant()) {
                    continue;
                }
		trial = trial.monic();
                ti++;
                if (ti % 1000 == 0) {
                    System.out.print("ti(" + ti + ") ");
                    TimeStatus.checkTime(ti + " % 1000 == 0");
                    if (ti % 10000 == 0) {
                        System.out.println("\ndl   = " + dl + ", deg(u) = " + deg);
                        System.out.println("ulist = " + ulist);
                        System.out.println("kr    = " + kr);
                        System.out.println("u     = " + u);
                    }
                }
                GenPolynomial<C> rem = PolyUtil.<C> basePseudoRemainder(u, trial);
                //System.out.println(" rem = " + rem);
                if (rem.isZERO()) {
                    logger.info("trial = " + trial);
                    //System.out.println("trial = " + trial);
                    factors.add(trial);
                    u = PolyUtil.<C> basePseudoDivide(u, trial); //u = u.divide( trial );
                    if ( u.isConstant() ) {
                        j = dl+1;
                        break;
                    }
                    //if (ulist.removeAll(flist)) {
                    ulist = removeOnce(ulist,flist);
                    if ( ulist != null ) {
                        //System.out.println("new ulist = " + ulist);
                        dl = (ulist.size() + 1) / 2;
                        j = 0; // since j++
                        break;
		    //} else {
                    //    logger.error("error removing flist from ulist = " + ulist);
                    }
                }
            }
        }
        if (!u.isONE() && !u.equals(P)) {
            logger.info("rest u = " + u);
            //System.out.println("rest u = " + u);
            factors.add(u);
        }
        if (factors.size() == 0) {
            logger.info("irred u = " + u);
            //System.out.println("irred u = " + u);
            factors.add(P);
        }
        return factors;
    }


    private static <T> List<T> removeOnce(List<T> a, List<T> b) {
	List<T> res = new ArrayList<T>();
	res.addAll(a);
	for ( T e : b ) {
	    boolean t = res.remove(e);
	}
	return res;
    }


    /**
     * Univariate GenPolynomial factorization ignoring multiplicities.
     * @param P GenPolynomial in one variable.
     * @return [p_1, ..., p_k] with P = prod_{i=1,...,k} p_i**{e_i} for some
     *         e_i.
     */
    public List<GenPolynomial<C>> baseFactorsRadical(GenPolynomial<C> P) {
        return new ArrayList<GenPolynomial<C>>(baseFactors(P).keySet());
    }


    /**
     * Univariate GenPolynomial factorization.
     * @param P GenPolynomial in one variable.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k}
     *         p_i**e_i.
     */
    public SortedMap<GenPolynomial<C>, Long> baseFactors(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<C> pfac = P.ring;
        SortedMap<GenPolynomial<C>, Long> factors = new TreeMap<GenPolynomial<C>, Long>(pfac.getComparator());
        if (P.isZERO()) {
            return factors;
        }
        if (pfac.nvar > 1) {
            throw new RuntimeException(this.getClass().getName() + " only for univariate polynomials");
        }
        C c;
        if (pfac.coFac.isField()) { //pfac.characteristic().signum() > 0
            c = P.leadingBaseCoefficient();
        } else {
            c = engine.baseContent(P);
            // move sign to the content
            if (P.signum() < 0 && c.signum() > 0) {
                c = c.negate();
                //P = P.negate();
            }
        }
        if (!c.isONE()) {
            GenPolynomial<C> pc = pfac.getONE().multiply(c);
            factors.put(pc, 1L);
            P = P.divide(c); // make primitive or monic
        }
        if (logger.isInfoEnabled()) {
            logger.info("base facs for P = " + P);
        }
        SortedMap<GenPolynomial<C>, Long> facs = sengine.baseSquarefreeFactors(P);
        if (logger.isInfoEnabled() && ( facs.size() > 1 || ( facs.size() == 1 && facs.get(facs.firstKey()) > 1 ) ) ) {
            logger.info("squarefree facs   = " + facs);
            //System.out.println("sfacs   = " + facs);
            //boolean tt = isFactorization(P,facs);
            //System.out.println("sfacs tt   = " + tt);
        }
        for (GenPolynomial<C> g : facs.keySet()) {
            Long k = facs.get(g);
            //System.out.println("g       = " + g);
            if (pfac.coFac.isField() && !g.leadingBaseCoefficient().isONE()) {
                g = g.monic(); // how can this happen?
                logger.warn("squarefree facs mon = " + g);
            }
            List<GenPolynomial<C>> sfacs = baseFactorsSquarefree(g);
            if (debug) {
                logger.info("factors of squarefree = " + sfacs);
                //System.out.println("sfacs   = " + sfacs);
            }
            for (GenPolynomial<C> h : sfacs) {
                Long j = factors.get(h); // evtl. constants
                if (j != null) {
                    k += j;
                }
                if ( ! h.isONE() ) {
                    factors.put(h, k);
                }
            }
        }
        //System.out.println("factors = " + factors);
        return factors;
    }


    /**
     * Univariate GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree and primitive! GenPolynomial in one variable.
     * @return [p_1, ..., p_k] with P = prod_{i=1,...,k} p_i.
     */
    public abstract List<GenPolynomial<C>> baseFactorsSquarefree(GenPolynomial<C> P);


    /**
     * GenPolynomial factorization ignoring multiplicities.
     * @param P GenPolynomial.
     * @return [p_1, ..., p_k] with P = prod_{i=1,...,k} p_i**{e_i} for some
     *         e_i.
     */
    public List<GenPolynomial<C>> factorsRadical(GenPolynomial<C> P) {
        return new ArrayList<GenPolynomial<C>>(factors(P).keySet());
    }


    /**
     * GenPolynomial list factorization ignoring multiplicities.
     * @param L list of GenPolynomials.
     * @return [p_1, ..., p_k] with p = prod_{i=1,...,k} p_i**{e_i} for some
     *         e_i for all p in L.
     */
    public List<GenPolynomial<C>> factorsRadical(List<GenPolynomial<C>> L) {
        SortedSet<GenPolynomial<C>> facs = new TreeSet<GenPolynomial<C>>();
        for ( GenPolynomial<C> p : L ) {
            List<GenPolynomial<C>> fs = factorsRadical(p);
            facs.addAll(fs);
        }
        return new ArrayList<GenPolynomial<C>>( facs );
    }


    /**
     * GenPolynomial factorization.
     * @param P GenPolynomial.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k}
     *         p_i**e_i.
     */
    public SortedMap<GenPolynomial<C>, Long> factors(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar == 1) {
            return baseFactors(P);
        }
        SortedMap<GenPolynomial<C>, Long> factors = new TreeMap<GenPolynomial<C>, Long>(pfac.getComparator());
        if (P.isZERO()) {
            return factors;
        }
        C c;
        if (pfac.coFac.isField()) { // pfac.characteristic().signum() > 0
            c = P.leadingBaseCoefficient();
        } else {
            c = engine.baseContent(P);
            // move sign to the content
            if (P.signum() < 0 && c.signum() > 0) {
                c = c.negate();
                //P = P.negate();
            }
        }
        if (!c.isONE()) {
            GenPolynomial<C> pc = pfac.getONE().multiply(c);
            factors.put(pc, 1L);
            P = P.divide(c); // make base primitive or base monic
        }
        if (logger.isInfoEnabled()) {
            logger.info("squarefree mfacs P = " + P);
        }
        SortedMap<GenPolynomial<C>, Long> facs = sengine.squarefreeFactors(P);
        if (logger.isInfoEnabled() ) {
            if ( facs.size() > 1 ) {
                logger.info("squarefree mfacs   = " + facs);
	    } else if ( facs.size() == 1 && facs.get(facs.firstKey()) > 1L ) {
                logger.info("squarefree mfacs   = " + facs);
	    } else {
                logger.warn("squarefree mfacs empty = " + facs);
	    }
        }
        for (GenPolynomial<C> g : facs.keySet()) {
            Long d = facs.get(g);
            List<GenPolynomial<C>> sfacs = factorsSquarefree(g);
            if (debug) {
                logger.info("factors of squarefree = " + sfacs);
                //System.out.println("sfacs   = " + sfacs);
            }
            for (GenPolynomial<C> h : sfacs) {
                Long j = factors.get(h); // evtl. constants
                if (j != null) {
                    d += j;
                }
                factors.put(h, d);
            }
        }
        //System.out.println("factors = " + factors);
        return factors;
    }


    /**
     * GenPolynomial greatest squarefree divisor. Delegates computation to a
     * GreatestCommonDivisor class.
     * @param P GenPolynomial.
     * @return squarefree(P).
     */
    public GenPolynomial<C> squarefreePart(GenPolynomial<C> P) {
        return sengine.squarefreePart(P);
    }


    /**
     * GenPolynomial primitive part. Delegates computation to a
     * GreatestCommonDivisor class.
     * @param P GenPolynomial.
     * @return primitivePart(P).
     */
    public GenPolynomial<C> primitivePart(GenPolynomial<C> P) {
        return engine.primitivePart(P);
    }


    /**
     * GenPolynomial base primitive part. Delegates computation to a
     * GreatestCommonDivisor class.
     * @param P GenPolynomial.
     * @return basePrimitivePart(P).
     */
    public GenPolynomial<C> basePrimitivePart(GenPolynomial<C> P) {
        return engine.basePrimitivePart(P);
    }


    /**
     * GenPolynomial squarefree factorization. Delegates computation to a
     * GreatestCommonDivisor class.
     * @param P GenPolynomial.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k}
     *         p_i**e_i.
     */
    public SortedMap<GenPolynomial<C>, Long> squarefreeFactors(GenPolynomial<C> P) {
        return sengine.squarefreeFactors(P);
    }


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial.
     * @param F = [p_1,...,p_k].
     * @return true if P = prod_{i=1,...,r} p_i, else false.
     */
    public boolean isFactorization(GenPolynomial<C> P, List<GenPolynomial<C>> F) {
        return sengine.isFactorization(P,F);
        // test irreducible
    }


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i , else false.
     */
    public boolean isFactorization(GenPolynomial<C> P, SortedMap<GenPolynomial<C>, Long> F) {
        return sengine.isFactorization(P,F);
        // test irreducible
    }


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i , else false.
     */
    public boolean isRecursiveFactorization(GenPolynomial<GenPolynomial<C>> P,
            SortedMap<GenPolynomial<GenPolynomial<C>>, Long> F) {
        return sengine.isRecursiveFactorization(P,F);
        // test irreducible
    }


    /**
     * Recursive GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree recursive GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    public List<GenPolynomial<GenPolynomial<C>>> recursiveFactorsSquarefree(GenPolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<GenPolynomial<C>>> factors = new ArrayList<GenPolynomial<GenPolynomial<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        GenPolynomialRing<C> qi = (GenPolynomialRing<C>) pfac.coFac;
        GenPolynomialRing<C> ifac = qi.extend(pfac.getVars());
        GenPolynomial<C> Pi = PolyUtil.<C> distribute(ifac, P);
        //System.out.println("Pi = " + Pi);

        C ldcf = Pi.leadingBaseCoefficient();
        if (!ldcf.isONE() && ldcf.isUnit()) {
            //System.out.println("ldcf = " + ldcf);
            Pi = Pi.monic();
        }

        // factor in C[x_1,...,x_n,y_1,...,y_m]
        List<GenPolynomial<C>> ifacts = factorsSquarefree(Pi);
        if (logger.isInfoEnabled()) {
            logger.info("ifacts = " + ifacts);
        }
        if (ifacts.size() <= 1) {
            factors.add(P);
            return factors;
        }
        if (!ldcf.isONE() && ldcf.isUnit()) {
            GenPolynomial<C> r = ifacts.get(0);
            ifacts.remove(r);
            r = r.multiply(ldcf);
            ifacts.add(0, r);
        }
        List<GenPolynomial<GenPolynomial<C>>> rfacts = PolyUtil.<C> recursive(pfac, ifacts);
        //System.out.println("rfacts = " + rfacts);
        if (logger.isDebugEnabled()) {
            logger.info("rfacts = " + rfacts);
        }
        factors.addAll(rfacts);
        return factors;
    }


    /**
     * Recursive GenPolynomial factorization.
     * @param P recursive GenPolynomial.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k}
     *         p_i**e_i.
     */
    public SortedMap<GenPolynomial<GenPolynomial<C>>, Long> recursiveFactors(GenPolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        SortedMap<GenPolynomial<GenPolynomial<C>>, Long> factors = new TreeMap<GenPolynomial<GenPolynomial<C>>, Long>(
                pfac.getComparator());
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.put(P, 1L);
            return factors;
        }
        GenPolynomialRing<C> qi = (GenPolynomialRing<C>) pfac.coFac;
        GenPolynomialRing<C> ifac = qi.extend(pfac.getVars());
        GenPolynomial<C> Pi = PolyUtil.<C> distribute(ifac, P);
        //System.out.println("Pi = " + Pi);

        C ldcf = Pi.leadingBaseCoefficient();
        if (!ldcf.isONE() && ldcf.isUnit()) {
            //System.out.println("ldcf = " + ldcf);
            Pi = Pi.monic();
        }

        // factor in C[x_1,...,x_n,y_1,...,y_m]
        SortedMap<GenPolynomial<C>, Long> dfacts = factors(Pi);
        if (logger.isInfoEnabled()) {
            logger.info("dfacts = " + dfacts);
        }
        if (!ldcf.isONE() && ldcf.isUnit()) {
            GenPolynomial<C> r = dfacts.firstKey();
            Long E = dfacts.remove(r);
            r = r.multiply(ldcf);
            dfacts.put(r, E);
        }
        for (GenPolynomial<C> f : dfacts.keySet()) {
            Long E = dfacts.get(f);
            GenPolynomial<GenPolynomial<C>> rp = PolyUtil.<C> recursive(pfac, f);
            factors.put(rp, E);
        }
        //System.out.println("rfacts = " + rfacts);
        if (logger.isInfoEnabled()) {
            logger.info("factors = " + factors);
        }
        return factors;
    }

}
