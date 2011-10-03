/*
 * $Id: GenMatrix.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.vector;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.AlgebraElem;

import edu.jas.kern.PrettyPrint;


/**
 * GenMatrix implements a generic matrix algebra over RingElem entries.
 * Matrix has n columns and m rows over C.
 * @author Heinz Kredel
 */

public class GenMatrix<C extends RingElem<C> > 
    implements AlgebraElem<GenMatrix<C>,C> {

    private static final Logger logger = Logger.getLogger(GenMatrix.class);

    public final GenMatrixRing< C > ring;

    public final ArrayList<ArrayList<C>> matrix;

    private int hashValue = 0;


    /**
     * Constructor for GenMatrix.
     */
    public GenMatrix(GenMatrixRing< C > r) {
        this( r, r.getZERO().matrix );
    }


    /**
     * Constructor for GenMatrix.
     */
    public GenMatrix(GenMatrixRing< C > r, List<List<C>> m) {
        ring = r;
        matrix = new ArrayList<ArrayList<C>>(r.rows);
        for ( List<C> row : m ) {
            ArrayList<C> nr = new ArrayList<C>(row);
            matrix.add(nr);
        }
        //System.out.println("using List<List> constructor");
    }


    /**
     * Constructor for GenMatrix.
     */
    public GenMatrix(GenMatrixRing< C > r, ArrayList<ArrayList<C>> m) {
        if ( r == null || m == null ) {
            throw new RuntimeException("Empty r or m not allowed, r = " + r + ", m = " +m);
        }
        ring = r;
        matrix = m;
    }


    /**
     * Get element at row i, column j.
     * @param i row index.
     * @param j column index.
     * @return this(i,j).
     */
    public C get(int i, int j) {
        return matrix.get(i).get(j);
    }


    /**
     * Set element at row i, column j.
     * Mutates this matrix.
     * @param i row index.
     * @param j column index.
     * @param el element to set.
     */
    public void setMutate(int i, int j, C el) {
        ArrayList<C> ri = matrix.get(i);
        ri.set(j,el);
        hashValue = 0; // invalidate
    }


    /**
     * Set element at row i, column j.
     * @param i row index.
     * @param j column index.
     * @param el element to set.
     * @return new matrix m, with m(i,j) == el.
     */
    public GenMatrix<C> set(int i, int j, C el) {
        GenMatrix<C> mat = this.clone();
        mat.setMutate(i,j,el);
        return mat;
    }


    /**
     * Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        boolean firstRow = true;
        s.append("[\n");
        for ( List<C> val : matrix ) {
            if ( firstRow ) {
                 firstRow = false;
            } else {
                 s.append(",\n");
            }
            boolean first = true;
            s.append("[ ");
            for ( C c : val ) {
                if ( first ) {
                   first = false;
                } else {
                   s.append(", ");
                }
                s.append( c.toString() );
            }
            s.append(" ]");
        }
        s.append(" ] ");
        if ( ! PrettyPrint.isTrue() ) {
           s.append(":: " + ring.toString());
           s.append("\n");
        }
        return s.toString();
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer();
        boolean firstRow = true;
        s.append("( ");
        for ( List<C> val : matrix ) {
            if ( firstRow ) {
                 firstRow = false;
            } else {
                 s.append(", ");
            }
            boolean first = true;
            s.append("( ");
            for ( C c : val ) {
                if ( first ) {
                   first = false;
                } else {
                   s.append(", ");
                }
                s.append( c.toScript() );
            }
            s.append(" )");
        }
        s.append(" ) ");
        return s.toString();
    }


    /** Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    //JAVA6only: @Override
    public String toScriptFactory() {
        // Python case
        return factory().toScript();
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public GenMatrixRing<C> factory() {
        return ring;
    }


    /**
     * clone method.
     * @see java.lang.Object#clone()
     */
    @Override
    @SuppressWarnings("unchecked")
    public GenMatrix<C> clone() {
        //return ring.copy(this);
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows ); 
        ArrayList<C> v; 
        for ( ArrayList<C> val : matrix ) {
            v = (ArrayList<C>)val.clone();
            m.add( v );
        }
        return new GenMatrix<C>( ring, m );
    }


    /**
     * Test if this is equal to a zero matrix.
     */
    public boolean isZERO() {
        return ( 0 == this.compareTo( ring.getZERO() ) );
    }


    /**
     * Test if this is one.
     * @return true if this is 1, else false.
     */
    public boolean isONE() {
        return ( 0 == this.compareTo( ring.getONE() ) );
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals( Object other ) { 
        if ( ! (other instanceof GenMatrix) ) {
            return false;
        }
        GenMatrix om = (GenMatrix)other;
        if ( ! ring.equals(om.ring) ) {
            return false;
        }
        if ( ! matrix.equals(om.matrix) ) {
            return false;
        }
        return true;
    }


    /** Hash code for this GenMatrix.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if ( hashValue == 0 ) {
           hashValue = 37 * matrix.hashCode() + ring.hashCode();
           if ( hashValue == 0 ) {
              hashValue = 1;
           }
        }
        return hashValue;
    }


    /**
     * compareTo, lexicogaphical comparison.
     * @param b other
     * @return 1 if (this &lt; b), 0 if (this == b) or -1 if (this &gt; b).
     */
    //JAVA6only: @Override
    public int compareTo(GenMatrix<C> b) {
        if ( ! ring.equals( b.ring ) ) {
            return -1;
        }
        ArrayList<ArrayList<C>> om = b.matrix;
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> ov = om.get( i++ );
            int j = 0;
            for ( C c : val ) {
                int s = c.compareTo( ov.get( j++ ) );
                if ( s != 0 ) {
                    return s;
                }
            }
        }
        return 0;
    }


    /**
     * Test if this is a unit. 
     * I.e. there exists x with this.multiply(x).isONE() == true.
     * Tests if all diagonal elements are units and all other elements are zero.
     * @return true if this is a unit, else false.
     */
    public boolean isUnit() {
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            int j = 0;
            for ( C el : val ) {
                if ( i == j ) {
                    if ( !el.isUnit()  ) {
                        return false;
                    }
                } else {
                    if ( !el.isZERO()  ) {
                        return false;
                    }
                }
                j++;
            }
            i++;
        }
        return true;
    }


    /**
     * sign of matrix.
     * @return 1 if (this &lt; 0), 0 if (this == 0) or -1 if (this &gt; 0).
     */
    public int signum() {
        return compareTo( ring.getZERO() );
    }


    /**
     * Sum of matrices.
     * @return this+b
     */
    public GenMatrix<C> sum(GenMatrix<C> b) {
        ArrayList<ArrayList<C>> om = b.matrix;
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> ov = om.get( i++ );
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            int j = 0;
            for ( C c : val ) {
                C e = c.sum( ov.get( j++ ) );
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * Difference of matrices.
     * @return this-b
     */
    public GenMatrix<C> subtract(GenMatrix<C> b) {
        ArrayList<ArrayList<C>> om = b.matrix;
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> ov = om.get( i++ );
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            int j = 0;
            for ( C c : val ) {
                C e = c.subtract( ov.get( j++ ) );
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * Negative of this matrix.
     * @return -this
     */
    public GenMatrix<C> negate() {
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        //int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            for ( C c : val ) {
                C e = c.negate();
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * Absolute value of this matrix.
     * @return abs(this)
     */
    public GenMatrix<C> abs() {
        if ( signum() < 0 ) { 
           return negate();
        } else {
           return this;
        }
    }


    /**
     * Product of this matrix with scalar.
     * @return this*s
     */
    public GenMatrix<C> scalarMultiply(C s) {
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        //int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            for ( C c : val ) {
                C e = c.multiply( s );
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * Left product of this matrix with scalar.
     * @return s*this
     */
    public GenMatrix<C> leftScalarMultiply(C s) {
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        //int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            for ( C c : val ) {
                C e = s.multiply( c );
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * Linear compination of this matrix with 
     * scalar multiple of other matrix.
     * @return this*s+b*t
     */
    public GenMatrix<C> linearCombination(C s, GenMatrix<C> b, C t) {
        ArrayList<ArrayList<C>> om = b.matrix;
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> ov = om.get( i++ );
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            int j = 0;
            for ( C c : val ) {
                C c1 = c.multiply(s);
                C c2 = ov.get( j++ ).multiply( t );
                C e = c1.sum( c2 );
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * Linear compination of this matrix with 
     * scalar multiple of other matrix.
     * @return this+b*t
     */
    public GenMatrix<C> linearCombination(GenMatrix<C> b, C t) {
        ArrayList<ArrayList<C>> om = b.matrix;
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> ov = om.get( i++ );
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            int j = 0;
            for ( C c : val ) {
                C c2 = ov.get( j++ ).multiply( t );
                C e = c.sum( c2 );
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * Left linear compination of this matrix with 
     * scalar multiple of other matrix.
     * @return this+t*b
     */
    public GenMatrix<C> linearCombination(C t, GenMatrix<C> b) {
        ArrayList<ArrayList<C>> om = b.matrix;
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> ov = om.get( i++ );
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            int j = 0;
            for ( C c : val ) {
                C c2 = t.multiply( ov.get( j++ ) );
                C e = c.sum( c2 );
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * left linear compination of this matrix with 
     * scalar multiple of other matrix.
     * @return s*this+t*b
     */
    public GenMatrix<C> leftLinearCombination(C s, C t, 
                                              GenMatrix<C> b) {
        ArrayList<ArrayList<C>> om = b.matrix;
        ArrayList<ArrayList<C>> m = new ArrayList<ArrayList<C>>( ring.rows );
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            ArrayList<C> ov = om.get( i++ );
            ArrayList<C> v = new ArrayList<C>( ring.cols );
            int j = 0;
            for ( C c : val ) {
                C c1 = s.multiply(c);
                C c2 = t.multiply( ov.get( j++ ) );
                C e = c1.sum( c2 );
                v.add( e );
           }
           m.add( v );
        }
        return new GenMatrix<C>(ring,m);
    }


    /**
     * Transposed matrix.
     * @return transpose(this)
     */
    public GenMatrix<C> transpose(GenMatrixRing<C> tr) {
        GenMatrix<C> t = tr.getZERO().clone();
        ArrayList<ArrayList<C>> m = t.matrix;
        int i = 0;
        for ( ArrayList<C> val : matrix ) {
            int j = 0;
            for ( C c : val ) {
                (m.get(j)).set( i, c ); //A[j,i] = A[i,j]
                j++;
            }
            i++;
        }
        // return new GenMatrix<C>(tr,m);
        return t;
    }


    /**
     * Multiply this with S.
     * @param S
     * @return this * S.
     */
    public GenMatrix<C> multiply(GenMatrix<C> S) {
      int na = ring.blocksize;
      int nb = ring.blocksize;
      //System.out.println("#blocks = " + (matrix.size()/na) + ", na = " + na 
      //    + " SeqMultBlockTrans");
      ArrayList<ArrayList<C>> m = matrix;
      //ArrayList<ArrayList<C>> s = S.matrix;

      GenMatrixRing<C> tr = S.ring.transpose();
      GenMatrix<C> T = S.transpose(tr);
      ArrayList<ArrayList<C>> t = T.matrix;
      //System.out.println("T = " + T); 

      GenMatrixRing<C> pr = ring.product( S.ring );
      GenMatrix<C> P = pr.getZERO().clone();
      ArrayList<ArrayList<C>> p = P.matrix;
      //System.out.println("P = " + P); 

      for (int ii=0; ii < m.size(); ii+=na) {
          for (int jj=0; jj < t.size(); jj+=nb) {

              for (int i=ii; i < Math.min((ii+na),m.size()); i++) {
                  ArrayList<C> Ai = m.get(i); //A[i];
                  for (int j=jj; j < Math.min((jj+nb),t.size()); j++) {
                      ArrayList<C> Bj = t.get(j); //B[j];
                      C c = ring.coFac.getZERO();
                      for (int k=0; k < Bj.size(); k++) {
                          c = c.sum( Ai.get(k).multiply( Bj.get( k ) ) ); 
                          //  c += Ai[k] * Bj[k];
                      }
                      (p.get(i)).set(j,c);  // C[i][j] = c;
                  }
              }

          }
      }
      return new GenMatrix<C>(pr,p);
    }


    /**
     * Multiply this with S.
     * Simple unblocked algorithm.
     * @param S
     * @return this * S.
     */
    public GenMatrix<C> multiplySimple(GenMatrix<C> S) {
      ArrayList<ArrayList<C>> m = matrix;
      ArrayList<ArrayList<C>> B = S.matrix;

      GenMatrixRing<C> pr = ring.product( S.ring );
      GenMatrix<C> P = pr.getZERO().clone();
      ArrayList<ArrayList<C>> p = P.matrix;

      for (int i=0; i < pr.rows; i++) {
          ArrayList<C> Ai = m.get(i); //A[i];
          for (int j=0; j < pr.cols; j++) {
              C c = ring.coFac.getZERO();
              for (int k=0; k < S.ring.rows; k++) {
                  c = c.sum( Ai.get(k).multiply( B.get( k ).get(j) ) ); 
                  //  c += A[i][k] * B[k][j];
              }
              (p.get(i)).set(j,c);  // C[i][j] = c;
          }
      }
      return new GenMatrix<C>(pr,p);
    }


    /**
     * Divide this by S.
     * @param S
     * @return this / S.
     */
    public GenMatrix<C> divide(GenMatrix<C> S) {
        throw new RuntimeException("divide not jet implemented");
        //return ZERO;
    }


    /**
     * Remainder after division of this by S.
     * @param S
     * @return this - (this / S) * S.
     */
    public GenMatrix<C> remainder(GenMatrix<C> S) {
        throw new RuntimeException("remainder not implemented");
        //return ZERO;
    }


    /**
     * Inverse of this.
     * @return x with this * x = 1, if it exists.
     */
    public GenMatrix<C> inverse() {
        throw new RuntimeException("inverse not jet implemented");
        //return ZERO;
    }


    /**
     * Greatest common divisor.
     * @param b other element.
     * @return gcd(this,b).
     */
    public GenMatrix<C> gcd(GenMatrix<C> b) {
        throw new RuntimeException("gcd not implemented");
        //return ZERO;
    }


    /**
     * Extended greatest common divisor.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    public GenMatrix<C>[] egcd(GenMatrix<C> b) {
        throw new RuntimeException("egcd not implemented");
        //return ZERO;
    }

}
