package org.mathpiper.builtin.javareflection;

/** An Importer that knows how to import a single class. **/
public class SingleImporter implements Importer {
  String fullName;
  Class c;

  public SingleImporter(String fullName) {
    this.fullName = fullName;
    reset();
  }

  public Class classNamed(String name) {
    /* An import may occur before the class is on the classpath,
       so Import.forName() will return null. **/
    if (c == null) reset();
    return (fullName.equals(name) || fullName.endsWith("."+name))
      ? c : null;
  }

  public boolean equals(Object x) {
    return this.getClass() == x.getClass() &&
      this.fullName == ((SingleImporter)x).fullName;
  }

  public int hashCode() {return this.fullName.hashCode();}

  public String toString() {return "(import " + fullName + ")";}

  public void reset() {this.c = Import.forName(fullName);}
}
