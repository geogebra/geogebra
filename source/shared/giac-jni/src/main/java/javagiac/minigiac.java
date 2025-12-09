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
// Command line interface to run Giac statements via JNI as a filter.

import java.util.Scanner;

public class minigiac {
  static {
    try {
	System.loadLibrary("javagiac");
    } catch (UnsatisfiedLinkError e) {
    	e.printStackTrace();
        System.exit(1);
    }
  }

  public static void main(String [] args) throws java.io.IOException
  {
    context C=new context();
    int n = 1;
    gen g;

    String line;
    Scanner stdin = new Scanner(System.in);
    while(stdin.hasNextLine() && ! (line = stdin.nextLine()).equals( "" )) {
	System.out.println(n + ">> " + line);
	g=new gen(line,C).eval(1,C);
	System.out.println(n + "<< " + g.print(C));
	n++;
        }
    stdin.close();
    }
}
