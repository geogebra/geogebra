package org.mathpiper.builtin.javareflection;

/**  
 * @author Ken R. Anderson, Copyright 2000, kanderso@bbn.com, <a href="license.txt">license</a>
 * subsequently modified by Jscheme project members
 * licensed under zlib licence (see license.txt)
 */

//import java.lang.reflect.AccessibleObject;  // only  in JDK1.2 revision:
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.mathpiper.builtin.JavaObject;
import org.mathpiper.lisp.cons.ConsPointer;

/** 
    Provides dynamic Java method invocation through Java's Reflection
    interface.  For a good discussion of a Scheme implementation, and
    the issues involved with dynamic method invocation in Java see:
   
   <p> Michael Travers, Java Q & A, Dr. Dobb's Journal, Jan., 2000,
   p. 103-112.

   <p>Primitive types are not widened because it would make method
   selection more ambiguous.  By memoizing constructorTable() and
   methodTable() dynamic method lookup can be done without consing.

   <p>You'll notice that Java doesn't make this very easy.  For
   example it would be nice if Method and Constructor shared an
   Invokable interface.

   <p>Privileged methods can be invoked if the JVM allows it.
   
   <p>The name of a method to be invoked can be any nonnull Object
   with a .toString() that names a method.  It should probably be
   changed to String.
 **/

public class Invoke {

  /** Each bucket in an method table contains a Class[] of
      parameterTypes and the corresponding method or constructor. **/
  public static final int BUCKET_SIZE = 2;

  public static Object peek(Object target, String name) throws Exception {
    return peek0(target.getClass(), name, target);
  }
  
  public static Object peekStatic(Class c, String name) throws Exception {
    return peek0(c, name, c);
  }

  private static Object peek0(Class c, String name, Object target) throws Exception {
    try {
      return c.getField(name).get(target);
    } catch (NoSuchFieldException e) {
      return E.error(target + " has no field named " + name);
    } catch (IllegalAccessException e) {
      return E.error("Can't access the " + name + " field of " + target);
    }
  }

  public static Object poke(Object target, String name, Object value) throws Exception {
    return poke0(target.getClass(), name, target, value);
  }
  
  public static Object pokeStatic(Class c, String name, Object value) throws Exception {
    return poke0(c, name, c, value);
  }

  private static Object poke0(Class c, String name, Object target, 
			      Object value) throws Exception {
    try {
      c.getField(name).set(target, value);
      return value;
    } catch (NoSuchFieldException e) {
      return E.error(target + " has no field named " + name);
    } catch (IllegalAccessException e) {
      return E.error("Can't access the " + name + " field of " + target);
    }
  }

  public static Object invokeConstructor(String c, Object[] args) throws Exception{
    Object[] ms = constructorTable(c, false);
    return invokeRawConstructor (((Constructor) findMethod(ms, args)), args);
  }

  public static Object invokeRawConstructor(Constructor m, Object[] args) throws Exception{
    try {
      return m.newInstance(args);
    } catch (InvocationTargetException e) { 
	//throw new BacktraceException(e.getTargetException(),new Object[]{m,args});
        throw e; //todo:tk.
    } catch (InstantiationException e) {
      return E.error("Error during instantiation: ", U.list(e, m, args));
    } catch (IllegalAccessException e) { 
      return E.error("Bad constructor application:", U.list(e, m, args));
    }
  }

  public static Object invokeStatic(Class c, String name, Object[] args) throws Exception{
    return invokeMethod(c, c, name, args, true, false);
  }

  public static Object invokeInstance(Object target, String name,
				      Object[] args,boolean isPrivileged) throws Exception{
    return invokeMethod(target.getClass(), target, name, args, false,
			isPrivileged);
  }

  public static Object invokeMethod(Class c, Object target, String name,
				    Object[] args, boolean isStatic,
				    boolean isPrivileged) throws Exception{
    Object[] ms = methodTable(c, name, isStatic,isPrivileged);
    return invokeRawMethod((Method) findMethod(ms, args), target, args);
  }

  public static Object invokeRawMethod(Method m, Object target, Object[] args) throws Exception{
    try {
	  return m.invoke(target, args);
    } catch (InvocationTargetException e) {
	//throw new BacktraceException(e.getTargetException(),new Object[]{m,target,args});
        throw e; //todo:tk.
    } catch (IllegalAccessException e) { 
	return E.error("Bad method application from a private class: ", U.list(e, m, args));
    } catch (java.lang.IllegalArgumentException e) {
      if (args == null) return E.error(e + "\n " + m.toString() + "\n called with target: " + U.stringify(target) + " and a null argument vector.");
	else return E.error(e + "\nARGUMENT MISMATCH for method \n\n  "+m.toString() +"\n called with " + U.vectorToList(args));
    }
  }
  public static final Hashtable constructorCache = new Hashtable(50);
  public static final Hashtable constructorCachePriv = new Hashtable(50);

  /** Return the constructor table for the named class. **/
  public static Object[] constructorTable(String c, boolean isPrivileged) throws Exception {
    if (isPrivileged) return constructorTable0Priv(c);
    else return constructorTable0(c);
  }

  public static Object[] constructorTable0Priv(String c) throws Exception {
    Object[] result = ((Object[]) constructorCachePriv.get(c));
    if (result == null) {
	try{
	  result = methodArray(makeAccessible(Import.classNamed(c).
					      getDeclaredConstructors()));
        }catch(Exception e){
	    result = methodArray(Import.classNamed(c).getConstructors());}
      constructorCachePriv.put(c, result);
    }
    if (result.length == 0)
      return((Object[]) E.error("Constructor " + c +
				" has no methods."));
    else return result;
  }

  public static Object[] constructorTable0(String c) throws Exception {
    Object[] result = ((Object[]) constructorCache.get(c));
    if (result == null) {
      result = methodArray(Import.classNamed(c).getConstructors());
      constructorCache.put(c, result);
    }
    if (result.length == 0)
      return((Object[]) E.error("Constructor " + c +
				" has no methods."));
    else return result;
  }
  /** Static method name -> Class -> parameter[]/method array. **/
  public static final Hashtable staticCache = new Hashtable(50);
  /** Instance method name -> Class -> parameter[]/method array. **/
  public static final Hashtable instanceCache = new Hashtable(100);
  private static Hashtable getMethodCache(boolean isStatic) {
    return (isStatic) ? staticCache : instanceCache;
  }

  private static Hashtable getNameTable(Hashtable table, String name) {
    Hashtable nameTable = ((Hashtable) table.get(name));
    if (nameTable != null) return ((Hashtable) nameTable);
    else {
      nameTable = new Hashtable(10);
      table.put(name, nameTable);
      return ((Hashtable) nameTable);
    }
  }

  /** Returns a Class -> prameter[]/method array for the method named
   * name. **/
  public static Hashtable getClassTable (String name, boolean isStatic) {
    return getNameTable(getMethodCache(isStatic), name);
  }

  public static Object[] getCachedMethodTable
    (Class c, String name, boolean isStatic) {
    return ((Object[]) getNameTable(getMethodCache(isStatic), name) .get(c));
  }

  public static void putCachedMethodTable
    (Class c, String name, boolean isStatic, Object value) {
    getNameTable(getMethodCache(isStatic), name).put(c, value);
  }

  public static Object[] methodTable0
    (Class c, String name, boolean isStatic,boolean isPrivileged) {
    String internalName = isPrivileged?name.concat("#"):name;
    Object[] result1 = getCachedMethodTable(c, internalName, isStatic);
    if (result1 == null) {
      result1 = methodTableLookup(c, name, isStatic,isPrivileged);
      putCachedMethodTable(c, internalName, isStatic, result1);
    }
    return result1;
  }

  public static Object[] methodTable
    (Class c, String name, boolean isStatic,boolean isPrivileged) throws Exception {
    Object[] result1 = methodTable0(c, name, isStatic,isPrivileged);
    if (result1 == null || result1.length == 0)
	if (isStatic)
	    return ((Object[]) E.error ("ERROR: \nNO STATIC METHOD  OF TYPE  \n\n  ("+ c.getName()+"."+ name+ " ...)"));
        else
	    return ((Object[]) E.error("ERROR: \nNO INSTANCE METHOD OF TYPE \n\n  (."+ name+ " "+ c.getName() +" ...)"));
    else return result1;
  }

  public static Object[] methodTableLookup(Class c, String name,boolean isStatic,boolean isPrivileged) {
    if (isStatic) return methodTableLookupStatic(c, name,isPrivileged);
    else return methodTableLookupInstance(c, name, isPrivileged);
  }

  public static Object[] methodTableLookupStatic(Class c, String name, boolean isPrivileged) {
    Method[] ms = getMethods(c,isPrivileged);
    Vector result = new Vector(ms.length);
    for(int i = 0; i < ms.length; i++) {
      Method m = ms[i];
      if (Modifier.isStatic(m.getModifiers()) && m.getName().equals(name))
	result.addElement(m);
    }
    Object[] result1 = new Object[result.size()];
    result.copyInto(result1);
    return methodArray(result1);
  }

  public static Object[] methodTableLookupInstance(Class c, String name) {
      return methodTableLookupInstance(c, name,false);
  }

  public static Object[] methodTableLookupInstance(Class c, String name,
						   boolean isPrivileged) {
    Vector result = methodVector(c, name, isPrivileged);
    Object[] result1 = new Object[result.size()];
    result.copyInto(result1);
    return methodArray(result1);
    }

  public static Vector methodVector(Class c, String name) {
     return methodVector(c,name,false);
  }

  public static Vector methodVector(Class c, String name, boolean isPrivileged) {
    return methodVectorMerge(c, name, new Vector(10),isPrivileged);
  }

  /** Add new methods to your superclasses table. **/
  public static Vector methodVectorMerge(Class c, String name, Vector result) {
     return methodVectorMerge(c, name, result, false);
  }

  public static Vector methodVectorMerge(Class c, String name, Vector result,boolean isPrivileged) {
    Class s = c.getSuperclass();

    if (s != null) result = methodVectorMerge(s, name, result,isPrivileged);
    Class[] is = c.getInterfaces();
    for (int i = 0; i < is.length; i = i + 1) 
      result = methodVectorMerge(is[i], name, result,isPrivileged);

    Method[] ms = getMethods(c,isPrivileged);  
    for(int i = 0; i < ms.length; i++) {
      Method m = ms[i];
      if ((!Modifier.isStatic(m.getModifiers())) &&
          // KRA 25OCT04: Fixes problem with .append in JDK 1.5.0
	  ((isPrivileged ||
	    (Modifier.isPublic(m.getModifiers()) &&
	     Modifier.isPublic(m.getDeclaringClass().getModifiers())))
	   &&
           m.getName().equals(name)))
        maybeAdd(result, m);
        
    }
    return result;
  }
  
  /** Only add an instance method if no superclass provides one. **/
  private static void maybeAdd(Vector result, Method m1) {
    for(int i = 0; i < result.size(); i++) {
      Method m2 = ((Method) result.elementAt(i));
	if(parameterTypesMatch(getParameterTypes(m1), getParameterTypes(m2)))
	  return;
    }
    result.addElement(m1);
  }

  private static Class[] getParameterTypes(Object m) {
    return (m instanceof Method) ? ((Method) m).getParameterTypes() :
      ((Constructor) m).getParameterTypes();
  }

  /** Returns Object[] of parameterType, method pairs. **/
  private static Object[] methodArray(Object[] v) {
    Object[] result = new Object[v.length*BUCKET_SIZE];
    for(int i = 0; i < v.length; i++) {
      result[i*BUCKET_SIZE] = getParameterTypes(v[i]);
      result[i*BUCKET_SIZE+1] = v[i];
    }
    return result;
  }

  /** Do the paramter types of an instance method match? **/
  public static boolean parameterTypesMatch(Class[] p1, Class[] p2) {
    if (p1.length == p2.length) {
      for (int i = 0; i < p1.length; i++)
	if (p1[i] != p2[i]) return false;
      return true;
    } else return false;
  }

  /** Find the most applicable method.  For instance methods
      getMethods() has already handled the "this" argument, so
      instance and static methods are matched the same way. **/

  public static Object findMethod(Object[] methods, Object[] args) throws Exception {
    if (methods.length == BUCKET_SIZE)
         return methods[1]; // Hope it works!
    return findMethodNoOpt(methods,args);
   }

  static Object findMethodNoOpt(Object[] methods, Object[] args) throws Exception {
    int best = -1;
    for(int m1 = 0; m1 < methods.length; m1 = m1 + BUCKET_SIZE) {
      Class[] p1 = ((Class[]) methods[m1]);
      if (isApplicable(p1, args) &&
	  (best == -1 || !moreApplicable(((Class[]) methods[best]), p1)))
	best = m1;
    }
    if (best != -1) return methods[best+1];

    // print debugging info
    StringBuffer alts = new StringBuffer();
    for(int m1 = 0; m1 < methods.length; m1 = m1 + BUCKET_SIZE)
	if (methods[m1+1] instanceof Member)
           alts.append("   * "+methods[m1+1] +"\n");
	else {
            Class[] ts=(Class[]) methods[m1];
            alts.append("   * "+methods[m1+1]+" ( ");
	    for (int i=0;i<ts.length; i++)
		alts.append(ts[i]+" ");
            alts.append(")\n");
	}

    StringBuffer argtypes = new StringBuffer();
    for(int i=0; i<args.length; i++)
      if (args[i] == null) argtypes.append(" ? ");
      else argtypes.append(" "+args[i].getClass()+" ");
    return E.error("\n\nERROR: NO " +
                   ((methods[1] instanceof Member)? 
                        ((methods[1] instanceof Method)? "METHOD":
			 "CONSTRUCTOR"): "PROCEDURE") +
                   " WITH NAME\n    "+
		   ((methods[1] instanceof Member)?
		    ((Member) methods[1]).getName() : "?") + 
                  "\n and args\n     "+ U.vectorToList(args) + 
                  "\n of types \n    "+argtypes.toString()+
                  "\n\n possible alternatives are :\n" + alts.toString() +
		   "\n\n");
  }

  public static boolean isApplicable (Class[] types, Object[] args) throws Exception {
    if (types.length == args.length) {
      for (int i = 0; i < args.length; i++)
	if (! isArgApplicable(types[i], args[i])) return false;
      return true;
    } else return false;
  }
  
      // Applets don't allow .getClass for non-public objects x
      // but (x instanceof C) is OK
  private static boolean isArgApplicable(Class p, Object a) throws Exception {
    return (a == null  && Object.class.isAssignableFrom(p)) ||
	p.isInstance(a) || 
      p.isPrimitive() && (primitiveWrapperType(p)).isInstance(a);
  }

  /** Given a primitive type return its wrapper class. **/
  private static Class primitiveWrapperType(Class p) throws Exception {
    return 
      p == Byte.TYPE ? Byte.class :
      p == Long.TYPE ? Long.class :
      p == Float.TYPE ? Float.class :
      p == Short.TYPE ? Short.class :
      p == Double.TYPE ? Double.class :
      p == Boolean.TYPE ? Boolean.class :
      p == Integer.TYPE ? Integer.class :
      p == Character.TYPE ? Character.class :
      (Class) E.error("unknow primitive type: ", p);
  }

  /** A method m1 is more specific than method m2 if all parameters of
      m1 are subclasses of the corresponding parameters of m2.  **/ 
  private static boolean moreApplicable(Class[] p1, Class[] p2) { 
    for(int i = 0; i < p1.length; i++)
      if (!p2[i].isAssignableFrom(p1[i])) return false;
    return true;
  }

  /** Look up a particular method given its name, and the name of its
      declaring class, and a list of argument type names.
      <P>This is only used by (method).
  **/
  public static Method findMethod(String name, Object target, ConsPointer types) throws Exception {
    try {
      return U.toClass(target).getMethod(name, toClassArray(types, 0));
    } catch(NoSuchMethodException e) {
      return ((Method) E.error("No method: ", U.list(name, target, types)));
    }
  }

  /** Look up a particular constructor given its name, and the name of its
      declaring class, and a list of argument type names.
      <p>This is only used by (constructor).
  **/
  public static Constructor findConstructor(Object target, ConsPointer types) throws Exception{
    try {
      return U.toClass(target).getConstructor(toClassArray(types, 0));
    } catch(NoSuchMethodException e) {
      return ((Constructor) E.error("No constructor: ", U.list(target, types)));
    }
  }

  public static Constructor findConstructor(String target, Object[] arguments) throws Exception{
     Class[] argumentsArray = new Class[arguments.length];

     for(int index = 0; index < arguments.length; index++)
     {
         Object argument = arguments[index];
         if(argument instanceof JavaObject)
         {
             argument = ((JavaObject)argument).getObject();
         }

         argumentsArray[index] = U.toClass(argument.getClass());
     }//for.

     Constructor constructor = U.toClass(target).getConstructor(argumentsArray);

     return constructor;
  }

  public static Class[] toClassArray(ConsPointer types, int n) throws Exception{
    if (types.getCons() == null /*types == Pair.EMPTY*/) return new Class[n];
    else {
      Class[] cs = toClassArray(((ConsPointer) types.getCons().cdr()), n + 1);
      cs[n] = U.toClass(types.car());
      return cs;
    }
  }

  /** Return all the methods for this class.  If you can't get all, for
   * some reason,, just return the public ones.
   <p>Memoizable.
   **/
  public static Method[] getMethods(Class c,boolean isPrivileged) {
    Method[] methods = getAllMethods(c,isPrivileged);
    return (methods == null) ? c.getMethods() : methods;
  }

  /** Return all the methods on this class, and make them accessable.
      If you can't for some reason, return null;
  **/
  private static Method[] getAllMethods(Class c) {
      return getAllMethods(c,false);
  }

  private static Method[] getAllMethods(Class c,boolean isPrivileged) {
      if (isPrivileged)
          try{return ((Method[]) makeAccessible(getAllMethods0(c)));}
          catch(Exception e){return null;}
      else return null;
  }

  /**
     In some situations you may not be able to get declared methods.
     We only try once.
   **/
  static final boolean ALLOW_PRIVATE_ACCESS=true;
  private static boolean CAN_GET_DECLARED_METHODS = ALLOW_PRIVATE_ACCESS
    ? canGetDeclaredMethods() : false;
  private static boolean canGetDeclaredMethods () {
    try {
      Invoke.class.getDeclaredMethods();
      return true;
    } catch (Exception e) {return false;}}

  private static Method[] getAllMethods0 (Class c) {
    if (CAN_GET_DECLARED_METHODS) {
      Hashtable table = new Hashtable(35);
      collectDeclaredMethods(c, table);
      Enumeration e = ((Enumeration) table.elements());
      Method[] ms = new Method[table.size()];
      for (int i=0; e.hasMoreElements(); i++)
	ms[i] = ((Method)e.nextElement());
      return ms;
    }	
    else return null;
  }

  private static void collectDeclaredMethods(Class c, Hashtable h) {
    Method[] ms = c.getDeclaredMethods();
    for (int i = 0; i < ms.length; i++) h.put(ms[i], ms[i]);
    Class[] is = c.getInterfaces();
    for (int j = 0; j < is.length; j++) collectDeclaredMethods(is[j], h);
    Class sup = c.getSuperclass();
    if (sup != null) collectDeclaredMethods(sup, h);
  }

  /**
     Check that this JVM has AccessibleObject.
     We only try once.
   **/
  static Method SETACCESSIBLE = getSetAccessibleMethod();
  private static Method getSetAccessibleMethod() {
    try {
      Class c = Class.forName("java.lang.reflect.AccessibleObject");
      Class ca = Class.forName("[Ljava.lang.reflect.AccessibleObject;");
      return c.getMethod("setAccessible", new Class[] { ca, Boolean.TYPE });
    } catch (Exception e) {return null;}}

  /** Items should be of type AccessibleObject[] but we can't say that
      on JVM's older than JDK 1.2
      <p>Also used by JavaField.
  **/
  static Object[] makeAccessible(Object[] items) {
    if (items != null && SETACCESSIBLE != null) {
      // AccessibleObject.setAccessible(items, true);
      try {
	SETACCESSIBLE.invoke(null, new Object[] { items, Boolean.TRUE });
      } catch (Exception e) {}
    }
    return items;
  }
}

