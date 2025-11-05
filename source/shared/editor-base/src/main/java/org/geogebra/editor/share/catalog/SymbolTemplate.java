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

/**
 * Template for mathematical symbols including Greek letters and operators.
 */
public class SymbolTemplate extends CharacterTemplate {
	private final String cas;

	SymbolTemplate(String casName, String texName,
			char unicode, int type) {
		super(texName, unicode, type);
		this.cas = casName;
	}

	/**
	 * @return CAS representation of the symbol
	 */
	public String getCasName() {
		return cas == null ? getUnicodeString() : cas;
	}
}
