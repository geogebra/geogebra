

package org.mathpiper.builtin.javareflection;

import org.mathpiper.lisp.cons.ConsPointer;


public class U {

  public static Class toClass(Object c) throws Exception {
    if (c instanceof Class) return (Class) c;
    else return Import.classNamed(stringify(c, false));
  }



  /** Convert x to a String giving its external representation.
   * Strings and characters are quoted. **/
  public static String stringify(Object x) { return stringify(x, true); }

  /** Convert x to a String giving its external representation.
   * Strings and characters are quoted iff <tt>quoted</tt> is true.. **/
  public static String stringify(Object x, boolean quoted) {
    // Handle these cases without consing:
    if (x instanceof String && !quoted) return ((String) x);
    /*else if (x instanceof Symbol) return ((Symbol) x).toString();
    else return stringify(x, quoted, new StringBuffer()).toString();*/

      return "";
  }//end method


    /** Creates a three element list. **/
  public static ConsPointer list(Object a, Object b, Object c) {
    //return new Pair(a, new Pair(b, new Pair(c, Pair.EMPTY)));
      return null;
  }

  /** Creates a two element list. **/
  public static ConsPointer list(Object a, Object b) {
    //return new Pair(a, new Pair(b, Pair.EMPTY));
      return null;
  }

  /** Creates a one element list. **/
  public static ConsPointer list(Object a) {
    //return new Pair(a, Pair.EMPTY);
      return null;
  }


  public static Object[] listToVector(Object x) {
    /*Pair list = toList(x);
    int L = list.length();
    Object[] result = new Object[L];
    for (int i = 0; isPair(list); i++, list = toList(list.rest))
      result[i] = first(list);
    return result; todo:tk */
      return null;
  }


  public static ConsPointer vectorToList(Object vec) {
    /*Pair result = Pair.EMPTY;
    for (int i = Array.getLength(vec)-1; i>=0; i--) {
      result = new Pair(Array.get(vec, i), result);
    }
    return result;*/
      return null;
  }

}//end class
