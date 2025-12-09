/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package javagiac;
// Command line interface to run Giac statements via JNI.
// Usage on command line:
// 1. Compile it: "javac *.java".
// 2. Run it: "cd ..; java -Djava.library.path=lib javagiac/minitest '1+2*(3+4)'

public class minitest {
  static {
    try {
	System.out.println("Loading giac java interface...");
		System.loadLibrary("javagiac64");
    } catch (UnsatisfiedLinkError e) {
    	e.printStackTrace();
      System.err.println("Native code library failed to load. See the chapter on Dynamic Linking Problems in the SWIG Java documentation for help.\n" + e);
      System.exit(1);
    }
  }

  public static void main(String argv[]) {
    context C = new context();
    String s = new String(argv[0]);
    gen g = new gen(s, C);
    g = g.eval(1, C);
    System.out.println(g.print(C));
  }
}
