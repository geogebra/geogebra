package org.mathpiper.builtin.javareflection;

import java.lang.reflect.Method;
import java.util.Hashtable;
import org.mathpiper.lisp.cons.ConsPointer;

/** This class allows you to call any Java method, just by naming it,
 * and doing the dispatch at runtime.
 * @author Peter Norvig, Copyright 1998, peter@norvig.com, <a href="license.txt">license</a>
 * subsequently modified by Jscheme project members
 * licensed under zlib licence (see license.txt)
**/

public class JavaMethod extends Reflector {

  public static final Object[] ZERO_ARGS = new Object[0];

  private String methodClass;
  /** Parameter/method table for a specific method. **/
  private transient Object[] methodTable;
  private boolean isStatic;
  /** Do we know the Class that this method applies to? **/
  private boolean isSpecific;
  /** Class -> methodTable map. **/
  private transient Hashtable classMethodTable;
  
  public boolean isStatic() { return this.isStatic;}
  
  /**

     If the method is static then Class c is not null.  For instance
     methods, if Class c is not null, then it is used at construction
     time to create a method table.  Otherwise, the class of the
     method is determined at call time from the target, and the method
     table is constructed then and cached. Examples (see DynamicVariable.java):

      <pre>
      new JavaMethod("getProperties", System.class, true) - static method
      new JavaMethod("put", Hashtable.class,false)        - specific instance method.
      new JavaMethod("put", null, false)                  - unspecified instance method
      </pre>
   **/

   public JavaMethod(String name, Class c, boolean isStatic, boolean isPrivileged) throws Exception {
    this.name = name;
    if (c != null) this.methodClass = c.getName();
    this.isStatic = isStatic;
    this.isSpecific = (c!=null);
    this.minArgs = isStatic ? 0 : 1;
    this.isPrivileged=isPrivileged;
    reset();
   }

    public JavaMethod(String name, Class c, boolean isStatic) throws Exception {
      this(name,c,isStatic,false);
    }

    public JavaMethod(String name, Class c) throws Exception {
      this(name,c,(c!=null));
    }      

  protected synchronized void reset() throws Exception {
    if (isSpecific) {
      methodTable = Invoke.methodTable0(Import.classNamed(methodClass),
					name,
					isStatic,
                                        isPrivileged);
      if (methodTable.length == 0) {
          methodTable = null;
          E.warn(  "No such "+ (isStatic?" static ":" instance ") + 
                    " method \"" + name + (isSpecific?("\" in class "+methodClass):""));
      }
    } else classMethodTable = new Hashtable(5);
  }

  public Object[] instanceMethodTable(Class c) throws Exception {
    Object[] ms = ((Object[]) classMethodTable.get(c));
    if (ms != null) return ms;
    ms = Invoke.methodTable0(c, name, isStatic, isPrivileged);
    if (ms != null && ms.length > 0) {
      classMethodTable.put(c, ms);
      return ms;
    } else return (Object[]) E.error(c + " has no methods for " + this.name);
  }

  /**
     For a static method, args is an Object[] of arguments.
     For an instance method, args is (vector target (vector arguments));
   **/
  public Object apply(Object[] args) throws Exception{
    if (!(isSpecific)) {
      Object[] methodTable = instanceMethodTable(args[0].getClass());
      Object[] as = (Object[]) args[1];
      Method m = (Method) Invoke.findMethod(methodTable, as);
      return Invoke.invokeRawMethod(m, args[0], as);
    } else {
      if (methodTable == null) return E.error(this + " has no methods");
      if (isStatic) {
	Method m = (Method) Invoke.findMethod(methodTable, args);
	return Invoke.invokeRawMethod(m, null, args);
      } else {
	Object[] as = (Object[]) args[1];
        Method m = (Method) Invoke.findMethod(methodTable, as);
        return Invoke.invokeRawMethod(m, args[0], as);
      }
    }
  }


  /*
  public Object[] makeArgArray(Object[] code,
                               Evaluator eval,
                               LexicalEnvironment lexenv) {
    if (isStatic) {
      int L = code.length - 1;
      if (L == 0) return ZERO_ARGS;
    
      Object[] args = new Object[L];
      for (int i = 0; i < L; i++)
	args[i] = eval.execute(code[i+1], lexenv);
      return args;
    } else {
      int L = code.length - 2;
      if (L < 0)
	return ((Object[])
		E.error("Wrong number of arguments in application: "
			+ U.stringify(code)));
      Object target = eval.execute(code[1], lexenv);
      if (L == 0) return new Object[] { target, ZERO_ARGS };
    
      Object[] args = new Object[L];
      for (int i = 0; i < L; i++)
	args[i] = eval.execute(code[i+2], lexenv);
      return new Object[] { target, args };
    }
  }
      
  public Object[] makeArgArray (ConsPointer args) {
    if (isStatic) return U.listToVector(args);
    else return new Object[] { args.first, U.listToVector(args.rest)} ;
  }

  */
}
