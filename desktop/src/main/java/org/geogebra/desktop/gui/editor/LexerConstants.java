/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.editor;

/**
 * 
 * @author Calixte DENIZET
 *
 */
public interface LexerConstants {

	/**
	 * DEFAULT : tokens which are not recognized
	 */
	public static final int DEFAULT = 0;

	/**
	 * WHITE : A white char ' '
	 */
	public static final int WHITE = 1;

	/**
	 * TAB : A tabulation '\t'
	 */
	public static final int TAB = 2;

	/**
	 * UNKNOWN : an unknown variables or command
	 */
	public static final int UNKNOWN = 3;
}
