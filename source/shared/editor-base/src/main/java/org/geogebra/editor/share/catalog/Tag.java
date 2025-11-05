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
 * Identifiers for various template types in the math editor.
 */
public enum Tag {
	CEIL("ceil"),

	FLOOR("floor"),

	SQUARE,

	REGULAR,

	CURLY,

	APOSTROPHES,

	MATRIX("$matrix"),

	CHAR,

	SUPERSCRIPT("^"),

	SUBSCRIPT("_"),

	FRAC("frac"),

	SQRT("sqrt"),

	CBRT("cbrt"),

	NROOT("nroot"),

	LOG("log"),

	DEF_INT("$defint"),

	LIM_EQ("$limeq"),

	PROD_EQ("$prodeq"),

	SUM_EQ("$sumeq"),

	VEC("$vec"),

	ATOMIC_PRE("$atomicpre"),

	ATOMIC_POST("$atomicpost"),

	POINT("$point"),

	POINT_AT("$pointAt"),

	VECTOR("$vector"),

	MIXED_NUMBER("mixedNumber"),

	RECURRING_DECIMAL("recurringDecimal"),

	APPLY,

	ABS("abs"),

	APPLY_SQUARE;

	private final String key;

	Tag(String key) {
		this.key = key;
	}

	Tag() {
		this("");
	}

	public String getKey() {
		return key;
	}

	/**
	 * @param name name
	 * @return tag with given name
	 */
	public static Tag lookup(String name) {
		if (name == null || name.isEmpty()) {
			return null;
		}
		for (Tag tag : Tag.values()) {
			if (name.equals(tag.key)) {
				return tag;
			}
		}
		return null;
	}

	/**
	 * @return whether template has highlighted boxes
	 */
	public boolean isRenderingOwnPlaceholders() {
		switch (this) {
		case POINT:
		case POINT_AT:
		case VECTOR:
		case MATRIX:
			return true;
		default:
			return false;
		}
	}
}
