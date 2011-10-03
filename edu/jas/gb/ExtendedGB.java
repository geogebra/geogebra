/*
 * $Id: ExtendedGB.java 2989 2010-01-31 11:06:39Z kredel $
 */

package edu.jas.gb;

import java.util.List;

import edu.jas.structure.RingElem;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;

import edu.jas.vector.ModuleList;


/**
  * Container for a GB and transformation matrices.
  * A container for F, G, calG and calF.
  * Immutable objects.
  * @param <C> coefficient type
  * @param F an ideal base.
  * @param G a Groebner base of F.
  * @param F2G a transformation matrix from F to G.
  * @param G2F a transformation matrix from G to F.
  */
public class ExtendedGB<C extends RingElem<C>> {

       public final List<GenPolynomial<C>> F;
       public final List<GenPolynomial<C>> G;
       public final List<List<GenPolynomial<C>>> F2G;
       public final List<List<GenPolynomial<C>>> G2F;
       public final GenPolynomialRing<C> ring;


       public ExtendedGB( List<GenPolynomial<C>> F,
                          List<GenPolynomial<C>> G,
                          List<List<GenPolynomial<C>>> F2G,
                          List<List<GenPolynomial<C>>> G2F) {
            this.F = F;
            this.G = G;
            this.F2G = F2G;
            this.G2F = G2F;
            GenPolynomialRing<C> r = null;
         if ( G != null ) {
               for ( GenPolynomial<C> p : G ) {
                   if ( p != null ) {
                      r = p.ring;
                      break;
                   }
               }
               if ( r != null && r.getVars() == null ) {
                  r.setVars( r.newVars("y") );
               }
         }
            this.ring = r;
        }


        /** Get the String representation.
         * @see java.lang.Object#toString()
         */
        @Override
          public String toString() {
            PolynomialList<C> P;
            ModuleList<C> M;
            StringBuffer s = new StringBuffer("ExtendedGB: \n\n");
            P = new PolynomialList<C>( ring, F );
            s.append("F = " + P + "\n\n");
            P = new PolynomialList<C>( ring, G );
            s.append("G = " + P + "\n\n");
            M = new ModuleList<C>( ring, F2G );
            s.append("F2G = " + M + "\n\n");
            M = new ModuleList<C>( ring, G2F );
            s.append("G2F = " + M + "\n");
            return s.toString();
        }

}
