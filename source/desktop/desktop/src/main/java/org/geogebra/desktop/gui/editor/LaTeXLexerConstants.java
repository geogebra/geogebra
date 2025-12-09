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
public interface LaTeXLexerConstants extends LexerConstants {

	/**
	 * Number of known tokens
	 */
	public static final int NUMBEROFTOKENS = 12;

	/**
	 * AMP : token '&amp;' in array env
	 */
	public static final int AMP = 4;

	/**
	 * SUBSUP : '_' or '^'
	 */
	public static final int SUBSUP = 5;

	/**
	 * NUMBER : I don't know ;)
	 */
	public static final int NUMBER = 6;

	/**
	 * OPENCLOSE : '{' or ']'
	 */
	public static final int OPENCLOSE = 7;

	/**
	 * COMMAND : commands such as \frac
	 */
	public static final int COMMAND = 8;

	/**
	 * DOLLAR : '$'
	 */
	public static final int DOLLAR = 9;

	/**
	 * COMMAND : commands such as \frac
	 */
	public static final int COMMENTS = 10;

	/**
	 * EOF : End Of File
	 */
	public static final int EOF = 11;
}
