/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Quick and dirty XML parser. Java Tip 128
 * http://www.javaworld.com/javaworld/javatips/jw-javatip128.html
 */

package geogebra.io;

import java.util.LinkedHashMap;

public interface DocHandler {
  public void startElement(String tag,LinkedHashMap<String, String> h) throws Exception;
  public void endElement(String tag) throws Exception;
  public void startDocument() throws Exception;
  public void endDocument() throws Exception;
  public void text(String str) throws Exception;
  // Added for Intergeo File Format (Yves Kreis) -->
  public int getConsStep();
  // <-- Added for Intergeo File Format (Yves Kreis)
}
