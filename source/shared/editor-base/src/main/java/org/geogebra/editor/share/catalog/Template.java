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

/**
 * Base class for all math editor templates (symbols, functions, arrays).
 */
public class Template implements Serializable {

	private final Tag tag;
	private final String texName;

	Template(Tag tag, String texName) {
		this.tag = tag;
		this.texName = texName;
	}

	/**
	 * @return Tag.
	 */
	public Tag getTag() {
		return tag;
	}

	/**
	 * @return TeX name.
	 */
	public String getTexName() {
		return texName;
	}
}
