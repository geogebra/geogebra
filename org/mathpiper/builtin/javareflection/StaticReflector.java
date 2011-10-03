package org.mathpiper.builtin.javareflection;
import java.lang.reflect.Constructor;

/** StaticReflector's like JavaConstructor and Generic can share this behavior.
 * @author Peter Norvig, Copyright 1998, peter@norvig.com, <a href="license.txt">license</a>
 * subsequently modified by Jscheme project members
 * licensed under zlib licence (see license.txt)
 **/

public abstract class StaticReflector extends Reflector {

  public static final Object[] args0 = new Object[0];

  /** Code is an Object[] who's first element is a JavaConstructor, and
   * remaining elements are arguments.
  **/
  /*
  public Object[] makeArgArray(Object[] code,
                               Evaluator eval,
                               LexicalEnvironment lexenv) {
    int L = code.length - 1;
    if (L == 0) return args0;
    
    Object[] args = new Object[L];
    for (int i = 0; i < L; i++)
      args[i] = eval.execute(code[i+1], lexenv);
    return args;
  }

  public Object[] makeArgArray (ConsPointer args) {
    return U.listToVector(args);
  }
   * */
}//end class
