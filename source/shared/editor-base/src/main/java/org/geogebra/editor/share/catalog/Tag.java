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
