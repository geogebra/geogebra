/*
 * $Id: TermOrderOptimization.java 3210 2010-07-05 12:25:27Z kredel $
 */

package edu.jas.poly;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

import edu.jas.arith.BigInteger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;

import edu.jas.vector.BasicLinAlg;


/**
 * Term order optimization.
 * See mas10/maspoly/DIPTOO.m{di}.
 * @author Heinz Kredel
 */

public class TermOrderOptimization {


    private static final Logger logger = Logger.getLogger(TermOrderOptimization.class);
    //private static boolean debug = logger.isDebugEnabled();


    /**
     * Degree matrix.
     * @param A polynomial to be considered.
     * @return degree matrix.
     */
    public static <C extends RingElem<C>> 
        List<GenPolynomial<BigInteger>> 
        degreeMatrix( GenPolynomial<C> A ) {

        List<GenPolynomial<BigInteger>> dem = null;
        if ( A == null ) {
           return dem;
        }

        BigInteger cfac = new BigInteger(); 
        GenPolynomialRing<BigInteger> ufac 
            = new GenPolynomialRing<BigInteger>(cfac,1);

        int nvar = A.numberOfVariables();
        dem = new ArrayList<GenPolynomial<BigInteger>>( nvar );

        for ( int i = 0; i < nvar; i++ ) {
            dem.add( ufac.getZERO() );
        }
        if ( A.isZERO() ) {
           return dem;
        }

        for ( ExpVector e: A.getMap().keySet() ) {
            dem = expVectorAdd(dem, e );
        }
        return dem;
    }


    /**
     * Degree matrix exponent vector add.
     * @param dm degree matrix.
     * @param e exponent vector.
     * @return degree matrix + e.
     */
    public static 
       List<GenPolynomial<BigInteger>> 
       expVectorAdd(List<GenPolynomial<BigInteger>> dm, ExpVector e) {
       for ( int i = 0; i < dm.size() && i < e.length(); i++ ) {
           GenPolynomial<BigInteger> p = dm.get(i);
           long u = e.getVal(i);
           ExpVector f = ExpVector.create(1,0,u);
           p = p.sum( p.ring.getONECoefficient(), f );
           dm.set(i,p);
       }
       return dm;
    }


    /**
     * Degree matrix of coefficient polynomials.
     * @param A polynomial to be considered.
     * @return degree matrix for the coeficients.
     */
    public static <C extends RingElem<C>> 
        List<GenPolynomial<BigInteger>> 
        degreeMatrixOfCoefficients( GenPolynomial<GenPolynomial<C>> A ) {
        if ( A == null ) {
           throw new IllegalArgumentException("polynomial must not be null");
        }
        return degreeMatrix( A.getMap().values() );
    }


    /**
     * Degree matrix.
     * @param L list of polynomial to be considered.
     * @return degree matrix.
     */
    public static <C extends RingElem<C>> 
       List<GenPolynomial<BigInteger>> 
       degreeMatrix( Collection<GenPolynomial<C>> L ) {
       if ( L == null ) {
          throw new IllegalArgumentException("list must be non null");
       }
       BasicLinAlg<BigInteger> bla 
           = new BasicLinAlg<BigInteger>();
       List<GenPolynomial<BigInteger>> dem = null;
       for ( GenPolynomial<C> p : L ) {
           List<GenPolynomial<BigInteger>> dm = degreeMatrix( p );
           if ( dem == null ) {
              dem = dm;
           } else {
              dem = bla.vectorAdd( dem, dm );
           }
       }
       return dem;
    }


    /**
     * Degree matrix of coefficient polynomials.
     * @param L list of polynomial to be considered.
     * @return degree matrix for the coeficients.
     */
    public static <C extends RingElem<C>> 
        List<GenPolynomial<BigInteger>> 
        degreeMatrixOfCoefficients( Collection<GenPolynomial<GenPolynomial<C>>> L ) {
        if ( L == null ) {
           throw new IllegalArgumentException("list must not be null");
        }
        BasicLinAlg<BigInteger> bla 
            = new BasicLinAlg<BigInteger>();
        List<GenPolynomial<BigInteger>> dem = null;
        for ( GenPolynomial<GenPolynomial<C>> p : L ) {
            List<GenPolynomial<BigInteger>> dm = degreeMatrixOfCoefficients( p );
            if ( dem == null ) {
               dem = dm;
            } else {
               dem = bla.vectorAdd( dem, dm );
            }
        }
        return dem;
    }


    /**
     * Optimal permutation for the Degree matrix.
     * @param D degree matrix.
     * @return optimal permutation for D.
     */
    public static 
       List<Integer> 
       optimalPermutation( List<GenPolynomial<BigInteger>> D ) {
       if ( D == null ) {
          throw new IllegalArgumentException("list must be non null");
       }
       List<Integer> P = new ArrayList<Integer>( D.size() ); 
       if ( D.size() == 0 ) {
          return P;
       }
       if ( D.size() == 1 ) {
          P.add(0);
          return P;
       }
       SortedMap< GenPolynomial<BigInteger>, List<Integer> > map 
           = new TreeMap<GenPolynomial<BigInteger>,List<Integer>>(); 
       int i = 0;
       for ( GenPolynomial<BigInteger> p : D ) {
           List<Integer> il = map.get(p);
           if ( il == null ) {
              il = new ArrayList<Integer>(3);
           }
           il.add( i );
           map.put( p, il );
           i++;
       }
       List<List<Integer>> V = new ArrayList<List<Integer>>( map.values() ); 
       //System.out.println("V = " + V);
       //for ( int j = V.size()-1; j >= 0; j-- ) {
       for ( int j = 0; j < V.size(); j++ ) {
           List<Integer> v = V.get(j); 
           for ( Integer k : v ) {
               P.add( k );
           }
       }
       return P;
    }


    /**
     * Permutation of a list.
     * @param L list.
     * @param P permutation.
     * @return P(L).
     */
    public static <T> 
       List<T> 
       listPermutation( List<Integer> P, List<T> L ) {
        if ( L == null || L.size() <= 1 ) {
           return L;
        }
        List<T> pl = new ArrayList<T>( L.size() );
        for ( Integer i : P ) {
            pl.add( L.get( (int)i ) );
        }
        return pl;
    }


    /**
     * Permutation of an array.
     * Compiles, but does not work, requires JDK 1.6 to work.
     * @param a array.
     * @param P permutation.
     * @return P(a).
     */
    @SuppressWarnings("unchecked") 
    public static <T>
       T[]
       arrayPermutation( List<Integer> P, T[] a ) {
        if ( a == null || a.length <= 1 ) {
           return a;
        }
        T[] b = (T[]) new Object[a.length];    // jdk 1.5 
        //T[] b = Arrays.<T>copyOf( a, a.length ); // jdk 1.6, works
        int j = 0;
        for ( Integer i : P ) {
            b[j] = a[ (int)i ];
            j++;
        }
        return b;
    }


    /**
     * Permutation of an array.
     * @param a array.
     * @param P permutation.
     * @return P(a).
     */
    public static
       String[]
       stringArrayPermutation( List<Integer> P, String[] a ) {
        if ( a == null || a.length <= 1 ) {
           return a;
        }
        String[] b = new String[a.length];    // jdk 1.5
        //T[] b = Arrays.<T>copyOf( a, a.length ); // jdk 1.6
        int j = 0;
        for ( Integer i : P ) {
            b[j] = a[ (int)i ];
            j++;
        }
        return b;
    }


    /**
     * Permutation of a long array.
     * @param a array of long.
     * @param P permutation.
     * @return P(a).
     */
    public static 
       long[]
       longArrayPermutation( List<Integer> P, long[] a ) {
        if ( a == null || a.length <= 1 ) {
           return a;
        }
        long[] b = new long[ a.length ];
        int j = 0;
        for ( Integer i : P ) {
            b[j] = a[ (int)i ];
            j++;
        }
        return b;
    }


    /**
     * Permutation of an exponent vector.
     * @param e exponent vector.
     * @param P permutation.
     * @return P(e).
     */
    public static
       ExpVector
       permutation( List<Integer> P, ExpVector e ) {
        if ( e == null ) {
           return e;
        }
        long[] u = longArrayPermutation( P, e.getVal() );
        ExpVector f = ExpVector.create( u );
        return f;
    }


    /**
     * Permutation of polynomial exponent vectors.
     * @param A polynomial.
     * @param R polynomial ring.
     * @param P permutation.
     * @return P(A).
     */
    public static <C extends RingElem<C>> 
       GenPolynomial<C> 
       permutation( List<Integer> P, GenPolynomialRing<C> R, GenPolynomial<C> A ) {
        if ( A == null ) {
           return A;
        }
        GenPolynomial<C> B = R.getZERO().clone();
        Map<ExpVector,C> Bv = B.val; //getMap();
        for ( Map.Entry<ExpVector,C> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            //System.out.println("e = " + e);
            ExpVector f = permutation(P,e);
            //System.out.println("f = " + f);
            Bv.put( f, a ); // assert f not in Bv
        }
        return B;
    }


    /**
     * Permutation of polynomial exponent vectors.
     * @param L list of polynomials.
     * @param R polynomial ring.
     * @param P permutation.
     * @return P(L).
     */
    public static <C extends RingElem<C>> 
       List<GenPolynomial<C>> 
       permutation( List<Integer> P, GenPolynomialRing<C> R, List<GenPolynomial<C>> L ) {
        if ( L == null || L.size() == 0 ) {
           return L;
        }
        List<GenPolynomial<C>> K = new ArrayList<GenPolynomial<C>>( L.size() );
        for ( GenPolynomial<C> a: L ) {
            GenPolynomial<C> b = permutation( P, R, a );
            K.add( b );
        }
        return K;
    }


    /**
     * Permutation of polynomial exponent vectors of coefficient polynomials.
     * @param A polynomial.
     * @param R polynomial ring.
     * @param P permutation.
     * @return P(A).
     */
    public static <C extends RingElem<C>> 
       GenPolynomial<GenPolynomial<C>> 
       permutationOnCoefficients( List<Integer> P, 
                                  GenPolynomialRing<GenPolynomial<C>> R, 
                                  GenPolynomial<GenPolynomial<C>> A ) {
        if ( A == null ) {
           return A;
        }
        GenPolynomial<GenPolynomial<C>> B = R.getZERO().clone();
        GenPolynomialRing<C> cf = (GenPolynomialRing<C>) R.coFac;
        Map<ExpVector,GenPolynomial<C>> Bv = B.val; //getMap();
        for ( Map.Entry<ExpVector,GenPolynomial<C>> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            //System.out.println("e = " + e);
            GenPolynomial<C> b = permutation(P,cf,a);
            //System.out.println("b = " + b);
            Bv.put( e, b ); // assert e not in Bv
        }
        return B;
    }


    /**
     * Permutation of polynomial exponent vectors of coefficients.
     * @param L list of polynomials.
     * @param R polynomial ring.
     * @param P permutation.
     * @return P(L).
     */
    public static <C extends RingElem<C>> 
       List<GenPolynomial<GenPolynomial<C>>> 
       permutationOnCoefficients( List<Integer> P, 
                                  GenPolynomialRing<GenPolynomial<C>> R, 
                                  List<GenPolynomial<GenPolynomial<C>>> L ) {
        if ( L == null || L.size() == 0 ) {
           return L;
        }
        List<GenPolynomial<GenPolynomial<C>>> K 
            = new ArrayList<GenPolynomial<GenPolynomial<C>>>( L.size() );
        for ( GenPolynomial<GenPolynomial<C>> a: L ) {
            GenPolynomial<GenPolynomial<C>> b = permutationOnCoefficients( P, R, a );
            K.add( b );
        }
        return K;
    }


    /**
     * Permutation of polynomial ring variables.
     * @param R polynomial ring.
     * @param P permutation.
     * @return P(R).
     */
    public static <C extends RingElem<C>> 
       GenPolynomialRing<C>
       permutation( List<Integer> P, GenPolynomialRing<C> R ) {
        if ( R == null ) {
           return R;
        }
        if ( R.vars == null || R.nvar <= 1 ) {
           return R;
        }
        GenPolynomialRing<C> S;
        TermOrder tord = R.tord;
        if ( tord.getEvord2() != 0 ) {
           throw new IllegalArgumentException("split term orders not permutable");
        }
        long[][] weight = tord.getWeight();
        if ( weight != null ) {
           long[][] w = new long[ weight.length ][];
           for ( int i = 0; i < weight.length; i++ ) {
               w[i] = longArrayPermutation(P,weight[i]);
           }
           tord = new TermOrder( w );
        }
        String[] v1 = new String[R.vars.length];
        for ( int i = 0; i < v1.length; i++ ) {
            v1[i] = R.vars[ v1.length-1 - i ];
        }
        String[] vars = stringArrayPermutation( P, v1 );
        String[] v2 = new String[R.vars.length];
        for ( int i = 0; i < v1.length; i++ ) {
            v2[i] = vars[ v2.length-1 - i ];
        }
        S = new GenPolynomialRing<C>( R.coFac, R.nvar, tord, v2 );
        return S;
    }


    /**
     * Optimize variable order.
     * @param R polynomial ring.
     * @param L list of polynomials.
     * @return optimized polynomial list.
     */
    public static <C extends RingElem<C>> 
       OptimizedPolynomialList<C>
       optimizeTermOrder( GenPolynomialRing<C> R, List<GenPolynomial<C>> L ) {
       List<Integer> perm = optimalPermutation( degreeMatrix(L) );
       GenPolynomialRing<C> pring;
       pring = TermOrderOptimization.<C>permutation( perm, R );

       List<GenPolynomial<C>> ppolys;
       ppolys = TermOrderOptimization.<C>permutation( perm, pring, L );

       OptimizedPolynomialList<C> op 
           = new OptimizedPolynomialList<C>(perm,pring,ppolys);
       return op;
    }


    /**
     * Optimize variable order.
     * @param P polynomial list.
     * @return optimized polynomial list.
     */
    public static <C extends RingElem<C>> 
       OptimizedPolynomialList<C>
       optimizeTermOrder( PolynomialList<C> P ) {
       if ( P == null ) {
          return null;
       }
       List<Integer> perm = optimalPermutation( degreeMatrix( P.list ) );
       GenPolynomialRing<C> pring;
       pring = TermOrderOptimization.<C>permutation( perm, P.ring );

       List<GenPolynomial<C>> ppolys;
       ppolys = TermOrderOptimization.<C>permutation( perm, pring, P.list );

       OptimizedPolynomialList<C> op 
           = new OptimizedPolynomialList<C>(perm,pring,ppolys);
       return op;
    }


    /**
     * Optimize variable order on coefficients.
     * @param P polynomial list.
     * @return optimized polynomial list.
     */
    public static <C extends RingElem<C>> 
       OptimizedPolynomialList<GenPolynomial<C>>
       optimizeTermOrderOnCoefficients( PolynomialList<GenPolynomial<C>> P ) {
       if ( P == null ) {
          return null;
       }
       List<Integer> perm = optimalPermutation( degreeMatrixOfCoefficients( P.list ) );

       GenPolynomialRing<GenPolynomial<C>> ring = P.ring;
       GenPolynomialRing<C> coFac = (GenPolynomialRing<C>) ring.coFac;
       GenPolynomialRing<C> pFac;
       pFac = TermOrderOptimization.<C>permutation( perm, coFac );

       GenPolynomialRing<GenPolynomial<C>> pring;
       pring = new GenPolynomialRing<GenPolynomial<C>>(pFac, ring.nvar, ring.tord, ring.vars);

       List<GenPolynomial<GenPolynomial<C>>> ppolys;
       ppolys = TermOrderOptimization.<C>permutationOnCoefficients( perm, pring, P.list );

       OptimizedPolynomialList<GenPolynomial<C>> op 
           = new OptimizedPolynomialList<GenPolynomial<C>>(perm,pring,ppolys);
       return op;
    }

}
