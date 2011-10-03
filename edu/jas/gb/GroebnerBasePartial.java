/*
 * $Id: GroebnerBasePartial.java 3131 2010-05-15 09:12:25Z kredel $
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.OptimizedPolynomialList;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderOptimization;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Partial Groebner Bases for subsets of variables. Let <code>pvars</code> be
 * a subset of variables <code>vars</code> of the polynomial ring K[vars].
 * Methods compute Groebner bases with coefficients from K[vars \ pvars] in the
 * polynomial ring K[vars \ pvars][pvars].
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GroebnerBasePartial<C extends GcdRingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(GroebnerBasePartial.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Backing Groebner base engine.
     */
    protected GroebnerBaseAbstract<C> bb;


    /**
     * Backing recursive Groebner base engine.
     */
    protected GroebnerBaseAbstract<GenPolynomial<C>> rbb; // can be null


    /**
     * Constructor.
     */
    public GroebnerBasePartial() {
        this(new GroebnerBaseSeq<C>(), null);
    }


    /**
     * Constructor.
     * @param rf coefficient ring factory.
     */
    public GroebnerBasePartial(RingFactory<GenPolynomial<C>> rf) {
        this(new GroebnerBaseSeq<C>(), new GroebnerBasePseudoRecSeq<C>(rf));
    }


    /**
     * Constructor.
     * @param bb Groebner base engine
     * @param rbb recursive Groebner base engine
     */
    public GroebnerBasePartial(GroebnerBaseAbstract<C> bb, GroebnerBaseAbstract<GenPolynomial<C>> rbb) {
        super();
        this.bb = bb;
        this.rbb = rbb;
        if (rbb == null) {
            //logger.warn("no recursive GB given");
        }
    }


    /**
     * Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        return bb.GB(modv, F);
    }


    /**
     * Groebner base test.
     * @param F polynomial list.
     * @return true, if F is a partial Groebner base, else false.
     */
    public boolean isGBrec(List<GenPolynomial<GenPolynomial<C>>> F) {
        return isGBrec(0, F);
    }


    /**
     * Groebner base test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a partial Groebner base, else false.
     */
    public boolean isGBrec(int modv, List<GenPolynomial<GenPolynomial<C>>> F) {
        if (F == null || F.size() == 0) {
            return true;
        }
        if (true) {
            rbb = new GroebnerBasePseudoRecSeq<C>(F.get(0).ring.coFac);
        }
        return rbb.isGB(modv, F);
    }


    /**
     * Partial permuation for specific variables. Computes a permutation perm
     * for the variables vars, such that perm(vars) == pvars ... (vars \ pvars).
     * Uses internal (reversed) variable sorting.
     * @param vars names for all variables.
     * @param pvars names for main variables, pvars subseteq vars.
     * @return permutation for vars, such that perm(vars) == pvars ... (vars \
     *         pvars).
     */
    public static List<Integer> partialPermutation(String[] vars, String[] pvars) {
        return partialPermutation(vars, pvars, null);
        //no: return getPermutation(vars,pvars);
    }


    /**
     * Permutation of variables for elimination.
     * @param aname variables for the full polynomial ring.
     * @param ename variables for the elimination ring, subseteq aname.
     * @return perm({vars \ ename},ename)
     */
    public static List<Integer> getPermutation(String[] aname, String[] ename) {
        if (aname == null || ename == null) {
            throw new IllegalArgumentException("aname or ename may not be null");
        }
        List<Integer> perm = new ArrayList<Integer>(aname.length);
        // add ename permutation
        for (int i = 0; i < ename.length; i++) {
            int j = indexOf(ename[i], aname);
            if (j < 0) {
                throw new IllegalArgumentException("ename not contained in aname");
            }
            perm.add(j);
        }
        //System.out.println("perm_low = " + perm);
        // add aname \ ename permutation
        for (int i = 0; i < aname.length; i++) {
            if (!perm.contains(i)) {
                perm.add(i);
            }
        }
        //System.out.println("perm_all = " + perm);
        // reverse permutation indices
        int n1 = aname.length - 1;
        List<Integer> perm1 = new ArrayList<Integer>(aname.length);
        for (Integer k : perm) {
            perm1.add(n1 - k);
        }
        perm = perm1;
        //System.out.println("perm_inv = " + perm1);
        Collections.reverse(perm);
        //System.out.println("perm_rev = " + perm);
        return perm;
    }


    /**
     * Index of s in A.
     * @param s search string
     * @param A string array
     * @return i if s == A[i] for some i, else -1.
     */
    public static int indexOf(String s, String[] A) {
        for (int i = 0; i < A.length; i++) {
            if (s.equals(A[i])) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Partial permuation for specific variables. Computes a permutation perm
     * for the variables vars, such that perm(vars) == pvars ... (vars \ pvars).
     * Uses internal (reversed) variable sorting.
     * @param vars names for all variables.
     * @param pvars names for main variables, pvars subseteq vars.
     * @param rvars names for remaining variables, rvars eq { vars \ pvars }.
     * @return permutation for vars, such that perm(vars) == (pvars, {vars \
     *         pvars}).
     */
    public static List<Integer> partialPermutation(String[] vars, String[] pvars, String[] rvars) {
        if (vars == null || pvars == null) {
            throw new IllegalArgumentException("no variable names found");
        }
        List<String> variables = new ArrayList<String>(vars.length);
        List<String> pvariables = new ArrayList<String>(pvars.length);
        for (int i = 0; i < vars.length; i++) {
            variables.add(vars[i]);
        }
        for (int i = 0; i < pvars.length; i++) {
            pvariables.add(pvars[i]);
        }
        if (rvars == null) {
            rvars = remainingVars(vars, pvars);
        }
        List<String> rvariables = new ArrayList<String>(rvars.length);
        for (int i = 0; i < rvars.length; i++) {
            rvariables.add(rvars[i]);
        }
        if (rvars.length + pvars.length == vars.length) {
            //System.out.println("pvariables  = " + pvariables);
            return getPermutation(vars, rvars);
        } else if (true) {
            logger.info("not implemented for " + variables + " != " + pvariables + " cup " + rvariables);
            throw new RuntimeException("not implemented");
        }
        if (!variables.containsAll(pvariables) || !variables.containsAll(rvariables)) {
            throw new IllegalArgumentException("partial variables not contained in all variables ");
        }
        Collections.reverse(variables);
        Collections.reverse(pvariables);
        Collections.reverse(rvariables);
        System.out.println("variables  = " + variables);
        System.out.println("pvariables = " + pvariables);
        System.out.println("rvariables = " + rvariables);

        List<Integer> perm = new ArrayList<Integer>();
        List<Integer> pv = new ArrayList<Integer>();
        for (String s : variables) {
            int j = pvariables.indexOf(s);
            if (j >= 0) {
                perm.add(j);
            }
        }
        int i = pvariables.size();
        for (String s : variables) {
            if (!pvariables.contains(s)) {
                pv.add(i);
            }
            i++;
        }

        System.out.println("perm, 1 = " + perm);
        //System.out.println("pv   = " + pv);
        // sort perm according to pvars
        int ps = perm.size(); // == pvars.length
        for (int k = 0; k < ps; k++) {
            for (int j = k + 1; j < ps; j++) {
                int kk = variables.indexOf(pvariables.get(k));
                int jj = variables.indexOf(pvariables.get(j));
                if (kk > jj) { // swap
                    int t = perm.get(k);
                    System.out.println("swap " + t + " with " + perm.get(j));
                    perm.set(k, perm.get(j));
                    perm.set(j, t);
                }
            }
        }
        //System.out.println("perm = " + perm);
        // sort pv according to rvars
        int rs = pv.size(); // == rvars.length
        for (int k = 0; k < rs; k++) {
            for (int j = k + 1; j < rs; j++) {
                int kk = variables.indexOf(rvariables.get(k));
                int jj = variables.indexOf(rvariables.get(j));
                if (kk > jj) { // swap
                    int t = pv.get(k);
                    //System.out.println("swap " + t + " with " + perm.get(j));
                    pv.set(k, pv.get(j));
                    pv.set(j, t);
                }
            }
        }
        //System.out.println("pv   = " + pv);
        perm.addAll(pv);
        System.out.println("perm, 2 = " + perm);
        return perm;
    }


    /**
     * Partial permuation for specific variables. Computes a permutation perm
     * for the variables vars, such that perm(vars) == (evars, pvars, (vars \ {
     * evars, pvars }). Uses internal (reversed) variable sorting.
     * @param vars names for all variables.
     * @param evars names for elimination variables, evars subseteq vars.
     * @param pvars names for main variables, pvars subseteq vars.
     * @param rvars names for remaining variables, rvars eq {vars \ { evars,
     *            pvars } }.
     * @return permutation for vars, such that perm(vars) == (evars,pvars, {vars \
     *         {evars,pvars}}.
     */
    public static List<Integer> 
      partialPermutation(String[] vars, String[] evars, String[] pvars, String[] rvars) {
        if (vars == null || evars == null || pvars == null) {
            throw new IllegalArgumentException("not all variable names given");
        }
        String[] uvars;
        if (rvars != null) {
            uvars = new String[pvars.length + rvars.length];
            for (int i = 0; i < pvars.length; i++) {
                uvars[i] = pvars[i];
            }
            for (int i = 0; i < rvars.length; i++) {
                uvars[pvars.length + i] = rvars[i];
            }
        } else {
            uvars = pvars;
        }
        //System.out.println("uvars = " + Arrays.toString(uvars));
        List<Integer> perm = partialPermutation(vars, evars, uvars);
        return perm;
    }


    /**
     * Remaining variables vars \ pvars. Uses internal (reversed) variable
     * sorting, original order is preserved.
     * @param vars names for all variables.
     * @param pvars names for main variables, pvars subseteq vars.
     * @return remaining vars = (vars \ pvars).
     */
    public static String[] remainingVars(String[] vars, String[] pvars) {
        if (vars == null || pvars == null) {
            throw new IllegalArgumentException("no variable names found");
        }
        List<String> variables = new ArrayList<String>(vars.length);
        List<String> pvariables = new ArrayList<String>(pvars.length);
        for (int i = 0; i < vars.length; i++) {
            variables.add(vars[i]);
        }
        for (int i = 0; i < pvars.length; i++) {
            pvariables.add(pvars[i]);
        }
        if (!variables.containsAll(pvariables)) {
            throw new IllegalArgumentException("partial variables not contained in all variables ");
        }
        // variables.setMinus(pvariables)
        List<String> rvariables = new ArrayList<String>(variables);
        for (String s : pvariables) {
            rvariables.remove(s);
        }
        int cl = vars.length - pvars.length;
        String[] rvars = new String[cl];
        int i = 0;
        for (String s : rvariables) {
            rvars[i++] = s;
        }
        return rvars;
    }


    /**
     * Partial recursive Groebner base for specific variables. Computes Groebner
     * base in K[vars \ pvars][pvars] with coefficients from K[vars \ pvars].
     * @param F polynomial list.
     * @param pvars names for main variables of partial Groebner base
     *            computation.
     * @return a container for a partial Groebner base of F wrt pvars.
     */
    public OptimizedPolynomialList<GenPolynomial<C>> partialGBrec(List<GenPolynomial<C>> F, String[] pvars) {
        if (F == null && F.isEmpty()) {
            throw new IllegalArgumentException("empty F not allowed");
        }
        GenPolynomialRing<C> fac = F.get(0).ring;
        String[] vars = fac.getVars();
        if (vars == null || pvars == null) {
            throw new IllegalArgumentException("not all variable names found");
        }
        if (vars.length == pvars.length) {
            throw new IllegalArgumentException("use non recursive partialGB algorithm");
        }
        // compute permutation (in reverse sorting)
        List<Integer> perm = partialPermutation(vars, pvars);

        GenPolynomialRing<C> pfac = TermOrderOptimization.<C> permutation(perm, fac);
        if (logger.isInfoEnabled()) {
            logger.info("pfac = " + pfac);
        }
        List<GenPolynomial<C>> ppolys = TermOrderOptimization.<C> permutation(perm, pfac, F);
        //System.out.println("ppolys = " + ppolys);

        int cl = fac.nvar - pvars.length; // > 0
        int pl = pvars.length;
        String[] rvars = remainingVars(vars, pvars);
        GenPolynomialRing<C> cfac = new GenPolynomialRing<C>(fac.coFac, cl, fac.tord, rvars);
        //System.out.println("cfac = " + cfac);
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, pl,
                fac.tord, pvars);
        if (logger.isInfoEnabled()) {
            logger.info("rfac = " + rfac);
        }
        //System.out.println("rfac = " + rfac);

        List<GenPolynomial<GenPolynomial<C>>> Fr = PolyUtil.<C> recursive(rfac, ppolys);
        //System.out.println("\nFr = " + Fr);

        if (true) {
            rbb = new GroebnerBasePseudoRecSeq<C>(cfac);
        }
        List<GenPolynomial<GenPolynomial<C>>> Gr = rbb.GB(Fr);
        //System.out.println("\nGr = " + Gr);
        //perm = perm.subList(0,pl);
        OptimizedPolynomialList<GenPolynomial<C>> pgb = new OptimizedPolynomialList<GenPolynomial<C>>(perm,
                rfac, Gr);
        return pgb;
    }


    /**
     * Partial Groebner base for specific variables. Computes Groebner base in
     * K[vars \ pvars][pvars] with coefficients from K[vars \ pvars] but returns
     * polynomials in K[vars \ pvars, pvars].
     * @param F polynomial list.
     * @param pvars names for main variables of partial Groebner base
     *            computation.
     * @return a container for a partial Groebner base of F wrt pvars.
     */
    public OptimizedPolynomialList<C> partialGB(List<GenPolynomial<C>> F, String[] pvars) {
        if (F == null && F.isEmpty()) {
            throw new IllegalArgumentException("empty F not allowed");
        }
        GenPolynomialRing<C> fac = F.get(0).ring;
        String[] vars = fac.getVars();
        // compute permutation (in reverse sorting)
        String[] xvars = remainingVars(vars, pvars);
        //System.out.println("xvars = " + Arrays.toString(xvars));

        List<Integer> perm = partialPermutation(vars, pvars);
        //System.out.println("pGB, perm   = " + perm);
        //System.out.println("pGB, perm,1 = " + getPermutation(vars, xvars));

        GenPolynomialRing<C> pfac = TermOrderOptimization.<C> permutation(perm, fac);
        if (logger.isInfoEnabled()) {
            logger.info("pfac = " + pfac);
        }
        List<GenPolynomial<C>> ppolys = TermOrderOptimization.<C> permutation(perm, pfac, F);
        //System.out.println("ppolys = " + ppolys);

        int cl = fac.nvar - pvars.length;
        if (cl == 0) { // non recursive case
            //GroebnerBaseSeq<C> bb = new GroebnerBaseSeq<C>();
            List<GenPolynomial<C>> G = bb.GB(ppolys);
            OptimizedPolynomialList<C> pgb = new OptimizedPolynomialList<C>(perm, pfac, G);
            return pgb;
        }
        // recursive case
        int pl = pvars.length;
        String[] rvars = remainingVars(vars, pvars);
        GenPolynomialRing<C> cfac = new GenPolynomialRing<C>(fac.coFac, cl, fac.tord, rvars);
        //System.out.println("cfac = " + cfac);

        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, pl,
                fac.tord, pvars);
        if (logger.isInfoEnabled()) {
            logger.info("rfac = " + rfac);
        }
        //System.out.println("rfac = " + rfac);

        List<GenPolynomial<GenPolynomial<C>>> Fr = PolyUtil.<C> recursive(rfac, ppolys);
        //System.out.println("\nFr = " + Fr);

        if (true) {
            rbb = new GroebnerBasePseudoRecSeq<C>(cfac);
        }
        List<GenPolynomial<GenPolynomial<C>>> Gr = rbb.GB(Fr);
        //System.out.println("\nGr = " + Gr);

        List<GenPolynomial<C>> G = PolyUtil.<C> distribute(pfac, Gr);
        //System.out.println("\nG = " + G);

        OptimizedPolynomialList<C> pgb = new OptimizedPolynomialList<C>(perm, pfac, G);
        return pgb;
    }


    /**
     * Partial Groebner base for specific variables. Computes Groebner base with
     * coefficients from K[pvars] but returns polynomials in K[pvars, evars].
     * @param F polynomial list.
     * @param evars names for upper main variables of partial Groebner base
     *            computation.
     * @param pvars names for lower main variables of partial Groebner base
     *            computation.
     * @return a container for a partial Groebner base of F wrt (pvars,evars).
     */
    public OptimizedPolynomialList<C> elimPartialGB(List<GenPolynomial<C>> F, String[] evars, String[] pvars) {
        if (F == null && F.isEmpty()) {
            throw new IllegalArgumentException("empty F not allowed");
        }
        GenPolynomialRing<C> fac = F.get(0).ring;
        String[] vars = fac.getVars();
        // compute permutation (in reverse sorting)
        //System.out.println("vars  = " + Arrays.toString(vars));
        //System.out.println("evars = " + Arrays.toString(evars));
        //System.out.println("pvars = " + Arrays.toString(pvars));
        List<Integer> perm = partialPermutation(vars, evars, pvars);
        //System.out.println("perm = " + perm);

        GenPolynomialRing<C> pfac = TermOrderOptimization.<C> permutation(perm, fac);
        if (logger.isInfoEnabled()) {
            logger.info("pfac = " + pfac);
        }
        List<GenPolynomial<C>> ppolys = TermOrderOptimization.<C> permutation(perm, pfac, F);
        //System.out.println("ppolys = " + ppolys);

        int cl = fac.nvar - evars.length - pvars.length;
        if (cl == 0) { // non recursive case
            TermOrder to = pfac.tord;
            int ev = to.getEvord();
            //ev = TermOrder.IGRLEX;
            TermOrder split = new TermOrder(ev, ev, pfac.nvar, evars.length);
            pfac = new GenPolynomialRing<C>(pfac.coFac, pfac.nvar, split, pfac.getVars());
            if (logger.isInfoEnabled()) {
                //logger.info("split = " + split);
                logger.info("pfac = " + pfac);
            }
            List<GenPolynomial<C>> Fs = new ArrayList<GenPolynomial<C>>(ppolys.size());
            for (GenPolynomial<C> p : ppolys) {
                Fs.add(pfac.copy(p));
            }
            List<GenPolynomial<C>> G = bb.GB(Fs);
            OptimizedPolynomialList<C> pgb = new OptimizedPolynomialList<C>(perm, pfac, G);
            if (logger.isInfoEnabled()) {
               logger.info("pgb = " + pgb);
            }
            return pgb;
        } else {
            logger.info("not meaningful for elimination " + cl);
        }
        // recursive case
        int pl = pvars.length + pvars.length;
        String[] rvars = remainingVars(vars, evars);
        rvars = remainingVars(rvars, pvars);
        String[] uvars = new String[evars.length + pvars.length];
        for (int i = 0; i < pvars.length; i++) {
            uvars[i] = pvars[i];
        }
        for (int i = 0; i < evars.length; i++) {
            uvars[pvars.length + i] = evars[i];
        }

        GenPolynomialRing<C> cfac = new GenPolynomialRing<C>(fac.coFac, cl, fac.tord, rvars);
        //System.out.println("cfac = " + cfac);

        TermOrder to = pfac.tord;
        int ev = to.getEvord();
        TermOrder split = new TermOrder(ev, ev, pl, evars.length);

        GenPolynomialRing<C> sfac = new GenPolynomialRing<C>(pfac.coFac, pfac.nvar, split, pfac.getVars());

        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, pl, split,
                uvars);
        //System.out.println("rfac = " + rfac);

        List<GenPolynomial<GenPolynomial<C>>> Fr = PolyUtil.<C> recursive(rfac, ppolys);
        if (logger.isInfoEnabled()) {
            logger.info("rfac = " + rfac);
            logger.info("Fr   = " + Fr);
        }

        if (true) {
            rbb = new GroebnerBasePseudoRecSeq<C>(cfac);
        }
        List<GenPolynomial<GenPolynomial<C>>> Gr = rbb.GB(Fr);
        //System.out.println("\nGr = " + Gr);

        List<GenPolynomial<C>> G = PolyUtil.<C> distribute(pfac, Gr);
        //System.out.println("\nG = " + G);

        OptimizedPolynomialList<C> pgb = new OptimizedPolynomialList<C>(perm, pfac, G);
        return pgb;
    }

}
