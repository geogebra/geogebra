/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.editor;

import java.io.IOException;

import javax.swing.text.Document;

/**
 * 
 * @author Calixte Denizet
 *
 */
public abstract class Lexer {

	public int start;

	/**
	 * Set the range to parse
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public abstract void setRange(int start, int end);

	/**
	 * Get the actual position in the parsed reader
	 * 
	 * @return the position
	 */
	public abstract int yychar();

	/**
	 * Get the length of the matched token
	 * 
	 * @return the length
	 */
	public abstract int yylength();

	/**
	 * Get the token's identifier
	 * 
	 * @return the id
	 */
	public abstract int scan() throws IOException;

	/**
	 * Get the keyword at the current position
	 * 
	 * @param position
	 *            where to search
	 * @param strict
	 *            if true then the keyword is searched just after position
	 * @return the keyword id
	 */
	public abstract int getKeyword(int position, boolean strict);

	/**
	 * Set the document in the lexer
	 * 
	 * @param doc
	 *            the document
	 */
	public abstract void setDocument(Document doc);
}
