package org.mathpiper.builtin.javareflection;
import java.lang.reflect.*;
import java.util.Hashtable;
import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/** 
 * Provides dynamic field access.

 If the field is static (or a Class is given) we cache the Field.
 Otherwise, we cache a class-> field map.
 * @author Peter Norvig, Copyright 1998, peter@norvig.com, <a href="license.txt">license</a>
 * subsequently modified by Jscheme project members
 * licensed under zlib licence (see license.txt)
**/

public class JavaField extends Reflector {

  /** Maps field name -> Class -> Field **/
  static final Hashtable fieldTable     = new Hashtable(20);
  static final Hashtable fieldTablePriv = new Hashtable(20);
  
  static Hashtable fieldTable0(boolean isPrivileged) {
    if (isPrivileged) return fieldTablePriv;
    else return fieldTable;
  }

  /**
     Return the field named <tt>name</tt> in Class <tt>c</tt>.
     Priviledged fields are made accessible if the JVM allows it.
     <p>Memoized.
  **/
  public static Field getField(Class c, String name, boolean isPrivileged) throws Exception {
    try{
      return isPrivileged
	? getDeclaredField(c, name)
	: c.getField(name);
    } catch(NoSuchFieldException e2) {
      return((Field)E.error("no such field: " + c+"."+name));
    } catch(Exception e) {
      return((Field)E.error
	     ("error accessing field: " + c+"."+name+ " is "+e));
    }
  }

  private static Hashtable getFieldClassTable
    (String name, boolean isPrivileged) {
    Hashtable ft = fieldTable0(isPrivileged);
    Hashtable table = ((Hashtable) ft.get(name));
    if (table == null) {
      table = new Hashtable(3);
      ft.put(name, table);
    }
    return table;
  }

  /** Wander over the declared fields, returning the first named
      <tt>name</tt> **/
   private static Field getDeclaredField (Class c, String name)
     throws NoSuchFieldException {
   try{
    Field[] fs = ((Field[]) Invoke.makeAccessible(c.getDeclaredFields()));
    for (int i = 0; i < fs.length; i++)
      if (fs[i].getName().equals(name)) return fs[i];
    Class s = c.getSuperclass();
    if (s != null) return getDeclaredField(s, name);
    else return ((Field) E.error
		 ("\n\nERROR: no field: \""+name+"\" for class \""+c+"\""));
   }catch(Exception e) {
       return c.getField(name);}
  }

  String className;
  transient Field f;
  boolean isStatic = false;
  /** Map Class -> Field **/
  transient Hashtable classTable;

  public JavaField(String name, Class c) throws Exception {
    this(name, c, false);
  }

  public JavaField(String name, Class c, boolean isPrivileged) throws Exception {
    this.name = name;
    this.isPrivileged=isPrivileged;
    if (c != null) this.className = c.getName();
    reset();
  }

  protected synchronized void reset() throws Exception {
    Class c = (className == null) ? null : Import.classNamed(className);
    if (c != null) {
      f = getField(c, name, isPrivileged);
      isStatic = Modifier.isStatic(f.getModifiers());
      minArgs = (isStatic) ? 0 : 1;
      maxArgs = (Modifier.isFinal(f.getModifiers())) ? minArgs : minArgs+1;
    } else {
      classTable = getFieldClassTable(name, isPrivileged);
      minArgs = 1;
      maxArgs = 2;
    }}


  /*
  public Object[] makeArgArray(Object[] code,
                               Evaluator eval,
                               LexicalEnvironment lexenv) {
    int L = code.length - 1;
    if (L == 0 && isStatic) return StaticReflector.args0;
    else if (L == 1)
      return new Object[] { eval.execute(code[1], lexenv) };
    else if (L == 2 && !isStatic)
      return new Object[] { eval.execute(code[1], lexenv),
			      eval.execute(code[2], lexenv) };
    else return ((Object[]) E.error("Wrong number of arguments to field " +
				    this + " " + U.stringify(code)));
  }*/


  /*
  public Object[] makeArgArray (ConsPointer args) throws Exception{
    int L = Utility.listLength(null, -1, args);//   args.length();
    if (L == 0 && isStatic) return StaticReflector.args0;
    else if (L == 1) return new Object[] { args.cdr() };
    else if (L == 2 && !isStatic)
      return new Object[] { args.cdr(), args.second() };
    else return ((Object[]) E.error("Wrong number of arguments to field " +
				    this + " " + U.stringify(args)));
  }*/

  public Object apply(Object[] args) throws Exception {
    int L = args.length;
    if (isStatic) {
      if (L == 1) return setStaticFieldValue(f, args[0]);
      else return getStaticFieldValue(f);
    } else {
      if (L == 1) return getFieldValue(args[0], getTargetField(args[0]));
      else return setFieldValue(args[0],
				getTargetField(args[0]),
				args[1]);
    }
  }

  public Field getTargetField(Object target) throws Exception {
    if (f != null) return f;
    Class c = target.getClass();
    Field it = ((Field) classTable.get(c));
    if (it != null) return it;
    it = getField(c, this.name, this.isPrivileged);
    if (it == null) return (Field) E.error(U.stringify(target) +
					   " does not have a field "
					   + this.name);
    classTable.put(c, it);
    return it;
  }

  public Object getFieldValue(Object target, Field f) throws Exception {
    try { return f.get(target); }
    catch (IllegalAccessException e) {
      return ((Object) E.error("Illegal Access to field: " + f + " in " +
			       U.stringify(target)));
    }}

  public Object setFieldValue(Object target, Field f, Object value) {
    try {
      Object old = f.get(target);
      f.set(target, value);
      return old;
    } catch (IllegalAccessException e) {
      return null;		// Sorry.
    }
  }

  public Object getStaticFieldValue(Field f) {
    try {
      return f.get(null);
    } catch(IllegalAccessException e) {
      return null;		//  Sorry.
    }
  }

  public Object setStaticFieldValue(Field f, Object value) {
    try {
      Object old = f.get(null);
      f.set(null, value);
      return old;
    } catch(IllegalAccessException e) {
      return null;		//  Sorry.
    }
  }
}
