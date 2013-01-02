/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.common.util;

public class Util extends Object {

    /**
     * Removes < > " * / ? | \ and replaces them with underscore (_)
	 * Michael Borcherds 2007-11-23
     */
    public static String processFilename(String name) {
		int length = name != null ? name.length() : 0;
    	
    	StringBuilder sb = new StringBuilder();
		for (int i=0; i < length ; i++) {
			char c = name.charAt(i);
			if     (c == '<' ||
					c == '>' ||
					c == '"' ||
					c == ':' ||
					c == '*' ||
					c == '/' ||
					c == '\\' ||
					c == '?' ||
					c == '\u00a3' || // seems to turn into '�' inside zips
					c == '|' )
			{
				sb.append("_");
			}
			else
			{
				sb.append(c);
			}
		}
		
		if (sb.length() == 0) {
			sb.append("geogebra");
		}
		
		return sb.toString();
	}
}
