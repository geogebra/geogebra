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
	 * @throws IOException when I/O problem occurs
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
