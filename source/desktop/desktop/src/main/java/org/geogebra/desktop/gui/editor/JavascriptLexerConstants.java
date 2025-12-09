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
public interface JavascriptLexerConstants extends LexerConstants {

	/**
	 * Number of known tokens
	 */
	public static final int NUMBEROFTOKENS = 20;

	/**
	 * OPERATOR : tokens like '+', '-', ...
	 */
	public static final int OPERATOR = 4;

	/**
	 * CONSTANTES : Constantes like 'pi' or 'e'
	 */
	public static final int CONSTANTE = 5;

	/**
	 * NUMBER : I don't know ;)
	 */
	public static final int NUMBER = 6;

	/**
	 * OPENCLOSE : '(' or ']'
	 */
	public static final int OPENCLOSE = 7;

	/**
	 * STRING : "bla bla bla"
	 */
	public static final int STRING = 8;

	/**
	 * BUILTINOBJECT : objects such as Array, String, ...
	 */
	public static final int BUILTINOBJECT = 9;

	/**
	 * KEYWORD : keyword such as for, while, ...
	 */
	public static final int KEYWORD = 10;

	/**
	 * IDENTIFIER : variable name
	 */
	public static final int IDENTIFIER = 11;

	/**
	 * FIELDDEF : 'myField: 123' in structure def
	 */
	public static final int FIELDDEF = 12;

	/**
	 * OBJECTNAME : in 'myObject.myField' would be myObject
	 */
	public static final int OBJECTNAME = 13;

	/**
	 * FIELD : in 'myObject.myField' would be myField
	 */
	public static final int FIELD = 14;

	/**
	 * GGBSPECIAL : ggbApplet for example
	 */
	public static final int GGBSPECIAL = 15;

	/**
	 * COMMENTS : comments
	 */
	public static final int LINECOMMENTS = 16;

	/**
	 * COMMENTS : comments
	 */
	public static final int MULTILINECOMMENTS = 17;

	/**
	 * FUNCTION : such as myfun(...)
	 */
	public static final int FUNCTION = 18;

	/**
	 * EOF : End Of File
	 */
	public static final int EOF = 19;
}
