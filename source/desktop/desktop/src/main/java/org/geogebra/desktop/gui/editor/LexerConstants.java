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

// This code has been written initially for Scilab (http://www.scilab.org/).
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
