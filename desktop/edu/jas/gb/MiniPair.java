/*
 * $Id: MiniPair.java 2412 2009-02-07 12:17:54Z kredel $
 */

package edu.jas.gb;

import java.io.Serializable;

/**
 * Subclass to hold pairs of polynomial indices.
 * What is this class good for?
 * @author Heinz Kredel
 */

class MiniPair implements Serializable {

      public final Integer i;
      public final Integer j;

      MiniPair(int i, int j) {
            this.i = new Integer(i); 
            this.j = new Integer(j);
      }

      @Override
     public String toString() {
          return "miniPair(" + i + "," + j + ")";
      }

}
