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

package org.geogebra.editor.share.catalog;

import java.io.Serializable;

public class ArrayDelimiter implements Serializable {
	private final char character;
	private final String tex;

	/**
	 * Symbol for opening / closing / splitting arrays.
	 * @param character plain text representation
	 * @param tex LaTeX representation
	 */
	public ArrayDelimiter(char character, String tex) {
		this.character = character;
		this.tex = tex;
	}

	/**
	 * Symbol for opening / closing / splitting arrays.
	 * @param character plain text representation
	 */
	public ArrayDelimiter(char character) {
		this(character, String.valueOf(character));
	}

	/**
	 * @return plain text character
	 */
	public char getCharacter() {
		return character;
	}

	/**
	 * @return LaTeX representation
	 */
	public String getTex() {
		return tex;
	}
}
