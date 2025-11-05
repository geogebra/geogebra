/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
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
