package org.mathpiper.builtin.javareflection;
import java.lang.reflect.Constructor;

/** Provides dynamic constructors.
 * @author Peter Norvig, Copyright 1998, peter@norvig.com, <a href="license.txt">license</a>
 * subsequently modified by Jscheme project members
 * licensed under zlib licence (see license.txt)
 **/

public class JavaConstructor extends StaticReflector {

  private transient Object[] methods;

  /** Depricated! **/
  public JavaConstructor(Class c) throws Exception {
    this(c.getName());
  }

  public JavaConstructor(String c, boolean isPrivileged) throws Exception {
    this.name = c;
    this.isPrivileged = isPrivileged;
    this.reset();
  }

  public JavaConstructor(String c) throws Exception {
      this(c,false);
  }

  public Object apply(Object[] args) throws Exception{
    return Invoke.invokeRawConstructor
      (((Constructor) Invoke.findMethod(methods, args)), args);
  }

  protected synchronized void reset() throws Exception {
    methods = Invoke.constructorTable(name, isPrivileged);

    int min = Integer.MAX_VALUE;
    int max = 0;

    for(int i = 0; i < methods.length; i = i + Invoke.BUCKET_SIZE) {
      int n = ((Object[]) methods[i]).length;
      if (n < min) min = n;
      if (n > max) max = n;
    }
    minArgs = min;
    maxArgs = max;
  }

  /** Code is like (vector Hashtable. 10), ie the first element is the
      Constructor. **/
}
