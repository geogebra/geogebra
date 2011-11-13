/*
 * $Id: PolyUtilApp.java 3176 2010-06-07 20:25:03Z kredel $
 */

package edu.jas.application;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.RegularRingElem;
import edu.jas.structure.Product;
import edu.jas.structure.ProductRing;
import edu.jas.structure.Complex;
import edu.jas.structure.ComplexRing;
import edu.jas.structure.UnaryFunctor;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.Rational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.BigDecimal;
import edu.jas.gb.GroebnerBase;
import edu.jas.gb.GroebnerBaseSeq;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;

import edu.jas.root.ComplexRootsSturm;
import edu.jas.root.ComplexRootsAbstract;
import edu.jas.root.RealRootsSturm;
import edu.jas.root.RealRootAbstract;
import edu.jas.root.RealAlgebraicNumber;
import edu.jas.root.RealAlgebraicRing;
import edu.jas.root.RootFactory;

import edu.jas.util.ListUtil;


/**
 * Polynomial utilities for applications,  
 * for example conversion ExpVector to Product or zero dimensional ideal root computation.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public class PolyUtilApp<C extends RingElem<C> > {

    private static final Logger logger = Logger.getLogger(PolyUtilApp.class);
    private static boolean debug = logger.isDebugEnabled();


    /**
     * Product representation.
     * @param <C> coefficient type.
     * @param pfac product polynomial ring factory.
     * @param c coefficient to be used.
     * @param e exponent vector.
     * @return Product represenation of c X^e in the ring pfac.
     */
    public static <C extends RingElem<C>> 
                             Product<GenPolynomial<C>> 
                             toProduct( ProductRing<GenPolynomial<C>> pfac, 
                                        C c, ExpVector e ) {
        SortedMap<Integer,GenPolynomial<C>> elem 
            = new TreeMap<Integer,GenPolynomial<C>>();
        for ( int i = 0; i < e.length(); i++ ) {
            RingFactory<GenPolynomial<C>> rfac = pfac.getFactory(i);
            GenPolynomialRing<C> fac = (GenPolynomialRing<C>) rfac;
            //GenPolynomialRing<C> cfac = fac.ring;
            long a = e.getVal(i);
            GenPolynomial<C> u;
            if ( a == 0 ) {
                u = fac.getONE();
            } else {
                u = fac.univariate(0,a);
            }
            u = u.multiply(c);
            elem.put( i, u );
        }
        return new Product<GenPolynomial<C>>( pfac, elem );
    }


    /**
     * Product representation.
     * @param <C> coefficient type.
     * @param pfac product polynomial ring factory.
     * @param A polynomial.
     * @return Product represenation of the terms of A in the ring pfac.
     */
    public static <C extends RingElem<C>> 
                             Product<GenPolynomial<C>> 
                             toProduct( ProductRing<GenPolynomial<C>> pfac, 
                                        GenPolynomial<C> A ) {
        Product<GenPolynomial<C>> P = pfac.getZERO();
        if ( A == null || A.isZERO() ) {
            return P;
        }
        for ( Map.Entry<ExpVector,C> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            Product<GenPolynomial<C>> p = toProduct(pfac,a,e);
            P = P.sum( p ); 
        }
        return P;
    }


    /**
     * Product representation.
     * @param <C> coefficient type.
     * @param pfac product ring factory.
     * @param c coefficient to be represented.
     * @return Product represenation of c in the ring pfac.
     */
    public static <C extends GcdRingElem<C>>
                             Product<C> 
                             toProductGen( ProductRing<C> pfac, C c) {

        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        for ( int i = 0; i < pfac.length(); i++ ) {
            RingFactory<C> rfac = pfac.getFactory(i);
            C u = rfac.copy( c );
            if ( u != null && !u.isZERO() ) {
                elem.put( i, u );
            }
        }
        return new Product<C>( pfac, elem );
    }


    /**
     * Product representation.
     * @param <C> coefficient type.
     * @param pfac polynomial ring factory.
     * @param A polynomial to be represented.
     * @return Product represenation of A in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
                             GenPolynomial<Product<C>> 
                             toProductGen( GenPolynomialRing<Product<C>> pfac, 
                                           GenPolynomial<C> A) {

        GenPolynomial<Product<C>> P = pfac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
            return P;
        }
        RingFactory<Product<C>> rpfac = pfac.coFac;
        ProductRing<C> rfac = (ProductRing<C>) rpfac;
        for ( Map.Entry<ExpVector,C> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            Product<C> p = toProductGen(rfac,a);
            if ( p != null && !p.isZERO() ) {
                P.doPutToMap( e, p );
            }        
        }
        return P;
    }


    /**
     * Product representation.
     * @param <C> coefficient type.
     * @param pfac polynomial ring factory.
     * @param L list of polynomials to be represented.
     * @return Product represenation of L in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
                             List<GenPolynomial<Product<C>>> 
                             toProductGen( GenPolynomialRing<Product<C>> pfac, 
                                           List<GenPolynomial<C>> L) {

        List<GenPolynomial<Product<C>>> 
            list = new ArrayList<GenPolynomial<Product<C>>>();
        if ( L == null || L.size() == 0 ) {
            return list;
        }
        for ( GenPolynomial<C> a : L ) {
            GenPolynomial<Product<C>> b = toProductGen( pfac, a );
            list.add( b );
        }
        return list;
    }



    /**
     * Product representation.
     * @param pfac product ring factory.
     * @param c coefficient to be represented.
     * @return Product represenation of c in the ring pfac.
     */
    public static 
        Product<ModInteger> toProduct( ProductRing<ModInteger> pfac, 
                                       BigInteger c) {

        SortedMap<Integer,ModInteger> elem = new TreeMap<Integer,ModInteger>();
        for ( int i = 0; i < pfac.length(); i++ ) {
            RingFactory<ModInteger> rfac = pfac.getFactory(i);
            ModIntegerRing fac = (ModIntegerRing) rfac;
            ModInteger u = fac.fromInteger( c.getVal() );
            if ( u != null && !u.isZERO() ) {
                elem.put( i, u );
            }
        }
        return new Product<ModInteger>( pfac, elem );
    }


    /**
     * Product representation.
     * @param pfac polynomial ring factory.
     * @param A polynomial to be represented.
     * @return Product represenation of A in the polynomial ring pfac.
     */
    public static 
        GenPolynomial<Product<ModInteger>> 
        toProduct( GenPolynomialRing<Product<ModInteger>> pfac, 
                   GenPolynomial<BigInteger> A) {

        GenPolynomial<Product<ModInteger>> P = pfac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
            return P;
        }
        RingFactory<Product<ModInteger>> rpfac = pfac.coFac;
        ProductRing<ModInteger> fac = (ProductRing<ModInteger>)rpfac;
        for ( Map.Entry<ExpVector,BigInteger> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            BigInteger a = y.getValue();
            Product<ModInteger> p = toProduct(fac,a);
            if ( p != null && !p.isZERO() ) {
                P.doPutToMap( e, p );
            }        
        }
        return P;
    }


    /**
     * Product representation.
     * @param pfac polynomial ring factory.
     * @param L list of polynomials to be represented.
     * @return Product represenation of L in the polynomial ring pfac.
     */
    public static 
        List<GenPolynomial<Product<ModInteger>>> 
        toProduct( GenPolynomialRing<Product<ModInteger>> pfac, 
                   List<GenPolynomial<BigInteger>> L) {

        List<GenPolynomial<Product<ModInteger>>> 
            list = new ArrayList<GenPolynomial<Product<ModInteger>>>();
        if ( L == null || L.size() == 0 ) {
            return list;
        }
        for ( GenPolynomial<BigInteger> a : L ) {
            GenPolynomial<Product<ModInteger>> b = toProduct( pfac, a );
            list.add( b );
        }
        return list;
    }


    /**
     * Product representation.
     * @param <C> coefficient type.
     * @param pfac polynomial ring factory.
     * @param L list of polynomials to be represented.
     * @return Product represenation of L in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
                             List<GenPolynomial<Product<Residue<C>>>> 
                             toProductRes( GenPolynomialRing<Product<Residue<C>>> pfac, 
                                           List<GenPolynomial<GenPolynomial<C>>> L) {

        List<GenPolynomial<Product<Residue<C>>>> 
            list = new ArrayList<GenPolynomial<Product<Residue<C>>>>();
        if ( L == null || L.size() == 0 ) {
            return list;
        }
        GenPolynomial<Product<Residue<C>>> b;
        for ( GenPolynomial<GenPolynomial<C>> a : L ) {
            b = toProductRes( pfac, a );
            list.add( b );
        }
        return list;
    }


    /**
     * Product representation.
     * @param <C> coefficient type.
     * @param pfac polynomial ring factory.
     * @param A polynomial to be represented.
     * @return Product represenation of A in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
                             GenPolynomial<Product<Residue<C>>> 
                             toProductRes( GenPolynomialRing<Product<Residue<C>>> pfac, 
                                           GenPolynomial<GenPolynomial<C>> A) {

        GenPolynomial<Product<Residue<C>>> P = pfac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
            return P;
        }
        RingFactory<Product<Residue<C>>> rpfac = pfac.coFac;
        ProductRing<Residue<C>> fac = (ProductRing<Residue<C>>)rpfac;
        Product<Residue<C>> p;
        for ( Map.Entry<ExpVector,GenPolynomial<C>> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            p = toProductRes(fac,a);
            if ( p != null && !p.isZERO() ) {
                P.doPutToMap( e, p );
            }        
        }
        return P;
    }


    /**
     * Product representation.
     * @param <C> coefficient type.
     * @param pfac product ring factory.
     * @param c coefficient to be represented.
     * @return Product represenation of c in the ring pfac.
     */
    public static <C extends GcdRingElem<C>>
                             Product<Residue<C>> 
                             toProductRes( ProductRing<Residue<C>> pfac, 
                                           GenPolynomial<C> c) {

        SortedMap<Integer,Residue<C>> elem = new TreeMap<Integer,Residue<C>>();
        for ( int i = 0; i < pfac.length(); i++ ) {
            RingFactory<Residue<C>> rfac = pfac.getFactory(i);
            ResidueRing<C> fac = (ResidueRing<C>) rfac;
            Residue<C> u = new Residue<C>( fac, c );
            //fac.fromInteger( c.getVal() );
            if ( u != null && !u.isZERO() ) {
                elem.put( i, u );
            }
        }
        return new Product<Residue<C>>( pfac, elem );
    }


    /**
     * Product residue representation.
     * @param <C> coefficient type.
     * @param CS list of ColoredSystems from comprehensive GB system.
     * @return Product residue represenation of CS.
     */
    public static <C extends GcdRingElem<C>>
                             List<GenPolynomial<Product<Residue<C>>>> 
                             toProductRes(List<ColoredSystem<C>> CS) {

        List<GenPolynomial<Product<Residue<C>>>> 
            list = new ArrayList<GenPolynomial<Product<Residue<C>>>>();
        if ( CS == null || CS.size() == 0 ) {
            return list;
        }
        GenPolynomialRing<GenPolynomial<C>> pr = null; 
        List<RingFactory<Residue<C>>> rrl 
            = new ArrayList<RingFactory<Residue<C>>>( CS.size() ); 
        for (ColoredSystem<C> cs : CS) {
            Ideal<C> id = cs.condition.zero;
            ResidueRing<C> r = new ResidueRing<C>(id);
            if ( ! rrl.contains(r) ) {
                rrl.add(r);
            }
            if ( pr == null ) {
                if ( cs.list.size() > 0 ) {
                    pr = cs.list.get(0).green.ring;
                }
            }
        }
        ProductRing<Residue<C>> pfac;
        pfac = new ProductRing<Residue<C>>(rrl);
        //System.out.println("pfac = " + pfac);
        GenPolynomialRing<Product<Residue<C>>> rf 
            = new GenPolynomialRing<Product<Residue<C>>>(pfac,pr.nvar,pr.tord,pr.getVars());
        GroebnerSystem<C> gs = new GroebnerSystem<C>( CS ); 
        List<GenPolynomial<GenPolynomial<C>>> F = gs.getCGB();
        list = PolyUtilApp.<C>toProductRes(rf, F);
        return list;
    }


    /**
     * Residue coefficient representation.
     * @param pfac polynomial ring factory.
     * @param L list of polynomials to be represented.
     * @return Represenation of L in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
                             List<GenPolynomial<Residue<C>>> 
                             toResidue( GenPolynomialRing<Residue<C>> pfac, 
                                        List<GenPolynomial<GenPolynomial<C>>> L) {
        List<GenPolynomial<Residue<C>>> 
            list = new ArrayList<GenPolynomial<Residue<C>>>();
        if ( L == null || L.size() == 0 ) {
            return list;
        }
        GenPolynomial<Residue<C>> b;
        for ( GenPolynomial<GenPolynomial<C>> a : L ) {
            b = toResidue( pfac, a );
            if ( b != null && !b.isZERO() ) {
                list.add( b );
            }
        }
        return list;
    }


    /**
     * Residue coefficient representation.
     * @param pfac polynomial ring factory.
     * @param A polynomial to be represented.
     * @return Represenation of A in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
                             GenPolynomial<Residue<C>> 
                             toResidue( GenPolynomialRing<Residue<C>> pfac, 
                                        GenPolynomial<GenPolynomial<C>> A) {
        GenPolynomial<Residue<C>> P = pfac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
            return P;
        }
        RingFactory<Residue<C>> rpfac = pfac.coFac;
        ResidueRing<C> fac = (ResidueRing<C>)rpfac;
        Residue<C> p;
        for ( Map.Entry<ExpVector,GenPolynomial<C>> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            p = new Residue<C>(fac,a);
            if ( p != null && !p.isZERO() ) {
                P.doPutToMap( e, p );
            }
        }
        return P;
    }


    /**
     * Product slice.
     * @param <C> coefficient type.
     * @param L list of polynomials with product coefficients.
     * @return Slices represenation of L.
     */
    public static <C extends GcdRingElem<C>>
                             Map<Ideal<C>,PolynomialList<GenPolynomial<C>>>
                             productSlice( PolynomialList<Product<Residue<C>>> L ) {

        Map<Ideal<C>,PolynomialList<GenPolynomial<C>>> map;
        RingFactory<Product<Residue<C>>> fpr = L.ring.coFac;
        ProductRing<Residue<C>> pr = (ProductRing<Residue<C>>)fpr;
        int s = pr.length();
        map = new TreeMap<Ideal<C>,PolynomialList<GenPolynomial<C>>>();
        List<GenPolynomial<GenPolynomial<C>>> slist;

        List<GenPolynomial<Product<Residue<C>>>> plist = L.list;
        PolynomialList<GenPolynomial<C>> spl;

        for ( int i = 0; i < s; i++ ) {
            RingFactory<Residue<C>> r = pr.getFactory( i );
            ResidueRing<C> rr = (ResidueRing<C>) r;
            Ideal<C> id = rr.ideal;
            GenPolynomialRing<C> cof = rr.ring;
            GenPolynomialRing<GenPolynomial<C>> pfc; 
            pfc = new GenPolynomialRing<GenPolynomial<C>>(cof,L.ring);
            slist = fromProduct( pfc, plist, i );
            spl = new PolynomialList<GenPolynomial<C>>(pfc,slist);
            PolynomialList<GenPolynomial<C>> d = map.get( id );
            if ( d != null ) {
                throw new RuntimeException("ideal exists twice " + id);
            }
            map.put( id, spl );
        }
        return map;
    }


    /**
     * Product slice at i.
     * @param <C> coefficient type.
     * @param L list of polynomials with product coeffients.
     * @param i index of slice.
     * @return Slice of of L at i.
     */
    public static <C extends GcdRingElem<C>>
                             PolynomialList<GenPolynomial<C>>
                             productSlice( PolynomialList<Product<Residue<C>>> L, int i ) {

        RingFactory<Product<Residue<C>>> fpr = L.ring.coFac;
        ProductRing<Residue<C>> pr = (ProductRing<Residue<C>>)fpr;
        List<GenPolynomial<GenPolynomial<C>>> slist;

        List<GenPolynomial<Product<Residue<C>>>> plist = L.list;
        PolynomialList<GenPolynomial<C>> spl;

        RingFactory<Residue<C>> r = pr.getFactory( i );
        ResidueRing<C> rr = (ResidueRing<C>) r;
        GenPolynomialRing<C> cof = rr.ring;
        GenPolynomialRing<GenPolynomial<C>> pfc; 
        pfc = new GenPolynomialRing<GenPolynomial<C>>(cof,L.ring);
        slist = fromProduct( pfc, plist, i );
        spl = new PolynomialList<GenPolynomial<C>>(pfc,slist);
        return spl;
    }


    /**
     * From product representation.
     * @param <C> coefficient type.
     * @param pfac polynomial ring factory.
     * @param L list of polynomials to be converted from product representation.
     * @param i index of product representation to be taken.
     * @return Represenation of i-slice of L in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
                             List<GenPolynomial<GenPolynomial<C>>>
                             fromProduct( GenPolynomialRing<GenPolynomial<C>> pfac,
                                          List<GenPolynomial<Product<Residue<C>>>> L,
                                          int i ) {

        List<GenPolynomial<GenPolynomial<C>>> 
            list = new ArrayList<GenPolynomial<GenPolynomial<C>>>();

        if ( L == null || L.size() == 0 ) {
            return list;
        }
        GenPolynomial<GenPolynomial<C>> b;
        for ( GenPolynomial<Product<Residue<C>>> a : L ) {
            b = fromProduct( pfac, a, i );
            if ( b != null && !b.isZERO() ) {
                b = b.abs();
                if ( ! list.contains( b ) ) {
                    list.add( b );
                }
            }
        }
        return list;
    }


    /**
     * From product representation.
     * @param <C> coefficient type.
     * @param pfac polynomial ring factory.
     * @param P polynomial to be converted from product representation.
     * @param i index of product representation to be taken.
     * @return Represenation of i-slice of P in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
                             GenPolynomial<GenPolynomial<C>>
                             fromProduct( GenPolynomialRing<GenPolynomial<C>> pfac,
                                          GenPolynomial<Product<Residue<C>>> P,
                                          int i ) {

        GenPolynomial<GenPolynomial<C>> b = pfac.getZERO().clone();
        if ( P == null || P.isZERO() ) {
            return b;
        }

        for ( Map.Entry<ExpVector,Product<Residue<C>>> y: P.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            Product<Residue<C>> a = y.getValue();
            Residue<C> r = a.get(i);
            if ( r != null && !r.isZERO() ) {
                GenPolynomial<C> p = r.val;
                if ( p != null && !p.isZERO() ) {
                    b.doPutToMap( e, p );
                }        
            }
        }
        return b;
    }


    /**
     * Product slice to String.
     * @param <C> coefficient type.
     * @param L list of polynomials with  to be represented.
     * @return Product represenation of L in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
      String
      productSliceToString( Map<Ideal<C>,PolynomialList<GenPolynomial<C>>> L ) {
        Set<GenPolynomial<GenPolynomial<C>>> sl 
            = new TreeSet<GenPolynomial<GenPolynomial<C>>>();
        PolynomialList<GenPolynomial<C>> pl = null;
        StringBuffer sb = new StringBuffer(); //"\nproductSlice ----------------- begin");
        for ( Ideal<C> id: L.keySet() ) {
            sb.append("\n\ncondition == 0:\n");
            sb.append( id.list.toScript() );
            pl = L.get( id );
            sl.addAll( pl.list );
            sb.append("\ncorresponding ideal:\n");
            sb.append( pl.toScript() );
        }
        //List<GenPolynomial<GenPolynomial<C>>> sll 
        //   = new ArrayList<GenPolynomial<GenPolynomial<C>>>( sl );
        //pl = new PolynomialList<GenPolynomial<C>>(pl.ring,sll);
        // sb.append("\nunion = " + pl.toString());
        //sb.append("\nproductSlice ------------------------- end\n");
        return sb.toString();
    }


    /**
     * Product slice to String.
     * @param <C> coefficient type.
     * @param L list of polynomials with product coefficients.
     * @return string represenation of slices of L.
     */
    public static <C extends GcdRingElem<C>>
      String productToString( PolynomialList<Product<Residue<C>>> L ) {
        Map<Ideal<C>,PolynomialList<GenPolynomial<C>>> M;
        M = productSlice( L ); 
        String s = productSliceToString( M );
        return s;
    }


    /**
     * Construct superset of complex roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal.
     * @param eps desired precision.
     * @return list of coordinates of complex roots for ideal(G)
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D>> 
      List<List<Complex<BigDecimal>>> complexRootTuples(Ideal<D> I, C eps) {
        List<GenPolynomial<D>> univs = I.constructUnivariate();
        if ( logger.isInfoEnabled() ) {
            logger.info("univs = " + univs);
        }
        return complexRoots(I,univs,eps);
    }

    /**
     * Construct superset of complex roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal.
     * @param univs list of univariate polynomials.
     * @param eps desired precision.
     * @return list of coordinates of complex roots for ideal(G)
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D>> 
      List<List<Complex<BigDecimal>>> complexRoots(Ideal<D> I, List<GenPolynomial<D>> univs, C eps) {
        List<List<Complex<BigDecimal>>> croots = new ArrayList<List<Complex<BigDecimal>>>();
        RingFactory<C> cf = (RingFactory<C>) I.list.ring.coFac;
        ComplexRing<C> cr = new ComplexRing<C>(cf);
        ComplexRootsAbstract<C> cra = new ComplexRootsSturm<C>(cr);
        List<GenPolynomial<Complex<C>>> cunivs = new ArrayList<GenPolynomial<Complex<C>>>();
        for ( GenPolynomial<D> p : univs ) {
            GenPolynomialRing<Complex<C>> pfac = new GenPolynomialRing<Complex<C>>(cr,p.ring);
            //System.out.println("pfac = " + pfac.toScript());
            GenPolynomial<Complex<C>> cp = PolyUtil.<C> toComplex(pfac,(GenPolynomial<C>) p);
            cunivs.add(cp);
            //System.out.println("cp = " + cp);
        }
        for ( int i = 0; i < I.list.ring.nvar; i++ ) {
            List<Complex<BigDecimal>> cri = cra.approximateRoots(cunivs.get(i),eps);
            //System.out.println("cri = " + cri);
            croots.add(cri);
        }
        croots = ListUtil.<Complex<BigDecimal>> tupleFromList( croots );
        return croots;
    }


    /**
     * Construct superset of complex roots for zero dimensional ideal(G).
     * @param Il list of zero dimensional ideals with univariate polynomials.
     * @param eps desired precision.
     * @return list of coordinates of complex roots for ideal(cap_i(G_i))
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D>> 
      List<List<Complex<BigDecimal>>> complexRootTuples(List<IdealWithUniv<D>> Il, C eps) {
        List<List<Complex<BigDecimal>>> croots = new ArrayList<List<Complex<BigDecimal>>>();
        for ( IdealWithUniv<D> I : Il ) {
            List<List<Complex<BigDecimal>>> cr  = complexRoots(I.ideal,I.upolys,eps); 
            croots.addAll(cr);
        }
        return croots;
    }


    /**
     * Construct superset of complex roots for zero dimensional ideal(G).
     * @param Il list of zero dimensional ideals with univariate polynomials.
     * @param eps desired precision.
     * @return list of ideals with coordinates of complex roots for ideal(cap_i(G_i))
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D>> 
      List<IdealWithComplexRoots<D>> complexRoots(List<IdealWithUniv<D>> Il, C eps) {
        List<IdealWithComplexRoots<D>> Ic = new ArrayList<IdealWithComplexRoots<D>>(Il.size());
        for ( IdealWithUniv<D> I : Il ) {
            List<List<Complex<BigDecimal>>> cr  = complexRoots(I.ideal,I.upolys,eps); 
            IdealWithComplexRoots<D> ic = new IdealWithComplexRoots<D>(I,cr);
            Ic.add(ic);
        }
        return Ic;
    }


    /**
     * Construct superset of complex roots for zero dimensional ideal(G).
     * @param G list of polynomials of a of zero dimensional ideal.
     * @param eps desired precision.
     * @return list of ideals with coordinates of complex roots for ideal(G)
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D>> 
      List<IdealWithComplexRoots<D>> complexRoots(Ideal<D> G, C eps) {
        List<IdealWithUniv<D>> Il = G.zeroDimDecomposition();
        return complexRoots(Il,eps);
    }


    /**
     * Construct superset of real roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal.
     * @param eps desired precision.
     * @return list of coordinates of real roots for ideal(G)
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D>> 
      List<List<BigDecimal>> realRootTuples(Ideal<D> I, C eps) {
        List<GenPolynomial<D>> univs = I.constructUnivariate();
        if ( logger.isInfoEnabled() ) {
            logger.info("univs = " + univs);
        }
        return realRoots(I,univs,eps);
    }


    /**
     * Construct superset of real roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal.
     * @param univs list of univariate polynomials.
     * @param eps desired precision.
     * @return list of coordinates of real roots for ideal(G)
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D>> 
      List<List<BigDecimal>> realRoots(Ideal<D> I, List<GenPolynomial<D>>univs, C eps) {
        List<List<BigDecimal>> roots = new ArrayList<List<BigDecimal>>();
        RingFactory<C> cf = (RingFactory<C>) I.list.ring.coFac;
        RealRootAbstract<C> rra = new RealRootsSturm<C>();
        for ( int i = 0; i < I.list.ring.nvar; i++ ) {
            List<BigDecimal> rri = rra.approximateRoots((GenPolynomial<C>)univs.get(i),eps);
            //System.out.println("rri = " + rri);
            roots.add(rri);
        }
        //System.out.println("roots-1 = " + roots);
        roots = ListUtil.<BigDecimal> tupleFromList( roots );
        //System.out.println("roots-2 = " + roots);
        return roots;
    }


    /**
     * Construct superset of real roots for zero dimensional ideal(G).
     * @param Il list of zero dimensional ideals with univariate polynomials.
     * @param eps desired precision.
     * @return list of coordinates of real roots for ideal(cap_i(G_i))
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D>> 
      List<List<BigDecimal>> realRootTuples(List<IdealWithUniv<D>> Il, C eps) {
        List<List<BigDecimal>> rroots = new ArrayList<List<BigDecimal>>();
        for ( IdealWithUniv<D> I : Il ) {
            List<List<BigDecimal>> rr  = realRoots(I.ideal,I.upolys,eps); 
            rroots.addAll(rr);
        }
        return rroots;
    }


    /**
     * Construct superset of real roots for zero dimensional ideal(G).
     * @param Il list of zero dimensional ideals with univariate polynomials.
     * @param eps desired precision.
     * @return list of ideals with coordinates of real roots for ideal(cap_i(G_i))
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D>> 
      List<IdealWithRealRoots<D>> realRoots(List<IdealWithUniv<D>> Il, C eps) {
        List<IdealWithRealRoots<D>> Ir = new ArrayList<IdealWithRealRoots<D>>(Il.size());
        for ( IdealWithUniv<D> I : Il ) {
            List<List<BigDecimal>> rr  = realRoots(I.ideal,I.upolys,eps); 
            IdealWithRealRoots<D> ir = new IdealWithRealRoots<D>(I,rr);
            Ir.add(ir);
        }
        return Ir;
    }


    /**
     * Construct superset of real roots for zero dimensional ideal(G).
     * @param G list of polynomials of a of zero dimensional ideal.
     * @param eps desired precision.
     * @return list of ideals with coordinates of real roots for ideal(G)
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D>> 
      List<IdealWithRealRoots<D>> realRoots(Ideal<D> G, C eps) {
        List<IdealWithUniv<D>> Il = G.zeroDimDecomposition();
        return realRoots(Il,eps);
    }


    /**
     * Test for real roots of zero dimensional ideal(L).
     * @param L list of polynomials.
     * @param roots list of real roots for ideal(G).
     * @param eps desired precision.
     * @return true if root is a list of coordinates of real roots for ideal(L)
     */
    public static 
      boolean isRealRoots(List<GenPolynomial<BigDecimal>> L, List<List<BigDecimal>> roots, BigDecimal eps) {
        if ( L == null || L.size() == 0 ) {
            return true;
        }
        // polynomials with decimal coefficients
        BigDecimal dc = BigDecimal.ONE;
        GenPolynomialRing<BigDecimal> dfac = L.get(0).ring;
        //System.out.println("dfac = " + dfac);
        for ( GenPolynomial<BigDecimal> dp : L ) {
            //System.out.println("dp = " + dp);
            for ( List<BigDecimal> r : roots ) {
                //System.out.println("r = " + r);
                BigDecimal ev = PolyUtil.<BigDecimal> evaluateAll(dc,dfac,dp,r);
                if ( ev.abs().compareTo(eps) > 0 ) {
                    System.out.println("ev = " + ev);
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Test for complex roots of zero dimensional ideal(L).
     * @param L list of polynomials.
     * @param roots list of real roots for ideal(G).
     * @param eps desired precision.
     * @return true if root is a list of coordinates of complex roots for ideal(L)
     */
    public static 
      boolean isComplexRoots(List<GenPolynomial<Complex<BigDecimal>>> L, 
                             List<List<Complex<BigDecimal>>> roots, BigDecimal eps) {
        if ( L == null || L.size() == 0 ) {
            return true;
        }
        // polynomials with decimal coefficients
        BigDecimal dc = BigDecimal.ONE;
        ComplexRing<BigDecimal> dcc = new ComplexRing<BigDecimal>(dc);
        GenPolynomialRing<Complex<BigDecimal>> dfac = L.get(0).ring;
        //System.out.println("dfac = " + dfac);
        for ( GenPolynomial<Complex<BigDecimal>> dp : L ) {
            //System.out.println("dp = " + dp);
            for ( List<Complex<BigDecimal>> r : roots ) {
                //System.out.println("r = " + r);
                Complex<BigDecimal> ev = PolyUtil.<Complex<BigDecimal>> evaluateAll(dcc,dfac,dp,r);
                if ( ev.norm().getRe().compareTo(eps) > 0 ) {
                    System.out.println("ev = " + ev);
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Construct real roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal with univariate irreducible polynomials.
     * @return list of real algebraic roots for ideal(G)
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D> & Rational> 
      IdealWithRealAlgebraicRoots<C,D> realAlgebraicRoots(IdealWithUniv<D> I) {
        List<List<RealAlgebraicNumber<D>>> ran = new ArrayList<List<RealAlgebraicNumber<D>>>();
        if ( I == null || I.ideal == null || I.ideal.isZERO() || I.upolys == null || I.upolys.size() == 0 ) {
            return new IdealWithRealAlgebraicRoots<C,D>(I,ran);
        }
        GenPolynomialRing<D> fac = I.ideal.list.ring;
        // case i == 0:
        GenPolynomial<D> p0 = I.upolys.get(0);
        GenPolynomial<D> p0p = selectWithVariable(I.ideal.list.list, fac.nvar-1 );
        if ( p0p == null ) {
            throw new RuntimeException("no polynomial found in " + (fac.nvar-1) + " of  " + I.ideal);
        }
        //System.out.println("p0  = " + p0);
        if ( logger.isInfoEnabled() ) {
            logger.info("p0p = " + p0p);
        }
        int[] dep0 = p0p.degreeVector().dependencyOnVariables();
        //System.out.println("dep0 = " + Arrays.toString(dep0));
        if ( dep0.length != 1 ) {
            throw new RuntimeException("wrong number of variables " + Arrays.toString(dep0));
        }
        List<RealAlgebraicNumber<D>> rra = RootFactory.<D> realAlgebraicNumbersIrred(p0);
        for ( RealAlgebraicNumber<D> rr : rra ) {
            List<RealAlgebraicNumber<D>> rl = new ArrayList<RealAlgebraicNumber<D>>();
            rl.add(rr);
            ran.add(rl);
        }
        // case i > 0:
        for ( int i = 1; i < I.upolys.size(); i++ ) {
             List<List<RealAlgebraicNumber<D>>> rn = new ArrayList<List<RealAlgebraicNumber<D>>>();
             GenPolynomial<D> pi = I.upolys.get(i);
             GenPolynomial<D> pip = selectWithVariable(I.ideal.list.list, fac.nvar-1-i );
             if ( pip == null ) {
                 throw new RuntimeException("no polynomial found in " + (fac.nvar-1-i) + " of  " + I.ideal);
             }
             //System.out.println("i   = " + i);
             //System.out.println("pi  = " + pi);
             if ( logger.isInfoEnabled() ) {
                 logger.info("pip = " + pip);
             }
             int[] depi = pip.degreeVector().dependencyOnVariables();
             //System.out.println("depi = " + Arrays.toString(depi));
             if ( depi.length < 1 || depi.length > 2 ) {
                 throw new RuntimeException("wrong number of variables " + Arrays.toString(depi));
             }
             rra = RootFactory.<D> realAlgebraicNumbersIrred(pi);
             if ( depi.length == 1 ) {
                 // all combinations are roots of the ideal I
                 for ( RealAlgebraicNumber<D> rr : rra ) {
                     //System.out.println("rr.ring = " + rr.ring);
                     for ( List<RealAlgebraicNumber<D>> rx : ran ) {
                         //System.out.println("rx = " + rx);
                         List<RealAlgebraicNumber<D>> ry = new ArrayList<RealAlgebraicNumber<D>>();
                         ry.addAll(rx);
                         ry.add(rr);
                         rn.add(ry);
                     }
                 }
             } else { // depi.length == 2
                 // select roots of the ideal I
                 GenPolynomial<D> pip2 = removeUnusedUpperVariables(pip);
                 //System.out.println("pip2 = " + pip2.ring);
                 GenPolynomialRing<D> ufac = ufac = pip2.ring.contract(1);
                 GenPolynomialRing<GenPolynomial<D>> rfac = new GenPolynomialRing<GenPolynomial<D>>(ufac,1);
                 GenPolynomial<GenPolynomial<D>> pip2r = PolyUtil.<D> recursive(rfac,pip2);
                 int ix = fac.nvar - 1 - depi[ depi.length-1 ];
                 //System.out.println("ix = " + ix);
                 for ( RealAlgebraicNumber<D> rr : rra ) {
                     //System.out.println("rr.ring = " + rr.ring);
                     GenPolynomial<D> pip2el = PolyUtil.<D> evaluateMain(ufac,pip2r,rr.ring.getRoot().left);
                     GenPolynomial<D> pip2er = PolyUtil.<D> evaluateMain(ufac,pip2r,rr.ring.getRoot().right);
                     GenPolynomialRing<D> upfac = I.upolys.get(ix).ring;
                     GenPolynomial<D> pip2elc = convert(upfac,pip2el);
                     GenPolynomial<D> pip2erc = convert(upfac,pip2er);
                     //System.out.println("pip2elc = " + pip2elc);
                     //System.out.println("pip2erc = " + pip2erc);
                     for ( List<RealAlgebraicNumber<D>> rx : ran ) {
                         //System.out.println("rx = " + rx);
                         RealAlgebraicRing<D> rar = rx.get(ix).ring;
                         RealAlgebraicNumber<D> rel = new RealAlgebraicNumber<D>(rar,pip2elc);
                         RealAlgebraicNumber<D> rer = new RealAlgebraicNumber<D>(rar,pip2erc);
                         int sl = rel.signum();
                         int sr = rer.signum();
                         //System.out.println("sl = " + sl + ", sr = " + sr + ", sl*sr = " + (sl*sr));
                         if ( sl*sr <= 0 ) {
                             List<RealAlgebraicNumber<D>> ry = new ArrayList<RealAlgebraicNumber<D>>();
                             ry.addAll(rx);
                             ry.add(rr);
                             rn.add(ry);
                         }
                     }
                 }
             }
             ran = rn;
        }
        IdealWithRealAlgebraicRoots<C,D> Ir = new IdealWithRealAlgebraicRoots<C,D>(I,ran);
        return Ir;
    }


    /**
     * Construct real roots for zero dimensional ideal(G).
     * @param I list of zero dimensional ideal with univariate irreducible polynomials.
     * @return list of real algebraic roots for all ideal(I_i)
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D> & Rational> 
      List<IdealWithRealAlgebraicRoots<C,D>> realAlgebraicRoots(List<IdealWithUniv<D>> I) {
        List<IdealWithRealAlgebraicRoots<C,D>> lir = new ArrayList<IdealWithRealAlgebraicRoots<C,D>>(I.size());
        for ( IdealWithUniv<D> iu : I ) {
            IdealWithRealAlgebraicRoots<C,D> iur = PolyUtilApp.<C,D> realAlgebraicRoots(iu);
            //System.out.println("iur = " + iur);
            lir.add( iur );
        }
        return lir;
    }


    /**
     * Select polynomial with univariate leading term in variable i.
     * @param i variable index.
     * @return polynomial with head term in variable i
     */
    public static <C extends RingElem<C>> 
      GenPolynomial<C> selectWithVariable(List<GenPolynomial<C>> P, int i) {
        for ( GenPolynomial<C> p : P ) {
            int[] dep = p.leadingExpVector().dependencyOnVariables();
            if ( dep.length == 1 && dep[0] == i ) {
                return p;
            }
        }
        return null; // not found       
    }    


    /**
     * Remove all upper variables, which do not occur in polynomial.
     * @param p polynomial.
     * @return polynomial with removed variables
     */
    public static <C extends RingElem<C>> 
      GenPolynomial<C> removeUnusedUpperVariables(GenPolynomial<C> p) {
        int[] dep = p.degreeVector().dependencyOnVariables();
        GenPolynomialRing<C> fac = p.ring;
        if ( fac.nvar == dep.length ) { // all variables appear
            return p; 
        }
        int l = dep[0];
        int r = dep[dep.length-1];
        int n = l;
        GenPolynomialRing<C> facr = fac.contract(n);
        Map<ExpVector,GenPolynomial<C>> mpr = p.contract(facr);
        if ( mpr.size() != 1 ) {
            throw new RuntimeException("this should not happen " + mpr);
        }
        GenPolynomial<C> pr = mpr.values().iterator().next();
        n = fac.nvar-1-r;
        if ( n == 0 ) {
            return pr;
        } // else case not implemented
        return pr;
    }    


    /**
     * Convert to a polynomial in given ring.
     * @param fac result polynomial ring.
     * @param p polynomial.
     * @return polynomial in ring fac
     * <b>Note: </b> if p can not be represented in fac then the results are unpredictable.
     */
    public static <C extends RingElem<C>> 
      GenPolynomial<C> convert(GenPolynomialRing<C> fac, GenPolynomial<C> p) {
        GenPolynomial<C> q = fac.parse( p.toString() );
        return q;
    }


    /**
     * Construct exact set of real roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal.
     * @return list of coordinates of real roots for ideal(G)
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D> & Rational> 
      List<IdealWithRealAlgebraicRoots<C,D>> realAlgebraicRoots(Ideal<D> I) {
        List<IdealWithUniv<D>> Ir = I.zeroDimRootDecomposition();
        //System.out.println("Ir = " + Ir);
        List<IdealWithRealAlgebraicRoots<C,D>> roots = PolyUtilApp.<C,D> realAlgebraicRoots(Ir);
        return roots;
    }


    /**
     * Construct primitive element for double field extension.
     * @param a algebraic number ring with squarefree monic minimal polynomial
     * @param b algebraic number ring with squarefree monic minimal polynomial
     * @return primitive element container with algebraic number ring c, with Q(c) = Q(a,b)
     */
    public static <C extends GcdRingElem<C>> 
      PrimitiveElement<C> primitiveElement(AlgebraicNumberRing<C> a, AlgebraicNumberRing<C> b) {
        GenPolynomial<C> ap = a.modul;
        GenPolynomial<C> bp = b.modul;

        // setup bivariate polynomial ring
        String[] cv = new String[2];
        cv[0] = ap.ring.getVars()[0];
        cv[1] = bp.ring.getVars()[0];
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        GenPolynomialRing<C> cfac = new GenPolynomialRing<C>(ap.ring.coFac,2,to,cv);
        GenPolynomial<C> as = ap.extendUnivariate(cfac,0);
        GenPolynomial<C> bs = bp.extendUnivariate(cfac,1);
        List<GenPolynomial<C>> L = new ArrayList<GenPolynomial<C>>(2);
        L.add(as);
        L.add(bs);
        List<GenPolynomial<C>> Op = new ArrayList<GenPolynomial<C>>();

        Ideal<C> id = new Ideal<C>(cfac,L);
        //System.out.println("id = " + id);
        IdealWithUniv<C> iu = id.normalPositionFor(0,1,Op);
        //System.out.println("iu = " + iu);

        // extract result polynomials
        List<GenPolynomial<C>> Np = iu.ideal.getList();
        as = Np.get(1); // take care
        bs = Np.get(0); // take care
        GenPolynomial<C> cs = Np.get(2);
        //System.out.println("as = " + as);
        //System.out.println("bs = " + bs);
        //System.out.println("cs = " + cs);
        String[] ev = new String[] { cs.ring.getVars()[0] };
        GenPolynomialRing<C> efac = new GenPolynomialRing<C>(ap.ring.coFac,1,to,ev);
        //System.out.println("efac = " + efac);
        cs = cs.contractCoeff(efac);
        //System.out.println("cs = " + cs);
        as = as.reductum().contractCoeff(efac);
	as = as.negate();
        //System.out.println("as = " + as);
        bs = bs.reductum().contractCoeff(efac);
	bs = bs.negate();
        //System.out.println("bs = " + bs);
        AlgebraicNumberRing<C> c = new AlgebraicNumberRing<C>(cs);
        AlgebraicNumber<C> ab = new AlgebraicNumber<C>(c,as);  
        AlgebraicNumber<C> bb = new AlgebraicNumber<C>(c,bs);  
        PrimitiveElement<C> pe = new PrimitiveElement<C>(c,ab,bb,a,b);
        if ( logger.isInfoEnabled() ) {
            logger.info("primitive element = " + c);
        }
        return pe;
    }


    /**
     * Convert to primitive element ring.
     * @param cfac primitive element ring.
     * @param A algebraic number representing the generating element of a in the new ring.
     * @param a algebraic number to convert.
     * @return a converted to the primitive element ring
     */
    public static <C extends GcdRingElem<C>> 
      AlgebraicNumber<C> convertToPrimitiveElem(AlgebraicNumberRing<C> cfac, 
                                                AlgebraicNumber<C> A, AlgebraicNumber<C> a) {
        GenPolynomialRing<C> aufac = a.ring.ring;
        GenPolynomialRing<AlgebraicNumber<C>> ar = new GenPolynomialRing<AlgebraicNumber<C>>(cfac,aufac);
        GenPolynomial<AlgebraicNumber<C>> aps = PolyUtil.<C> convertToAlgebraicCoefficients(ar,a.val);
        AlgebraicNumber<C> ac = PolyUtil.<AlgebraicNumber<C>> evaluateMain(cfac,aps,A);
	return ac;
    }


    /**
     * Convert coefficients to primitive element ring.
     * @param cfac primitive element ring.
     * @param A algebraic number representing the generating element of a in the new ring.
     * @param a polynomial with coefficients algebraic number to convert.
     * @return a with coefficients converted to the primitive element ring
     */
    public static <C extends GcdRingElem<C>> 
      GenPolynomial<AlgebraicNumber<C>> convertToPrimitiveElem(AlgebraicNumberRing<C> cfac, 
                                        AlgebraicNumber<C> A, GenPolynomial<AlgebraicNumber<C>> a) {
        GenPolynomialRing<AlgebraicNumber<C>> cr = new GenPolynomialRing<AlgebraicNumber<C>>(cfac,a.ring);
        return PolyUtil.<AlgebraicNumber<C>,AlgebraicNumber<C>> map(cr,a,new CoeffConvertAlg<C>(cfac,A) );
    }


    /**
     * Convert to primitive element ring.
     * @param cfac primitive element ring.
     * @param A algebraic number representing the generating element of a in the new ring.
     * @param a recursive algebraic number to convert.
     * @return a converted to the primitive element ring
     */
    public static <C extends GcdRingElem<C>> 
      AlgebraicNumber<C> convertToPrimitiveElem(AlgebraicNumberRing<C> cfac, 
                         AlgebraicNumber<C> A, AlgebraicNumber<C> B, AlgebraicNumber<AlgebraicNumber<C>> a) {
        GenPolynomial<AlgebraicNumber<C>> aps = PolyUtilApp.<C> convertToPrimitiveElem(cfac,A,a.val);
        AlgebraicNumber<C> ac = PolyUtil.<AlgebraicNumber<C>> evaluateMain(cfac,aps,B);
	return ac;
    }


    /**
     * Construct primitive element for double field extension.
     * @param b algebraic number ring with squarefree monic minimal polynomial over Q(a)
     * @return primitive element container with algebraic number ring c, with Q(c) = Q(a)(b)
     */
    public static <C extends GcdRingElem<C>> 
      PrimitiveElement<C> primitiveElement(AlgebraicNumberRing<AlgebraicNumber<C>> b) {
        GenPolynomial<AlgebraicNumber<C>> bp = b.modul;
        AlgebraicNumberRing<C> a = (AlgebraicNumberRing<C>) b.ring.coFac;
        GenPolynomial<C> ap = a.modul;
        //System.out.println("ap = " + ap);
        //System.out.println("bp = " + bp);

        // setup bivariate polynomial ring
        String[] cv = new String[2];
        cv[0] = ap.ring.getVars()[0];
        cv[1] = bp.ring.getVars()[0];
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        GenPolynomialRing<C> cfac = new GenPolynomialRing<C>(ap.ring.coFac,2,to,cv);
        GenPolynomialRing<GenPolynomial<C>> rfac 
           = new GenPolynomialRing<GenPolynomial<C>>(a.ring,1,bp.ring.getVars());
        GenPolynomial<C> as = ap.extendUnivariate(cfac,0);
        GenPolynomial<GenPolynomial<C>> bss = PolyUtil.<C> fromAlgebraicCoefficients(rfac,bp);
        GenPolynomial<C> bs = PolyUtil.<C> distribute(cfac,bss);
        //System.out.println("as = " + as);
        //System.out.println("bs = " + bs);
        List<GenPolynomial<C>> L = new ArrayList<GenPolynomial<C>>(2);
        L.add(as);
        L.add(bs);
        List<GenPolynomial<C>> Op = new ArrayList<GenPolynomial<C>>();

        Ideal<C> id = new Ideal<C>(cfac,L);
        //System.out.println("id = " + id);
        IdealWithUniv<C> iu = id.normalPositionFor(0,1,Op);
        //System.out.println("iu = " + iu);

        // extract result polynomials
        List<GenPolynomial<C>> Np = iu.ideal.getList();
        as = Np.get(1);
        bs = Np.get(0);
        GenPolynomial<C> cs = Np.get(2);
        //System.out.println("as = " + as);
        //System.out.println("bs = " + bs);
        //System.out.println("cs = " + cs);
        String[] ev = new String[] { cs.ring.getVars()[0] };
        GenPolynomialRing<C> efac = new GenPolynomialRing<C>(ap.ring.coFac,1,to,ev);
        // System.out.println("efac = " + efac);
        cs = cs.contractCoeff(efac);
        // System.out.println("cs = " + cs);
        as = as.reductum().contractCoeff(efac);
	as = as.negate();
        // System.out.println("as = " + as);
        bs = bs.reductum().contractCoeff(efac);
	bs = bs.negate();
        //System.out.println("bs = " + bs);
        AlgebraicNumberRing<C> c = new AlgebraicNumberRing<C>(cs);
        AlgebraicNumber<C> ab = new AlgebraicNumber<C>(c,as);  
        AlgebraicNumber<C> bb = new AlgebraicNumber<C>(c,bs);  
        PrimitiveElement<C> pe = new PrimitiveElement<C>(c,ab,bb); // missing ,a,b);
        if ( logger.isInfoEnabled() ) {
            logger.info("primitive element = " + pe);
        }
        return pe;
    }


    /**
     * Convert to primitive element ring.
     * @param cfac primitive element ring.
     * @param A algebraic number representing the generating element of a in the new ring.
     * @param a polynomial with recursive algebraic number coefficients to convert.
     * @return a converted to the primitive element ring
     */
    public static <C extends GcdRingElem<C>> 
      GenPolynomial<AlgebraicNumber<C>> convertToPrimitiveElem(AlgebraicNumberRing<C> cfac, 
                                                 AlgebraicNumber<C> A, AlgebraicNumber<C> B, 
                                                 GenPolynomial<AlgebraicNumber<AlgebraicNumber<C>>> a) {
        GenPolynomialRing<AlgebraicNumber<C>> cr = new GenPolynomialRing<AlgebraicNumber<C>>(cfac,a.ring);
        return PolyUtil.<AlgebraicNumber<AlgebraicNumber<C>>,AlgebraicNumber<C>> 
                         map(cr,a, new CoeffRecConvertAlg<C>(cfac,A,B) );
    }

}


/**
 * Coefficient to convert algebriac functor.
 */
class CoeffConvertAlg<C extends GcdRingElem<C>> 
                     implements UnaryFunctor<AlgebraicNumber<C>,AlgebraicNumber<C>> {

    final protected AlgebraicNumberRing<C> afac;
    final protected AlgebraicNumber<C> A;

    public CoeffConvertAlg(AlgebraicNumberRing<C> fac, AlgebraicNumber<C> a) {
        if ( fac == null || a == null) {
            throw new IllegalArgumentException("fac and a must not be null");
        }
        afac = fac;
        A = a;
    }

    public AlgebraicNumber<C> eval(AlgebraicNumber<C> c) {
        if ( c == null ) {
            return afac.getZERO();
        } else {
            return PolyUtilApp.<C> convertToPrimitiveElem(afac,A,c);
        }
    }
}


/**
 * Coefficient recursive to convert algebriac functor.
 */
class CoeffRecConvertAlg<C extends GcdRingElem<C>> 
                     implements UnaryFunctor<AlgebraicNumber<AlgebraicNumber<C>>,AlgebraicNumber<C>> {

    final protected AlgebraicNumberRing<C> afac;
    final protected AlgebraicNumber<C> A;
    final protected AlgebraicNumber<C> B;

    public CoeffRecConvertAlg(AlgebraicNumberRing<C> fac, AlgebraicNumber<C> a, AlgebraicNumber<C> b) {
        if ( fac == null || a == null || b == null) {
            throw new IllegalArgumentException("fac, a and b must not be null");
        }
        afac = fac;
        A = a;
        B = b;
    }

    public AlgebraicNumber<C> eval(AlgebraicNumber<AlgebraicNumber<C>> c) {
        if ( c == null ) {
            return afac.getZERO();
        } else {
            return PolyUtilApp.<C> convertToPrimitiveElem(afac,A,B,c);
        }
    }
}
