package org.mathpiper.builtin.javareflection;

/**  
     Class importing.

  @author Ken R. Anderson, Copyright 2000, kanderso@bbn.com, <a href="license.txt">license</a>
  subsequently modified by Jscheme project members
  licensed under zlib licence (see license.txt)

   <p>Import provides support for Scheme's <tt>(import)</tt>
   procedure.  It is <i>roughly</i> like Java's import statement, with
   important differences described below.

   <p><tt>(import)</tt> can be used to import a single class, such as:
   <pre>
   (import "java.util.Date")
   </pre>

   Or all the classes of a package using the wildcard "*":

   <pre>
   (import "java.util.*")
   </pre>

   <p> <b>However</b>, using wildcard imports is not recommend
   (deprecated) for the following reasons:

   <ul>

   <li>Class name lookup using wildcards requires generating class names
   that do not exits.  While this is fast for an application, it can
   take about a second for each lookup in an applet.

   <li>Conflicts between imports are identified at (import) time,
   rather than later in runtime.
   </ul>

**/
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

public class Import {

  private static ClassLoader CLASSLOADER = Import.class.getClassLoader();
  static {
   try {
         Thread.currentThread().setContextClassLoader
            (Import.class.getClassLoader());}
   catch (Exception e) {;}
  }
    
  /** Get the ClassLoader used to look up classes. **/
  public static synchronized ClassLoader getClassLoader() {
    return CLASSLOADER;
  }
  /** Set the ClassLoader used to look up classes. **/
  public static synchronized void setClassLoader(ClassLoader cl) {
    CLASSLOADER = cl;
      Thread.currentThread().setContextClassLoader(cl);
  }

  /**
     Fields singles and wilds should be HashSets which won't exist
     until JDK 1.2.  So we simulate them with Vectors, which existed
     since JDK 1.0.
   **/
  public static final Vector singles = new Vector(50);
  public static final Vector wilds = new Vector(50);
  public static final Hashtable table = new Hashtable (200);

  // KRA 17AUG01: Eventually add these as singles and wilds.
  static {
    addImport("java.lang.Object");
    addImport("java.lang.*");
    addImport("java.lang.reflect.*");
    addImport("java.util.*");
    addImport("jsint.*");
  }

  /** Add an import, clearing the cache if it's wild. **/
  public static synchronized void addImport(String name) {
    // System.out.println("addImport: " + name);
    if (name.endsWith("*")) {
      addNew(wilds, new WildImporter(name));
      table.clear();
    } else addNew(singles, new SingleImporter(name));
  }

  /* Use Vector to simulate a HashSet. */
  private static void addNew(Vector v, Object x) {
    if (x != null &&!v.contains(x)) v.addElement(x);
  }

  /**
     Find a Class named <tt>name</tt> either relative to imports, or
     absolute, or error. Names of the form <tt>$name</tt> are
     interpreted as absolute specifications for package-less classes
     for historical reasons.  
   **/
  public static Class classNamed(String name) throws Exception {
    Class c = maybeClassNamed(name);
    return (c == null) ?
      (Class) E.error("Can't find class " + name + "."):
      c;
  }

  /** Returns a class or return null. **/
  public static synchronized Class maybeClassNamed(String name) throws Exception {
    Class c = ((Class) table.get(name)); // Cached?
    if (c != null) return c;
    c = classNamedLookup(name);
    if (c != null) table.put(name, c);
    return c;
  }

  private static Class classNamedLookup(String name) throws Exception {
    if (name.endsWith("[]"))
      return classNamedArray(name.substring(0, name.length() - "[]".length()));
    Class c = classNamedImported(name);
    if (c != null) return c;
    return primitiveClassNamed(name);
  }

  /**
     Search for class named <tt>name</tt> looking in singles.
     Search packageless classes and wilds only if necessary.
  **/
  private static Class classNamedImported(String name) {
    Vector classes = find(singles, name, new Vector(5));
    if (name.lastIndexOf(".") == -1) { // No package prefix.
      if (classes.size() == 0) classes = classNamedNoPackage(name, classes);
      if (classes.size() == 0) classes = find(wilds, name, classes);
    } else addNew(classes, Import.forName(name));
    return returnClass(name, classes);
  }

  private static Class returnClass(String name, Vector classes) {
    int L = classes.size();
    if (L == 0) return null;
    if (L == 1) return ((Class) classes.elementAt(0));
    else 
      return ((Class) E.warn("Class " + name + " is ambiguous " + classes +
			     " choosing " + ((Class) classes.elementAt(0))));
  }

  private static Vector classNamedNoPackage(String name, Vector classes) {
    addNew(classes, Import.forName((name.startsWith("$"))
				   ? name.substring(1,name.length())
				   : name));
    return classes;
  }

  public static Vector find(Vector imports, String name, Vector classes) {
    Enumeration is = imports.elements();
    while (is.hasMoreElements()) 
      addNew(classes, ((Importer) is.nextElement()).classNamed(name));
    return classes;
  }

  /** name is the name of the component class. **/
  private static Class classNamedArray(String name)  throws Exception {
    Class c = classNamed(name);
    if (c.isPrimitive()) return classNamedArrayPrimitive(c);
    if (c.isArray()) return Import.forName("[" + c.getName());
    else return Import.forName("[L" + c.getName() + ";");
  }

  /** Ask the ClassLoader for a class given its full name. **/
  public static Class forName(String name) {
    ClassLoader loader = getClassLoader();
    if (loader == null) 
      try { return Class.forName(name);}
    catch (ClassNotFoundException e) { return null;}
    else
      try { return loader.loadClass(name); }
    catch (ClassNotFoundException e) { return null; }
    // KRA 28JUN00: Renu found this!    
    catch (NoClassDefFoundError e) { return null; } 
  }

  /** Class.forName() doesn't work for primitive types. **/
  private static Class primitiveClassNamed(String name) {
    return
      name.equals("void")    ?      Void.TYPE :
      name.equals("boolean") ?   Boolean.TYPE :
      name.equals("byte")    ?      Byte.TYPE :
      name.equals("char")    ? Character.TYPE :
      name.equals("short")   ?     Short.TYPE :
      name.equals("int")     ?   Integer.TYPE :
      name.equals("long")    ?      Long.TYPE :
      name.equals("float")   ?     Float.TYPE :
      name.equals("double")  ?    Double.TYPE :
      null;
  }

  private static Class classNamedArrayPrimitive(Class c) {
    return
   // (c == void.class)    ?      void[].class :
      (c == boolean.class) ?   boolean[].class :
      (c == byte.class)    ?      byte[].class :
      (c == char.class)    ?      char[].class :
      (c == short.class)   ?     short[].class :
      (c == int.class)     ?       int[].class :
      (c == long.class)    ?      long[].class :
      (c == float.class)   ?     float[].class :
      (c == double.class)  ?    double[].class :
      null;
  }

  private Import() {}		// Don't make one yourself.
}
