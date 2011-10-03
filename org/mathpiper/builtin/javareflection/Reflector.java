package org.mathpiper.builtin.javareflection;

import java.util.Enumeration;
import java.util.Vector;

/** A Reflector contains one or more Java metaobjects that are cached.
    They need to be reset() when the classpath is reset.
**/


public abstract class Reflector {  // todo:tk extends { Procedure {

  //todo:tk:added these variables because they were inherited from Procedure.
  public String name = "??";
  public int minArgs = 0;
  public int maxArgs = Integer.MAX_VALUE;


  public static final Vector reflectors = new Vector(100);

  /** Reset all know reflectors **/
  public static void resetAll() throws Exception {
    Enumeration i = reflectors.elements();
    while (i.hasMoreElements())
      ((Reflector) i.nextElement()).reset();
  }

  public boolean isPrivileged = false;

  /** Add yourself to the reflectors **/
  public Reflector() {
    reflectors.addElement(this);
  }

  /** Reset your classpath dependent state.  This method can't be
      abstract.
  **/
  protected synchronized void reset() throws Exception {}

  protected Object readResolve() throws Exception {
    reset();
    return this;
  }

}
