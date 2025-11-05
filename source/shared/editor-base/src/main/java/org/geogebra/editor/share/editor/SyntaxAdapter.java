/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.editor;

/**
 * Syntax aware adapter for the editor
 * @author michael
 */
public interface SyntaxAdapter {

	/**
	 * @param exp
	 *            expression in ggb, LaTeX or Presentation MathML format
	 * @return expression converted into editor syntax if possible
	 */
	String convert(String exp);

	/**
	 * @param casName function name
	 * @return whether a function with this name is supported
	 */
	boolean isFunction(String casName);
}
